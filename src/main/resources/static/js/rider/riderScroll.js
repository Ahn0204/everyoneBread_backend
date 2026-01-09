$(document).ready(function () {
    // tab에 active 권한 부여

    // 스크롤이 발생하는 메인 콘텐츠 영역
    const $mainContent = $('.main-content');

    // 현재 AJAX 요청이 진행 중인지 여부 (중복 요청 방지용)
    let isLoading = false;

    // infinite 스크롤 이벤트 등록
    $mainContent.on('scroll.infinite', function () {
        // 현재 스크롤 위치 = 스크롤된 높이 + 보이는 화면 높이
        let scrollPosition = $mainContent.scrollTop() + $mainContent.innerHeight();
        // 전체 콘텐츠 높이
        let scrollHeight = $mainContent[0].scrollHeight;
        // console.log('scrollPosition : ' + scrollPosition);
        // console.log('scrollHeight : ' + scrollHeight);

        // 스크롤이 거의 끝에 도달했고, 로딩중이 아닐 경우
        if (scrollPosition >= scrollHeight - 10 && !isLoading) {
            isLoading = true; // AJAX 요청 시작 설정 (중복 호출 방지)
        }
    });
});
