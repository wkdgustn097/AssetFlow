<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AssetFlow - 로그인</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: 'Segoe UI', system-ui, sans-serif; background: #f1f5f9; display: flex; align-items: center; justify-content: center; min-height: 100vh; }
    .login-card { background: #fff; border-radius: 16px; padding: 48px 40px; width: 100%; max-width: 400px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
    .logo { text-align: center; font-size: 28px; font-weight: 800; color: #0f172a; margin-bottom: 8px; letter-spacing: -1px; }
    .logo span { color: #6366f1; }
    .subtitle { text-align: center; color: #64748b; font-size: 14px; margin-bottom: 32px; }
    .form-group { margin-bottom: 18px; }
    label { display: block; font-size: 13px; font-weight: 600; color: #374151; margin-bottom: 6px; }
    input[type=text], input[type=password] {
      width: 100%; padding: 11px 14px; border: 1.5px solid #e2e8f0; border-radius: 8px;
      font-size: 14px; color: #0f172a; outline: none; transition: border-color 0.15s;
    }
    input:focus { border-color: #6366f1; }
    .btn-login {
      width: 100%; padding: 12px; background: #6366f1; color: #fff; border: none;
      border-radius: 8px; font-size: 15px; font-weight: 600; cursor: pointer; margin-top: 8px;
      transition: background 0.15s;
    }
    .btn-login:hover { background: #4f46e5; }
    .alert { padding: 11px 14px; border-radius: 8px; font-size: 13px; margin-bottom: 18px; }
    .alert-error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
    .alert-success { background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }
  </style>
</head>
<body>
<div class="login-card">
  <div class="logo">Asset<span>Flow</span></div>
  <p class="subtitle">자산 관리 대시보드</p>

  <c:if test="${param.error != null}">
    <div class="alert alert-error">아이디 또는 비밀번호가 올바르지 않습니다.</div>
  </c:if>
  <c:if test="${param.logout != null}">
    <div class="alert alert-success">로그아웃 되었습니다.</div>
  </c:if>

  <form method="post" action="/login/process">
    <div class="form-group">
      <label for="username">아이디</label>
      <input type="text" id="username" name="username" placeholder="아이디 입력" autocomplete="username" required>
    </div>
    <div class="form-group">
      <label for="password">비밀번호</label>
      <input type="password" id="password" name="password" placeholder="비밀번호 입력" autocomplete="current-password" required>
    </div>
    <button type="submit" class="btn-login">로그인</button>
  </form>
</div>
</body>
</html>
