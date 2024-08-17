package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a host which contains the details of an {@link Address} and an optional {@link Port}.
 * This class serves as a generic container for network-related information, where the address must
 * be a subclass of {@link Address} and the port is optional.
 * <p>
 * A host is defined as a combination of:
 * <ul>
 *     <li>An {@link Address} object representing the network address, which can be any subclass of {@link Address}.
 *     <li>An optional {@link Port} representing the network port, which can be {@code null}.
 * </ul>
 * This class implements both {@link Serializable} and {@link Cloneable}, enabling instances to be
 * serialized and deeply cloned. The class is also designed to be immutable, ensuring thread safety
 * and reliable behavior in concurrent environments.
 *
 * @param <T> the type of {@link Address}, constrained to subclasses of {@link Address}
 */
public abstract class Host<T extends Address> implements Serializable, Cloneable {

    // Object

    /**
     * The {@link Address} of this host. This is a required field and cannot be {@code null}.
     */
    private final @NotNull T address;

    /**
     * The {@link Port} of this host, which is optional and may be {@code null}.
     */
    private final @Nullable Port port;

    /**
     * Constructs a new {@code Host} with the specified address and port.
     *
     * @param address the address of the host, must not be {@code null}
     * @param port    the port of the host, may be {@code null} to indicate the absence of a port
     * @throws NullPointerException if the provided address is {@code null}
     */
    protected Host(@NotNull T address, @Nullable Port port) {
        this.address = Objects.requireNonNull(address, "address must not be null");
        this.port = port;
    }

    // Getters

    /**
     * Returns the name representation of this host
     *
     * @return the name of this host
     */
    public @NotNull String getName() {
        return toString();
    }

    /**
     * Returns the {@link Address} of this host.
     *
     * @return the address, never {@code null}
     */
    public @NotNull T getAddress() {
        return address;
    }

    /**
     * Returns the {@link Port} of this host, or {@code null} if no port is specified.
     *
     * @return the port, or {@code null} if none is set
     */
    public @Nullable Port getPort() {
        return port;
    }

    // Implementations

    /**
     * Compares this host to another object for equality. Two hosts are considered equal if they
     * have the same address and the same port.
     *
     * @param object the object to compare with this host
     * @return {@code true} if this host is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Host)) return false;
        @NotNull Host<?> that = (Host<?>) object;
        return Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getPort(), that.getPort());
    }

    /**
     * Returns a hash code value for this host. The hash code is calculated based on the address
     * and port, allowing for efficient storage and retrieval in hash-based collections.
     *
     * @return a hash code value for this host
     */
    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getPort());
    }

    /**
     * Returns a string representation of this host. The string representation includes the address
     * and the port (if specified), in the format "{@code address:port}". If the port is {@code null},
     * only the address is included.
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

    /**
     * Creates a new host instance with the same address but with a specified port.
     * This method allows for easy cloning of the host while modifying the port.
     *
     * @param newPort the new port to use, can be {@code null} to keep the existing port
     * @return a new {@link Host} instance with the specified port
     */
    public abstract @NotNull Host<T> clone(@Nullable Port newPort);

    /**
     * Creates a deep copy of this host instance, ensuring that both the address and port are
     * copied. This method supports deep cloning by utilizing the {@link Cloneable} interface.
     *
     * @return a new {@code Host} instance that is a clone of this one
     * @throws RuntimeException if cloning is not supported
     */
    @Override
    public @NotNull Host<T> clone() {
        try {
            //noinspection unchecked
            return (Host<T>) super.clone();
        } catch (@NotNull CloneNotSupportedException e) {
            throw new RuntimeException("Cannot clone host", e);
        }
    }

}