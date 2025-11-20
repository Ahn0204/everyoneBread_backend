package com.eob.rider.model.data;

import java.security.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiderRegisterForm {

    private String driverLicense;

    private Timestamp licenseCreatedAt;

    private String licenseFile;
    

}
