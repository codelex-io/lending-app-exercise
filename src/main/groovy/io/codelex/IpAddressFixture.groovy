package io.codelex


import static org.apache.commons.lang.RandomStringUtils.randomNumeric

class IpAddressFixture {
    static String randomIpAddress() {
        return randomNumeric(3) + '.' + randomNumeric(3) + '.' + randomNumeric(3) + '.' + randomNumeric(3)
    }
}
