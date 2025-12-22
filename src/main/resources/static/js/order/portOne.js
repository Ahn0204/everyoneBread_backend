//결제API 호출
function checkout(orderPrice) {
    //포트원 SDK 초기화
    IMP.init('imp22127625'); //가맹점 식별코드 - 포트원에서 사용자를 구분하는 코드 ..v1에만 필요?

    //결제창 호출
    IMP.request_pay(
        //IMP.request_pay(param, callback)
        {
            //param

            //채널키 - 관리자콘솔에서 생성한 결제를 진행할 채널
            //channelKey: '{channel-key-8018838b-284a-4799-a831-5377d32cee50}', //V2에서 필요
            //PG사 설정 - 관리자 콘솔에서 추가한 채널의 pg Provider와 동일하게 쓸 것!!
            pg: 'nice', //나이스페이먼츠
            //결제방식
            pay_method: 'card', // 신용카드
            //주문고유번호
            merchant_uid: Number(new Date().getTime()) + Number($('#buyerMemberNo').val()), // 주문일시 + memberNo
            //결제 정보
            name: '테스트결제', //결제할 상품명 또는 주문명
            amount: orderPrice, //결제 금액 (최소 100원부터 가능)
            //구매자 정보
            /*                  buyer_name : 
                   	buyer_email :
                   	buyer_tel : 
                   	buyer_addr :  */
        },
        //콜백 함수
        function (response) {
            if (response.success) {
                //결제 성공 시
                console.log(response);
                //결제 식별번호
                let merchantInput = '<input type="hidden" name="merchantUid" value="' + response.merchant_uid + '"/>';
                //장바구니 객체
                const cart = JSON.parse(sessionStorage.getItem('cart'));
                const cartJson = JSON.stringify(cart);
                console.log(cartJson);
                let cartInput = '<input type="hidden" name="cart" value="' + cartJson + '"/>';
                $('#orderForm').append(cartInput);
                $('#orderForm').append(merchantInput);
                $('#orderForm').submit(); // form 제출
            } else {
                //결제 실패 시
                console.error(response);
                showErrorAlert('결제가 취소되었습니다.');
            }
        } //콜백함수 종료
    ); //request_pay 종료
} //checkout함수 종료
