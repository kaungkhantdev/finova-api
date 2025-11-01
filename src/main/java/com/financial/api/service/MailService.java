package com.financial.api.service;

import com.financial.api.dto.request.MailRequest;

public interface MailService {
    public void sendMail(MailRequest request);
}
