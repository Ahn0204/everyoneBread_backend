package com.eob.rider.model.data;

import java.time.LocalDateTime;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "RIDER_LICENSE_FILE")
public class RiderLicenseFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rider_license_seq")
    @SequenceGenerator(name = "rider_license_seq", sequenceName = "rider_license_seq", allocationSize = 1)
    private long riderFileNo;

    @OneToOne(mappedBy = "riderLicenseFile")
    private RiderEntity rider;

    @NotNull
    private String LicenseFile;

    @NotNull
    private LocalDateTime createdAt;
}
