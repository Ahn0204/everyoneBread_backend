/**
 * =========================================
 * 상품 상세 관리 모달 JS
 * - 모달 열기 / 닫기
 * - 상품 상세 조회 (AJAX)
 * - 수정 모드 전환
 * - 수정 저장 (POST)
 * - 판매 상태 변경 (AJAX)
 * - 상품 삭제 (AJAX)
 * =========================================
 */

$(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    const $modal = $('#productDetailModal');

    let currentProductId = null;   // 현재 선택된 상품 번호
    let editMode = false;          // 수정 모드 여부 (조회 / 수정 상태 구분용)

    /* 1. 상품 카드 클릭 → 모달 열기 + 상세 조회 */
    $(document).on('click', '.open-product-detail', function () {

        // 클릭한 카드에 심어둔 상품 번호 가져오기
        currentProductId = $(this).data('id');

        // 모달 열기
        $modal.fadeIn(150);
        $('body').addClass('modal-open');

        // 상품 상세 정보 조회
        loadProductDetail(currentProductId);
    });

    /* 2. 상품 상세 조회 (AJAX) */
    function loadProductDetail(productId) {

        $.ajax({
            url: `/shop/products/${productId}/detail`,
            type: 'GET',
            success: function (product) {

                // ===== 조회 모드 데이터 바인딩 =====
                $('#detailProductName').text(product.productName);
                $('#detailProductPrice').text(product.price.toLocaleString() + '원');
                $('#detailProductDescription').text(product.summary ?? '');
                $('#detailProductCategory').text(product.catName);
                $('#detailProductStatus').text(convertStatus(product.status));
                // 등록일
                $('#detailProductCreatedAt').text( formatDate(product.createdAt) );
                // 수정일
                $('#detailProductUpdatedAt').text( product.updatedAt ? formatDate(product.updatedAt) : '-' );

                // 상품 이미지 세팅
                // if (product.imgUrl) {
                //     $('#detailProductImage')
                //         .attr('src', '/upload/product/' + product.imgUrl);
                // }

                // 수정 모드 해제 + 버튼 초기화
                editMode = false;
                resetButtons();
            },
            error: function (xhr, status, error) {

                // 서버에서 내려온 상태 코드 (401, 403, 500 등)
                console.error('AJAX 상태코드:', xhr.status);

                // 서버에서 내려온 응답 내용 (에러 메시지, HTML, JSON 등)
                console.error('AJAX 응답:', xhr.responseText);

                // jQuery가 판단한 에러 타입
                console.error('AJAX 에러 타입:', status);

                showErrorAlert('상품 정보를 불러오지 못했습니다.');
                closeModal();
            }
        });
    }

    /* 3. 수정 버튼 클릭 → 수정 모드 전환 */
    $('#detailEditBtn').on('click', function () {

        // 조회 상태면 수정 모드 진입
        if (!editMode) {
            enterEditMode();
        }
        // 이미 수정 모드면 저장 처리
        else {
            saveProduct();
        }
    });

    /*
       조회 모드 → 수정 모드
       - span → input / textarea / select 로 교체
    */
    function enterEditMode() {

        editMode = true;

        // 기존 값 추출
        const name = $('#detailProductName').text();
        const price = $('#detailProductPrice').text().replace(/[^0-9]/g, '');
        const desc = $('#detailProductDescription').text();
        const category = $('#detailProductCategory').text();
        const statusText = $('#detailProductStatus').text();

        // ===== span → input / textarea =====
        $('#detailProductName').replaceWith(
            `<input id="editProductName" class="edit-input" value="${name}">`
        );

        $('#detailProductPrice').replaceWith(
            `<input id="editProductPrice" type="number" class="edit-input" value="${price}">`
        );

        $('#detailProductDescription').replaceWith(
            `<textarea id="editProductDescription" class="edit-textarea">${desc}</textarea>`
        );

        $('#detailProductCategory').replaceWith(
            `<input id="editProductCategory" class="edit-input" value="${category}">`
        );

        $('#detailProductStatus').replaceWith(`
            <select id="editProductStatus" class="edit-select">
                <option value="ON_SALE" ${statusText === '판매중' ? 'selected' : ''}>판매중</option>
                <option value="SOLD_OUT" ${statusText === '품절' ? 'selected' : ''}>품절</option>
            </select>
        `);

        // ===== 버튼 UI 변경 =====
        $('#detailEditBtn').text('저장');
        $('#detailStatusBtn, #detailDeleteBtn').hide();

        // 취소 버튼이 없을 때만 추가
        if ($('#detailCancelBtn').length === 0) {
            $('#detailEditBtn').after(
                `<button type="button" class="btn btn-outline" id="detailCancelBtn">취소</button>`
            );
        }
    }

    /* 4. 상품 수정 저장 (POST) */
    function saveProduct() {

        const data = {
            productName: $('#editProductName').val(),
            price: $('#editProductPrice').val(),
            summary: $('#editProductDescription').val(),
            catName: $('#editProductCategory').val(),
            status: $('#editProductStatus').val()
        };

        $.ajax({
            url: `/shop/products/${currentProductId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function () {
                showSuccessAlert('상품이 수정되었습니다.');
                // 서버 기준 최신 데이터로 다시 조회
                $.ajax({
                    url: `/shop/products/${currentProductId}/detail`,
                    type: 'GET',
                    success: function (product) {
                        restoreViewMode(product);
                    }
                });
            },
            error: function (xhr) {
                console.error(xhr.status, xhr.responseText);
                showErrorAlert('상품 수정에 실패했습니다.');
            }
        });
    }

    /* 5. 수정 취소 → 조회 모드 복구 */
    $(document).on('click', '#detailCancelBtn', function () {
        loadProductDetail(currentProductId);
    });

    /* 6. 판매 상태 변경 (판매중 <-> 품절) */
    $('#detailStatusBtn').on('click', function () {

        if (!currentProductId) return;

        const statusText = $('#detailProductStatus').text();

        let newStatus;
        let confirmMsg;

        // 현재 상태 기준 분기
        if (statusText === '판매중') {
            newStatus = 'SOLD_OUT';
            confirmMsg = '이 상품을 품절 처리하시겠습니까?';
        } else {
            newStatus = 'ON_SALE';
            confirmMsg = '이 상품을 다시 판매하시겠습니까?';
        }

        if (!confirm(confirmMsg)) return;

        $.ajax({
            url: `/shop/products/${currentProductId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ status: newStatus }),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function () {
                showSuccessAlert('판매 상태가 변경되었습니다.');
                loadProductDetail(currentProductId);
            },
            error: function () {
                showErrorAlert('판매 상태 변경에 실패했습니다.');
            }
        });
    });

    /* 7. 상품 삭제 (AJAX) */
    $('#detailDeleteBtn').on('click', function () {

        if (!currentProductId) return;

        if (!confirm('정말 이 상품을 삭제하시겠습니까?\n삭제된 상품은 복구할 수 없습니다.')) {
            return;
        }

        $.ajax({
            url: `/shop/products/${currentProductId}/delete`,
            type: 'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function () {

                showSuccessAlert('상품이 삭제되었습니다.');

                // 모달 닫기
                closeModal();

                // 상품 관리 페이지 카드 즉시 제거
                $(`.open-product-detail[data-id="${currentProductId}"]`)
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

    /* 8. 모달 닫기 관련 처리 */
    $modal.on('click', '.modal-close', closeModal);

    $modal.on('click', function (e) {
        if ($(e.target).is('.modal-overlay')) {
            closeModal();
        }
    });

    $(document).on('keydown', function (e) {
        if (e.key === 'Escape' && $modal.is(':visible')) {
            closeModal();
        }
    });

    function closeModal() {
        $modal.fadeOut(150);
        $('body').removeClass('modal-open');
    }

    /* 9. 기타 유틸 함수 */

    // 상태 코드 → 한글 변환
    function convertStatus(status) {
        switch (status) {
            case 'ON_SALE': return '판매중';
            case 'SOLD_OUT': return '품절';
            default: return status;
        }
    }

    // 버튼 상태 초기화
    function resetButtons() {
        $('#detailEditBtn').text('수정');
        $('#detailStatusBtn, #detailDeleteBtn').show();
        $('#detailCancelBtn').remove();
    }
    function restoreViewMode(product) {

        // 상품명
        $('#editProductName').replaceWith(
            `<span class="info-value" id="detailProductName">${product.productName}</span>`
        );

        // 가격
        $('#editProductPrice').replaceWith(
            `<span class="info-value" id="detailProductPrice">
                ${product.price.toLocaleString()}원
            </span>`
        );

        // 설명
        $('#editProductDescription').replaceWith(
            `<div class="description-box" id="detailProductDescription">
                ${product.summary ?? ''}
            </div>`
        );

        // 카테고리
        $('#editProductCategory').replaceWith(
            `<span class="meta-value" id="detailProductCategory">${product.catName}</span>`
        );

        // 상태
        $('#editProductStatus').replaceWith(
            `<span class="info-value status" id="detailProductStatus">
                ${convertStatus(product.status)}
            </span>`
        );

        editMode = false;
        resetButtons();
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';

        const date = new Date(dateStr);

        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');

        return `${yyyy}.${mm}.${dd}`;
    }

});