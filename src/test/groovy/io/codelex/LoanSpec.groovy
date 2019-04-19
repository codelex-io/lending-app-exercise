package io.codelex


import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static io.codelex.CustomerFixture.authRequest
import static io.codelex.model.LoanStatus.OPEN
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic
import static org.springframework.http.HttpStatus.BAD_REQUEST

class LoanSpec extends BaseSpecification {
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

    def 'should be able to view loans'() {
        given:
            def req = authRequest()
            def sessionId = authenticationApi.register(req)
            loanApi.applyForLoan(sessionId, 500.0, 30)
        when:
            def loan = loanApi.fetchLoans(sessionId).first()
        then:
            !loan.id.empty
            loan.status == OPEN
            loan.created.isEqual(now.toLocalDate())
            loan.dueDate.isEqual(now.toLocalDate().plusDays(30))
            loan.principal == 500.0
            loan.interest == 50.0
            loan.total == 550.0
    }

    def 'should generate loan id in proper format'() {
        given:
            def req = authRequest()
            def sessionId = authenticationApi.register(req)
            loanApi.applyForLoan(sessionId, 500.0, 30)
        when:
            def loan = loanApi.fetchLoans(sessionId).first()
        then:
            loan.id ==~ /[0-9]{4}-[0-9]{4}/
    }

    @Unroll
    def 'should be able to extend loan'() {
        given:
            def req = authRequest()
            def sessionId = authenticationApi.register(req)
            loanApi.applyForLoan(sessionId, 500.0, 30)
        and:
            now = now.plusDays(30)
            testApi.setTimeFixedAt(now)
        and:
            def loan = loanApi.fetchLoans(sessionId).first()
        when:
            loan = loanApi.extendLoan(sessionId, loan.id, extensionDays)
        then:
            loan.dueDate.isEqual(now.toLocalDate().plusDays(extensionDays))
            loan.principal == 500.0
            loan.interest == interest
            loan.total == totalAmount
        where:
            extensionDays | interest | totalAmount
            7             | 62.84    | 562.84
            14            | 75.66    | 575.66
            30            | 105.00   | 605.00
    }

    @Unroll
    def 'should not be able to extend loan with wrong term'() {
        given:
            def req = authRequest()
            def sessionId = authenticationApi.register(req)
            loanApi.applyForLoan(sessionId, 500.0, 30)
        and:
            def loan = loanApi.fetchLoans(sessionId).first()
        when:
            loanApi.extendLoan(sessionId, loan.id, extensionDays)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            extensionDays << [null, -1, 0, 6, 31]
    }

    def 'should not be able to extend other customer loan'() {
        given:
            def sessionId1 = authenticationApi.register(authRequest())
            loanApi.applyForLoan(sessionId1, 500.0, 30)
            def loan = loanApi.fetchLoans(sessionId1).first()
        and:
            def sessionId2 = authenticationApi.register(authRequest())
        when:
            loanApi.extendLoan(sessionId2, loan.id, 14)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
    }

    def 'should not be able to extend non existing loan'() {
        given:
            def sessionId = authenticationApi.register(authRequest())
        when:
            loanApi.extendLoan(sessionId, randomAlphabetic(8), 14)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
    }

    def 'should be able to view extensions'() {
        given:
            def sessionId = authenticationApi.register(authRequest())
            loanApi.applyForLoan(sessionId, 500.0, 30)
        and:
            def loan = loanApi.fetchLoans(sessionId).first()
            loanApi.extendLoan(sessionId, loan.id, 7)
        and:
            now = testApi.setTimeFixedAt(now.plusDays(7))
            loanApi.extendLoan(sessionId, loan.id, 14)
        when:
            loan = loanApi.fetchLoans(sessionId).first()
        then:
            loan.extensions.size() == 2
        and:
            with(loan.extensions[0]) {
                it.created.isEqual(now.minusDays(7).toLocalDate())
                it.days == 7
                it.interest == 12.84
            }
            with(loan.extensions[1]) {
                it.created.isEqual(now.toLocalDate())
                it.interest == 25.66
            }
    }
}
