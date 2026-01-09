package com.eob.member.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    private String alias;
    private String address;
    private Boolean isDefault;
    private Double latitude;
    private Double longitude;
    
}