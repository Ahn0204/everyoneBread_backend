// CSRF
const csrfToken = $('meta[name="_csrf"]').attr('content');
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

$(document).ajaxSend(function (e, xhr) {
    xhr.setRequestHeader(csrfHeader, csrfToken);
});

// ================== 주문 취소 ==================
$(document).on('click', '.cancelOrderBtn', function () {
    const orderNo = $(this).data('orderno');

    showConfirmAlert(
        '주문을 취소하시겠습니까?',
        () => {
            cancelOrder(orderNo);
        },
        () => {}
    );
});

function cancelOrder(orderNo) {
    $.ajax({
        url: `/member/orders/${orderNo}/cancel`,
        type: 'POST',
        success: function () {
            showToast('주문이 취소되었습니다.', 'success');
            location.reload();
        },
        error: function () {
            showToast('주문 취소에 실패했습니다.', 'error');
        }
    });
}

// 모달 HTML 로드 (페이지 진입 시 1번)
$(function () {
    $('#modal-order-detail').load('/member/mypage/order-detail');
});

// 주문 상세 버튼
$(document).on('click', '.orderDetailBtn', function () {
    const orderNo = $(this).data('orderno');
    loadOrderDetail(orderNo);
});

// 상세 조회
function loadOrderDetail(orderNo) {
    $.ajax({
        url: `/member/orders/${orderNo}`,
        type: 'GET',
        success: function (data) {
            renderOrderDetail(data);

            $('.dimmed').fadeIn(200);
            $('.modal-layer').fadeIn(200);
        },
        error: function () {
            showToast('주문 정보를 불러오지 못했습니다.', 'error');
        }
    });
}

// 닫기 (X 버튼)
$(document).on('click', '.closeBtn', closeModal);
// 바깥 클릭
$(document).on('click', '.dimmed', closeModal);

function closeModal() {
    $('.modal-layer').fadeOut(200);
    $('.dimmed').fadeOut(200);
}

// 렌더링
function renderOrderDetail(order) {
    $('#modal-orderNo').text(order.orderNo);
    $('#modal-deliveryFee').text(order.deliveryFee.toLocaleString());
    $('#modal-totalPrice').text(order.orderPrice.toLocaleString());
    $('#modal-address').text(order.address);

    const itemsBox = $('#modal-items');
    itemsBox.empty();

    order.items.forEach(item => {
        itemsBox.append(`
            <div>
                ${item.productName} × ${item.quantity}개
                (${item.price.toLocaleString()}원)
            </div>
        `);
    });

    if (order.status === 'WAIT') {
        $('#modalCancelBtn')
            .show()
            .data('orderno', order.orderNo);
    } else {
        $('#modalCancelBtn').hide();
    }
}
