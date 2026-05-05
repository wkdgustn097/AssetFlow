<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AssetFlow - 개인 설정</title>
</head>
<body>
<%@ include file="layout/sidebar.jsp" %>

<div class="page-header">
  <h1>개인 설정</h1>
  <p>대시보드 기간 및 개인 설정을 관리합니다</p>
</div>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<div class="card" style="max-width: 480px;">
  <div class="card-title">월급날 설정</div>
  <p style="font-size:13px; color:#64748b; margin-bottom:20px;">
    월급날을 설정하면 대시보드가 <strong>월급날 기준 구간</strong>으로 표시됩니다.<br>
    예) 13일 설정 시 → 3/13 ~ 4/12 구간으로 표시
  </p>
  <form method="post" action="/settings" style="display:block;">
    <div style="margin-bottom:16px;">
      <label style="display:block; font-size:13px; font-weight:600; color:#374151; margin-bottom:8px;">
        월급날 (1~31일, 비워두면 월 기준)
      </label>
      <input type="number" name="payday" min="1" max="31"
             value="${settings.payday != null ? settings.payday : ''}"
             placeholder="예: 13"
             style="width:120px; padding:9px 12px; border:1.5px solid #e2e8f0; border-radius:8px; font-size:14px;">
    </div>
    <button type="submit" class="btn btn-primary">저장</button>
  </form>
</div>

<!-- 예산 설정 -->
<div class="card" style="max-width: 600px;">
  <div class="card-title">카테고리별 예산 설정</div>
  <p style="font-size:13px; color:#64748b; margin-bottom:20px;">
    카테고리별 월 지출 한도를 설정하면 대시보드에서 사용률을 확인할 수 있습니다.
  </p>

  <!-- 예산 추가 폼 -->
  <form method="post" action="/settings/budget" style="display:flex; gap:8px; align-items:flex-end; flex-wrap:wrap; margin-bottom:20px;">
    <div>
      <label style="display:block; font-size:12px; font-weight:600; color:#374151; margin-bottom:4px;">카테고리</label>
      <input type="text" name="category" placeholder="예: 외식" required
             style="padding:8px 12px; border:1.5px solid #e2e8f0; border-radius:8px; font-size:14px; width:140px;">
    </div>
    <div>
      <label style="display:block; font-size:12px; font-weight:600; color:#374151; margin-bottom:4px;">월 한도 (원)</label>
      <input type="number" name="monthlyLimit" placeholder="예: 300000" min="1" required
             style="padding:8px 12px; border:1.5px solid #e2e8f0; border-radius:8px; font-size:14px; width:160px;">
    </div>
    <button type="submit" class="btn btn-primary">추가/수정</button>
  </form>

  <!-- 설정된 예산 목록 -->
  <c:choose>
    <c:when test="${empty budgets}">
      <p style="color:#94a3b8; font-size:13px;">설정된 예산이 없습니다.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr><th>카테고리</th><th class="text-right">월 한도</th><th class="text-right">삭제</th></tr>
        </thead>
        <tbody>
          <c:forEach items="${budgets}" var="b">
            <tr>
              <td>${b.category}</td>
              <td class="text-right"><fmt:formatNumber value="${b.monthlyLimit}" pattern="#,###" />원</td>
              <td class="text-right">
                <form method="post" action="/settings/budget/delete" style="display:inline;">
                  <input type="hidden" name="category" value="${b.category}">
                  <button type="submit" class="btn btn-danger btn-sm"
                          onclick="return confirm('삭제하시겠습니까?')">삭제</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</div>

</div><!-- main-content -->
</body>
</html>
