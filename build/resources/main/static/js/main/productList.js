const shopNo = $('#shopNo').val(); //페이지의 상점 번호

//장바구니 비우기
function deleteCart() {
    sessionStorage.removeItem('shopNo');
    sessionStorage.removeItem('cart');
    $('.cart-product').empty();
    showCart();
}

//$(document).ready()대신 사용
$(function () {
    showCart();
    //cart 삭제
    $('.deleteAllBtn').on('click', function () {
        showConfirmAlert(
            '주문이 모두 삭제됩니다.',
            () => {
                deleteCart();
            },
            () => {}
        );
    });

    //주문 버튼
    $('.orderBtn').on('click', function () {
        const deliveryFee = $('.deliveryFee').text();
        showConfirmAlert(
            '주문 페이지로 이동합니다.',
            () => {
                const form = $('form[name = "cartForm"]');
                $('.deliveryFee').val(deliveryFee);
                // form.append($('<input>').attr('type', 'hidden').attr('name', 'deliveryFee').val(deliveryFee));
                form.submit();
            },
            () => {}
        );
    });

    //비로그인 시
    $('.beforeLogin').on('click', function () {
        showConfirmAlert(
            '로그인이 필요합니다. \n 페이지를 이동하겠습니까?',
            () => {
                window.location = '/member/login';
            },
            () => {}
        );
    });
});

//장바구니 담기 버튼
$('.cartBtn').on('click', function () {
    const cartShopNo = sessionStorage.getItem('shopNo'); //장바구니의 shopNo
    const cart = JSON.parse(sessionStorage.getItem('cart')); //장바구니
    const thisBtn = $(this);
    //세션스토리지에 'cart'가 없다면
    if (!cart) {
        //cart생성
        createCart(shopNo);
        //장바구니에 product 추가
        addCart(thisBtn);
        //장바구니 출력
        showCart();
    } else {
        //세션스토리지에 'cart'가 있다면
        if (cartShopNo != null && cartShopNo == shopNo) {
            // shopNo가 일치한다면
            addCart(thisBtn);
            //장바구니 출력
            showCart();
        } else if (cartShopNo != null) {
            // shopNo가 일치하지 않는다면
            showConfirmAlert(
                '다른 가게 주문이 담겨있습니다. 주문을 변경하시겠습니까?',
                () => {
                    //예
                    //이전 cart 삭제
                    deleteCart();
                    //cart생성
                    createCart(shopNo);
                    //상품 추가
                    addCart();
                    //장바구니 출력
                    showCart();
                },
                () => {
                    //아니오
                    showToast('같은 가게 주문만 담을 수 있습니다.', 'error');
                }
            );
        }
    }
});

//cart생성
function createCart(shopNo) {
    // shopNo 저장
    sessionStorage.setItem('shopNo', shopNo);
    // 배열 'cart' 저장
    sessionStorage.setItem('cart', JSON.stringify([]));
}

//cart에 선택된 상품 추가
function addCart(thisBtn) {
    //product정보
    const product = thisBtn.closest('.product');
    const productName = product.find('.productName').text();
    const productPrice = product.find('.productPrice').text();
    const quantity = thisBtn.siblings('.quantity').val();
    const productNo = thisBtn.data('productno');
    const data = { productNo, productName, productPrice, quantity };

    const cart = JSON.parse(sessionStorage.getItem('cart')); //장바구니
    //상품 일치여부 확인
    const existing = cart.find((c) => c.productNo === data.productNo);
    if (existing) {
        //원래 있는 상품이라면
        showToast('이미 추가된 상품입니다.', 'warning');
    } else {
        //새로 추가하는 상품이라면
        cart.push(data);
        showToast('장바구니에 담았습니다.', 'success');
    }
    //장바구니 저장
    sessionStorage.setItem('cart', JSON.stringify(cart));
}

//cart출력
function showCart() {
    //주문 내역의 shopNo
    const shopNo = sessionStorage.getItem('shopNo');
    const cart = JSON.parse(sessionStorage.getItem('cart'));
    if (cart) {
        //장바구니에 값이 있다면
        $('.cart-product').empty(); //이전 목록 삭제
        cart.forEach((item) => {
            $('.cart-product').append(`
                    <div class="cart-item border-bottom p-2">
                        <div class="d-flex justify-content-between">
                            <div class="cart-name pb-1">${item.productName}</div>
                            <div class="cart-deleteBtn">&times;</div>
                        </div>
                        <div class="d-flex justify-content-between">
                            <input type="number" name="quantity" class="cart-qty" min="1" max="999" value="${item.quantity}" />
                            <div class="cart-price">${item.productPrice}원</div>
                        </div>
                        <input type="hidden" name="cart-productNo" class="cart-productNo" value="${item.productNo}"/>
                    </div>
                    `);
        });
        //총 주문 금액 출력
        showTotalPrice(cart);
        //주문 버튼 보이기
        $('.beforeLogin, .orderBtn').removeAttr('disabled');
    } else {
        //장바구니가 비었다면
        $('.cart-product').append('<div class="noCart"> 담긴 상품이 없습니다.</div>');
    }
}

//주문 총액 새로 반영하는 함수
function showTotalPrice(cart) {
    let totalPrice = Number($('.deliveryFee').text());
    cart.forEach((item) => {
        totalPrice += item.productPrice * item.quantity;
    });
    $('.cart-totalPrice').text(totalPrice);
}

//동적으로 생성한 스크립트에 적용하기 위함
$(document).on('change', '.cart-qty', function () {
    //수량이 바뀌면 주문총액 바꾸기
    // $('.cart-qty').on('change', function () {
    const cartQty = $(this);
    const newQty = Number(cartQty.val());
    const productNo = cartQty.closest('.cart-item').find('.cart-productNo').val();
    const cart = JSON.parse(sessionStorage.getItem('cart'));

    //cart에서 해당 상품 찾기
    const target = cart.find((c) => c.productNo === Number(productNo));
    //새 수량 대입
    target.quantity = newQty;
    sessionStorage.setItem('cart', JSON.stringify(cart));
    //주문 총액 새로 반영
    showTotalPrice(cart);
});

//주문표 상품 옆 x 눌렀을 때
$(document).on('click', '.cart-deleteBtn', function () {
    // $('.cart-deleteBtn').on('click', function () {
    const thisProduct = showConfirmAlert(
        '주문을 삭제합니다.',
        () => {
            deleteCartProduct($(this));
        },
        () => {}
    );
});

function deleteCartProduct(btn) {
    const productNo = btn.closest('.cart-item').find('.cart-productNo').val();
    const cart = JSON.parse(sessionStorage.getItem('cart'));

    //해당 상품 제외하고 새 배열 생성
    const newCart = cart.filter((c) => c.productNo !== Number(productNo));

    //새 배열에 상품이 없다면
    if (newCart.length < 1) {
        //cart에 값이 없다면 장바구니 비우기
        deleteCart();
    } else {
        //배열 cart 갱신
        sessionStorage.setItem('cart', JSON.stringify(newCart));
        showCart();
    }
}
