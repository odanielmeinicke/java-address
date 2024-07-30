package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.domain.Domain;
import codes.laivy.address.ip.IPv4Address;
import codes.laivy.address.ip.IPv6Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * The {@code Host} class represents a combination of an address and an optional port.
 * It encapsulates information about a network host, which includes an address (e.g., a domain, an IP address,
 * or any other type of network identifier) and a port number. The main purpose of the {@code Host} class is to
 * provide a unified representation of network hosts, regardless of the specific type of address.
 * <p>
 * In networking, an address without a port does not fully specify a destination for communication,
 * as many services can be running on the same address (host) on different ports. The {@code Host} class
 * provides a way to associate an {@link Address} with a {@link Port}, ensuring that the destination can be
 * uniquely identified.
 *
 * <h2>Purpose and Usage</h2>
 * The {@code Host} class abstracts the concept of a network host, which can be used in various networking
 * contexts. By separating the concepts of address and port, it allows developers to work with network destinations
 * more flexibly and efficiently. This class supports different types of addresses (such as domains and IP addresses)
 * and provides methods for creating, validating, and manipulating host representations.
 *
 * <h2>Example</h2>
 * Here is an example of how to create a {@code Host} instance using a domain name and a port:
 * <pre>{@code
 * Address address = Domain.parse("example.com");
 * Port port = new Port(8080);
 * Host host = new Host(address, port);
 * }</pre>
 *
 * <h2>Note</h2>
 * The {@code Host} class does not impose any restrictions on the type of address. This allows the address to be
 * of any type that implements the {@link Address} interface, such as domain names, IP addresses, or even custom
 * implementations.
 * <p>
 * The {@code Host} class implements {@link Serializable} and {@link Cloneable}, allowing instances to be
 * serialized and cloned if needed.
 */
public class Host implements Serializable, Cloneable {

    // Static initializers

    /**
     * Validates a string representation of a host.
     * This method checks if the provided string conforms to a valid host format, which includes an optional port.
     * The format and validation criteria depend on the type of address inferred from the string.
     *
     * <h3>Validation Rules</h3>
     * <ul>
     *   <li>If the address type is {@link IPv6Address}, the string should include brackets ("[") and ("]"),
     *       and it must end with a valid port if a port is specified.</li>
     *   <li>If the address type is {@link Domain} or {@link IPv4Address}, the string should either be a single
     *       address or an address followed by a valid port separated by a colon (":").</li>
     * </ul>
     *
     * @param string the string representation of the host to validate
     * @return {@code true} if the string is a valid host, {@code false} otherwise
     */
    public static boolean validate(@NotNull String string) {
        @Nullable Class<? extends Address> type = Address.getType(string);

        if (type != null) {
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
        } else {
            return false;
        }
    }

    /**
     * Parses a string representation of a host and returns a {@code Host} instance.
     * This method extracts the address and optional port from the string and creates a {@code Host} object.
     * The address type is inferred from the string, and the port is parsed if specified.
     *
     * <h3>Parsing Rules</h3>
     * <ul>
     *   <li>If the address type is {@link IPv6Address}, the string should include brackets ("[") and ("]"),
     *       and the port (if present) should be extracted after the last colon (":").</li>
     *   <li>If the address type is {@link Domain} or {@link IPv4Address}, the string may include a port
     *       after a colon (":").</li>
     * </ul>
     *
     * @param string the string representation of the host to parse
     * @return a {@code Host} instance representing the parsed host
     * @throws IllegalArgumentException if the string cannot be parsed as a valid host
     * @throws UnsupportedOperationException if the address type is unsupported
     */
    public static @NotNull Host parse(@NotNull String string) {
        @Nullable Class<? extends Address> type = Address.getType(string);

        if (type != null) { // Validate address
            @NotNull Address address;
            @Nullable Port port = null;

            if (type == IPv6Address.class) {
                if (string.contains("[") && string.contains("]")) {
                    @NotNull String[] parts = string.split(":");
                    port = Port.parse(parts[parts.length - 1]);
                }

                address = IPv6Address.parse(string);
            } else if (type == Domain.class || type == IPv4Address.class) {
                @NotNull String[] parts = string.split(":");

                if (parts.length == 2) {
                    port = Port.parse(parts[parts.length - 1]);
                }

                address = type == Domain.class ? Domain.parse(string) : IPv4Address.parse(string);
            } else {
                throw new UnsupportedOperationException("Unsupported address type '" + type.getName() + "'");
            }

            return new Host(address, port);
        } else {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as a valid host");
        }
    }

    /**
     * Creates a new {@code Host} instance with the specified address and optional port.
     * This static factory method provides a convenient way to instantiate a {@code Host} object
     * without directly using the constructor.
     * It ensures that the address and port are properly
     * assigned to the new {@code Host} instance.
     *
     * <p>The method takes an {@link Address} and an optional {@link Port}. If the port is {@code null},
     * the resulting {@code Host} will not have a specific port assigned.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Address address = new IPv4Address("192.168.1.1");
     * Port port = new Port(8080);
     * Host host = Host.create(address, port);
     * }</pre>
     *
     * @param address the address to associate with the host, cannot be {@code null}
     * @param port the port to associate with the host, can be {@code null} if no specific port is required
     * @return a new {@code Host} instance with the specified address and port
     */
    public static @NotNull Host create(@NotNull Address address, @Nullable Port port) {
        return new Host(address, port);
    }

    // Object

    private final @NotNull Address address;
    private final @Nullable Port port;

    /**
     * Constructs a {@code Host} instance with the specified address and optional port.
     *
     * @param address the address associated with this host, cannot be {@code null}
     * @param port the port associated with this host, can be {@code null} if not specified
     */
    protected Host(@NotNull Address address, @Nullable Port port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Returns the address associated with this host.
     * This method returns a non-null address object, which represents the network location of the host.
     * The address can be of various types, depending on the specific implementation and use case.
     *
     * <p>The return type is a generic type parameter {@code <T>}, which extends {@link Address}. This allows the
     * method to return an instance of a specific subclass of {@code Address}, providing type safety and flexibility
     * in handling different types of addresses.</p>
     *
     * @param <T> the type of the address, extending {@link Address}
     * @return the address of this host
     */
    public <T extends Address> @NotNull T getAddress() {
        //noinspection unchecked
        return (T) address;
    }

    /**
     * Returns the port associated with this host, if any.
     * The port is used to specify a specific service or endpoint on the host. It can be {@code null} if the host
     * does not have a specific port or if the port is unspecified.
     *
     * @return the port associated with this host, or {@code null} if no port is specified
     */
    public @Nullable Port getPort() {
        return port;
    }

    // Implementations

    /**
     * Compares this host to another object for equality.
     * Two hosts are considered equal if they have the same address and the same port.
     *
     * @param object the object to compare with this host
     * @return {@code true} if this host is equal to the specified object, {@code false} otherwise
     */
    @Override
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Host)) return false;
        @NotNull Host that = (Host) object;
        return Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getPort(), that.getPort());
    }

    /**
     * Returns a hash code value for this host.
     * The hash code is calculated based on the address and port, allowing for efficient storage
     * and retrieval in hash-based collections.
     *
     * @return a hash code value for this host
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getAddress(), getPort());
    }

    /**
     * Returns a string representation of this host.
     * The string representation includes the address and the port (if specified), in the format
     * "{@code address:port}". If the port is {@code null}, only the address is included.
     *
     * @return a string representation of this host
     */
    @Override
    public final @NotNull String toString() {
        if (getPort() != null) {
            return getAddress().toString(getPort());
        } else {
            return getAddress().toString();
        }
    }

    // Clones

    /**
     * Creates a new host instance with the same address but with a specified port.
     *
     * @param newPort the new port to use, can be {@code null} to keep the existing port
     * @return a new {@link Host} instance with the specified port
     */
    public @NotNull Host clone(@Nullable Port newPort) {
        return new Host(getAddress(), newPort);
    }

    /**
     * Creates a deep copy of this host instance.
     *
     * @return a new {@code Host} instance that is a clone of this one
     * @throws RuntimeException if cloning is not supported
     */
    @Override
    public @NotNull Host clone() {
        try {
            return (Host) super.clone();
        } catch (@NotNull CloneNotSupportedException e) {
            throw new RuntimeException("Cannot clone host", e);
        }
    }
}