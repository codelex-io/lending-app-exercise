package io.codelex

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

class SessionIdHandler {
    static String extractSessionId(ResponseEntity response) {
        return response.headers.getFirst('Set-Cookie')
    }
    
    static HttpHeaders headers(String sessionId){
        def headers = new HttpHeaders()
        headers.set('Cookie', sessionId)
        return headers
    }
}
