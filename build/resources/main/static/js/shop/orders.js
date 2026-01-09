// =========================
// 주문 거절 모달
// =========================
function openRejectModal(orderNo) {
    // 모달 열기
    const modal = document.getElementById('rejectModal');
    // 폼 액션 설정
    const form = document.getElementById('rejectForm');
    // 숨겨진 input에 주문 번호 설정
    form.action = `/shop/orders/${orderNo}/reject`;
    modal.style.display = 'flex';
}

// 모달 닫기
function closeRejectModal() {
    document.getElementById('rejectModal').style.display = 'none';
}

// =========================
// 주문 대시보드 AJAX
// =========================
function loadOrderDashboard() {
    // 대시보드 데이터 요청
    fetch('/shop/orders/dashboard')
        // 응답을 JSON으로 파싱
        .then((response) => response.json())
        .then((data) => {
            document.getElementById('dash-today').innerText = data.today; // 오늘 주문 수
            document.getElementById('dash-order').innerText = data.order; // 대기 주문 수
            document.getElementById('dash-pickup').innerText = data.pickup; // 배송 중 주문 수
            document.getElementById('dash-complete').innerText = data.complete; // 완료 주문 수
        })
        // 에러 처리
        .catch((err) => console.error('대시보드 로딩 실패', err));
}

// 페이지 로드 시 1회 실행
document.addEventListener('DOMContentLoaded', () => {
    loadOrderDashboard();
    // 5분마다 갱신
    setInterval(loadOrderDashboard, 300000);
});

//관리자 문의 모달
//관리자에 문의 버튼 클릭
$(document).on('click', '.orderInquiry', function () {
    //btn의 data-orderno 가져오기
    const orderNo = $(this).data('orderno');

    //모달에 data 가져오기
    $.ajax({
        url: '/customerCenter/orderInquiry',
        type: 'GET',
        data: { orderNo: orderNo },
        success: function (order) {
            //order객체를 받은 order-inquiry.html이 리턴됨
            //modal 출력부를 ajax응답으로 replace
            $('#modal-order-inquiry').html(order);
            //불러온 모달에 orderno 저장
            $('#modal-writeForm').data('orderno', orderNo);
            //배경, 모달 보이기
            $('#dimmed, #modal-writeForm').show();
        },
        error: function (error) {
            closeModal();
            showToast('다시 시도해주세요.', 'error');
        },
    });
});

//모달 내 문의하기 버튼 클릭 시
// $('.insertInquiry').on('click', function () {
$(document).on('click', '.insertInquiry', function () {
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');
    $(document).ajaxSend(function (e, xhr) {
        if (token && header) xhr.setRequestHeader(header, token);
    });

    const memberNo = $(this).data('memberno');
    const question = $('#question').val();
    const orderNo = $('#modal-writeForm').data('orderno');
    console.log(orderNo);
    showConfirmAlert(
        '관리자에게 문의가 작성됩니다.',
        //예
        () => {
            $.ajax({
                url: '/shop/insertBanInquiry',
                type: 'POST',
                data: { memberNo: memberNo, question: question, orderNo: orderNo },
                success: function (success) {
                    if (success == true) {
                        closeModal();
                        showToast('문의가 작성되었습니다.', 'success');
                    } else {
                        showToast('다시 시도해주세요.', 'error');
                    }
                },
                error: function (error) {
                    showToast('다시 시도해주세요.', 'error');
                },
            });
        },
        //아니오
        () => {
            showToast('작성이 취소되었습니다.', 'warning');
        }
    );
});

//모달 - 닫기 버튼, dimmed 클릭
// $('.closeBtn, #dimmed').on('click', function () {
$(document).on('click', '.closeBtn, #dimmed', function () {
    closeModal();
});

//모달 닫는 함수
function closeModal() {
    //기존값 초기화
    $('#question').empty();
    //배경, 모달 숨김
    $('#dimmed, #modal-writeForm').hide();
}
