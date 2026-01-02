/**
 * =========================================
 * 상품 관리 페이지 JS
 * - 상품 카드 클릭 → 상세 모달 오픈
 * - 상품 상태 변경 (AJAX)
 * - 상품 삭제 (AJAX, 소프트 삭제)
 * - 버튼 클릭 시 카드 클릭 이벤트 차단
 * =========================================
 */

$(function () {

    const token  = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    // 모든 AJAX 요청에 CSRF 자동 포함
    $(document).ajaxSend(function (e, xhr) {
        xhr.setRequestHeader(header, token);
    });
    
    /**
     * 1. 상품 카드 클릭 → 상품 상세 모달 열기
     * - 카드 전체가 클릭 영역
     * - data-id 에서 상품 번호 추출
     */
    $(document).on('click', '.open-product-detail', function () {

        const productId = $(this).data('id');
        if (!productId) return;

        // product-detail-modal.js 쪽에서 이 id를 사용해 상세 조회
        window.currentProductId = productId;

        $('#productDetailModal').fadeIn(150);
        $('body').addClass('modal-open');
    });

    /**
     * 2. 버튼 / select 클릭 시
     *    → 카드 클릭 이벤트 전파 차단
     *
     * 이유:
     * - 수정 / 삭제 / 상태 변경 눌렀는데
     *   모달이 같이 열리는 문제 방지
     */
    $(document).on('click change', '.product-actions *', function (e) {
        e.stopPropagation();
    });

    /**
     * 3. 상품 상태 변경 (AJAX)
     * - 판매중 <-> 품절
     * - 상태 변경 후 카드 UI 즉시 반영
     */
    $(document).on('change', '.status-select', function () {

        const $select = $(this);
        const productId = $select.data('id');
        const newStatus = $select.val();

        if (!productId) return;

        // 이전 상태 저장 (실패 시 되돌리기용)
        const prevStatus = $select.data('prev') || prevStatusFromSelect($select);

        // 상태별 확인 메시지
        let confirmMsg = (newStatus === 'SOLD_OUT')
            ? '이 상품을 품절로 변경하시겠습니까?'
            : '이 상품을 다시 판매중으로 변경하시겠습니까?';

        if (!confirm(confirmMsg)) {
            $select.val(prevStatus);
            return;
        }

        // 방어 코드: select로 DELETED 변경 시도 차단
        if (newStatus === 'DELETED') {
            showErrorAlert('삭제는 삭제 버튼을 이용해주세요.');
            $select.val(prevStatus);
            return;
        }

        $.ajax({
            url: `/shop/products/${productId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ status: newStatus }),
            success: function () {

                showSuccessAlert('상품 상태가 변경되었습니다.');

                // 카드 상태 class 즉시 변경
                const $card = $select.closest('.product-card');
                $card.removeClass('status-ON_SALE status-SOLD_OUT')
                     .addClass(`status-${newStatus}`);

                // 현재 상태 저장
                $select.data('prev', newStatus);
            },
            error: function () {

                showErrorAlert('상태 변경에 실패했습니다.');
                $select.val(prevStatus);
            }
        });
    });

    /**
     * select에서 현재 선택된 상태값을 안전하게 가져오기
     */
    function prevStatusFromSelect($select) {
        return $select.find('option:selected').val();
    }

    /**
     * 4. 상품 삭제 (AJAX)
     * - 실제 삭제 X
     * - ProductStatus = DELETED 로 변경 (소프트 삭제)
     * - 성공 시 카드 제거
     */
    $(document).on('click', '.btn-delete-product', function () {

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
            },
            error: function () {
                showErrorAlert('상품 삭제에 실패했습니다.');
            }
        });
    });

    /**
     * 5. 상품 목록 비었는지 체크
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