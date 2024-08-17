package codes.laivy.address.test;

import codes.laivy.address.host.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.stream.Stream;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class HttpHostTest {

    // Static initializers

    public static @NotNull String @NotNull [] valids() {
        @NotNull String[] valids = new String[] {
                "https://localhost/",
                "http://www.example.com/path",
                "http://www.example.com",
                "http://www.example.com:80/",
                "localhost/path",
                "https://localhost",
                "https://localhost:80/",
                "https://metadotis.com/path/cool",
                "https://metadotis.com:5555/path/cool",
                "https://2001:0db8:85a3::8a2e:370:7334/path/cool",
                "https://[2001:0db8:85a3::8a2e:370:7334]/path/cool",
                "https://[2001:0db8:85a3::8a2e:370:7334]:8080/path/cool",
                "https://192.168.1.1/path/cool",
                "https://192.168.1.1:8080/path/cool"
        };

        return Stream.of(DomainTest.valids, IPv4AddressTest.valids, IPv6AddressTest.valids, valids).flatMap(Arrays::stream).toArray(String[]::new);
    }

    // Object

    @Test
    void string() {
        for (@NotNull String string : valids()) {
            @NotNull HttpHost<?> host = HttpHost.parse(string);
            @NotNull HttpHost<?> clone = HttpHost.parse(host.toString());

            Assertions.assertEquals(host, clone);
        }
    }
    @Test
    void name() {
        for (@NotNull String string : valids()) {
            @NotNull HttpHost<?> host = HttpHost.parse(string);
            @NotNull HttpHost<?> clone = HttpHost.parse(host.toString());

            Assertions.assertEquals(host.toString(), clone.toString());
        }
    }

}
