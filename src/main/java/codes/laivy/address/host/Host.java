package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * A {@code Host} represents a combination of an address and a port.
 * It encapsulates information about a network host, which includes an address (e.g., a domain, an IP address,
 * or any other type of network identifier) and an optional port number. The main purpose of the {@code Host}
 * interface is to provide a unified representation of network hosts, regardless of the specific type of address.
 * <p>
 * In networking, an address without a port does not fully specify a destination for communication,
 * as many services can be running on the same address (host) on different ports. The {@code Host} interface
 * provides a way to associate an {@link Address} with a {@link Port}, ensuring that the destination can be
 * uniquely identified.
 *
 * <h2>Purpose and Usage</h2>
 * The {@code Host} interface abstracts the concept of a network host, which can be used in various networking
 * contexts. By separating the concepts of address and port, it allows developers to work with network destinations
 * more flexibly and efficiently.
 *
 * <h2>Example</h2>
 * Here is an example of how to create a {@code Host} instance using a domain name and a port:
 * <pre>{@code
 * Address address = Domain.parse("example.com");
 * Port port = new Port(8080);
 * Host host = Host.create(address, port);
 * }</pre>
 *
 * <h2>Note</h2>
 * The {@code Host} interface does not impose any restrictions on the type of address. This allows the address to be
 * of any type that implements the {@link Address} interface, such as domain names, IP addresses, or even custom
 * implementations.
 */
public interface Host extends Serializable, Cloneable {

    /**
     * Creates a new {@link Host} instance with the specified address and port.
     * This static method serves as a factory method to create an anonymous implementation of the {@code Host}
     * interface. It associates the provided address with the given port, allowing the resulting {@code Host} object
     * to represent a complete network destination.
     *
     * <h2>Parameters</h2>
     * <ul>
     *   <li>{@code address} - the address of the host, which must not be null. This can be a domain name,
     *   an IP address, or any other type of address that implements {@link Address}.</li>
     *   <li>{@code port} - the port associated with the address, which can be null if not applicable.</li>
     * </ul>
     *
     * <h2>Returns</h2>
     * A new {@code Host} instance representing the combination of the provided address and port.
     *
     * <h2>Example</h2>
     * <pre>{@code
     * IPAddress ip = new IPAddress("192.168.1.1");
     * Port port = new Port(80);
     * Host host = Host.create(ip, port);
     * }</pre>
     *
     * @param address the address of the host must not be null
     * @param port the port associated with the address, or null if not specified
     * @return a new {@link Host} instance
     * @throws IllegalArgumentException if the address is null
     */
    static @NotNull Host create(
            final @NotNull Address address,
            final @Nullable Port port
    ) {
        return new Host() {

            /**
             * Returns the address associated with this host.
             * The address could represent various types of network identifiers, such as a domain name,
             * an IP address, or another type of address, depending on the implementation.
             *
             * @param <T> the type of the address, extending {@link Address}
             * @return the address of this host
             */
            @Override
            public <T extends Address> @NotNull T getAddress() {
                //noinspection unchecked
                return (T) address;
            }

            /**
             * Returns the port associated with this host, if any.
             * The port represents the entry point for network communication at the given address.
             * It can be null if no specific port is associated with the address.
             *
             * @return the port associated with this host, or null if no port is specified
             */
            @Override
            public @Nullable Port getPort() {
                return port;
            }

            /**
             * Returns a new clone of this current host with the same address and port
             *
             * @return this same host with the same address and same port
             */
            @Override
            public @NotNull Host clone() {
                try {
                    return (Host) super.clone();
                } catch (@NotNull CloneNotSupportedException e) {
                    throw new RuntimeException("cannot clone host", e);
                }
            }

            /**
             * Compares this host to another object for equality.
             * Two hosts are considered equal if they have the same address and the same port.
             *
             * @param object the object to compare with this host
             * @return {@code true} if this host is equal to the specified object, {@code false} otherwise
             */
            @Override
            public boolean equals(@Nullable Object object) {
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
            public int hashCode() {
                return Objects.hash(getAddress(), getPort());
            }

            /**
             * Returns a string representation of this host.
             * The string representation includes the address and the port (if specified), in the format
             * "{@code address:port}". If the port is null, only the address is included.
             *
             * @return a string representation of this host
             */
            @Override
            public @NotNull String toString() {
                if (getPort() != null) {
                    return getAddress().toString(getPort());
                } else {
                    return getAddress().toString();
                }
            }
        };
    }

    /**
     * Returns the address associated with this host.
     * This method should return a non-null address object, which represents the network location of the host.
     * The address can be of various types, depending on the specific implementation and use case.
     *
     * <p>The return type is a generic type parameter {@code <T>}, which extends {@link Address}. This allows the
     * method to return an instance of a specific subclass of {@code Address}, providing type safety and flexibility
     * in handling different types of addresses.</p>
     *
     * @param <T> the type of the address, extending {@link Address}
     * @return the address of this host
     */
    <T extends Address> @NotNull T getAddress();

    /**
     * Returns the port associated with this host, if any.
     * The port is used to specify a specific service or endpoint on the host. It can be null if the host
     * does not have a specific port or if the port is unspecified.
     *
     * @return the port associated with this host, or null if no port is specified
     */
    @Nullable Port getPort();

    // Utilities

    /**
     * Checks if the host has a port specified.
     *
     * @return {@code true} if a port is specified, {@code false} otherwise
     */
    default boolean hasPort() {
        return getPort() != null;
    }

    // Clones

    /**
     * Creates a new host instance with the same address but with a specified port.
     *
     * @param newPort the new port to use
     * @return a new {@link Host} instance with the specified port
     */
    default @NotNull Host clone(@Nullable Port newPort) {
        return Host.create(getAddress(), newPort);
    }
    @NotNull Host clone();

}
