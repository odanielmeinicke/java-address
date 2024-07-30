package codes.laivy.address.host;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code ConcreteHost} class extends the {@link Host} class to enforce a specific type of address.
 * Unlike the more general {@code Host} class, which can work with any type of {@link Address},
 * the {@code ConcreteHost} class is designed for scenarios
 * where the address type is fixed and known at compile time.
 *
 * <p>This class provides a more type-safe approach by ensuring that only a specific type of address is used with the host.
 * This reduces the risk of runtime errors related to type mismatches and provides stronger type guarantees for operations
 * involving addresses of known types such as {@code IPAddress}, {@code Domain}, etc.</p>
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * ConcreteHost<IPAddress> ipHost = ConcreteHost.create(new IPAddress("192.168.1.1"), new Port(8080));
 *
 * IPAddress ip = ipHost.getAddress(); // Safe cast to IPAddress
 * Port port = ipHost.getPort(); // Retrieves the associated port
 * }</pre>
 *
 * @param <T> the specific type of {@link Address} associated with this host. This type parameter should extend
 *            {@link Address} and ensures that the address used with this host is of a specific type.
 */
@SuppressWarnings("unchecked")
public class ConcreteHost<T extends Address> extends Host {

    /**
     * Constructs a new {@code ConcreteHost} with the specified address and optional port.
     * This constructor initializes the {@code ConcreteHost} with a specific type of address and port.
     *
     * @param address the address to associate with this host, which must be of type {@code T} and cannot be {@code null}
     * @param port the port to associate with this host, which can be {@code null} if no specific port is required
     */
    protected ConcreteHost(@NotNull T address, @Nullable Port port) {
        super(address, port);
    }

    /**
     * Retrieves the specific type of address associated with this host.
     * The address type is determined by the generic parameter {@code T}, ensuring type safety when accessing the address.
     *
     * @return the specific type of {@link Address} associated with this host, cast to type {@code T}
     */
    @Override
    public @NotNull T getAddress() {
        return super.getAddress();
    }

    /**
     * Creates a new {@code ConcreteHost} instance that is a clone of this instance,
     * with a specified new port.
     * This method creates a new {@code ConcreteHost} with the same address as this instance,
     * but with the port replaced by the specified {@code newPort}. The address is cloned to ensure
     * that the new {@code ConcreteHost} has a separate instance of the address.
     *
     * <p>If the {@code newPort} is {@code null}, the cloned {@code ConcreteHost} will not have a specific
     * port assigned.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * ConcreteHost<IPAddress> originalHost = Host.concrete(new IPAddress("192.168.1.1"), new Port(8080));
     * ConcreteHost<IPAddress> clonedHost = originalHost.clone(new Port(9090));
     *
     * // clonedHost now has the same address as originalHost but with port 9090
     * }</pre>
     *
     * @param newPort the new port to use for the cloned {@code ConcreteHost}, which can be {@code null}
     * @return a new {@code ConcreteHost} instance that is a clone of this instance with the specified port
     */
    @Override
    public @NotNull ConcreteHost<T> clone(@Nullable Port newPort) {
        return new ConcreteHost<>((T) getAddress().clone(), newPort);
    }

    /**
     * Creates a deep copy of this {@code ConcreteHost} instance.
     * This method creates a new {@code ConcreteHost} instance that is a clone of this instance,
     * including a clone of the address. The port remains the same as the original instance.
     *
     * <p>This method provides a way to create an exact copy of the {@code ConcreteHost}, including
     * cloning the address to ensure that the new instance is completely independent of the original instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * ConcreteHost<IPAddress> originalHost = Host.concrete(new IPAddress("192.168.1.1"), new Port(8080));
     * ConcreteHost<IPAddress> clonedHost = originalHost.clone();
     *
     * // clonedHost is a deep copy of originalHost, including a cloned address
     * }</pre>
     *
     * @return a new {@code ConcreteHost} instance that is a deep copy of this instance
     */
    @Override
    public @NotNull ConcreteHost<T> clone() {
        return (ConcreteHost<T>) super.clone();
    }

}