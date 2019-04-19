package io.codelex

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static io.codelex.CustomerFixture.authRequest
import static io.codelex.model.ApplicationStatus.APPROVED
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CONFLICT

class ApplyForLoanSpec extends BaseSpecification {
    @Autowired
    AuthenticationApi authenticationApi

    @Autowired
    LoanApi loanApi

    @Autowired
    TestApi testApi

    def now = LocalDate.of(2019, 1, 1).atTime(9, 0)

    void setup() {
        testApi.setTimeFixedAt(now)
    }

    def 'should be able to apply for loan'() {
        given:
            def sessionId = authenticationApi.register(authRequest())
        when:
            def res = loanApi.applyForLoan(sessionId, 500.0, 30)
        then:
            res.status == APPROVED
    }

    @Unroll
    def 'should not be able to apply with wrong amount - #amount'() {
        given:
            def sessionId = authenticationApi.register(authRequest())
        when:
            loanApi.applyForLoan(sessionId, amount, 30)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            amount << [null, -1.0, 0.0, 99.99, 500.01]
    }

    @Unroll
    def 'should not be able to apply with wrong term - #termDays'() {
        given:
            def sessionId = authenticationApi.register(authRequest())
        when:
            loanApi.applyForLoan(sessionId, 500.0, termDays)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            termDays << [null, -1, 0, 6, 31]
    }

    def 'should not be able to apply for loan if open loan present'() {
        given:
            def sessionId = authenticationApi.register(authRequest())
            loanApi.applyForLoan(sessionId, 500.0, 30)
        when:
            loanApi.applyForLoan(sessionId, 500.0, 30)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == CONFLICT
    }
}
