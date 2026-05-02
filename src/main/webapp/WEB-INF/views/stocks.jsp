<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AssetFlow - 주식 포트폴리오</title>
  <style>
    .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,0.4); z-index:200; align-items:center; justify-content:center; }
    .modal-overlay.open { display:flex; }
    .modal { background:#fff; border-radius:14px; padding:32px; width:420px; max-width:95vw; }
    .modal h2 { font-size:18px; font-weight:700; margin-bottom:20px; }
    .form-group { margin-bottom:14px; }
    .form-group label { display:block; font-size:13px; font-weight:600; color:#374151; margin-bottom:5px; }
    .form-group input { width:100%; padding:9px 12px; border:1.5px solid #e2e8f0; border-radius:8px; font-size:14px; }
    .form-group input:focus { outline:none; border-color:#6366f1; }
    .form-actions { display:flex; gap:10px; justify-content:flex-end; margin-top:20px; }
  </style>
</head>
<body>
<%@ include file="layout/sidebar.jsp" %>

<div class="page-header">
  <h1>주식 포트폴리오</h1>
  <p>해외 주식 보유 현황 및 수익/손실을 확인하세요</p>
</div>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<!-- 액션 버튼 -->
<div style="display:flex; gap:10px; margin-bottom:20px;">
  <button class="btn btn-primary" onclick="document.getElementById('addModal').classList.add('open')">
    + 종목 추가
  </button>
  <form action="/stocks/refresh" method="post">
    <button type="submit" class="btn btn-secondary">가격 갱신</button>
  </form>
</div>

<!-- 포트폴리오 요약 -->
<c:if test="${not empty portfolio}">
  <div style="display:grid; grid-template-columns:repeat(2,1fr); gap:16px; margin-bottom:20px;">
    <div class="card" style="margin-bottom:0;">
      <div style="font-size:13px; color:#64748b; font-weight:500; margin-bottom:6px;">총 평가금액</div>
      <div style="font-size:24px; font-weight:700;">
        $<fmt:formatNumber value="${totalMarketValue}" pattern="#,##0.00" />
      </div>
    </div>
    <div class="card" style="margin-bottom:0;">
      <div style="font-size:13px; color:#64748b; font-weight:500; margin-bottom:6px;">총 평가손익</div>
      <div style="font-size:24px; font-weight:700;" class="${totalPnl >= 0 ? 'text-success' : 'text-danger'}">
        ${totalPnl >= 0 ? '+' : ''}<fmt:formatNumber value="${totalPnl}" pattern="#,##0.00" />
      </div>
    </div>
  </div>
</c:if>

<!-- 포트폴리오 테이블 -->
<div class="card">
  <div class="card-title">보유 종목</div>
  <c:choose>
    <c:when test="${empty portfolio}">
      <p style="text-align:center; color:#94a3b8; padding:40px 0; font-size:14px;">
        보유 종목이 없습니다. 종목을 추가하세요.
      </p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>심볼</th>
            <th>회사명</th>
            <th class="text-right">수량</th>
            <th class="text-right">평균단가</th>
            <th class="text-right">현재가</th>
            <th class="text-right">평가금액</th>
            <th class="text-right">평가손익</th>
            <th class="text-right">수익률</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${portfolio}" var="s">
            <tr>
              <td><strong>${s.symbol}</strong></td>
              <td style="color:#64748b;">${s.companyName}</td>
              <td class="text-right"><fmt:formatNumber value="${s.quantity}" pattern="#,##0.####" /></td>
              <td class="text-right">$<fmt:formatNumber value="${s.avgCost}" pattern="#,##0.00" /></td>
              <td class="text-right">
                <c:choose>
                  <c:when test="${s.priceAvailable}">$<fmt:formatNumber value="${s.currentPrice}" pattern="#,##0.00" /></c:when>
                  <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                </c:choose>
              </td>
              <td class="text-right">
                <c:choose>
                  <c:when test="${s.priceAvailable}">$<fmt:formatNumber value="${s.marketValue}" pattern="#,##0.00" /></c:when>
                  <c:otherwise><span class="text-muted">-</span></c:otherwise>
                </c:choose>
              </td>
              <td class="text-right ${s.pnl >= 0 ? 'text-success' : 'text-danger'}">
                <c:if test="${s.priceAvailable}">
                  ${s.pnl >= 0 ? '+' : ''}<fmt:formatNumber value="${s.pnl}" pattern="#,##0.00" />
                </c:if>
              </td>
              <td class="text-right ${s.pnlPercent >= 0 ? 'text-success' : 'text-danger'}">
                <c:if test="${s.priceAvailable}">
                  ${s.pnlPercent >= 0 ? '+' : ''}<fmt:formatNumber value="${s.pnlPercent}" pattern="#,##0.00" />%
                </c:if>
              </td>
              <td class="text-right">
                <form action="/stocks/delete/${s.id}" method="post" onsubmit="return confirm('삭제하시겠습니까?')">
                  <button type="submit" class="btn btn-danger btn-sm">삭제</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</div>

<!-- 종목 추가 모달 -->
<div class="modal-overlay" id="addModal" onclick="closeModal(event)">
  <div class="modal">
    <h2>종목 추가 / 업데이트</h2>
    <form action="/stocks" method="post">
      <div class="form-group">
        <label>심볼 (예: AAPL, TSLA)</label>
        <input type="text" name="symbol" placeholder="AAPL" required style="text-transform:uppercase;">
      </div>
      <div class="form-group">
        <label>회사명 (선택)</label>
        <input type="text" name="companyName" placeholder="Apple Inc.">
      </div>
      <div class="form-group">
        <label>수량</label>
        <input type="number" name="quantity" step="0.000001" min="0.000001" placeholder="10" required>
      </div>
      <div class="form-group">
        <label>평균 매입단가 (USD)</label>
        <input type="number" name="avgCost" step="0.0001" min="0.0001" placeholder="150.00" required>
      </div>
      <div class="form-actions">
        <button type="button" class="btn btn-secondary" onclick="document.getElementById('addModal').classList.remove('open')">취소</button>
        <button type="submit" class="btn btn-primary">추가</button>
      </div>
    </form>
  </div>
</div>

<script>
  function closeModal(e) {
    if (e.target === document.getElementById('addModal')) {
      document.getElementById('addModal').classList.remove('open');
    }
  }
</script>
</div><!-- main-content -->
</body>
</html>
