package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.domain.Domain;
import codes.laivy.address.http.HttpAddress;
import codes.laivy.address.ip.IPv4Address;
import codes.laivy.address.ip.IPv6Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an HTTP host that contains an {@link HttpAddress}, an optional {@link Port}, and an optional {@link Protocol}.
 * This class extends {@link Host} and is designed to handle HTTP-related network addresses, including the HTTP and HTTPS protocols.
 * <p>
 * An HTTP host is defined as a combination of:
 * <ul>
 *     <li>An {@link HttpAddress}, which can represent an IPv4, IPv6, or domain address.
 *     <li>An optional {@link Port} that specifies the port used by the HTTP host.
 *     <li>An optional {@link Protocol} which indicates whether the host uses HTTP or HTTPS.
 * </ul>
 * This class provides methods to validate and parse HTTP host strings, making it useful for applications
 * handling HTTP connections. It also includes methods to determine whether the host is secure (i.e., using HTTPS).
 *
 * @param <T> the type of {@link HttpAddress}, constrained to subclasses of {@link HttpAddress}
 */
public class HttpHost<T extends HttpAddress> extends Host<T> {

    // Static initializers

    /**
     * Validates whether the given string represents a valid HTTP host.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Checks for the presence of HTTP or HTTPS protocol prefixes. If present, they are removed.
     *     <li>Removes any path components from the host string.
     *     <li>Identifies the type of address (IPv4, IPv6, or domain) using {@link Address#getType(String)}.
     *     <li>For IPv6 addresses, validates the presence of brackets and port numbers.
     *     <li>For domain or IPv4 addresses, checks whether a valid port is specified, if any.
     * </ol>
     * The method returns {@code true} if the string represents a valid HTTP host, or {@code false} otherwise.
     *
     * @param string the string to validate as an HTTP host
     * @return {@code true} if the string is a valid HTTP host, {@code false} otherwise
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
     * Parses a string into an {@link HttpHost} instance, extracting the address, port, and protocol.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Identifies and removes the HTTP or HTTPS protocol, if present.
     *     <li>Removes any path components from the string.
     *     <li>Determines the type of address (IPv4, IPv6, or domain) using {@link Address#getType(String)}.
     *     <li>Parses the {@link HttpAddress} and optional {@link Port}.
     * </ol>
     * If the string does not represent a valid HTTP host, an {@link IllegalArgumentException} is thrown.
     *
     * @param string the string to parse as an HTTP host
     * @return an {@link HttpHost} instance representing the parsed HTTP host
     * @throws IllegalArgumentException if the string does not represent a valid HTTP host
     */
    public static @NotNull HttpHost<?> parse(@NotNull String string) {
        // Parse protocols
        @Nullable Protocol protocol = null;

        if (string.toLowerCase().startsWith("http://")) {
            string = string.substring(7);
            protocol = Protocol.HTTP;
        } else if (string.toLowerCase().startsWith("https://")) {
            string = string.substring(8);
            protocol = Protocol.HTTPS;
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

            return new HttpHost<>((IPv6Address) address, port, protocol);
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

        return new HttpHost<>(address, port, protocol);
    }

    // Object

    private final @Nullable Protocol protocol;

    /**
     * Constructs a new {@code HttpHost} with the specified address, port, and protocol.
     * <p>
     * The address must be a subclass of {@link HttpAddress}, while the port and protocol are optional.
     *
     * @param address  the HTTP address of the host, must not be {@code null}
     * @param port     the port of the host, may be {@code null}
     * @param protocol the protocol of the host (HTTP or HTTPS), may be {@code null}
     */
    public HttpHost(@NotNull T address, @Nullable Port port, @Nullable Protocol protocol) {
        super(address, port);
        this.protocol = protocol;
    }

    // Getters

    /**
     * Returns the name representation without the protocol of this http host
     *
     * @return the name without protocol
     */
    @Override
    public @NotNull String getName() {
        return super.toString();
    }

    /**
     * Returns the {@link Protocol} of this HTTP host, which may be {@code null}.
     * The protocol can be null if the host does not have an explicit "http://" or "https://".
     *
     * @return the protocol, or {@code null} if not explicitly set
     */
    public @Nullable Protocol getProtocol() {
        return protocol;
    }

    /**
     * Checks if the protocol used by this host is secure (HTTPS).
     *
     * @return {@code true} if the protocol is HTTPS, {@code false} otherwise
     */
    public boolean isSecure() {
        return getProtocol() != null && getProtocol() == Protocol.HTTPS;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof HttpHost)) return false;
        if (!super.equals(object)) return false;
        @NotNull HttpHost<?> httpHost = (HttpHost<?>) object;
        return getProtocol() == httpHost.getProtocol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getProtocol());
    }

    @Override
    public @NotNull String toString() {
        return (getProtocol() != null ? getProtocol().toString() : "") + super.toString();
    }

    @Override
    public @NotNull HttpHost<T> clone() {
        return (HttpHost<T>) super.clone();
    }

    @Override
    public @NotNull HttpHost<T> clone(@Nullable Port newPort) {
        //noinspection unchecked
        return new HttpHost<>((T) getAddress().clone(), newPort, getProtocol());
    }

    // Classes

    /**
     * Enum representing HTTP protocols, either HTTP or HTTPS.
     */
    public enum Protocol {

        HTTP("http://"),
        HTTPS("https://");

        private final @NotNull String representation;

        Protocol(@NotNull String representation) {
            this.representation = representation;
        }

        @Override
        public @NotNull String toString() {
            return representation;
        }
    }

}