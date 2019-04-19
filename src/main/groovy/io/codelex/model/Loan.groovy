package io.codelex.model

import java.time.LocalDate

class Loan {
    String id
    LoanStatus status
    LocalDate created
    LocalDate dueDate
    BigDecimal principal
    BigDecimal interest
    BigDecimal total
    List<Extension> extensions
}
