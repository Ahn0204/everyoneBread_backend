/* =========================
   정산 계좌 정보 수정 (유효성 포함)
========================= */

function toggleEdit(btn, inputId, type) {
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    /* =====================
       수정 모드 진입
    ===================== */
    if (input.readOnly) {
        input.readOnly = false;
        input.focus();
        btn.textContent = '저장';
        return;
    }

    /* =====================
       유효성 검사
    ===================== */

    // 예금주명
    if (type === 'accountName') {
        const nameRegex = /^[가-힣]{2,10}$/;
        if (!nameRegex.test(value)) {
            showErrorAlert('예금주명은 한글 2~10자로 입력해주세요.');
            input.focus();
            return;
        }
    }

    // 은행명
    if (type === 'bankName') {
        const bankRegex = /^[가-힣]{2,10}$/;
        if (!bankRegex.test(value)) {
            showErrorAlert('은행명은 한글 2~10자로 입력해주세요.');
            input.focus();
            return;
        }
    }

    // 계좌번호
    if (type === 'accountNo') {
        const accountRegex = /^[0-9\-]{8,20}$/;
        if (!accountRegex.test(value)) {
            showErrorAlert('계좌번호 형식이 올바르지 않습니다.');
            input.focus();
            return;
        }
    }

    /* =====================
       서버 저장 요청
    ===================== */
    fetch(`/shop/mypage/update/${type}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            // 계좌번호는 하이픈 제거 후 저장
            value: (type === 'accountNo')
                ? value.replaceAll('-', '')
                : value
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('변경되었습니다.');
            input.readOnly = true;
            btn.textContent = '변경';

            // 계좌번호는 다시 하이픈 유지
            if (type === 'accountNo') {
                input.value = value;
            }
        } else {
            showErrorAlert(data.message || '변경에 실패했습니다.');
        }
    })
    .catch(() => showErrorAlert('서버 오류가 발생했습니다.'));
}

/* =========================
   입력 중 실시간 포맷 보정
========================= */

// 계좌번호 입력 시 숫자 + 하이픈만 허용
$('#accountNo').on('input', function () {
    let v = $(this).val()
        .replace(/[^0-9\-]/g, '')
        .replace(/-+/g, '-');

    $(this).val(v);
});