const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

/* 탭 전환 */
$('.tab-btn').click(function () {
    $('.tab-btn').removeClass('active');
    $(this).addClass('active');

    $('.tab-content').removeClass('active');
    $('#tab-' + $(this).data('tab')).addClass('active');
});

/* 정보 수정 */
function toggleEdit(btn, inputId, type) {
    const input = document.getElementById(inputId);

    if (input.readOnly) {
        input.readOnly = false;
        input.focus();
        btn.textContent = '저장';
        return;
    }

    fetch(`/member/mypage/info/update/${type}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({ value: input.value })
    })
    .then(r => r.json())
    .then(res => {
        if (res.result === 'OK') {
            showSuccessAlert('변경되었습니다.');
            input.readOnly = true;
            btn.textContent = '변경';
        } else {
            showErrorAlert(res.message);
        }
    });
}

/* 비밀번호 변경 */
function changePassword() {
    fetch('/member/mypage/password/change', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({
            currentPw: $('#currentPw').val(),
            newPw: $('#newPw').val()
        })
    })
    .then(r => r.json())
    .then(res => {
        res.result === 'OK'
            ? showSuccessAlert('비밀번호 변경 완료')
            : showErrorAlert(res.message);
    });
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
                'Content-Type': 'application/json', [header]: token
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