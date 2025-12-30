// 다른 로그인 이동
document.getElementById('otherLoginSelect').addEventListener('change', function () {
    const url = this.value;
    if (url) {
        window.location.href = url;
    }
});

// 로그인 실패 / 계정 상태별 알럿 처리
document.addEventListener('DOMContentLoaded', function(){
    const error = document.getElementById('loginError').value;

    if(!error) return;

    switch(error){
        case 'PENDING':
            showErrorAlert("가입 대기 중입니다. \n관리자 승인 후 이용 가능합니다.");
            break;
        case 'SUSPENDED':
            showErrorAlert("관리자에 의해 정지된 계정입니다.");
            break;
        case 'WITHDRAW':
            showErrorAlert("이미 탈퇴 처리된 계정입니다. \n재가입이 필요합니다.");
            break;
        case 'INACTIVE':
            showErrorAlert("휴면 계정입니다. \n본인 인증 후 사용 가능합니다.");
            break;
            
        default:
            showErrorAlert("아이디 또는 비밀번호를 다시 확인해주세요.");
    }
})