//위치 정보 사용
//카카오맵 sdk 로드 우선
//getUserPosition()으로 사용자 기기의 위치 좌표 획득
//getAddress()로 위치좌표를 카카오맵api를 통해 주소로 변환

//현재 사용자의 위치 좌표를 가져오는 함수 (promise 생성)
function getUserPosition() {
    //리턴값을 promise로 래핑
    return new Promise((resolve, reject) => {
        //성공,실패 시
        //위치좌표 가져옴
        navigator.geolocation.getCurrentPosition(
            (position) =>
                resolve({
                    //성공 시 promise가 리턴할 값
                    lat: position.coords.latitude, //현재 위도
                    lng: position.coords.longitude, //현재 경도
                }),
            //실패 시 promise가 리턴할 값
            (error) => reject(error), //실패 시 코드에 따른 행동은 사용하는 곳에서 지정하기
            //옵션 설정
            {
                timeout: 10000, //위치 정보 승인 요청의 대기 시간(밀리초)-10초
                enableHighAccuracy: false, //고정밀도 위치요청을 캐치할 것인지
                maximumAge: 0, //위치 정보의 캐시 기한(밀리초) - 캐시된 위치 정보 사용안함=항상 최신 위치 정보
            }
        );
    });
}

//현재 좌표를 주소로 변환하는 함수 (promise 생성)
function getAddress(lat, lng) {
    return new Promise((resolve, reject) => {
        //주소-좌표 변환 객체 생성
        let geocoder = new kakao.maps.services.Geocoder();
        //coord={전체주소, 시도, 시군구}
        let coord = new kakao.maps.LatLng(lat, lng);
        //변환
        geocoder.coord2Address(coord.getLng(), coord.getLat(), function (result, status) {
            //변환 성공 시
            if (status === kakao.maps.services.Status.OK) {
                //전체주소 addr 변환
                const addr = result[0].road_address || result[0].address;
                //addr 얻기
                resolve({
                    addr: addr.address_name,
                });
            } else {
                reject('주소 변환 실패');
            }
        });
    });
}
