// CSRF 토큰 설정
const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

/* =========================
   탭 전환
========================= */
$('.tab-btn').click(function () {
    $('.tab-btn').removeClass('active');
    $(this).addClass('active');

    $('.tab-content').removeClass('active');
    $('#tab-' + $(this).data('tab')).addClass('active');
});

/* =========================
   개인정보 수정
========================= */
function toggleEdit(btn, inputId, type) {
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (input.readOnly) {
        input.readOnly = false;
        input.focus();
        btn.textContent = '저장';
        return;
    }

    /* ===== 유효성 검사 ===== */
    if (type === 'phone' && !/^010\d{8}$/.test(value)) {
        showErrorAlert('휴대폰 번호 형식이 올바르지 않습니다.');
        return;
    }

    if (type === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        showErrorAlert('이메일 형식이 올바르지 않습니다.');
        return;
    }

    fetch(`/shop/mypage/info/update/${type}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({ value })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('변경되었습니다.');
            input.readOnly = true;
            btn.textContent = '변경';
        } else {
            showErrorAlert(data.message || '변경 실패');
        }
    })
    .catch(() => showErrorAlert('서버 오류'));
}

/* =========================
   비밀번호 변경
========================= */
function changePassword() {
    const currentPw = $('#currentPw').val().trim();
    const newPw = $('#newPw').val().trim();
    const confirmPw = $('#confirmPw').val().trim();

    if (!currentPw || !newPw || !confirmPw) {
        showErrorAlert('모든 항목을 입력해주세요.');
        return;
    }

    if (newPw.length < 8) {
        showErrorAlert('비밀번호는 8자 이상이어야 합니다.');
        return;
    }

    if (newPw !== confirmPw) {
        showErrorAlert('새 비밀번호가 일치하지 않습니다.');
        return;
    }

    fetch('/shop/mypage/password/change', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({
            currentPw,
            newPw
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('비밀번호가 변경되었습니다.');
            $('#currentPw,#newPw,#confirmPw').val('');
        } else {
            showErrorAlert(data.message || '비밀번호 변경 실패');
        }
    })
    .catch(() => showErrorAlert('서버 오류'));
}

/* =========================
   회원탈퇴
========================= */
function openWithdrawModal() {
    Swal.fire({
        title: '회원탈퇴',
        html: `
            <select id="withdrawReason" class="swal2-select">
                <option value="">탈퇴 사유를 선택해주세요</option>
                <option value="이용 빈도 낮음">이용 빈도 낮음</option>
                <option value="서비스 불만">서비스 불만</option>
                <option value="다른 서비스 이용">다른 서비스 이용</option>
                <option value="개인정보 우려">개인정보 우려</option>
                <option value="기타">기타</option>
            </select>
            <textarea id="etcReason"
                class="swal2-textarea"
                placeholder="기타 사유를 입력해주세요"
                style="display:none;"></textarea>
        `,
        showCancelButton: true,
        confirmButtonText: '탈퇴하기',
        cancelButtonText: '취소',
        didOpen: () => {
            document.getElementById('withdrawReason')
                .addEventListener('change', function () {
                    document.getElementById('etcReason').style.display =
                        this.value === '기타' ? 'block' : 'none';
                });
        },
        preConfirm: () => {
            const reason = document.getElementById('withdrawReason').value;
            const etc = document.getElementById('etcReason').value.trim();

            if (!reason) {
                Swal.showValidationMessage('탈퇴 사유를 선택해주세요.');
                return false;
            }

            if (reason === '기타' && etc.length < 5) {
                Swal.showValidationMessage('기타 사유를 5자 이상 입력해주세요.');
                return false;
            }

            return {
                reason: reason === '기타' ? etc : reason
            };
        }
    }).then(result => {
        if (!result.isConfirmed) return;

        fetch('/member/mypage/withdraw', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [document.querySelector('meta[name="_csrf_header"]').content]:
                document.querySelector('meta[name="_csrf"]').content
            },
            body: JSON.stringify(result.value)
        })
        .then(res => res.json())
        .then(data => {
            if (data.result === 'OK') {
                Swal.fire({
                    icon: 'success',
                    title: '탈퇴 완료',
                    text: '그동안 이용해주셔서 감사합니다.'
                }).then(() => {
                    window.location.href = '/member/logout';
                });
            } else {
                showErrorAlert(data.message || '탈퇴 처리 실패');
            }
        })
        .catch(() => showErrorAlert('서버 오류'));
    });
}