const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;

function checkPassword() {

    const password = document.getElementById('password').value;

    if (!password) {
        showConfirmAlert('비밀번호를 입력해주세요.');
        return;
    }

    fetch('/member/mypage/info/check', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json', [header]: token
        },
        body: JSON.stringify({
            password: password
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.result === 'OK') {
            location.href = '/member/mypage/info';
        } else {
            showErrorAlert(data.message);
        }
    });
}