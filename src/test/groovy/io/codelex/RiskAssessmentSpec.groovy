package io.codelex

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static io.codelex.CustomerFixture.authRequest
import static io.codelex.IpAddressFixture.randomIpAddress
import static io.codelex.model.ApplicationStatus.APPROVED
import static io.codelex.model.ApplicationStatus.REJECTED

class RiskAssessmentSpec extends BaseSpecification {
    @Autowired
    AuthenticationApi authenticationApi

    @Autowired
    LoanApi loanApi

    @Autowired
    ProductConstraintsApi productConstraintsApi

    @Autowired
    TestApi testApi

    @Autowired
    IpAddressContext ipAddressContext

    @Unroll
    def 'should not be able to apply at night with max amount'() {
        given:
            def now = LocalDate.of(2019, 1, 1).atTime(hour, 0)
            testApi.setTimeFixedAt(now)
            def constraints = productConstraintsApi.fetchConstraints()
            def sessionId = authenticationApi.register(authRequest())
        when:
            def application = loanApi.applyForLoan(sessionId, constraints.maxAmount, constraints.maxTermDays)
        then:
            application.status == expectedStatus
        where:
            hour || expectedStatus
            20   || APPROVED
            21   || REJECTED
            0    || REJECTED
            4    || REJECTED
            5    || APPROVED
            10   || APPROVED
            14   || APPROVED
    }

    def 'should not be able to apply if there were three or more applications from the same ip address'() {
        given:
            def ipAddress = randomIpAddress()
            ipAddressContext.setIpAddressForRequest(ipAddress)
        and:
            def now = LocalDate.of(2019, 1, 1).atTime(12, 0)
            testApi.setTimeFixedAt(now)
        and:
            def sessionId1 = authenticationApi.register(authRequest())
            loanApi.applyForLoan(sessionId1, 500.0, 30)
        and:
            def sessionId2 = authenticationApi.register(authRequest())
            loanApi.applyForLoan(sessionId2, 500.0, 30)
        and:
            now = LocalDate.of(2019, 1, 2).atTime(0, 1)
            testApi.setTimeFixedAt(now)
        and:
            def sessionId3 = authenticationApi.register(authRequest())
            loanApi.applyForLoan(sessionId3, 500.0, 30)
        and:
            def sessionId4 = authenticationApi.register(authRequest())
            def application = loanApi.applyForLoan(sessionId4, 500.0, 30)
        expect:
            application.status == REJECTED
    }
}
