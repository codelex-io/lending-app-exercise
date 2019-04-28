package io.codelex

import io.codelex.model.AuthRequest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static io.codelex.CustomerFixture.*
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.FORBIDDEN

class CustomerSpec extends BaseSpecification {
    @Autowired
    AuthenticationApi authenticationApi

    @Autowired
    LoanApi loanApi

    @Autowired
    TestApi testApi

    def now = LocalDate.of(2019, 1, 1).atTime(9, 0)

    void setup() {
        testApi.setTimeFixedAt(now)
        testApi.clearDatabase()
    }

    def 'customer should be able to register and access his account'() {
        given:
            def req = authRequest()
            def sessionId = authenticationApi.register(req)
        when:
            def loans = loanApi.fetchLoans(sessionId)
        then:
            loans.empty
    }

    def 'customer should be able to sign in'() {
        given:
            def req = authRequest()
            authenticationApi.register(req)
            def sessionId = authenticationApi.signIn(req)
        when:
            def loans = loanApi.fetchLoans(sessionId)
        then:
            loans.empty
    }

    def 'customer should be able to sign out'() {
        given:
            def req = authRequest()
            def sessionId = authenticationApi.register(req)
        when:
            authenticationApi.signOut(sessionId)
        then:
            noExceptionThrown()
    }

    @Unroll
    def 'should not be possible to register with same email=#email twice'() {
        given:
            def req = authRequest().tap { it.email = 'customer@codelex.io' }
            authenticationApi.register(req)
        when:
            authenticationApi.register(req.tap { it.email = email })
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            email << [
                    'customer@codelex.io',
                    ' customer@codelex.io',
                    'customer@codelex.io ',
                    ' customer@codelex.io ',
                    'Customer@codelex.io',
                    'customer@codelex.IO'
            ]
    }

    def 'should not be possible to sign in with wrong password'() {
        given:
            def req = authRequest()
            authenticationApi.register(req)
        when:
            authenticationApi.signIn(req.tap { password = randomPassword() })
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
    }

    def 'should not be possible to access loans with no authentication'() {
        when:
            loanApi.fetchLoans(randomSessionId())
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == FORBIDDEN
    }

    def 'should not be possible to apply with no authentication'() {
        when:
            loanApi.applyForLoan(randomSessionId(), 500.0, 30)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == FORBIDDEN
    }

    def 'should not be possible to extend loan with no authentication'() {
        when:
            loanApi.extendLoan(randomSessionId(), '123', 7)
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == FORBIDDEN
    }

    @Unroll
    def 'should not be able to register with invalid email=#email'() {
        when:
            authenticationApi.register(new AuthRequest(email: email, password: PASSWORD))
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            email << [null, '', ' ', 'codelex @codelex.io', '@codelex.io']
    }

    @Unroll
    def 'should not be able to sign in with invalid email=#email'() {
        when:
            authenticationApi.signIn(new AuthRequest(email: email, password: PASSWORD))
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            email << [null, '', ' ', 'codelex @codelex.io', '@codelex.io']
    }

    @Unroll
    def 'should not be able to register with invalid password'() {
        when:
            def req = authRequest()
            authenticationApi.register(req.tap { it.password = password })
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            password << [null, '', ' ', '123']
    }

    @Unroll
    def 'should not be able to sign in with invalid password'() {
        when:
            def req = authRequest()
            authenticationApi.register(req.tap { it.password = password })
        then:
            def e = thrown InvalidStatusException
            e.httpStatus == BAD_REQUEST
        where:
            password << [null, '', ' ', '123']
    }
}
