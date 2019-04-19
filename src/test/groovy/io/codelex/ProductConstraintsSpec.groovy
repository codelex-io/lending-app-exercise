package io.codelex

import org.springframework.beans.factory.annotation.Autowired

class ProductConstraintsSpec extends BaseSpecification {
    @Autowired
    ProductConstraintsApi productConstraintsApi

    def 'should fetch constraints'() {
        when:
            def constraints = productConstraintsApi.fetchConstraints()
        then:
            constraints.minAmount == 100.0
            constraints.maxAmount == 500.0
            constraints.minTermDays == 7
            constraints.maxTermDays == 30
            constraints.minExtensionDays == 7
            constraints.maxExtensionDays == 30
    }
}
