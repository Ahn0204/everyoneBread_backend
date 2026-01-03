/**
 * =========================================
 * 상품 관리 페이지 JS
 * 
 * [역할 분리 설계]
 * 1. 상품명 클릭 -> 상세 모달 오픈
 * 2. 상태 select -> 상태 변경 AJAX
 * 3. 삭제 버튼 -> 삭제 AJAX
 * 
 * * 카드 전체 클릭 / 이벤트 전파 제어는 사용하지 않음
 * -> 클릭 타겟을 명확히 해서 구조적으로 문제 해결
 * =========================================
 */
console.log('✅ shop-products.js 로드됨');

$(function () {

    /**
     * CSRF 설정
     * - 모든 AJAX 요청에 자동 포함
    */
    const token  = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function (e, xhr) {
        xhr.setRequestHeader(header, token);
    });
    
    /**
     * 1. 상품 카드 클릭 → 상품 상세 모달 열기
     * - data-id 에서 상품 번호 추출
     */
    $(document).on('click', '.open-product-detail', function () {

        // 클릭된 상품 번호
        const productId = $(this).data('id');
        if(!productId) return;

        // 상세 모달 JS에서 사용할 전역 상품 번호
        window.currentProductId = productId;

        // 모달 오픈
        $('#productDetailModal').fadeIn(150);
        $('body').addClass('modal-open');
    });

    /**
     * 2. 상품 상태 변경 (AJAX)
     * - 판매중 <-> 품절
     * - 상태 변경 후 카드 UI 즉시 반영
     */
    $(document).on('change', '.status-select', function () {

        const $select = $(this);
        const productId = $select.data('id');
        const newStatus = $select.val();

        const prevStatus = $select.data('prev') || $select.find('option:selected').val();

        // 상태별 확인 메시지
        let confirmMsg = (newStatus === 'SOLD_OUT')
            ? '이 상품을 품절로 변경하시겠습니까?'
            : '이 상품을 판매중으로 변경하시겠습니까?';

        showConfirmAlert(
            confirmMsg,

            // 확인 눌렀을 때 (기존 confirm(true) 영역)
            () => {
                $.ajax({
                    url: `/shop/products/${productId}/status`,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ status: newStatus }),
                    success: function () {

                        showSuccessAlert('상품 상태가 변경되었습니다.');

                        const $card = $select.closest('.product-card');
                        $card.removeClass('status-ON_SALE status-SOLD_OUT')
                            .addClass(`status-${newStatus}`);

                        // 현재 상태 저장
                        $select.data('prev', newStatus);

                        $(document).trigger('productStatusChanged', {
                            productId: productId,
                            status: newStatus
                        });
                    },
                    error: function () {
                        showErrorAlert('상태 변경에 실패했습니다.');
                        $select.val(prevStatus);
                    }
                });
            },

            // 취소 눌렀을 때 (기존 confirm(false) 영역)
            () => {
                $select.val(prevStatus);
            }
        );

    });

    /**
     * 3. 상품 삭제 (AJAX)
     */
    $(document).on('click', '.btn-delete-product', function (e) {
        
        e.preventDefault(); // 혹시 모를 기본 동작 차단
        e.stopPropagation(); // 다른 클릭 이벤트 차단
        alert('삭제 버튼 클릭됨');

        const productId = $(this).data('id');
        if (!productId) return;

        if (!confirm('정말 이 상품을 삭제하시겠습니까?\n삭제된 상품은 복구할 수 없습니다.')) {
            return;
        }

        $.ajax({
            url: `/shop/products/${productId}/delete`,
            type: 'POST',
            success: function () {

                showSuccessAlert('상품이 삭제되었습니다.');

                // 해당 상품 카드 제거
                $(`.btn-delete-product[data-id="${productId}"]`)
                    .closest('.product-card')
                    .fadeOut(200, function () {
                        $(this).remove();
                        checkEmptyProductList();
                    });
                // 상세 모달 열려 있으면 닫기
                if($('#productDetailModal').is(':visible') &&
                    window.currentProductId === productId
                ){
                    $('#productDetailModal').fadeOut(150);
                    $('body').removeClass('modal-open');
                }
            },
            error: function () {
                showErrorAlert('상품 삭제에 실패했습니다.');
            }
        });
    });

    /**
     * 4. 상품 목록 비었는지 체크
     * - 삭제 후 호출됨
     * - 카드가 하나도 없으면 안내 문구 표시
     */
    function checkEmptyProductList() {

        const count = $('.product-card').length;

        if (count === 0) {
            $('#emptyProductMessage').fadeIn(150);
        }
    }
});