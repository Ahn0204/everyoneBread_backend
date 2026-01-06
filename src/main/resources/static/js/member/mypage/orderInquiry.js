//문의 작성 모달 열기

// $(function () { 이 방식은 html생성 시 1회 실행되고, 나중에 fragment된 요소는 인식하지 못한다.
// $('.orderInquiry').on('click', function () {

//관리자에 문의 버튼 클릭
$(document).on('click', '.orderInquiry', function () {
    //배경, 모달 보이기
    $('#dimmed, #modal-writeForm').show();
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
            $('#modal-order-inquiry').replaceWith(order);
        },
        error: function (error) {
            closeModal();
            showToast('다시 시도해주세요.', 'error');
        },
    });
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
