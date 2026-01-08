$(document).ready(function () {

    /*
        전역 변수 – 상태 체크
    */
    let idCheck = false;            // 아이디 중복확인 여부
    let emailCheck = false;         // 이메일 중복확인 여부
//    let phoneAuthSent = false;      // 인증번호 발송 여부
    let phoneAuthOk = false;        // 인증 성공 여부

    
    // CSRF 토큰 설정 - 개발 단계에서는 미사용 / 운영 시 주석 해제 예정
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');
    $(document).ajaxSend(function (e, xhr) {
        if (token && header) xhr.setRequestHeader(header, token);
    });
    


    /*
        아이디 중복확인
        - 영문으로 시작
        - 영문/숫자 조합 5~20자
        - 중복확인 AJAX 요청
    */
    $("#id-chk-btn").click(function () {

        let memberId = $("input[name='memberId']").val().trim();

        // 기본 빈칸 체크
        if (memberId === "") {
            showErrorAlert("아이디를 입력해주세요.");
            return;
        }

        // 영문으로 시작 + 영문/숫자만 가능 + 5~20자
        let idReg = /^[a-zA-Z][a-zA-Z0-9]{4,19}$/;
        if (!idReg.test(memberId)) {
            showErrorAlert("아이디는 영문으로 시작하며, 영문+숫자 조합 5~20자리여야 합니다.");
            return;
        }

        // 연속 문자를 금지 (aaa, 111, abc)
        let repeatReg = /(.)\1\1/; // 같은 문자 3연속
        if (repeatReg.test(memberId)) {
            showErrorAlert("같은 문자를 3번 이상 반복할 수 없습니다.");
            return;
        }

        // AJAX 중복확인 요청 (URL은 실제 컨트롤러에 맞게 변경)
        $.ajax({
            url: "/member/check-id",
            type: "GET",
            data: { memberId: memberId },
            success: function (result) {
                if (result === true) {
                    showSuccessAlert("사용 가능한 아이디입니다.");
                    idCheck = true;
                } else {
                    showErrorAlert("이미 사용 중인 아이디입니다.");
                    idCheck = false;
                }
            }
        });
    });

    // 아이디 입력 변경 → 중복확인 다시 해야 함
    $("input[name='memberId']").on("input", function () {
        idCheck = false;
    });


    /*
        비밀번호 실시간 유효성 검사
        - 영문 + 숫자 + 특수문자 포함
        - 8~20자
        - 동일 문자 반복 금지
        - 아이디/이름 포함 금지
    */
    $("input[name='memberPw'], input[name='memberPwConfirm']").on("input", function () {

        let pw = $("input[name='memberPw']").val();
        let pw2 = $("input[name='memberPwConfirm']").val();
        let id = $("input[name='memberId']").val();
        let name = $("input[name='memberName']").val();

        // 비밀번호 기본 조건 정규식
        let pwReg = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/;

        // 동일 문자 3회 이상 금지
        let sameReg = /(.)\1\1/;

        // 비밀번호 포함 금지 요소
        if (pw.includes(id) && id.length > 0) {
            $("input[name='memberPw']").css("border", "2px solid red");
        } 
        else if (pw.includes(name) && name.length > 0) {
            $("input[name='memberPw']").css("border", "2px solid red");
        }
        else if (!pwReg.test(pw)) {
            $("input[name='memberPw']").css("border", "2px solid red");
        } 
        else if (sameReg.test(pw)) {
            $("input[name='memberPw']").css("border", "2px solid red");
        }
        else {
            $("input[name='memberPw']").css("border", "1px solid #000");
        }

        // 비밀번호 확인 일치 여부
        if (pw !== pw2) {
            $("input[name='memberPwConfirm']").css("border", "2px solid red");
        } else {
            $("input[name='memberPwConfirm']").css("border", "1px solid #000");
        }
    });

    // 이름 변경 시 비밀번호 경고 border 초기화
    $("input[name='memberName']").on("input", function () {
        $("input[name='memberPw']").css("border", "1px solid #000");
        $("input[name='memberPwConfirm']").css("border", "1px solid #000");
    });

    /*
        이메일 중복확인
        - 기본 정규식 검사
        - 중복확인 AJAX 요청
    */
    $("#email-chk-btn").click(function () {

        let email = $("input[name='memberEmail']").val().trim();

        if (email === "") {
            showErrorAlert("이메일을 입력해주세요.");
            return;
        }

        // 이메일 정규식 (기본적인 RFC 형태)
        let emailReg = /^[a-zA-Z0-9._%+-]{2,20}@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailReg.test(email)) {
            showErrorAlert("올바른 이메일 형식이 아닙니다.");
            return;
        }

        $.ajax({
            url: "/member/check-email",
            type: "GET",
            data: { memberEmail: email },
            success: function (result) {
                if (result === true) {
                    showSuccessAlert("사용 가능한 이메일입니다.");
                    emailCheck = true;
                } else {
                    showErrorAlert("이미 사용 중인 이메일입니다.");
                    emailCheck = false;
                }
            }
        });

    });

    // 이메일 입력 변경 → 중복확인 다시 해야 함
    $("input[name='memberEmail']").on("input", function () {
        emailCheck = false;
        // 이메일 자동 소문자 변환
        $(this).val($(this).val().toLowerCase());
    });

    /*
        휴대폰 번호 자동 하이픈 삽입 (010-1234-5678)
    */
    $("input[name='memberPhone']").on("input", function () {

        let value = $(this).val().replace(/[^0-9]/g, ""); //숫자만

        // 최대 11자리 제한
        if (value.length > 11) {
            value = value.substring(0, 11);
        }

        let result = "";

        if (value.length < 4) {
            result = value;
        } else if (value.length < 8) {
            result = value.substring(0, 3) + "-" + value.substring(3);
        } else {
            result =
                value.substring(0, 3) + "-" +
                value.substring(3, 7) + "-" +
                value.substring(7, 11);
        }

        $(this).val(result);
    });

    /*
        휴대폰 인증번호 발송
        POST /sms/send
    */
    $("#phone-chk-btn").click(function () {

        const phone = $("input[name='memberPhone']").val().replace(/-/g, "");

        // 기본 유효성
        if (phone.length !== 11 || !/^010\d{8}$/.test(phone)) {
            showErrorAlert("올바른 휴대폰 번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/sms/send",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ phone: phone }),
            success: function (res) {
                if (res.success) {
                    showSuccessAlert(res.message);
                    $("input[name= 'authCode']").focus();
                } else {
                    showErrorAlert(res.message);
                }
            }, error: function () {
                showErrorAlert("서버 오류로 인증번호 발송에 실패했습니다.");
            }
        });
    });

    /*
        인증번호 확인
        POST /sms/check
    */
    $("#auth-code-btn").click(function () {

        const phone = $("input[name='memberPhone']").val().replace(/-/g, "");
        const authCode = $("input[name='authCode']").val().trim();

        if(authCode === ""){
            showErrorAlert("인증번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/sms/check",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ phone: phone, authCode: authCode }),
            success: function (res) {
                if (res.success) {
                    phoneAuthOk = true;
                    showSuccessAlert(res.message);

                    // 인증 완료 후 입력 잠금
                    $("input[name='memberPhone']").prop("readonly", true);
                    $("input[name='authCode']").prop("readonly", true);
                    $("#phone-chk-btn").prop("disabled", true);
                    $("#auth-code-btn").prop("disabled", true);

                } else {
                    phoneAuthOk = false;
                    showErrorAlert(res.message);
                }
            }, error: function(){
                showErrorAlert("서버 통신 오류");
            }
        });
    });

    /*
        주민등록번호 입력 제한
        - 앞6자리 YYMMDD → 숫자만
        - 뒤1자리 → 숫자만
        - 초과 입력 방지
        */
    $("input[name='jumin1']").on("input", function () {
        let v = $(this).val().replace(/[^0-9]/g, "");

        // 최대 6자리 제한
        if (v.length > 6) v = v.substring(0, 6);
        $(this).val(v);

        // 6자리 입력되면 자동으로 뒷자리로 포커스 이동
        if (v.length === 6) {
            $("input[name='jumin2']").focus();
        }
    });

    $("input[name='jumin2']").on("input", function () {
        let v = $(this).val().replace(/[^0-9]/g, "");
        if (v.length > 1) v = v.substring(0, 1);
        $(this).val(v);
    });

    /*
        전체 유효성 검사 후 submit 허용
    */
    $("form").submit(function () {

        let id = $("input[name='memberId']").val().trim();
        let pw = $("input[name='memberPw']").val();
        // 전화번호 하이픈 제거 (01012345678 형태로 만들기)
        $("input[name='memberPhone']").val(
            $("input[name='memberPhone']").val().replace(/-/g, "")
        );
        let pw2 = $("input[name='memberPwConfirm']").val();
        let name = $("input[name='memberName']").val().trim();
        let j1 = $("input[name='jumin1']").val().trim();
        let j2 = $("input[name='jumin2']").val().trim();
        let email = $("input[name='memberEmail']").val().trim();
        let phone = $("input[name='memberPhone']").val().trim();
        let address = $("input[name='memberAddress']").val().trim();

        /* 아이디 */
        if (!idCheck) {
            showErrorAlert("아이디 중복확인을 완료해주세요.");
            return false;
        }

        // 아이디 앞뒤 공백 금지
        if (/^\s+|\s+$/g.test(id)) {
            showErrorAlert("아이디 앞뒤에 공백을 포함할 수 없습니다.");
            return false;
        }

        // 숫자만으로 구성 금지
        if (/^\d+$/.test(id)) {
            showErrorAlert("아이디는 숫자만으로 구성할 수 없습니다.");
            return false;
        }

        /* 이름 - 한글만 허용 */
        let nameReg = /^[가-힣]{2,20}$/;
        if (!nameReg.test(name)) {
            showErrorAlert("이름은 한글 2~20자만 가능합니다.");
            return false;
        }

        /* 비밀번호 */
        let pwReg = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/;
        if (!pwReg.test(pw)) {
            showErrorAlert("비밀번호는 영문+숫자+특수문자 포함 8~20자입니다.");
            return false;
        }

        // 공백 포함 금지
        if (/\s/.test(pw)) {
            showErrorAlert("비밀번호에는 공백을 포함할 수 없습니다.");
            return false;
        }

        if (pw !== pw2) {
            showErrorAlert("비밀번호가 일치하지 않습니다.");
            return false;
        }

        /* 주민등록번호 */
        if (j1.length !== 6 || j2.length !== 1) {
            showErrorAlert("주민등록번호를 정확히 입력해주세요.");
            return false;
        }
        // 주민등록번호 앞자리 날짜 유효성 검사 (YYMMDD)
        let year = parseInt(j1.substring(0, 2));
        let month = parseInt(j1.substring(2, 4));
        let day = parseInt(j1.substring(4, 6));

        if (month < 1 || month > 12 || day < 1 || day > 31) {
            showErrorAlert("주민등록번호 앞자리를 다시 확인해주세요.");
            return false;
        }

        // 실제 존재하는 날짜인지 검사
        let fullYear = 2000 + year;  // 20xx 기준
        let dateObj = new Date(fullYear, month - 1, day);

        if (
            dateObj.getFullYear() !== fullYear ||
            dateObj.getMonth() + 1 !== month ||
            dateObj.getDate() !== day
        ) {
            showErrorAlert("주민등록번호 앞자리 날짜가 실제 존재하지 않습니다.");
            return false;
        }

        // 주민등록번호 뒷자리는 1~4로 시작해야 함
        if (!/^[1-4]$/.test(j2)) {
            showErrorAlert("주민등록번호 뒷자리는 1~4로 시작해야 합니다.");
            return false;
        }

        /* 이메일 */
        if (!emailCheck) {
            showErrorAlert("이메일 중복확인을 완료해주세요.");
            return false;
        }

        // 특수문자 연속 금지
        if (email.includes("..") || email.includes("__") || email.includes("--")) {
            showErrorAlert("이메일에 특수문자를 연속으로 사용할 수 없습니다.");
            return false;
        }

        /* 휴대폰 인증 */
        // 나중에 주석 제거
         if (!phoneAuthOk) {
            showErrorAlert("휴대폰 인증을 완료해주세요.");
            return false;
         }

        // 010만 허용
        if (!/^010/.test(phone)) {
            showErrorAlert("휴대폰 번호는 010으로 시작해야 합니다.");
            return false;
        }

        /* 주소 */
        if (address.length < 5) {
            showErrorAlert("주소를 정확히 입력해주세요.");
            return false;
        }

        return true;
    });
    
});
    // 주소 API
    function execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function(data) {

                let addr = "";

                if (data.userSelectedType === "R") { 
                    addr = data.roadAddress;      // 도로명 주소
                } else { 
                    addr = data.jibunAddress;     // 지번 주소
                }

                // 주소 입력창에 값 채우기
                document.getElementById("memberAddress").value = addr;

                // 상세주소 입력창으로 이동
                document.getElementsByName("memberAddressDetail")[0].focus();
            }
        }).open();    
    }