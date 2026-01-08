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
5) 은행명 유효성 검사
------------------------------ */
$("input[name='bankName']").on('input', function () {
    let v = $(this).val().trim();

    const bankRegex = /^[가-힣]{2,10}$/;

    if (!bankRegex.test(v)) {
        $(this).css("border", "2px solid red");
    } else {
        $(this).css("border", "1px solid #ccc");
    }
});

/* ------------------------------
6) 계좌번호 유효성 검사 + 자동 정리
------------------------------ */
$("input[name='accountNo']").on('input', function () {
    let v = $(this).val()
        .replace(/[^0-9\-]/g, '');

    // 연속 하이픈 제거
    v = v.replace(/-+/g, '-');

    $(this).val(v);

    const accountRegex = /^[0-9\-]{8,20}$/;

    if (!accountRegex.test(v)) {
        $(this).css("border", "2px solid red");
    } else {
        $(this).css("border", "1px solid #ccc");
    }
});

/* ------------------------------
7) 최종 submit AJAX 처리
------------------------------ */
$("form").submit(function (e) {
    e.preventDefault();

    /* 상점명 중복확인 */
    if (!shopNameOk) {
        showErrorAlert("상점명 중복확인을 완료해주세요.");
        return false;
    }

    /* 주소 */
    let addr = $("input[name='shopAddress']").val().trim();
    if (addr.length < 5) {
        showErrorAlert("사업장 주소를 입력해주세요.");
        return false;
    }

    /* 사업자 번호 */
    let biz = $("input[name='bizNo']").val().trim();
    if (biz.length !== 12) {
        showErrorAlert("올바른 사업자 등록번호 형식이 아닙니다.");
        return false;
    }

    /* 은행명 */
    let bankName = $("input[name='bankName']").val().trim();
    const bankRegex = /^[가-힣]{2,10}$/;
    if (!bankRegex.test(bankName)) {
        showErrorAlert("은행명은 한글 2~10자로 입력해주세요.");
        return false;
    }

    /* 계좌번호 */
    let accountNo = $("input[name='accountNo']").val().trim();
    const accountRegex = /^[0-9\-]{8,20}$/;
    if (!accountRegex.test(accountNo)) {
        showErrorAlert("계좌번호 형식이 올바르지 않습니다.");
        return false;
    }

    /* FormData 생성 */
    let formData = new FormData(this);

    // 계좌번호 하이픈 제거 후 전송
    formData.set("accountNo", accountNo.replaceAll("-", ""));

    $.ajax({
        url: "/shop/register/step",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (res) {
            if (res.result !== "OK") {
                showErrorAlert(res.message || "입점 신청에 실패했습니다.");
                return;
            }

            Swal.fire({
                icon: 'success',
                title: '입점 신청이 완료되었습니다!',
                confirmButtonText: '확인'
            }).then(() => {
                window.location.href = "/shop/login";
            });
        },
        error: function () {
            showErrorAlert("서버 오류가 발생했습니다.");
            window.location.href = "/shop/register/step";
        }
    });
});