$('#resetPwForm').on('submit', function (e) {
    e.preventDefault();

    const pw  = $('#newPassword').val();
    const pw2 = $('#confirmPassword').val();

    if (!pw || !pw2) {
        showErrorAlert('비밀번호를 입력해주세요.');
        return;
    }

    if (pw !== pw2) {
        showErrorAlert('비밀번호가 일치하지 않습니다.');
        return;
    }

    if (pw.length < 8) {
        showErrorAlert('비밀번호는 8자 이상이어야 합니다.');
        return;
    }

    $.ajax({
        url: '/api/member/reset-password',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            newPassword: pw
        }),
        success: function () {
            showSuccessAlert('비밀번호가 변경되었습니다.');
            $('#resetPwModal').hide();
            location.href = '/member/login';
        },
        error: function () {
            showErrorAlert('비밀번호 변경에 실패했습니다.');
        }
    });
});
$('#resetPwForm').on('submit', function (e) {
    e.preventDefault();

    const pw1 = $('#newPassword').val();
    const pw2 = $('#confirmPassword').val();

    if (pw1 !== pw2) {
        showErrorAlert('비밀번호가 일치하지 않습니다.');
        return;
    }

    $.ajax({
        url: '/member/find/password/reset',
        type: 'POST',
        data: { newPassword: pw1 },
        success: function () {
            showSuccessAlert('비밀번호가 변경되었습니다.');
            location.href = '/member/login';
        }
    });
});
