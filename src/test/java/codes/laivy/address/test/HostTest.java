package codes.laivy.address.test;

import codes.laivy.address.host.Host;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.stream.Stream;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class HostTest {

    // Static initializers

    public static @NotNull String @NotNull [] valids() {
        return Stream.of(DomainTest.valids, IPv4AddressTest.valids, IPv6AddressTest.valids).flatMap(Arrays::stream).toArray(String[]::new);
    }

    // Object

    @Test
    @Order(value = 0)
    void validator() {
        for (@NotNull String string : valids()) {
            Assertions.assertTrue(Host.validate(string), "cannot validate host with address '" + string + "'");
        }
    }
    @Test
    @Order(value = 1)
    void parser() {
        for (@NotNull String string : valids()) {
            try {
                @NotNull Host host = Host.parse(string);
                Assertions.assertEquals(host, Host.parse(host.toString()), "the host string '" + string + "' has been parsed into a different host '" + Host.parse(host.toString()) + "'");
            } catch (@NotNull Throwable throwable) {
                throw new IllegalArgumentException("cannot parse '" + string + "' as a valid host", throwable);
            }
        }
    }

    @Test
    void string() {
        for (@NotNull String string : valids()) {
            @NotNull Host host = Host.parse(string);
            @NotNull Host clone = Host.parse(host.toString());

            Assertions.assertEquals(host, clone);
        }
    }
    @Test
    void name() {
        for (@NotNull String string : valids()) {
            @NotNull Host host = Host.parse(string);
            @NotNull Host clone = Host.parse(host.toString());

            Assertions.assertEquals(host.toString(), clone.toString());
        }
    }

}
