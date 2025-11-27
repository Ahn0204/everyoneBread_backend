package com.eob.rider.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiderRegisterForm {

    @NotEmpty(message = "운전면허번호는 필수 입력 항목입니다.")
    private String driverLicense;

    private LocalDate licenseCreatedAt;

    private String licenseFile;

}
