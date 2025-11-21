package com.eob.member.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    //  아이디
    //  - 영문 시작
    //  - 영문/숫자 조합 5~20
    //  - 연속문자, 숫자-only는 JS에서 걸림
    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(
        regexp = "^[a-zA-Z][a-zA-Z0-9]{4,19}$",
        message = "아이디는 영문 시작 + 영문/숫자 조합 5~20자여야 합니다."
    )
    private String memberId;

    //  비밀번호
    //  - 영문 + 숫자 + 특수문자 포함
    //  - 8~20자
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$",
        message = "비밀번호는 영문+숫자+특수문자 포함 8~20자여야 합니다."
    )
    private String memberPw;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String memberPwConfirm;

    //  이름 (한글만 2~20자)
    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(
        regexp = "^[가-힣]{2,20}$",
        message = "이름은 한글 2~20자만 가능합니다."
    )
    private String memberName;

    //  주민등록번호
    //  jumin1 = 앞 6자리(YYMMDD)
    //  jumin2 = 뒤 1자리(1~4)
    @NotBlank(message = "주민등록번호 앞자리를 입력해주세요.")
    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "주민등록번호 앞자리는 6자리 숫자만 가능합니다."
    )
    private String jumin1;

    @NotBlank(message = "주민등록번호 뒤 1자리를 입력해주세요.")
    @Pattern(
        regexp = "^[1-4]{1}$",
        message = "주민등록번호 뒤 자리는 1~4 중 하나여야 합니다."
    )
    private String jumin2;

    //  이메일
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String memberEmail;

    //  휴대폰 번호 (01012345678)
    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(
        regexp = "^010[0-9]{8}$",
        message = "휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다."
    )
    private String memberPhone;

    //  주소
    @NotBlank(message = "주소를 입력해주세요.")
    @Size(min = 5, message = "주소를 정확히 입력해주세요.")
    private String memberAddress;

    // 상세주소 (선택 입력)
    @Size(max = 100, message = "상세주소가 너무 깁니다.")
    private String memberAddressDetail;

    //  회원 역할
    //  USER / SHOP
    @NotBlank(message = "회원 유형이 지정되지 않았습니다.")
    private String memberRole;
}
