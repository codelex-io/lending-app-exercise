package io.codelex

import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class TestingApiSpec extends BaseSpecification {
    @Autowired
    TestApi testApi

    def 'should be able to set time'() {
        given:
            def now = LocalDate.of(2019, 1, 1).atStartOfDay()
            testApi.setTimeFixedAt(now)
        when:
            def response = testApi.fetchTime()
        then:
            now.isEqual(response)
    }

    def 'should be able to reset time'() {
        given:
            def now = LocalDate.of(2019, 1, 1).atStartOfDay()
            testApi.setTimeFixedAt(now)
        when:
            testApi.resetTime()
            def response = testApi.fetchTime()
        then:
            !now.isEqual(response)
    }
}
