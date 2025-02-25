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
 * Represents an HTTP host, which is an extension of {@link Host} that specializes in handling
 * HTTP-specific addresses, including IPv4, IPv6, and domain names.
 * This class also provides
 * methods to parse and validate host strings in the context of HTTP protocols.
 *
 * @param <T> The type of {@link HttpAddress} this host will handle.
 */
public class HttpHost<T extends HttpAddress> extends Host<T> {

    /**
     * Validates a given string to determine whether it represents a valid HTTP or HTTPS host.
     * This method checks if the string begins with either the "http://" or "https://" prefix,
     * then removes the protocol and path to evaluate only the address and optional port.
     * It verifies that the address corresponds to a valid {@link HttpAddress}, which can be
     * either an IPv4 address, an IPv6 address, or a domain name.
     *
     * @param string the string to validate, representing an HTTP or HTTPS host.
     * @return {@code true} if the string is a valid HTTP or HTTPS host, {@code false} otherwise.
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
     * Parses a given string into an {@link HttpHost} object. This method strips the HTTP or
     * HTTPS protocol prefix, removes any path, and attempts to create a valid {@link HttpHost}
     * object by determining whether the address is an IPv4, IPv6, or domain name, with or without
     * a port. If the string contains a port, it is parsed and added to the resulting {@link HttpHost}.
     * <p>
     * If the address type is unsupported, an {@link UnsupportedOperationException} is thrown.
     *
     * @param string the string to parse, which must represent a valid HTTP or HTTPS host.
     * @return a {@link HttpHost} instance corresponding to the parsed host string.
     * @throws IllegalArgumentException if the string does not represent a valid address.
     * @throws UnsupportedOperationException if the address type is unsupported.
     */
    public static @NotNull HttpHost<?> parse(@NotNull String string) {
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

            return new HttpHost<>((IPv6Address) address, port);
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

        return new HttpHost<>(address, port);
    }

    // Object

    /**
     * Constructs a new {@link HttpHost} instance with the specified {@link HttpAddress} and
     * {@link Port}. This constructor is used to initialize an HTTP host object with an address
     * and optional port, without specifying any particular protocol.
     *
     * @param address the {@link HttpAddress} of this host, which must not be {@code null}.
     * @param port    the {@link Port} of this host, which can be {@code null}.
     */
    public HttpHost(@NotNull T address, @Nullable Port port) {
        super(address, port);
    }

    // Implementations

    /**
     * Creates a deep copy of this {@link HttpHost} instance. This method performs a deep
     * cloning of the host address and creates a new {@link HttpHost} instance with the
     * same address and port.
     *
     * @return a new {@link HttpHost} instance that is a clone of this instance.
     */
    @Override
    public @NotNull HttpHost<T> clone() {
        return (HttpHost<T>) super.clone();
    }

    /**
     * Creates a new {@link HttpHost} instance with the same address but a new specified port.
     * This method is useful when you want to change the port of an existing host without
     * modifying the address.
     *
     * @param newPort the new {@link Port} to set, which can be {@code null}.
     * @return a new {@link HttpHost} instance with the same address but the specified port.
     */
    @Override
    public @NotNull HttpHost<T> clone(@Nullable Port newPort) {
        //noinspection unchecked
        return new HttpHost<>((T) getAddress().clone(), newPort);
    }

}