package com.eob.admin.model.data;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertAdminForm { // 관리자 계정 추가 시 유효성검사&dto겸용 form

    // 아이디
    // - 영문 시작
    // - 영문/숫자 조합 5~20
    // - 연속문자, 숫자-only는 JS에서 걸림
    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{4,19}$", message = "아이디는 영문 시작 + 영문/숫자 조합 5~20자여야 합니다.")
    private String adminId; // 관리자 id MEMBER_ID

    // 비밀번호
    // - 영문 + 숫자 + 특수문자 포함
    // - 8~20자
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$", message = "비밀번호는 영문+숫자+특수문자 포함 8~20자여야 합니다.")
    private String adminPassword; // 관리자 pw MEMBER_PW

    // 이름 (한글만 2~20자)
    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[가-힣]{2,20}$", message = "이름은 한글 2~20자만 가능합니다.")
    private String adminName; // 관리자 이름 MEMBER_NAME
}
