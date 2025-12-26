//주소를 좌표로 변환 -> 경도, 위도 얻기
function getLngLat(address) {
    let geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, function (result, status) {
        if (status === kakao.maps.services.Status.OK) {
            let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
            let longitude = coords.getLng();
            $('#logitude').val(longitude);
            let latitude = coords.getLat();
            $('#latitude').val(latitude);
            console.log('좌표 변환 성공');
        }
    });
}
