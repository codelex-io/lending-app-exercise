package io.codelex

import org.springframework.stereotype.Component

import static io.codelex.IpAddressFixture.randomIpAddress
import static java.util.Optional.ofNullable

@Component
class IpAddressContext {
    private String ipAddress

    void setIpAddressForRequest(String ipAddress) {
        this.ipAddress = ipAddress
    }

    String getIpAddressForRequest() {
        return ofNullable(ipAddress).orElse(randomIpAddress())
    }
}
