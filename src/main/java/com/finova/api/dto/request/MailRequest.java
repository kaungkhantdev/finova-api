package com.finova.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailRequest {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
}
