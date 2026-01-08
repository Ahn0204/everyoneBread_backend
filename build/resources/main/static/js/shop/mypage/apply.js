const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;
/* =========================
   기타 사유 토글
========================= */
$('#closeReasonSelect').change(function () {
    const value = $(this).val();
    if (value === '기타') {
        $('#etcReasonBox').show();
    } else {
        $('#etcReasonBox').hide();
        $('#etcReason').val('');
    }
});

/* =========================
   폐점 신청
========================= */
$('#closeRequestBtn').click(function () {
    const reasonSelect = $('#closeReasonSelect').val();
    const etcReason = $('#etcReason').val().trim();

    if (!reasonSelect) {
        showErrorAlert('폐점 사유를 선택해주세요.');
        return;
    }

    let finalReason = reasonSelect;

    if (reasonSelect === '기타') {
        if (etcReason.length < 5) {
            showErrorAlert('기타 사유는 5자 이상 입력해주세요.');
            return;
        }
        finalReason = `기타: ${etcReason}`;
    }

    Swal.fire({
        title: '폐점 신청',
        text: '폐점 신청 후에는 관리자 승인 전까지 취소할 수 없습니다.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '신청',
        cancelButtonText: '취소'
    }).then(result => {
        if (!result.isConfirmed) return;

        $.ajax({
            url: '/shop/mypage/apply/close',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ reason: finalReason }),
            success: function (res) {
                showSuccessAlert('폐점 신청이 완료되었습니다.');
                location.href = '/shop/mypage';
            },
            error: function (xhr) {
                showErrorAlert(xhr.responseText || '폐점 신청 실패');
            }
        });
    });
});