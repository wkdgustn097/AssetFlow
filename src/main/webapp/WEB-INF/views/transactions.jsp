<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AssetFlow - 거래 내역</title>
  <style>
    .upload-zone {
      border: 2px dashed #e2e8f0; border-radius: 10px; padding: 28px;
      text-align: center; cursor: pointer; transition: border-color 0.15s;
    }
    .upload-zone:hover { border-color: #6366f1; }
    .upload-zone input[type=file] { display: none; }
    .upload-zone label { cursor: pointer; color: #6366f1; font-weight: 500; }
    .filter-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
    .filter-bar input[type=month] {
      padding: 8px 12px; border: 1.5px solid #e2e8f0; border-radius: 8px; font-size: 14px;
    }
  </style>
</head>
<body>
<%@ include file="layout/sidebar.jsp" %>

<div class="page-header">
  <h1>거래 내역</h1>
  <p>CSV 또는 Excel 파일로 소비 내역을 가져오세요</p>
</div>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<!-- 파일 업로드 -->
<div class="card">
  <div class="card-title">파일 업로드</div>
  <form action="/transactions/upload" method="post" enctype="multipart/form-data">
    <div class="upload-zone" onclick="document.getElementById('fileInput').click()">
      <svg fill="none" stroke="#94a3b8" viewBox="0 0 24 24" width="40" height="40" style="margin-bottom:8px;">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"/>
      </svg>
      <p style="color:#64748b; font-size:14px; margin-bottom:6px;">파일을 클릭하여 선택하세요</p>
      <p style="color:#94a3b8; font-size:12px;">지원 형식: CSV, XLS, XLSX</p>
      <p style="color:#94a3b8; font-size:12px; margin-top:4px;">필수 컬럼: date, description, category, amount</p>
      <label><input type="file" id="fileInput" name="file" accept=".csv,.xls,.xlsx" onchange="updateFileName(this)"></label>
      <p id="fileName" style="color:#6366f1; font-size:13px; margin-top:8px;"></p>
    </div>
    <button type="submit" class="btn btn-primary" style="margin-top:14px;">업로드</button>
  </form>
</div>

<!-- 월 필터 + 목록 -->
<div class="card">
  <div class="filter-bar">
    <div class="card-title" style="margin-bottom:0;">거래 목록</div>
    <form method="get" action="/transactions" style="display:flex; gap:8px; align-items:center; margin-left:auto;">
      <input type="month" name="yearMonth" value="${yearMonth}" onchange="this.form.submit()">
    </form>
  </div>

  <c:choose>
    <c:when test="${empty transactions}">
      <p style="text-align:center; color:#94a3b8; padding:40px 0; font-size:14px;">
        ${yearMonth} 거래 내역이 없습니다.
      </p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>날짜</th>
            <th>설명</th>
            <th>카테고리</th>
            <th class="text-right">금액</th>
            <th class="text-right">삭제</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${transactions}" var="t">
            <tr>
              <td style="color:#64748b; font-size:13px;">${t.txnDate}</td>
              <td>${t.description}</td>
              <td><span class="badge badge-blue">${t.category}</span></td>
              <td class="text-right" style="font-weight:600; color:${t.amount >= 0 ? '#6366f1' : '#94a3b8'};">
                <fmt:formatNumber value="${t.amount}" pattern="#,###" />원
              </td>
              <td class="text-right">
                <form action="/transactions/delete/${t.id}" method="post"
                      onsubmit="return confirm('삭제하시겠습니까?')">
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

<script>
  function updateFileName(input) {
    document.getElementById('fileName').textContent = input.files[0] ? input.files[0].name : '';
  }
</script>
</div><!-- main-content -->
</body>
</html>
