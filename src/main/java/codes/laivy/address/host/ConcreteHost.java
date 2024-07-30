package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The {@code ConcreteHost} interface extends the {@link Host} interface to enforce a specific type of address.
 * Unlike the general {@code Host} interface, which can work with any {@link Address} type,
 * the {@code ConcreteHost} interface is designed for scenarios where the type of address
 * is fixed and known at compile time.
 *
 * <p>This interface defines a contract for hosts that have a specific type of address,
 * ensuring type safety and reducing the possibility of runtime errors related to type mismatches.
 * It is particularly useful when working with a known set of address types, such as
 * {@code IPAddress}, {@code Domain}, etc.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * ConcreteHost<IPAddress> ipHost = ConcreteHost.create(new IPAddress("192.168.1.1"), new Port(8080));
 *
 * IPAddress ip = ipHost.getAddress(); // Safe cast to IPAddress
 * Port port = ipHost.getPort(); // Retrieves the associated port
 * }
 * </pre>
 *
 * @param <T> the specific type of {@link Address} associated with this host
 */
@SuppressWarnings("unchecked")
public interface ConcreteHost<T extends Address> extends Host {

    /**
     * Creates a new {@code ConcreteHost} instance with the specified address and port.
     * The created instance is immutable and thread-safe.
     *
     * @param <E>     the specific type of {@link Address} to associate with the host
     * @param address the address associated with this host; must not be {@code null}
     * @param port    the port associated with this host; can be {@code null} if no port is specified
     * @return a new {@code ConcreteHost} instance with the specified address and port
     * @throws NullPointerException if the address is {@code null}
     */
    static <E extends Address> ConcreteHost<E> create(
            final @NotNull E address,
            final @Nullable Port port
    ) {
        return new ConcreteHost<E>() {

            // Getters

            @Override
            public @NotNull E getAddress() {
                return address;
            }

            @Override
            public @Nullable Port getPort() {
                return port;
            }

            @Override
            public @NotNull Host clone() {
                try {
                    return (Host) super.clone();
                } catch (@NotNull CloneNotSupportedException e) {
                    throw new RuntimeException("cannot clone concrete host", e);
                }
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof ConcreteHost)) return false;
                @NotNull ConcreteHost<?> that = (ConcreteHost<?>) object;
                return Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getPort(), that.getPort());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getAddress(), getPort());
            }

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
     * Retrieves the specific type of address associated with this host.
     * The type of the address is determined by the generic parameter {@code T},
     * ensuring type safety when accessing the address.
     *
     * @return the specific type of {@link Address} associated with this host
     */
    @Override
    @NotNull T getAddress();

}