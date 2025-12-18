package com.eob.alert.model.data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertDTO {

    private Long alertNo;
    private String title;
    private String content;
    private String linkUrl;
    private String readYn;
    private LocalDateTime createdAt;

    public static AlertDTO from(AlertEntity alert) {
        return new AlertDTO(
                alert.getAlertNo(),
                alert.getTitle(),
                alert.getContent(),
                alert.getLinkUrl(),
                alert.getReadYn(),
                alert.getCreatedAt());
    }
}
