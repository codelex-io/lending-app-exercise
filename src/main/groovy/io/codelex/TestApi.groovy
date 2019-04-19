package io.codelex

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Component

import java.time.LocalDateTime

import static org.springframework.http.HttpEntity.EMPTY
import static org.springframework.http.HttpMethod.*
import static org.springframework.http.HttpStatus.OK

@Component
class TestApi {
    @Autowired
    TestRestTemplate testingApiTemplate

    void resetTime() {
        def response = testingApiTemplate.exchange('/reset-time', POST, EMPTY, Void)
        assert response.statusCode == OK
    }

    LocalDateTime setTimeFixedAt(LocalDateTime dateTime) {
        def response = testingApiTemplate.exchange('/time', PUT, new HttpEntity<>(dateTime), Void)
        assert response.statusCode == OK
        return dateTime
    }

    LocalDateTime fetchTime() {
        def response = testingApiTemplate.exchange('/time', GET, EMPTY, LocalDateTime)
        assert response.statusCode == OK
        return response.body
    }

    void clearDatabase() {
        def response = testingApiTemplate.exchange('/clear-database', POST, EMPTY, Void)
        assert response.statusCode == OK
    }
}
