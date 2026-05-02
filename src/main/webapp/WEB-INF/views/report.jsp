<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AssetFlow - AI 리포트</title>
  <style>
    .report-box {
      background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px;
      padding: 24px; white-space: pre-wrap; font-size: 14px; line-height: 1.8;
      color: #334155; min-height: 200px;
    }
    .generating { opacity: 0.6; cursor: not-allowed; }
  </style>
</head>
<body>
<%@ include file="layout/sidebar.jsp" %>

<div class="page-header">
  <h1>AI 주식 리포트</h1>
  <p>Claude AI가 포트폴리오를 분석하고 요약 리포트를 생성합니다</p>
</div>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<!-- 리포트 생성 버튼 -->
<div class="card">
  <form action="/report/generate" method="post" onsubmit="onGenerate(this)">
    <div style="display:flex; align-items:center; gap:16px; flex-wrap:wrap;">
      <div>
        <div style="font-size:15px; font-weight:600; color:#0f172a; margin-bottom:4px;">새 리포트 생성</div>
        <div style="font-size:13px; color:#64748b;">현재 포트폴리오를 기준으로 AI 분석 리포트를 생성합니다.</div>
      </div>
      <button type="submit" id="generateBtn" class="btn btn-primary" style="margin-left:auto;">
        ✨ 리포트 생성
      </button>
    </div>
  </form>
</div>

<!-- 최신 리포트 -->
<div class="card">
  <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;">
    <div class="card-title" style="margin-bottom:0;">최신 리포트</div>
    <c:if test="${not empty report}">
      <span style="font-size:12px; color:#94a3b8;">
        생성일시: ${report.createdAt}
      </span>
    </c:if>
  </div>

  <c:choose>
    <c:when test="${empty report}">
      <div class="report-box" style="display:flex; align-items:center; justify-content:center; color:#94a3b8;">
        아직 생성된 리포트가 없습니다. 위 버튼으로 리포트를 생성하세요.
      </div>
    </c:when>
    <c:otherwise>
      <div class="report-box">${report.reportText}</div>
    </c:otherwise>
  </c:choose>
</div>

<script>
  function onGenerate(form) {
    var btn = document.getElementById('generateBtn');
    btn.textContent = '생성 중...';
    btn.classList.add('generating');
    btn.disabled = true;
  }
</script>
</div><!-- main-content -->
</body>
</html>
