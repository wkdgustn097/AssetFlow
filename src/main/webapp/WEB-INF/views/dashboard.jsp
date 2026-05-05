<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AssetFlow - 대시보드</title>
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
<%@ include file="layout/sidebar.jsp" %>

<div class="page-header">
  <h1>대시보드</h1>
  <p>월별 지출 현황을 한눈에 확인하세요</p>
</div>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<!-- 월 선택 -->
<div class="card" style="padding: 16px 24px;">
  <form method="get" action="/dashboard" style="display:flex; align-items:center; gap:12px;">
    <label for="yearMonth" style="font-size:14px; font-weight:600;">기준 월</label>
    <input type="month" id="yearMonth" name="yearMonth" value="${yearMonth}"
           style="padding:7px 12px; border:1.5px solid #e2e8f0; border-radius:8px; font-size:14px;"
           onchange="this.form.submit()">
  </form>
</div>

<!-- 요약 카드 -->
<div style="display:grid; grid-template-columns:repeat(3,1fr); gap:16px; margin-bottom:20px;">
  <div class="card" style="margin-bottom:0;">
    <div style="font-size:13px; color:#64748b; font-weight:500; margin-bottom:8px;">${periodLabel} 지출</div>
    <div style="font-size:26px; font-weight:700; color:#0f172a;">
      <fmt:formatNumber value="${summary.monthlyTotal}" pattern="#,###" />
      <span style="font-size:14px; color:#64748b;">원</span>
    </div>
    <div style="font-size:12px; color:#94a3b8; margin-top:4px;">${periodLabel}</div>
  </div>
  <div class="card" style="margin-bottom:0;">
    <div style="font-size:13px; color:#64748b; font-weight:500; margin-bottom:8px;">최다 지출 카테고리</div>
    <div style="font-size:22px; font-weight:700; color:#6366f1;">${summary.topCategory}</div>
    <div style="font-size:12px; color:#94a3b8; margin-top:4px;">${periodLabel} 기준</div>
  </div>
  <div class="card" style="margin-bottom:0;">
    <div style="font-size:13px; color:#64748b; font-weight:500; margin-bottom:8px;">총 거래 건수</div>
    <div style="font-size:26px; font-weight:700; color:#0f172a;">${summary.transactionCount}<span style="font-size:14px; color:#64748b;">건</span></div>
    <div style="font-size:12px; color:#94a3b8; margin-top:4px;">전체 기간</div>
  </div>
</div>

<!-- 차트 영역 -->
<div style="display:grid; grid-template-columns:1fr 1fr; gap:20px;">
  <div class="card">
    <div class="card-title">카테고리별 지출</div>
    <canvas id="categoryChart" height="260"></canvas>
    <c:if test="${empty summary.categorySummaries}">
      <p style="text-align:center; color:#94a3b8; padding:40px 0; font-size:14px;">데이터가 없습니다.</p>
    </c:if>
  </div>
  <div class="card">
    <div class="card-title">월별 지출 추이 (최근 6개월)</div>
    <canvas id="trendChart" height="260"></canvas>
    <c:if test="${empty summary.monthlyTrend}">
      <p style="text-align:center; color:#94a3b8; padding:40px 0; font-size:14px;">데이터가 없습니다.</p>
    </c:if>
  </div>
</div>

<script>
  var categoryData = JSON.parse('${categoryJson}');
  var trendData = JSON.parse('${trendJson}');
  var colors = ['#6366f1','#f59e0b','#10b981','#ef4444','#3b82f6','#8b5cf6','#06b6d4','#ec4899'];

  if (categoryData.length > 0) {
    var ctx1 = document.getElementById('categoryChart').getContext('2d');
    new Chart(ctx1, {
      type: 'doughnut',
      data: {
        labels: categoryData.map(function(d){ return d.category; }),
        datasets: [{
          data: categoryData.map(function(d){ return d.totalAmount; }),
          backgroundColor: colors.slice(0, categoryData.length),
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom', labels: { font: { size: 12 }, padding: 12 } },
          tooltip: {
            callbacks: {
              label: function(ctx) {
                return ctx.label + ': ' + ctx.raw.toLocaleString() + '원';
              }
            }
          }
        }
      }
    });
  }

  if (trendData.length > 0) {
    var ctx2 = document.getElementById('trendChart').getContext('2d');
    new Chart(ctx2, {
      type: 'line',
      data: {
        labels: trendData.map(function(d){ return d.month; }),
        datasets: [{
          label: '월 지출',
          data: trendData.map(function(d){ return d.totalAmount; }),
          borderColor: '#6366f1',
          backgroundColor: 'rgba(99,102,241,0.08)',
          borderWidth: 2,
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#6366f1',
          pointRadius: 4
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: function(ctx) { return ctx.raw.toLocaleString() + '원'; }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function(v) { return v.toLocaleString() + '원'; },
              font: { size: 11 }
            },
            grid: { color: '#f1f5f9' }
          },
          x: { grid: { display: false }, ticks: { font: { size: 11 } } }
        }
      }
    });
  }
</script>

</div><!-- main-content -->
</body>
</html>
