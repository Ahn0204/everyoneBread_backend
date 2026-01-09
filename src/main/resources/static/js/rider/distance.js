// ================================================================
// 📍 매장 ↔ 라이더 거리 계산 (직선거리, Haversine)
// ================================================================

initDistances();

/**
 * 두 좌표 거리 계산 (Haversine 공식)
 * @returns {number} 거리 (km)
 */
function getDistance(lat1, lng1, lat2, lng2) {
    const R = 6371;
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLng = ((lng2 - lng1) * Math.PI) / 180;

    const a = Math.sin(dLat / 2) ** 2 + Math.cos((lat1 * Math.PI) / 180) * Math.cos((lat2 * Math.PI) / 180) * Math.sin(dLng / 2) ** 2;

    return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
}

/**
 * 거리 DOM 업데이트
 */
function updateDistanceElements(riderLat, riderLng) {
    document.querySelectorAll('.distance').forEach((span) => {
        const shopLat = parseFloat(span.dataset.lat);
        const shopLng = parseFloat(span.dataset.lng);

        if (isNaN(shopLat) || isNaN(shopLng)) {
            span.innerText = '위치 오류';
            return;
        }

        const dist = getDistance(riderLat, riderLng, shopLat, shopLng);
        span.innerText = dist < 1 ? (dist * 1000).toFixed(0) + 'm' : dist.toFixed(1) + 'km';
    });
}

let watchId = null;

/**
 * 초기 위치 + 실시간 추적 시작
 */
function initDistances() {
    if (!navigator.geolocation) {
        showToast('이 브라우저는 위치 정보를 지원하지 않습니다.');
        return;
    }

    // ✅ 1회 빠른 위치 확보
    navigator.geolocation.getCurrentPosition(
        (pos) => {
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;

            updateDistanceElements(lat, lng);
            startWatch();
        },
        (err) => {
            console.error('초기 위치 실패:', err);
            showToast('위치 권한을 허용해주세요.');
        },
        {
            enableHighAccuracy: false,
            timeout: 6000,
            maximumAge: 10000,
        }
    );
}

/**
 * 실시간 위치 추적
 */
function startWatch() {
    watchId = navigator.geolocation.watchPosition(
        (pos) => {
            updateDistanceElements(pos.coords.latitude, pos.coords.longitude);
        },
        (err) => {
            console.error('위치 추적 실패:', err);
        },
        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 5000,
        }
    );
}

/**
 * 실시간 위치 추적 중단
 */
function stopWatch() {
    if (watchId !== null) {
        navigator.geolocation.clearWatch(watchId);
        watchId = null;
    }
}
