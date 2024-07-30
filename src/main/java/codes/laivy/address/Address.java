package codes.laivy.address;

import codes.laivy.address.domain.Domain;
import codes.laivy.address.domain.TLD;
import codes.laivy.address.ip.IPv4Address;
import codes.laivy.address.ip.IPv6Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Represents a network address, providing a unified interface for various address types
 * such as {@link IPv4Address}, {@link IPv6Address}, and {@link Domain}. This interface
 * defines methods to retrieve the raw byte representation, textual representation, and
 * other characteristics of an address.
 * Example implementations include:
 * <ul>
 *     <li>{@link IPv4Address} - Represents an IPv4 address (e.g., "192.168.0.1").</li>
 *     <li>{@link IPv6Address} - Represents an IPv6 address (e.g., "2001:0db8:85a3:0000:0000:8a2e:0370:7334").</li>
 *     <li>{@link Domain} - Represents a domain name (e.g., "example.com").</li>
 * </ul>
 * <p>
 * Static methods are provided to parse strings into corresponding address types,
 * validate address formats, and determine address types.
 * </p>
 */
public interface Address extends Serializable, Cloneable {

    /**
     * The main method used for loading classes or testing purposes.
     *
     * @param args the command line arguments.
     */
    static void main(@NotNull String @NotNull [] args) {
        // Example of loading the TLD class with a specific value
        TLD.parse("com");
    }

    /**
     * Determines the type of address based on its string representation.
     *
     * @param string the string to analyze.
     * @return the class type of the address, or {@code null} if the type cannot be determined.
     */
    static @Nullable Class<? extends Address> getType(@NotNull String string) {
        if (string.isEmpty()) {
            return null;
        }

        if (string.startsWith("[") || string.split(":").length > 2) { // IPv6 address
            if (IPv6Address.validate(string)) return IPv6Address.class;
            else return null;
        } else if (Character.isDigit(string.charAt(0))) { // Potential IPv4 address
            if (IPv4Address.validate(string)) return IPv4Address.class;
        }

        if (Domain.validate(string)) return Domain.class;
        else return null;
    }

    /**
     * Validates whether a given string represents a valid IPv4, IPv6, or Domain address.
     *
     * @param string the string to validate.
     * @return {@code true} if the string is a valid address; {@code false} otherwise.
     */
    static boolean validate(@NotNull String string) {
        return getType(string) != null;
    }

    /**
     * Parses a string into an {@link Address} instance, which may be an {@link IPv4Address},
     * {@link IPv6Address}, or {@link Domain}.
     *
     * @param string the string to parse.
     * @return the parsed {@link Address} instance.
     * @throws IllegalArgumentException if the string cannot be parsed as a valid address.
     */
    static @NotNull Address parse(@NotNull String string) {
        @Nullable Class<? extends Address> type = getType(string);

        if (type != null) {
            if (type == Domain.class) {
                return Domain.parse(string);
            } else if (type == IPv4Address.class) {
                return IPv4Address.parse(string);
            } else if (type == IPv6Address.class) {
                return IPv6Address.parse(string);
            } else {
                throw new UnsupportedOperationException("Unsupported address class: '" + type.getName() + "'");
            }
        } else {
            throw new IllegalArgumentException("Invalid address: '" + string + "'");
        }
    }

    /**
     * Returns the raw byte array representing the address.
     *
     * @return a byte array containing the address data.
     */
    byte @NotNull [] getBytes();

    /**
     * Returns the textual representation of the address,
     * such as "192.168.0.1" for an IPv4 address or just the TLD and SLD of a domain.
     *
     * @return the string representation of the address.
     */
    @NotNull String getName();

    /**
     * Converts the address to a string, including the specified port number.
     *
     * @param port the port number to include in the string representation.
     * @return the string representation of the address with the specified port.
     */
    @NotNull String toString(@NotNull Port port);

    /**
     * Determines if this address is a localhost address.
     *
     * @return {@code true} if the address is localhost; {@code false} otherwise.
     */
    boolean isLocal();

    /**
     * Determines if this address is a remote address, i.e., not localhost.
     *
     * @return {@code true} if the address is remote; {@code false} otherwise.
     */
    default boolean isRemote() {
        return !isLocal();
    }

    /**
     * Creates and returns a copy of this address.
     *
     * @return a clone of this address.
     */
    @NotNull Address clone();
}
