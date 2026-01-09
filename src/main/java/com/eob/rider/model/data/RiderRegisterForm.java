package com.eob.rider.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiderRegisterForm {

    @NotEmpty(message = "운전면허번호는 필수 입력 항목입니다.")
    private String driverLicense;

    private LocalDate licenseCreatedAt;

    // input="file" 이기 때문에 MultipartFile 객체로 받아준다.
    private MultipartFile licenseFile;

    @NotEmpty(message = "은행명은 필수 입력 항목입니다.")
    private String bankName;

    @NotEmpty(message = "예금주는 필수 입력 항목입니다.")
    private String accountName;

    @NotEmpty(message = "계좌번호는 필수 입력 항목입니다.")
    private String accountNo;

}
