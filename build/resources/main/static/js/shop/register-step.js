const token = $('meta[name="_csrf"]').attr('content');
const header = $('meta[name="_csrf_header"]').attr('content');

/* ------------------------------
1) 상점명 중복확인
------------------------------ */
let shopNameOk = false;

$('#shopNameChkBtn').click(function () {
let shopName = $("input[name='shopName']").val().trim();

$.ajax({
    url: '/shop/check-name',
    type: 'GET',
    data: { shopName: shopName },
    success: function (available) {
        if (available === true) {
            Swal.fire('사용 가능한 상점명입니다.');
            shopNameOk = true;
        } else {
            Swal.fire('이미 등록된 상점명입니다.');
            shopNameOk = false;
        }
    },
    error: function () {
        Swal.fire('서버 오류가 발생했습니다.');
    },
});
});

$("input[name='shopName']").on('input', function () {
shopNameOk = false;
});

/* ------------------------------
2) 사업자 등록번호 자동 하이픈
------------------------------ */
$("input[name='bizNo']").on('input', function () {
let v = $(this)
    .val()
    .replace(/[^0-9]/g, '');
if (v.length > 10) v = v.substring(0, 10);

let res = '';

if (v.length < 4) res = v;
else if (v.length < 6) res = v.substring(0, 3) + '-' + v.substring(3);
else res = v.substring(0, 3) + '-' + v.substring(3, 5) + '-' + v.substring(5);

$(this).val(res);
});

/* ------------------------------
3) 카카오 주소 API
------------------------------ */
function execDaumPostcode() {
new daum.Postcode({
    oncomplete: async function (data) {
        let addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;
        document.getElementById('shopAddress').value = addr;
        //[예솔] 위치 좌표 등록하기
        //입력받은 주소 좌표로 변환, longitude, latitude에 저장
        const { lat, lng } = await getLngLat(addr);
        $('#longitude').val(lng);
        $('#latitude').val(lat);
        console.log('좌표 변환 성공');
    },
}).open();
}

/* ------------------------------
4) 파일 첨부
------------------------------ */
//     $('#bizFileBtn').click(function () {
//         $('#bizFile').click();
//     });

//     $('#bizFile').on('change', function () {
//         let file = this.files[0];
//         if (!file) return;

//     if (!file.type.match("image.*")) {
//         showErrorAlert("이미지 파일만 업로드 가능합니다.");
//         $(this).val("");
//         $("#fileNameDisplay").text("");
//         return;
//     }

//     $("#fileNameDisplay").text("선택된 파일: " + file.name);
// });


/* ------------------------------
5) 최종 submit  AJAX 처리
------------------------------ */
$("form").submit(function(e) {
e.preventDefault();  // 폼 기본 제출 막기

// 클라이언트 유효성 검사
if (!shopNameOk) {
showErrorAlert("상점명 중복확인을 완료해주세요.");
return false;
}

let addr = $("input[name='shopAddress']").val().trim();
if (addr.length < 5) {
showErrorAlert("사업장 주소를 입력해주세요.");
return false;
}

let biz = $("input[name='bizNo']").val().trim();
if (biz.length !== 12) {
showErrorAlert("올바른 사업자 등록번호 형식이 아닙니다.");
return false;
}

// 파일 포함 FormData
let formData = new FormData(this);

$.ajax({
url: "/shop/register/step",
type: "POST",
data: formData,
processData: false,
contentType: false,
beforeSend: function (xhr) { xhr.setRequestHeader(header, token); },
success: function(res) {
    console.log("서버 응답:", res);

    // 서버 응답 검사
    if (res.result !== "OK") {
        showErrorAlert({
            icon: "error",
            title: "입점 신청 실패",
            text: res.message || "입점 신청에 실패했습니다.",
            confirmButtonText: "확인"
        });
        return;
    }
    Swal.fire({icon: 'success', title: '입점 신청이 완료되었습니다!', confirmButtonText: '확인'}).then(() => {
        window.location.href = "/shop/login";
    });
    
},

error: function(xhr) {
    console.log("에러 응답:", xhr.responseJSON);

    showErrorAlert("서버 오류가 발생했습니다.");

        // form 내부 입력값 전체 초기화
        $("form")[0].reset();

        // 파일명 표시 영역도 초기화
        $("#fileNameDisplay").text("");

        // 중복확인 상태 초기화
        shopNameOk = false;

        // 페이지 다시 로드하여 완전 초기화
        window.location.href = "/shop/register/step";
}

//$('#fileNameDisplay').text('선택된 파일: ' + file.name);
    });
});