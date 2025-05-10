package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.domain.Domain;
import codes.laivy.address.http.HttpAddress;
import codes.laivy.address.ip.IPv4Address;
import codes.laivy.address.ip.IPv6Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an HTTP host, which is a specialized form of {@link Host} that handles HTTP-specific
 * addresses such as domain names, IPv4, and IPv6 addresses. This class provides functionality for
 * validating and parsing host strings commonly found in HTTP URLs.
 * <p>
 * An {@code HttpHost} consists of an {@link HttpAddress} and an optional {@link Port}, and is
 * typically used to represent network endpoints in the context of web protocols like HTTP and HTTPS.
 * It supports parsing host strings with or without protocol prefixes ("http://" or "https://"), and
 * may include ports or paths. This class simplifies handling of raw HTTP host strings and provides
 * validation mechanisms to ensure correct formatting and compliance with web standards.
 * <p>
 * Supported address types include:
 * <ul>
 *     <li>{@link Domain} - e.g., "example.com"</li>
 *     <li>{@link IPv4Address} - e.g., "192.168.0.1"</li>
 *     <li>{@link IPv6Address} - e.g., "[2001:db8::1]"</li>
 * </ul>
 */
public class HttpHost extends Host {

    /**
     * Validates whether the given string is a well-formed HTTP or HTTPS host.
     * <p>
     * This method accepts strings with "http://" or "https://" prefixes, optionally followed by
     * a path (which is removed for validation purposes). It determines whether the address component
     * represents a valid {@link HttpAddress}, and, if a port is present, verifies its validity using
     * {@link Port#validate(String)}.
     *
     * @param string the host string to validate, potentially including protocol and path.
     * @return {@code true} if the host is valid for HTTP or HTTPS, {@code false} otherwise.
     */
    public static boolean validate(@NotNull String string) {
        // HTTP or HTTPs protocols
        if (string.toLowerCase().startsWith("http://")) {
            string = string.substring(7);
        } else if (string.toLowerCase().startsWith("https://")) {
            string = string.substring(8);
        }

        // Remove the path
        if (string.endsWith("/")) {
            string = string.substring(0, string.indexOf('/'));
        }

        // Verify the host type
        @Nullable Class<? extends Address> type = Address.getType(string);

        if (type == null || !HttpAddress.class.isAssignableFrom(type)) {
            return false;
        }

        // Parse
        if (type == IPv6Address.class) {
            if (string.contains("[") && string.contains("]")) {
                @NotNull String[] parts = string.split(":");
                return Port.validate(parts[parts.length - 1]);
            } else {
                return true;
            }
        } else if (type == Domain.class || type == IPv4Address.class) {
            @NotNull String[] parts = string.split(":");
            return parts.length == 1 || (parts.length == 2 && Port.validate(parts[parts.length - 1]));
        } else {
            return false;
        }
    }

    /**
     * Parses a string representation of an HTTP or HTTPS host into an {@link HttpHost} instance.
     * <p>
     * This method processes input strings with optional protocol prefixes ("http://" or "https://")
     * and removes trailing paths. It determines the type of the address (IPv4, IPv6, or domain),
     * validates the format, and parses the corresponding {@link HttpAddress} and optional {@link Port}.
     * <p>
     * IPv6 addresses enclosed in square brackets (e.g., "[::1]:8080") are correctly parsed to separate
     * the address from the port. If the address type is not a valid HTTP address, or if parsing fails,
     * exceptions are thrown to indicate the error.
     *
     * @param string the string to parse, typically a URL host portion with or without protocol and port.
     * @return a fully constructed {@link HttpHost} representing the parsed host and port.
     * @throws IllegalArgumentException if the string does not contain a valid HTTP address.
     * @throws UnsupportedOperationException if the address type is unsupported for HTTP.
     */
    public static @NotNull HttpHost parse(@NotNull String string) {
        // Parse protocols
        if (string.toLowerCase().startsWith("http://")) {
            string = string.substring(7);
        } else if (string.toLowerCase().startsWith("https://")) {
            string = string.substring(8);
        }

        // Remove the path
        if (string.contains("/")) {
            string = string.substring(0, string.indexOf('/'));
        }

        // Type
        @Nullable Class<? extends Address> type = Address.getType(string);

        if (type == null) {
            throw new IllegalArgumentException("this host doesn't have a valid address '" + string + "'");
        } else if (!HttpAddress.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("the detected address doesn't represent an http address '" + type.getName() + "'");
        }

        // Retrieve port
        @NotNull HttpAddress address;
        @Nullable Port port = null;

        if (type == IPv6Address.class) {
            address = IPv6Address.parse(string);

            if (string.startsWith("[") && string.contains("]:")) { // Has port
                port = Port.parse(string.substring(string.indexOf("]:") + 2));
            }

            return new HttpHost(address, port);
        } else if (type == IPv4Address.class) {
            address = IPv4Address.parse(string);
        } else if (type == Domain.class) {
            address = Domain.parse(string);
        } else {
            throw new UnsupportedOperationException("unsupported address type '" + type + "'");
        }

        if (string.contains(":")) {
            @NotNull String[] parts = string.split(":");
            if (parts.length == 2) port = Port.parse(parts[1]);
        }

        return new HttpHost(address, port);
    }

    // Object

    /**
     * Constructs a new {@link HttpHost} instance with the specified {@link HttpAddress} and optional {@link Port}.
     * <p>
     * This constructor is intended for manual instantiation of an HTTP host when the address and port
     * components are already available and validated.
     *
     * @param address the HTTP address of this host, must not be {@code null}.
     * @param port    the port of this host, may be {@code null} to indicate default or unspecified port.
     */
    public HttpHost(@NotNull HttpAddress address, @Nullable Port port) {
        super(address, port);
    }

    // Getters

    /**
     * Returns the address of this host, cast to {@link HttpAddress}.
     * <p>
     * This override ensures that the address returned is specifically an {@link HttpAddress},
     * reflecting the HTTP-specific nature of this host.
     *
     * @return the non-null {@link HttpAddress} of this host.
     */
    @Override
    public @NotNull HttpAddress getAddress() {
        return (HttpAddress) super.getAddress();
    }

}