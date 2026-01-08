//답변 완료
$('.updateAnswer').on('click', function () {
    const banInquiryNo = $(this).data('baninquiryno');
    const answer = $('#answer').val();
    showConfirmAlert(
        '답변이 작성됩니다.',
        //예
        () => {
            $.ajax({
                url: '/admin/inquiry/updatebanInquiryAnswer',
                type: 'POST',
                data: { banInquiryNo: banInquiryNo, answer: answer },
                success: function (success) {
                    if (success == true) {
                        showToast('답변이 작성되었습니다.', 'success');
                        //답변 작성자에게 웹소켓 알림 주기
                        setInterval(function () {
                            location.reload();
                        }, 2000);
                    } else {
                        showToast('다시 시도해주세요.', 'error');
                    }
                },
                error: function (error) {
                    showToast('다시 시도해주세요.', 'error');
                },
            });
        },
        //아니오
        () => {
            showToast('답변 작성이 취소되었습니다.', 'warning');
        }
    );
});
