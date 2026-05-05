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
    <div style="font-size:12px; color:#94a3b8; margin-top:4px;">
      ${periodLabel}
      <c:if test="${not empty deltaLabel}">
        <span style="margin-left:6px; font-weight:600; color:${deltaLabel.startsWith('+') ? '#10b981' : '#ef4444'};">${deltaLabel} 전월比</span>
      </c:if>
    </div>
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
    <!-- 탭 헤더 -->
    <div style="display:flex; gap:0; margin-bottom:16px; border-bottom:1px solid #e2e8f0;">
      <button id="tab-stock" onclick="switchTab('stock')"
        style="padding:8px 16px; font-size:13px; font-weight:600; border:none; background:none; cursor:pointer; color:#6366f1; border-bottom:2px solid #6366f1;">
        보유 주식
      </button>
      <button id="tab-trend" onclick="switchTab('trend')"
        style="padding:8px 16px; font-size:13px; font-weight:600; border:none; background:none; cursor:pointer; color:#94a3b8; border-bottom:2px solid transparent;">
        월별 추이
      </button>
    </div>

    <!-- 주식 탭 -->
    <div id="panel-stock">
      <c:choose>
        <c:when test="${empty portfolio}">
          <p style="text-align:center; color:#94a3b8; padding:40px 0; font-size:14px;">등록된 주식이 없습니다.</p>
        </c:when>
        <c:otherwise>
          <table>
            <thead>
              <tr><th>심볼</th><th>회사명</th><th class="text-right">수익률</th></tr>
            </thead>
            <tbody>
              <c:forEach items="${portfolio}" var="s">
                <tr>
                  <td style="font-weight:600;">${s.symbol}</td>
                  <td style="color:#64748b; font-size:13px;">${s.companyName}</td>
                  <td class="text-right" style="font-weight:600; color:${s.pnlPercent >= 0 ? '#10b981' : '#ef4444'};">
                    <c:if test="${s.priceAvailable}">${s.pnlPercent >= 0 ? '+' : ''}${s.pnlPercent}%</c:if>
                    <c:if test="${!s.priceAvailable}"><span style="color:#94a3b8; font-weight:400;">-</span></c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- 월별 추이 탭 -->
    <div id="panel-trend" style="display:none;">
      <c:if test="${empty summary.monthlyTrend}">
        <p style="text-align:center; color:#94a3b8; padding:40px 0; font-size:14px;">데이터가 없습니다.</p>
      </c:if>
      <canvas id="trendChart" height="220"></canvas>
    </div>
  </div>
</div>

<!-- 예산 사용률 (예산 설정된 경우만) -->
<c:if test="${not empty budgetUsages}">
<div class="card">
  <div class="card-title">예산 사용률</div>
  <c:forEach items="${budgetUsages}" var="b">
    <div style="margin-bottom:14px;">
      <div style="display:flex; justify-content:space-between; font-size:13px; margin-bottom:5px;">
        <span style="font-weight:500;">${b.category}</span>
        <span style="color:${b.overBudget ? '#ef4444' : '#64748b'};">
          <fmt:formatNumber value="${b.spent}" pattern="#,###" />원
          / <fmt:formatNumber value="${b.limit}" pattern="#,###" />원
          (${b.usagePercent}%)
        </span>
      </div>
      <div style="background:#f1f5f9; border-radius:4px; height:8px; overflow:hidden;">
        <div style="width:${b.usagePercent}%; height:100%; background:${b.overBudget ? '#ef4444' : b.usagePercent >= 80 ? '#f59e0b' : '#10b981'}; border-radius:4px; transition:width 0.3s;"></div>
      </div>
    </div>
  </c:forEach>
</div>
</c:if>

<script>
  function switchTab(tab) {
    document.getElementById('panel-stock').style.display = tab === 'stock' ? '' : 'none';
    document.getElementById('panel-trend').style.display = tab === 'trend' ? '' : 'none';
    document.getElementById('tab-stock').style.color = tab === 'stock' ? '#6366f1' : '#94a3b8';
    document.getElementById('tab-stock').style.borderBottom = tab === 'stock' ? '2px solid #6366f1' : '2px solid transparent';
    document.getElementById('tab-trend').style.color = tab === 'trend' ? '#6366f1' : '#94a3b8';
    document.getElementById('tab-trend').style.borderBottom = tab === 'trend' ? '2px solid #6366f1' : '2px solid transparent';
    if (tab === 'trend' && !window.trendChartRendered) renderTrendChart();
  }

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

  window.trendChartRendered = false;
  function renderTrendChart() {
    if (trendData.length === 0 || window.trendChartRendered) return;
    window.trendChartRendered = true;
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
  }  // renderTrendChart end
</script>

</div><!-- main-content -->
</body>
</html>
