package io.codelex

import com.fasterxml.jackson.core.type.TypeReference
import io.codelex.model.Application
import io.codelex.model.ApplyForLoan
import io.codelex.model.Loan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

import static io.codelex.RestTemplateConfiguration.mapper
import static io.codelex.SessionIdHandler.headers
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpStatus.OK

@Component
class LoanApi {
    @Autowired
    TestRestTemplate publicApiTemplate

    @Autowired
    IpAddressContext ipAddressContext

    List<Loan> fetchLoans(String sessionId) {
        def response = publicApiTemplate.exchange(
                '/loans',
                GET,
                new HttpEntity<>(headers(sessionId)),
                String
        )
        if (response.statusCode != OK) {
            throw new InvalidStatusException(response.statusCode)
        }
        return mapper.readValue(response.body, new TypeReference<List<Loan>>() {})
    }

    Application applyForLoan(String sessionId, BigDecimal amount, Integer termDays) {
        def headers = headers(sessionId)
        headers.set('X-FORWARDED-FOR', ipAddressContext.ipAddressForRequest)
        def response = publicApiTemplate.exchange(
                '/loans/apply',
                POST,
                new HttpEntity<>(new ApplyForLoan(amount: amount, days: termDays), headers),
                String
        )
        if (response.statusCode != OK) {
            throw new InvalidStatusException(response.statusCode)
        }
        return mapper.readValue(response.body, Application)
    }

    Loan extendLoan(String sessionId, String loanId, Integer days) {
        def uri = UriComponentsBuilder.fromPath('/loans/')
                .path(loanId)
                .path('/extend')
                .queryParam('days', days)
                .build().toUriString()
        def response = publicApiTemplate.exchange(
                uri,
                POST,
                new HttpEntity<>(headers(sessionId)),
                String
        )
        if (response.statusCode != OK) {
            throw new InvalidStatusException(response.statusCode)
        }
        return mapper.readValue(response.body, Loan)
    }
}
