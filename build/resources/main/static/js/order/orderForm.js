$(function () {
    //주문 내역의 shopNo
    showCart();
    //배송지 출력
    showAddress();
});

//cart 내역출력
function showCart() {
    const shopNo = sessionStorage.getItem('shopNo');
    const cart = JSON.parse(sessionStorage.getItem('cart'));
    //총 결제 금액에 배송비 추가
    let orderPrice = Number($('#deliveryFee').text());
    if (cart) {
        //장바구니에 값이 있다면
        $('.cart-product').empty(); //이전 목록 삭제
        //상품 내역 출력
        cart.forEach((item) => {
            $('.cart-product').append(`
                <div class="cart-item d-flex flex-row gap-2">
                    <div class="cart-name">${item.productName}</div>
                    <div class="text-muted small">|</div>
                    <div class="cart-price">${item.productPrice}원</div>
                    <div class="text-muted small">&times;</div>
                    <div class="cart-qty">${item.quantity}개</div>
                    </div>
                    `);
            orderPrice += item.productPrice * item.quantity;
        });
        //총 결제 금액 출력
        $('#orderPrice').text(orderPrice);
        $('.orderPrice').val(orderPrice);
        //상점 번호 출력
        if (shopNo) {
            $('.cart-product').append(`<input type="hidden" class="shopNo" id="shopNo" name="shopNo" value="` + shopNo + `"/>`);
        } else {
            showToast('다시 접속해주세요.');
        }
    } else {
        //장바구니가 비었다면
        $('.cart-product').append('<div class="noCart"> 담긴 상품이 없습니다.</div>');
        showToast('상품을 담은 후 주문이 가능합니다.');
        $('#order').attr('disabled');
    }
}

//location에 저장된 address (배송지) 출력
function showAddress() {
    const address = JSON.parse(sessionStorage.getItem('location')).addr;
    $('#orderAddress').val(address);
}

//결제 버튼 클릭 시 포트원 API 호출
$('#order').on('click', function () {
    //유효성 검사
    const name = $('#buyerName').val().trim();
    const phone = $('#buyerPhone').val().trim();
    const address1 = $('#orderAddress').val().trim();
    const address2 = $('#orderAddress-2').val().trim();

    //입력값 유효성 검사
    if (!(name && phone && address1)) {
        if (!phone || !name) {
            showToast('다시 주문해주세요.', 'error');
        } else if (!address1) {
            $('#orderAddress').focus();
        }
        return;
    }

    //주문진행하는 함수
    function doCheckout() {
        //주소 합치기
        const address = address2 ? address1 + ' ' + address2 : address1;
        $('#orderAddress').val(address);
        const orderPrice = $('#orderPrice').text(); //총 결제 금액 가져오기
        checkout(orderPrice); //결제 API를 호출하는 함수 호출 & DB에 기록 insert
    }

    //상세 주소 없을 때
    if (!address2) {
        showConfirmAlert(
            '상세 주소가 없습니다. \n주문을 진행하겠습니까?',
            () => {
                doCheckout();
            },
            () => {
                return;
            }
        );
        return;
    }

    //상세 주소 있을 때
    doCheckout();
});
