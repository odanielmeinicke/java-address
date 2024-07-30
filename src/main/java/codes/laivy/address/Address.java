package codes.laivy.address;

import codes.laivy.address.domain.Domain;
import codes.laivy.address.ip.IPv4Address;
import codes.laivy.address.ip.IPv6Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Represents an address. The library already provides two types of addresses: {@link IPv4Address} and {@link IPv6Address}.
 * <p>
 * This interface defines methods to get the raw byte values of the address, get the name representation of the address,
 * and convert the address to a string with a specified port.
 * </p>
 * <p>
 * Example implementations:
 * <ul>
 *     <li>{@link IPv4Address} - Represents an IPv4 address, e.g., "192.168.0.1".</li>
 *     <li>{@link IPv6Address} - Represents an IPv6 address, e.g., "2001:0db8:85a3:0000:0000:8a2e:0370:7334".</li>
 * </ul>
 * </p>
 */
public interface Address extends Serializable, Cloneable {

    // Static initializers

    /**
     * Validates if a given string is either a valid IPv4, IPv6 or Domain address.
     *
     * @param string the string to validate.
     * @return {@code true} if the string is a valid IPv4, IPv6 or address; {@code false} otherwise.
     */
    static boolean validate(@NotNull String string) {
        return IPv4Address.validate(string) || IPv6Address.validate(string) || Domain.validate(string);
    }

    /**
     * Parses a given string into an {@link Address} instance, either {@link IPv4Address}, {@link IPv6Address} or {@link Domain}.
     *
     * @param string the string to parse.
     * @return the parsed {@link Address} instance. (Can be IPv4, IPv6 and a Domain address)
     * @throws IllegalArgumentException if the string cannot be parsed as a valid IPv4, IPv6 or Domain address.
     */
    static @NotNull Address parse(@NotNull String string) {
        if (IPv4Address.validate(string)) {
            return IPv4Address.parse(string);
        } else if (IPv6Address.validate(string)) {
            return IPv6Address.parse(string);
        } else if (Domain.validate(string)) {
            return Domain.parse(string);
        } else {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as a valid IPv4, IPv6 or Domain address");
        }
    }

    // Object

    /**
     * Returns the raw byte values of this address.
     *
     * @return a byte array representing the raw byte values of this address.
     */
    byte @NotNull [] getBytes();

    /**
     * Returns the name representation of this address, for example, "192.168.0.1" for IPv4 addresses.
     *
     * @return the name representation of this address.
     */
    @NotNull String getName();

    /**
     * Converts this address into a string with a specified port.
     *
     * @param port the port to be included in the string representation.
     * @return a string representation of this address with the specified port.
     */
    @NotNull String toString(@NotNull Port port);

    /**
     * Checks if this address belongs to the localhost.
     *
     * @return {@code true} if this address is a localhost address; {@code false} otherwise.
     */
    boolean isLocal();

    /**
     * Checks if this address belongs to the remote network.
     *
     * @return {@code true} if this address is a remote address; {@code false} otherwise.
     */
    default boolean isRemote() {
        return !isLocal();
    }

    // Cloneable

    @NotNull Address clone();

}