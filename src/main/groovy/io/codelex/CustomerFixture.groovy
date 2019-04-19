package io.codelex

import io.codelex.model.AuthRequest

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

class CustomerFixture {
    static final String PASSWORD = 'Password123'

    static AuthRequest authRequest() {
        return new AuthRequest(email: randomAlphabetic(12) + '@codelex.io', password: PASSWORD)
    }

    static String randomPassword() {
        return randomAlphabetic(12)
    }

    static String randomSessionId() {
        return randomAlphabetic(12)
    }
}
