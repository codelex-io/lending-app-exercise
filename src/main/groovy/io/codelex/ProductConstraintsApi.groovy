package io.codelex

import io.codelex.model.ProductConstraints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.stereotype.Component

import static org.springframework.http.HttpEntity.EMPTY
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpStatus.OK

@Component
class ProductConstraintsApi {
    @Autowired
    TestRestTemplate publicApiTemplate

    ProductConstraints fetchConstraints() {
        def response = publicApiTemplate.exchange('/constraints', GET, EMPTY, ProductConstraints)
        assert response.statusCode == OK
        return response.body
    }
}
