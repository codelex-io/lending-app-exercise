package io.codelex

import org.springframework.http.HttpStatus

class InvalidStatusException extends RuntimeException{
    final HttpStatus httpStatus

    InvalidStatusException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus
    }
}
