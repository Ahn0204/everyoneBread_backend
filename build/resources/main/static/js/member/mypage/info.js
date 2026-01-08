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

/* 회원 탈퇴 */
function withdraw() {

    const reason = prompt('탈퇴 사유를 입력해주세요.');

    if (!reason) return;

    fetch('/member/mypage/withdraw', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [header]: token },
        body: JSON.stringify({ reason: reason })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            showSuccessAlert('탈퇴가 완료되었습니다.');
            location.href = '/';
        } else {
            showErrorAlert(data.message);
        }
    });
}