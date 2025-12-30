    // URL 파라미터 읽기
    const params = new URLSearchParams(window.location.search);

    /* ==========================
       1) 로그인 성공 처리
       ========================== */
    if (params.get("success") === "login-ok") {

        showSuccessAlert("판매자 계정으로 로그인되었습니다.").then(() => {
            // URL 파라미터 제거
            const cleanUrl = window.location.origin + window.location.pathname;
            window.history.replaceState({}, document.title, cleanUrl);
        });
    }

    /* ==========================
       2) 로그인 실패 처리
       ========================== */
    if (params.get("error")) {

        // URL에서 인코딩된 메시지 복원
        const errorMsg = decodeURIComponent(params.get("error"));

        showErrorAlert("아이디 또는 비밀번호가 올바르지 않습니다.").then(() => {
            // 실패 파라미터 제거 (중복 방지)
            const cleanUrl = window.location.origin + window.location.pathname;
            window.history.replaceState({}, document.title, cleanUrl);
        });
    }