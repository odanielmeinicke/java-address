package codes.laivy.address.ip;

import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents an IPv4 address. An IPv4 (Internet Protocol version 4) address is a 32-bit number that uniquely identifies a network interface on a machine.
 * IPv4 addresses are typically represented in dot-decimal notation, which consists of four decimal numbers, each ranging from 0 to 255, separated by dots (e.g., "192.168.0.1").
 * <p>
 * This class provides methods to validate, parse, and retrieve information about an IPv4 address.
 * <p>
 * The dot-decimal notation corresponds to four octets (8 bits each), making up a 32-bit address. For instance, the address "192.168.0.1" consists of the following octets:
 * <ul>
 *   <li>192</li>
 *   <li>168</li>
 *   <li>0</li>
 *   <li>1</li>
 * </ul>
 * </p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.0
 */
public final class IPv4Address implements IPAddress {

    // Static initializers

    private static final long serialVersionUID = 6642293391661876693L;

    /**
     * Validates if a given string is a valid IPv4 address.
     * <p>
     * The validation process consists of the following steps:
     * <ol>
     *   <li>Checks if the string length exceeds 21 characters (maximum length including optional port and separators).</li>
     *   <li>Splits the string on the colon (":") to separate the address from the optional port.</li>
     *   <li>Verifies that there are at most two parts (address and optional port) and at least one part (address).</li>
     *   <li>If a port is specified, validates the port using {@link Port#validate(String)}.</li>
     *   <li>Splits the address part on the dot (".") to separate the octets.</li>
     *   <li>Checks if there are exactly four octets.</li>
     *   <li>Parses each octet to an integer and verifies that it is within the range 0-255.</li>
     * </ol>
     * </p>
     *
     * @param string the string to validate.
     * @return {@code true} if the string is a valid IPv4 address; {@code false} otherwise.
     */
    public static boolean validate(@NotNull String string) {
        // Step 1: Check if the length of the string is greater than 21 characters
        if (string.length() > 21) {
            return false;
        } else {
            // Step 2: Split the string on the colon to separate the address from the port
            @NotNull String[] parts = string.split(":");

            // Step 3: Verify the number of parts
            if (parts.length > 2 || parts.length == 0) {
                return false;
                // Step 4: Validate the port if present
            } else if (parts.length == 2 && !Port.validate(parts[1])) {
                return false;
            }

            // Consider only the address part for further validation
            string = parts[0];
        }

        // Prevent string ending with "."
        if (string.endsWith(".")) {
            return false;
        }

        // Step 5: Split the address part on the dot to separate the octets
        @NotNull String[] parts = string.split("\\.");

        // Step 6: Check if there are exactly four octets
        if (parts.length != 4) {
            return false;
        } else {
            // Step 7: Validate each octet
            for (@NotNull String part : parts) {
                try {
                    // Avoid leading zeros
                    if (part.length() > 1 && part.charAt(0) == '0') {
                        return false;
                    }

                    // Parse
                    int octet = Integer.parseInt(part);
                    if (octet < 0 || octet > 255) return false;
                } catch (@NotNull NumberFormatException ignore) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Parses a given string into an {@link IPv4Address} instance.
     * <p>
     * The parsing process involves the following steps:
     * <ol>
     *   <li>Validates the input string using the {@link #validate(String)} method.</li>
     *   <li>Splits the string on the colon (":") to separate the address from the optional port.</li>
     *   <li>Extracts the address part and splits it on the dot (".") to separate the octets.</li>
     *   <li>Converts each octet from a string to an integer.</li>
     *   <li>Creates a new {@link IPv4Address} instance with the parsed octets.</li>
     * </ol>
     * </p>
     *
     * @param string the string to parse.
     * @return the parsed {@link IPv4Address} instance.
     * @throws IllegalArgumentException if the string cannot be parsed as a valid IPv4 address.
     */
    public static @NotNull IPv4Address parse(@NotNull String string) throws IllegalArgumentException {
        // Step 1: Validate the input string
        if (validate(string)) {
            // Step 2: Split the string on the colon to separate the address from the port
            @NotNull String[] parts = string.split(":");
            @NotNull String name = parts[0];

            // Prevent string ending with "."
            if (string.endsWith(".")) {
                throw new IllegalArgumentException("ipv4 addresses cannot end with '.'");
            }

            // Step 3: Split the address part on the dot to separate the octets
            parts = name.split("\\.");
            int[] octets = new int[4];

            // Step 4: Convert each octet from a string to an integer
            for (int index = 0; index < 4; index++) {
                octets[index] = Integer.parseInt(parts[index]);
            }

            // Step 5: Create a new IPv4Address instance
            return new IPv4Address(octets);
        } else {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as a valid IPv4 address");
        }
    }

    /**
     * Converts a 32-bit integer representation to an IPv4 address.
     * <p>
     * The integer is split into four octets to form the IPv4 address.
     * </p>
     *
     * @param value the 32-bit integer representation.
     * @return the {@link IPv4Address} created from the integer.
     */
    public static @NotNull IPv4Address fromInteger(int value) {
        int[] octets = new int[4];
        octets[0] = (value >> 24) & 0xFF;
        octets[1] = (value >> 16) & 0xFF;
        octets[2] = (value >> 8) & 0xFF;
        octets[3] = value & 0xFF;
        return new IPv4Address(octets);
    }

    // Object

    // This array will always have 4 elements
    private final int @NotNull [] octets;

    /**
     * Constructs an IPv4Address instance with the specified octets.
     *
     * @param octets an array of four integers representing the octets of the IPv4 address.
     * @throws IllegalArgumentException if the array does not have exactly four elements or if any octet is out of range (0-255).
     */
    public IPv4Address(int @NotNull [] octets) {
        this.octets = octets;

        // Verifications
        if (octets.length != 4) {
            throw new IllegalArgumentException("An IPv4 address must have four octets");
        } else {
            for (int octet : octets) {
                if (octet < 0 || octet > 255) {
                    throw new IllegalArgumentException("Invalid octet '" + octet + "'");
                }
            }
        }
    }

    // Getters

    /**
     * Returns the octets of this IPv4 address.
     *
     * @return an array of four integers representing the octets of this IPv4 address.
     */
    public int @NotNull [] getOctets() {
        return octets;
    }

    /**
     * Returns the raw byte values of this IPv4 address.
     *
     * @return a byte array representing the raw byte values of this IPv4 address.
     */
    @Override
    public byte @NotNull [] getBytes() {
        return getName().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns the name representation of this IPv4 address, for example, "192.168.0.1".
     *
     * @return the name representation of this IPv4 address.
     */
    @Override
    public @NotNull String getName() {
        return getOctets()[0] + "." + getOctets()[1] + "." + getOctets()[2] + "." + getOctets()[3];
    }

    // Modules

    /**
     * Checks if this IPv4 address belongs to the localhost.
     * <p>
     * In IPv4, the localhost address range is 127.0.0.0 to 127.255.255.255.
     * Any address with the first octet as 127 is considered localhost.
     * </p>
     *
     * @return {@code true} if this IPv4 address is a localhost address; {@code false} otherwise.
     */
    public boolean isLocalhost() {
        return octets[0] == 127;
    }

    /**
     * Checks if this IPv4 address is a private address.
     * <p>
     * Private address ranges:
     * - 10.0.0.0 to 10.255.255.255
     * - 172.16.0.0 to 172.31.255.255
     * - 192.168.0.0 to 192.168.255.255
     * </p>
     *
     * @return {@code true} if this IPv4 address is a private address; {@code false} otherwise.
     */
    public boolean isPrivate() {
        return (octets[0] == 10) ||
                (octets[0] == 172 && octets[1] >= 16 && octets[1] <= 31) ||
                (octets[0] == 192 && octets[1] == 168);
    }

    /**
     * Checks if this IPv4 address is a broadcast address for a given subnet mask.
     * <p>
     * Broadcast addresses typically have all bits of the host portion set to 1.
     * </p>
     *
     * @param subnetMask the subnet mask to check against.
     * @return {@code true} if this IPv4 address is a broadcast address; {@code false} otherwise.
     */
    public boolean isBroadcast(int @NotNull [] subnetMask) {
        for (int i = 0; i < 4; i++) {
            if ((octets[i] & ~subnetMask[i]) != ~subnetMask[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this IPv4 address is a broadcast address for a given subnet.
     * <p>
     * A broadcast address for a subnet has all host bits set to 1. This method calculates
     * the broadcast address for the given subnet and checks if the current address matches it.
     * </p>
     *
     * @param subnet the subnet mask as an {@link IPv4Address}.
     * @return {@code true} if this IPv4 address is a broadcast address for the given subnet; {@code false} otherwise.
     */
    public boolean isBroadcast(@NotNull IPv4Address subnet) {
        int[] subnetOctets = subnet.getOctets();

        // Calculate the broadcast address for the subnet
        int[] broadcastOctets = new int[4];
        for (int i = 0; i < 4; i++) {
            broadcastOctets[i] = (octets[i] | ~subnetOctets[i]) & 0xFF;
        }

        return Arrays.equals(octets, broadcastOctets);
    }

    /**
     * Checks if this IPv4 address is a multicast address.
     * <p>
     * Multicast address range: 224.0.0.0 to 239.255.255.255.
     * </p>
     *
     * @return {@code true} if this IPv4 address is a multicast address; {@code false} otherwise.
     */
    public boolean isMulticast() {
        return octets[0] >= 224 && octets[0] <= 239;
    }

    /**
     * Checks if this IPv4 address is publicly routable.
     * <p>
     * Publicly routable addresses are those that are not private, localhost, broadcast, or reserved.
     * </p>
     *
     * @return {@code true} if this IPv4 address is publicly routable; {@code false} otherwise.
     */
    public boolean isPubliclyRoutable() {
        return !isPrivate() && !isLocalhost() && !isMulticast();
    }

    /**
     * Computes the network address for this IPv4 address given a subnet mask.
     * <p>
     * The network address is obtained by performing a bitwise AND operation between
     * the address and the subnet mask.
     * </p>
     *
     * @param subnetMask the subnet mask as an {@link IPv4Address}.
     * @return the network address as an {@link IPv4Address}.
     */
    public @NotNull IPv4Address getNetworkAddress(@NotNull IPv4Address subnetMask) {
        int[] maskOctets = subnetMask.getOctets();
        int[] networkOctets = new int[4];
        for (int i = 0; i < 4; i++) {
            networkOctets[i] = octets[i] & maskOctets[i];
        }

        return new IPv4Address(networkOctets);
    }

    /**
     * Checks if this IPv4 address is within a given range.
     * <p>
     * The range is defined by a start address and an end address. The method verifies
     * if the current address falls within these bounds.
     * </p>
     *
     * @param start the start address of the range as an {@link IPv4Address}.
     * @param end the end address of the range as an {@link IPv4Address}.
     * @return {@code true} if this IPv4 address is within the specified range; {@code false} otherwise.
     */
    public boolean isWithinRange(@NotNull IPv4Address start, @NotNull IPv4Address end) {
        int[] startOctets = start.getOctets();
        int[] endOctets = end.getOctets();

        boolean withinRange = true;
        for (int i = 0; i < 4; i++) {
            if (octets[i] < startOctets[i] || octets[i] > endOctets[i]) {
                withinRange = false;
                break;
            }
        }

        return withinRange;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof IPv4Address)) return false;
        @NotNull IPv4Address that = (IPv4Address) object;
        return Objects.deepEquals(getOctets(), that.getOctets());
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(getOctets());
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }

    /**
     * Returns a string representation of this IPv4 address with a specified port.
     *
     * @param port the port to be included in the string representation.
     * @return a string representation of this IPv4 address with the specified port.
     */
    @Override
    public @NotNull String toString(@NotNull Port port) {
        return getName() + ":" + port;
    }

    /**
     * Returns a clone of this ipv4 address with the same bytes.
     *
     * @return the clone of this ipv4 address
     */
    @Override
    public @NotNull IPv4Address clone() {
        try {
            return (IPv4Address) super.clone();
        } catch (@NotNull CloneNotSupportedException e) {
            throw new RuntimeException("cannot clone ipv4 address", e);
        }
    }

    /**
     * Converts this IPv4 address to a 32-bit integer representation.
     * <p>
     * The integer representation is calculated by shifting and combining the octets.
     * </p>
     *
     * @return the integer representation of the IPv4 address.
     */
    public int toInteger() {
        return (octets[0] << 24) | (octets[1] << 16) | (octets[2] << 8) | octets[3];
    }

}
