const csrfToken  = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
/**
 * 즐겨찾기(찜) 토글 처리
 * - 하트 클릭 시 호출
 * - 서버에 shopNo 전달
 * - DELETED면 카드 제거
 */
function toggleWish(el) {
    
    // 중복 클릭 방지
    el.style.pointerEvents = 'none';

    // 1️. 클릭된 하트에서 상점 번호 추출
    const shopNo = el.dataset.shopno;

    // 2️. 서버에 토글 요청
    fetch('/wishlist/toggle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({
            shopNo: shopNo
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('찜 처리 중 오류가 발생했습니다.');
        }
        return response.json();
    })
    .then(data => {

        // 3️. 서버 응답 상태 확인
        // status: ACTIVE / DELETED
        if (data.status === 'DELETED') {

            // 마이페이지에서는 찜 해제 시 카드 제거
            const wishItem = el.closest('.wish-item');
            wishItem.remove();

            // 찜 목록이 모두 사라졌다면 안내 문구 표시
            if (document.querySelectorAll('.wish-item').length === 0) {
                showEmptyMessage();
            }
        }

        // ACTIVE인 경우 (마이페이지에서는 특별한 처리 없음)
    })
    .catch(error => {
        console.error(error);
        showErrorAlert('즐겨찾기 처리 중 문제가 발생했습니다.');
    });
}

/**
 * 즐겨찾기 목록이 비었을 때 안내 문구 표시
 */
function showEmptyMessage() {

    // id 기반 DOM 접근
    const container = document.getElementById('wishlist-container');

    const emptyDiv = document.createElement('div');
    emptyDiv.className = 'empty-text';
    emptyDiv.innerHTML = `
        아직 즐겨찾기한 빵집이 없어요 🍞<br>
        마음에 드는 빵집을 찜해보세요!
    `;

    container.appendChild(emptyDiv);
}