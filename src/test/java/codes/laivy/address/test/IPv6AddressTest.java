package codes.laivy.address.test;

import codes.laivy.address.ip.IPv6Address;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class IPv6AddressTest {

    public static final @NotNull String[] valids = new String[] {
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334",  // Example address
            "fe80::1ff:fe23:4567:890a",                 // Link-local address
            "2001:0db8:0000:0000:0000:0000:1428:57ab",  // Uni-cast address
            "2001:db8::1428:57ab",                      // Abbreviated Uni-cast address
            "2001:0db8:0000:0042:0000:8a2e:0370:7334",  // Uni-cast address
            "::1",                                      // Loop-back address
            "2001:db8::",                               // Documentation address
            "2001:db8:0:1::1",                          // Documentation address with segment
            "ff02::1",                                  // All nodes multicast address
            "ff02::2",                                  // All routers multicast address
            "ff02::5",                                  // OSPF routers multicast address
            "2001:0db8:0000:0000:0000:ff00:0042:8329",  // Uni-cast address
            "2001:db8:0:0:0:0:1428:57ab",               // Abbreviated Uni-cast address
            "2001:0db8:1234:5678:9abc:def0:1234:5678",  // Uni-cast address
            "::ffff:192.0.2.128",                       // IPv4-mapped IPv6 address
            "::ffff:c000:280",                          // Another IPv4-mapped IPv6 address
            "2001:0db8:0:0:0:0:0:1",                    // Uni-cast address
            "2001:db8:0:0:0:0:0:1",                     // Abbreviated Uni-cast address
            "2001:0db8:0:0:0:0:0:2",                    // Uni-cast address
            "fe80::1",                                  // Link-local address
            "2001:0db8:85a3::8a2e:370:7334",            // Uni-cast address
            "2001:db8:85a3:42::7334",                   // Abbreviated Uni-cast address
            "2001:db8:85a3:0:0:8a2e:370:7334",          // Uni-cast address
            "2001:db8:1234:5678::",                     // Documentation address
            "2001:db8:abcd:ef12:3456:7890:abcd:ef12",   // Uni-cast address
            "2001:db8:1234:5678:9abc:def0:1234:5678",   // Uni-cast address
            "2001:0db8:0:0:8:800:200c:417a",            // Uni-cast address
            "ff02::fb",                                 // Multicast address
            "ff02::1:ff00:42",                          // Solicited-node multicast address
            "ff02::1:ffab:cdef"                         // Solicited-node multicast address
    };

    @Test
    @Order(value = 0)
    void validator() {
        for (@NotNull String string : valids) {
            Assertions.assertTrue(IPv6Address.validate(string), "cannot validate ipv6 address '" + string + "'");
        }
    }
    @Test
    @Order(value = 1)
    void parser() {
        for (@NotNull String string : valids) {
            try {
                @NotNull IPv6Address address = IPv6Address.parse(string);
                Assertions.assertEquals(address, IPv6Address.parse(address.getName()), "the address string '" + string + "' has been parsed into a different ipv6 address '" + address + "'");
            } catch (@NotNull Throwable throwable) {
                throw new IllegalArgumentException("cannot parse '" + string + "' as a valid ipv6 address", throwable);
            }
        }
    }

    @Test
    void string() {
        for (@NotNull String string : valids) {
            @NotNull IPv6Address address = IPv6Address.parse(string);
            @NotNull IPv6Address clone = IPv6Address.parse(address.toString());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void name() {
        for (@NotNull String string : valids) {
            @NotNull IPv6Address address = IPv6Address.parse(string);
            @NotNull IPv6Address clone = IPv6Address.parse(address.getName());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void raw() {
        for (@NotNull String string : valids) {
            @NotNull IPv6Address address = IPv6Address.parse(string);
            @NotNull IPv6Address clone = IPv6Address.parse(address.getRawName());

            Assertions.assertEquals(address, clone);
        }
    }

    // Failures

    @Test
    @Order(value = 2)
    void invalidators() {
        @NotNull String[] invalids = {
                "2001:0db8:::85a3:0000:0000:8a2e:0370:7334",     // More than one "::" sequence
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:1234",  // More than 8 segments
                "2001:0db8:85a3:0000:0000:8a2e:0370:733g",       // Invalid character 'g'
                "2001:0db8:85a3:0000:0000:8a2e:0370",            // Less than 8 segments
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:0000",  // More than 8 segments
                "12001:0db8:85a3:0000:0000:8a2e:0370:7334",      // Segment with more than 4 digits ("12001")
                "2001:0db8:85a3::85a3::7334",                    // Two occurrences of "::"
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:",      // Trailing colon
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:0",     // Segment with more than 4 digits
                "2001:0db8:85a3:0000:0000:8a2e::7334:1234",      // Segment after "::" exceeds allowed limit
                "2001::0db8:85a3:0000:0000:8a2e:0370:7334",      // "::" not used correctly
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:1::1",  // "::" used incorrectly
                "2001:0db8:85a3:0000:0000:8a2e::0370:7334",      // "::" usage without zero suppression
                "2001:0db8:85a3::0000:8a2e:0370:7334::",         // "::" used more than once
                "2001:0db8:85a3:0000:0000:8a2e:0370:0000:",      // Trailing colon
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:abcd:", // Trailing colon with segment exceeding 4 characters
                "2001:0db8:85a3:0000:0000:8a2e:0370:7334:zxyx",  // Invalid characters "zxyx"
                ":2001:0db8:85a3:0000:0000:8a2e:0370:7334"       // Leading colon
        };

        for (@NotNull String invalid : invalids) {
            try {
                IPv6Address.parse(invalid);
                throw new IllegalArgumentException("the ipv6 address '" + invalid + "' isn't invalid.");
            } catch (@NotNull Throwable throwable) {
                Assertions.assertFalse(IPv6Address.validate(invalid), "cannot invalidate ipv6 address '" + invalid + "'");
            }
        }
    }

}
