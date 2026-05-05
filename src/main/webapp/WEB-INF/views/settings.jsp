<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

</div><!-- main-content -->
</body>
</html>
