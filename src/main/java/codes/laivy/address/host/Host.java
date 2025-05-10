package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an immutable network host, defined by an {@link Address} and an optional {@link Port}.
 * <p>
 * This class serves as a fundamental component for representing network endpoints. A {@code Host}
 * encapsulates the information required to identify a destination or source in a network communication
 * context, including a non-null address and an optional port.
 *
 * <h2>Immutability and Thread Safety</h2>
 * The {@code Host} class is immutable: once constructed, its state cannot be changed. Both the address
 * and port are declared as {@code final} and are set during object construction. This design guarantees
 * thread safety without requiring synchronization, making {@code Host} instances safe to share across
 * threads.
 *
 * <h2>Structure</h2>
 * A host is composed of:
 * <ul>
 *     <li>A non-null {@link Address}, representing the destination or source address.
 *     <li>An optional {@link Port}, which may be {@code null} if the port is unspecified.
 * </ul>
 *
 * <h2>Usage</h2>
 * Hosts are typically used to abstract the endpoint of a network connection, where the address defines
 * the target and the port defines the access point. This abstraction supports various use cases such as
 * connection management, endpoint serialization, and display formatting.
 *
 * <h2>Serialization</h2>
 * This class implements {@link Serializable}, allowing instances to be serialized and deserialized across
 * different I/O streams, enabling persistence or communication over a network.
 */
public class Host implements Serializable {

    /**
     * The {@link Address} associated with this host.
     * This field is mandatory and must not be {@code null}.
     */
    private final @NotNull Address address;

    /**
     * The {@link Port} associated with this host.
     * This field is optional and may be {@code null}, representing the absence of a specific port.
     */
    private final @Nullable Port port;

    /**
     * Constructs a new {@code Host} instance with the specified {@link Address} and {@link Port}.
     *
     * @param address the network address, must not be {@code null}
     * @param port    the network port, may be {@code null} to indicate an unspecified port
     * @throws NullPointerException if {@code address} is {@code null}
     */
    public Host(@NotNull Address address, @Nullable Port port) {
        this.address = Objects.requireNonNull(address, "address must not be null");
        this.port = port;
    }

    /**
     * Returns the name representation of this host, equivalent to {@link #toString()}.
     * This method is a semantic alias to clarify the context where a host's name
     * (e.g., "localhost:8080" or "192.168.0.1") is required.
     *
     * @return the string name of the host
     */
    public @NotNull String getName() {
        return toString();
    }

    /**
     * Returns the {@link Address} of this host.
     *
     * @return the address, guaranteed to be non-null
     */
    public @NotNull Address getAddress() {
        return address;
    }

    /**
     * Returns the {@link Port} of this host.
     *
     * @return the port, or {@code null} if no port is defined
     */
    public @Nullable Port getPort() {
        return port;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two {@code Host} instances are considered equal if both their address and port are equal.
     *
     * @param object the reference object with which to compare
     * @return {@code true} if this object is equal to the {@code object} argument; {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Host)) return false;
        @NotNull Host that = (Host) object;
        return Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getPort(), that.getPort());
    }

    /**
     * Returns a hash code value for this host.
     * The hash code is computed using both the address and the port, enabling
     * efficient storage in hash-based data structures like {@link java.util.HashMap}.
     *
     * @return a hash code value for this host
     */
    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getPort());
    }

    /**
     * Returns a human-readable string representation of this host.
     * The output format is either:
     * <ul>
     *     <li>{@code address:port} if the port is present
     *     <li>{@code address} if the port is absent
     * </ul>
     *
     * @return a string representation of the host
     */
    @Override
    public @NotNull String toString() {
        if (getPort() != null) {
            return getAddress().toString(getPort());
        } else {
            return getAddress().toString();
        }
    }

}