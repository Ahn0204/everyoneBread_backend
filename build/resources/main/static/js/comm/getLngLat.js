//주소를 좌표로 변환 -> 경도, 위도 얻기
function getLngLat(address) {
    return new Promise((resolve, reject) => {
        let geocoder = new kakao.maps.services.Geocoder();
        geocoder.addressSearch(address, function (result, status) {
            if (status === kakao.maps.services.Status.OK) {
                let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                //위도,경도 return
                resolve({
                    lat: coords.getLat(),
                    lng: coords.getLng(),
                });
            } else {
                reject('주소 변환 실패');
            }
        });
    });
}
