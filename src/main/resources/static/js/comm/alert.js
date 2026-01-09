//웹소켓으로 전송된 메세지 출력 로직

//웹소켓 생성 - 웹소켓 컨피그의 경로와 일치해야함
const socket = new SockJS('/ws/alert');
//stompClient객체 생성 - 웹소켓을 STOMP클라이언트로 감싸기
const stompClient = Stomp.over(socket);

//stompClient 연결
stompClient.connect({}, () => {
    //판매자에게 알림 출력
    //stompClient.서버로부터 전달받음('웹소켓경로',람다식 실행부)
    stompClient.subscribe('/member/to/order', (msg) => {
        showToast(msg.body);
    });
});
