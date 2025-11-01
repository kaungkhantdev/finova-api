package com.financial.api.util;

public class MailParts {
    private final String userName;
    private final String domain;

    public MailParts(String userName, String domain) {
        this.userName = userName;
        this.domain = domain;
    }

    public String getUserName() { return userName; }
    public String getDomain() { return domain; }

    @Override
    public String toString() {
        return "EmailParts{username='" + userName + "', domain='" + domain + "'}";
    }
}
