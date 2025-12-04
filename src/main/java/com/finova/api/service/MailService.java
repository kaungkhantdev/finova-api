package com.finova.api.service;

import com.finova.api.dto.request.MailRequest;

public interface MailService {
    public void sendMail(MailRequest request);
}
