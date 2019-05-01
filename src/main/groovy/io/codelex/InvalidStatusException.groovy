package io.codelex

import org.springframework.http.HttpStatus

class InvalidStatusException extends RuntimeException {
    final HttpStatus httpStatus
    final String sessionId

    InvalidStatusException(HttpStatus httpStatus, String sessionId = null) {
        this.httpStatus = httpStatus
        this.sessionId = sessionId
    }

    @Override
    String getMessage() {
        return "Invalid status code $httpStatus"
    }
}
