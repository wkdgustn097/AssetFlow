<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: 'Segoe UI', system-ui, sans-serif; background: #f8fafc; color: #334155; display: flex; min-height: 100vh; }

  .sidebar {
    width: 240px; min-height: 100vh; background: #1e293b; color: #e2e8f0;
    display: flex; flex-direction: column; position: fixed; top: 0; left: 0; bottom: 0; z-index: 100;
  }
  .sidebar-logo {
    padding: 24px 20px; font-size: 20px; font-weight: 700; color: #fff;
    border-bottom: 1px solid #334155; letter-spacing: -0.5px;
  }
  .sidebar-logo span { color: #6366f1; }
  .sidebar-nav { flex: 1; padding: 16px 0; }
  .sidebar-nav a {
    display: flex; align-items: center; gap: 10px; padding: 11px 20px;
    color: #94a3b8; text-decoration: none; font-size: 14px; font-weight: 500;
    border-radius: 0; transition: background 0.15s, color 0.15s;
  }
  .sidebar-nav a:hover { background: #334155; color: #e2e8f0; }
  .sidebar-nav a.active { background: #6366f1; color: #fff; }
  .sidebar-nav a svg { width: 18px; height: 18px; flex-shrink: 0; }
  .sidebar-footer { padding: 16px 20px; border-top: 1px solid #334155; }
  .sidebar-footer a {
    display: flex; align-items: center; gap: 8px; color: #64748b;
    text-decoration: none; font-size: 13px; transition: color 0.15s;
  }
  .sidebar-footer a:hover { color: #e2e8f0; }

  .main-content { margin-left: 240px; flex: 1; padding: 32px; min-height: 100vh; }
  .page-header { margin-bottom: 28px; }
  .page-header h1 { font-size: 24px; font-weight: 700; color: #0f172a; }
  .page-header p { color: #64748b; font-size: 14px; margin-top: 4px; }

  .card {
    background: #fff; border-radius: 12px; padding: 24px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.07), 0 1px 2px rgba(0,0,0,0.05);
    margin-bottom: 20px;
  }
  .card-title { font-size: 15px; font-weight: 600; color: #0f172a; margin-bottom: 16px; }

  .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; }
  .alert-success { background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }
  .alert-error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }

  .btn {
    display: inline-flex; align-items: center; gap: 6px; padding: 9px 18px;
    border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer;
    border: none; text-decoration: none; transition: opacity 0.15s;
  }
  .btn:hover { opacity: 0.85; }
  .btn-primary { background: #6366f1; color: #fff; }
  .btn-danger { background: #ef4444; color: #fff; }
  .btn-secondary { background: #e2e8f0; color: #475569; }
  .btn-success { background: #10b981; color: #fff; }
  .btn-sm { padding: 5px 12px; font-size: 13px; }

  table { width: 100%; border-collapse: collapse; }
  thead th { background: #f8fafc; padding: 11px 14px; text-align: left; font-size: 13px; font-weight: 600; color: #475569; border-bottom: 1px solid #e2e8f0; }
  tbody td { padding: 12px 14px; font-size: 14px; border-bottom: 1px solid #f1f5f9; }
  tbody tr:last-child td { border-bottom: none; }
  tbody tr:hover { background: #f8fafc; }

  .text-success { color: #10b981 !important; font-weight: 600; }
  .text-danger { color: #ef4444 !important; font-weight: 600; }
  .text-muted { color: #94a3b8; }
  .text-right { text-align: right; }

  .badge { display: inline-block; padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
  .badge-blue { background: #eff6ff; color: #2563eb; }
  .badge-green { background: #f0fdf4; color: #16a34a; }
  .badge-orange { background: #fff7ed; color: #c2410c; }

  form { display: inline; }
</style>

<div class="sidebar">
  <div class="sidebar-logo">Asset<span>Flow</span></div>
  <nav class="sidebar-nav">
    <a href="/dashboard" class="${currentPage == 'dashboard' ? 'active' : ''}">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg>
      대시보드
    </a>
    <a href="/transactions" class="${currentPage == 'transactions' ? 'active' : ''}">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>
      거래 내역
    </a>
    <a href="/stocks" class="${currentPage == 'stocks' ? 'active' : ''}">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/></svg>
      주식 포트폴리오
    </a>
    <a href="/report" class="${currentPage == 'report' ? 'active' : ''}">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
      AI 리포트
    </a>
  </nav>
  <div class="sidebar-footer">
    <a href="/logout">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" width="16" height="16"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
      ${displayName} 로그아웃
    </a>
  </div>
</div>
<div class="main-content">
