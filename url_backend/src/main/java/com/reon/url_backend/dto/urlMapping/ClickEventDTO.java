package com.reon.url_backend.dto.urlMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClickEventDTO {
    private LocalDateTime clickDate;
    private Long count;
}
