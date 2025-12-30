    // URL 파라미터 읽기
    const params = new URLSearchParams(window.location.search);

    /* ===========================
       로그인 성공 시 알럿
       =========================== */
    if (params.get("success") === "login-ok") {

        showSuccessAlert("로그인 성공!", null);

        // 알럿 한 번만 뜨도록 URL 파라미터 제거
        const cleanUrl = window.location.origin + window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
    }

    /* ===========================
       로그인 실패 시 알럿
       =========================== */
    if (params.get("error")) {

        let msg = params.get("error");

        showErrorAlert(msg, null);

        // 중복 알럿 방지
        const cleanUrl = window.location.origin + window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
    }