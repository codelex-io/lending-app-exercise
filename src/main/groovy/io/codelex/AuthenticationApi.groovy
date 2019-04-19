package io.codelex

import io.codelex.model.AuthRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Component

import static io.codelex.SessionIdHandler.extractSessionId
import static io.codelex.SessionIdHandler.headers
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpStatus.OK

@Component
class AuthenticationApi {
    @Autowired
    TestRestTemplate publicApiTemplate

    String register(AuthRequest req) {
        def response = publicApiTemplate.exchange('/register', POST, new HttpEntity<>(req), Void)
        if (response.statusCode != OK) {
            throw new InvalidStatusException(response.statusCode)
        }
        return extractSessionId(response)
    }

    String signIn(AuthRequest req) {
        def response = publicApiTemplate.exchange('/sign-in', POST, new HttpEntity<>(req), Void)
        if (response.statusCode != OK) {
            throw new InvalidStatusException(response.statusCode)
        }
        return extractSessionId(response)
    }

    void signOut(String sessionId) {
        def response = publicApiTemplate.exchange('/sign-out', POST, new HttpEntity<>(headers(sessionId)), Void)
        if (response.statusCode != OK) {
            throw new InvalidStatusException(response.statusCode)
        }
    }
}
