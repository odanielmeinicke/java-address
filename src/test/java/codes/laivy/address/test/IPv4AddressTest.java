package codes.laivy.address.test;

import codes.laivy.address.ip.IPv4Address;
import codes.laivy.address.ip.IPv6Address;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class IPv4AddressTest {

    public static final @NotNull String[] valids = new String[] {
            "127.0.0.1",       // Loop-back (localhost)
            "0.0.0.0",         // Unspecified address
            "255.255.255.255", // Broadcast address
            "192.168.1.1",     // Private address (commonly used in local networks)
            "10.0.0.1",        // Private address (commonly used in local networks)
            "172.16.0.1",      // Private address (commonly used in local networks)
            "224.0.0.1",       // Multicast address (all hosts)
            "240.0.0.1",       // Reserved for future use
            "169.254.1.1",     // Link-local (automatic address when DHCP is unavailable)
            "203.0.113.0",     // Reserved for documentation
            "192.0.2.0",       // Reserved for documentation
            "198.51.100.0",    // Reserved for documentation
            "198.18.0.0",      // Reserved for benchmarking tests
            "192.88.99.1",     // Any-cast address for IPv6 to IPv4 relay
            "100.64.0.1",      // Reserved for carrier-grade NAT
            "8.8.8.8",         // Google public DNS
            "1.1.1.1",         // Cloudflare public DNS
            "203.0.113.1",     // Reserved for documentation (example)
            "192.0.0.1",       // Reserved address
    };

    @Test
    @Order(value = 0)
    void validator() {
        for (@NotNull String string : valids) {
            Assertions.assertTrue(IPv4Address.validate(string), "cannot validate ipv4 address '" + string + "'");
        }
    }
    @Test
    @Order(value = 1)
    void parser() {
        for (@NotNull String string : valids) {
            try {
                @NotNull IPv4Address address = IPv4Address.parse(string);
                Assertions.assertEquals(address, IPv4Address.parse(address.getName()), "the address string '" + string + "' has been parsed into a different ipv4 address '" + address.getName() + "'");
            } catch (@NotNull Throwable throwable) {
                throw new IllegalArgumentException("cannot parse '" + string + "' as a valid ipv4 address", throwable);
            }
        }
    }

    @Test
    void string() {
        for (@NotNull String string : valids) {
            @NotNull IPv4Address address = IPv4Address.parse(string);
            @NotNull IPv4Address clone = IPv4Address.parse(address.toString());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void name() {
        for (@NotNull String string : valids) {
            @NotNull IPv4Address address = IPv4Address.parse(string);
            @NotNull IPv4Address clone = IPv4Address.parse(address.getName());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void integer() {
        for (@NotNull String string : valids) {
            @NotNull IPv4Address address = IPv4Address.parse(string);
            @NotNull IPv4Address clone = IPv4Address.fromInteger(address.toInteger());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void ipv6() {
        for (@NotNull String string : valids) try {
            @NotNull IPv4Address address = IPv4Address.parse(string);
            @NotNull IPv6Address ipv6 = address.toIPv6();
            @NotNull IPv4Address clone = ipv6.toIPv4();

            Assertions.assertEquals(address, clone);
        } catch (@NotNull Throwable e) {
            throw new IllegalStateException("cannot create ipv4-mapped address using '" + string + "'", e);
        }
    }

    // Failures

    @Test
    @Order(value = 2)
    void invalidators() {
        @NotNull String[] invalids = new String[] {
                "999.999.999.999",  // All octets out of range
                "256.256.256.256",  // All octets out of range
                "123.456.78.90",    // Second octet out of range
                "192.168.1.1.1",    // Too many octets
                "192.168.1",        // Missing one octet
                "192.168..1",       // Missing octet
                "192.168.1.256",    // Fourth octet out of range
                "abc.def.ghi.jkl",  // Non-numeric characters
                "192.168.-1.1",     // Negative octet value
                "192.168.1.01",     // Leading zero in octet
                "192.168.1.1.",     // Trailing dot
                ".192.168.1.1",     // Leading dot
                "192.168.1.1/",     // Trailing slash
                "192,168,1,1",      // Commas instead of dots
                "192 168 1 1",      // Spaces instead of dots
                "192.168.1.one",    // Mixed numeric and alphabetic characters
                "192.168.1. 1",     // Space within an octet
                "192.168.1..1",     // Double dot
                " 192.168.1.1",     // Leading space
                "192.168.1.1 ",     // Trailing space
                "example.com",      // Domain Name
        };

        for (@NotNull String invalid : invalids) {
            try {
                IPv4Address.parse(invalid);
                throw new IllegalArgumentException("the ipv4 address '" + invalid + "' isn't invalid.");
            } catch (@NotNull Throwable throwable) {
                Assertions.assertFalse(IPv4Address.validate(invalid), "cannot invalidate ipv4 address '" + invalid + "'");
            }
        }
    }

}
