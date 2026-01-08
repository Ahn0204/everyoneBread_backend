const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

/* 탭 전환 */
function showTab(tab) {
    const infoTab = document.getElementById('info-tab');
    const passwordTab = document.getElementById('password-tab');
    const tabs = document.querySelectorAll('.tab-btn');

    tabs.forEach(btn => btn.classList.remove('active'));

    if (tab === 'info') {
        infoTab.style.display = 'block';
        passwordTab.style.display = 'none';
        tabs[0].classList.add('active');
    } else {
        infoTab.style.display = 'none';
        passwordTab.style.display = 'block';
        tabs[1].classList.add('active');
    }
}

/* 개인정보 수정 */
function updateInfo(type) {

    let value;
    if (type === 'phone') {
        value = document.getElementById('phone').value;
    } else if (type === 'email') {
        value = document.getElementById('email').value;
    }

    fetch(`/member/mypage/info/update/${type}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({ value: value })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('변경되었습니다.');
        } else {
            showErrorAlert(data.message);
        }
    });
}

/* 비밀번호 변경 */
function changePassword() {

    const currentPw = document.getElementById('currentPw').value;
    const newPw = document.getElementById('newPw').value;
    const confirmPw = document.getElementById('confirmPw').value;

    if (!currentPw || !newPw || !confirmPw) {
        showErrorAlert('모든 항목을 입력해주세요.');
        return;
    }

    if (newPw !== confirmPw) {
        showErrorAlert('새 비밀번호가 일치하지 않습니다.');
        return;
    }

    fetch('/member/mypage/password/change', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({
            currentPw: currentPw,
            newPw: newPw
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('비밀번호가 변경되었습니다.');
            document.getElementById('currentPw').value = '';
            document.getElementById('newPw').value = '';
            document.getElementById('confirmPw').value = '';
            showTab('info');
        } else {
            showErrorAlert(data.message);
        }
    });
}

/* =========================
   회원탈퇴 모달
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
                placeholder="기타 사유를 입력해주세요 (5자 이상)"
                style="display:none;"></textarea>
        `,
        showCancelButton: true,
        confirmButtonText: '탈퇴하기',
        cancelButtonText: '취소',
        confirmButtonColor: '#d33',
        cancelButtonColor: '#999',

        didOpen: () => {
            document
                .getElementById('withdrawReason')
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
    })
    .then(result => {
        if (!result.isConfirmed) return;

        fetch('/member/mypage/withdraw', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(result.value)
        })
        .then(async res => {
            const data = await res.json().catch(() => ({}));
            if (!res.ok) {
                throw data.message || '탈퇴 처리 중 오류가 발생했습니다.';
            }
            return data;
        })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: '탈퇴 완료',
                text: '그동안 모두의빵을 이용해주셔서 감사합니다.',
                confirmButtonText: '확인'
            }).then(() => {
                // 로그아웃 → 메인 이동
                location.href = '/member/logout';
            });
        })
        .catch(err => {
            showErrorAlert(err);
        });
    });
}