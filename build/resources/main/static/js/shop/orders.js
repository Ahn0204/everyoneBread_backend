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
            .then(response => response.json())
            .then(data => {
                document.getElementById('dash-today').innerText = data.today;           // 오늘 주문 수
                document.getElementById('dash-wait').innerText = data.wait;             // 대기 주문 수
                document.getElementById('dash-delivering').innerText = data.delivering; // 배송 중 주문 수
                document.getElementById('dash-complete').innerText = data.complete;     // 완료 주문 수
            })
            // 에러 처리
            .catch(err => console.error('대시보드 로딩 실패', err));
    }

    // 페이지 로드 시 1회 실행
    document.addEventListener('DOMContentLoaded', () => {
        loadOrderDashboard();
        // 5분마다 갱신
        setInterval(loadOrderDashboard, 300000);
    });