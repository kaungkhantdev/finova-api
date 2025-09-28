package com.financial.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private int expiresIn;
}

// write builder manually
//public class AuthResponse {
//    private String accessToken;
//    private String refreshToken;
//    private String tokenType;
//    private int expiresIn;
//
//    public static AuthResponseBuilder builder() {
//        return new AuthResponseBuilder();
//    }
//
//    public static class AuthResponseBuilder {
//        private String accessToken;
//        private String refreshToken;
//        private String tokenType;
//        private int expiresIn;
//
//        public AuthResponseBuilder accessToken(String accessToken) {
//            this.accessToken = accessToken;
//            return this;
//        }
//
//        public AuthResponseBuilder refreshToken(String refreshToken) {
//            this.refreshToken = refreshToken;
//            return this;
//        }
//
//        public AuthResponseBuilder tokenType(String tokenType) {
//            this.tokenType = tokenType;
//            return this;
//        }
//
//        public AuthResponseBuilder expiresIn(int expiresIn) {
//            this.expiresIn = expiresIn;
//            return this;
//        }
//
//        public AuthResponse build() {
//            AuthResponse response = new AuthResponse();
//            response.accessToken = this.accessToken;
//            response.refreshToken = this.refreshToken;
//            response.tokenType = this.tokenType;
//            response.expiresIn = this.expiresIn;
//            return response;
//        }
//    }
//}
