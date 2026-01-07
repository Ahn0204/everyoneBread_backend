$('#findIdBtn').on('click', function () {

    const name  = $('#findIdName').val();
    const email = $('#findIdEmail').val();

    if (!name || !email) {
        showErrorAlert('이름과 이메일을 입력해주세요.');
        return;
    }

    $.ajax({
        url: '/api/member/find-id',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            name: name,
            email: email
        }),
        success: function (res) {
            $('#maskedId').text(res.maskedId);
            $('#findIdResult').removeClass('hidden');
        },
        error: function () {
            showErrorAlert('일치하는 회원 정보가 없습니다.');
        }
    });
});
