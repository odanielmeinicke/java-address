package codes.laivy.address;

import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents an IPv6 address.
 * <p>
 * IPv6 (Internet Protocol version 6) is the most recent version of the Internet Protocol (IP), the communications protocol that provides an identification and location system for computers on networks and routes traffic across the Internet. IPv6 was developed to deal with the long-anticipated problem of IPv4 address exhaustion. IPv6 addresses are 128 bits long, compared to 32 bits in IPv4, which allows for a vastly larger number of unique IP addresses.
 * </p>
 * <p>
 * An IPv6 address is typically represented as eight groups of four hexadecimal digits, each group representing 16 bits of the address. The groups are separated by colons (e.g., "2001:0db8:85a3:0000:0000:8a2e:0370:7334"). Leading zeros in each group can be omitted, and consecutive groups of zero values can be replaced with a double colon (::). However, this substitution can only be used once in an address to avoid ambiguity.
 * </p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.0
 */
public final class IPv6Address implements Address {

    // Static initializers

    /**
     * Validates if a given string is a valid IPv6 address.
     * <p>
     * The validation process consists of the following steps:
     * <ol>
     *   <li>If the string starts with a square bracket '[', it removes the bracket.</li>
     *   <li>Splits the string on the closing square bracket ']' to separate the address from the optional port.</li>
     *   <li>Checks that there are at most two parts (address and optional port) and at least one part (address).</li>
     *   <li>If a port is specified, validates the port using {@link Port#validate(String)}.</li>
     *   <li>Replaces the double colon (::) with the appropriate number of zero groups to normalize the address.</li>
     *   <li>Splits the normalized address on colons (:) to separate the hexadecimal groups.</li>
     *   <li>Verifies that there are exactly eight hexadecimal groups.</li>
     *   <li>Parses each hexadecimal group to an integer to ensure they are valid hexadecimal numbers.</li>
     * </ol>
     * </p>
     *
     * @param string the string to validate.
     * @return {@code true} if the string is a valid IPv6 address; {@code false} otherwise.
     */
    public static boolean validate(@NotNull String string) {
        // Step 1: Remove the starting square bracket if present
        if (string.startsWith("[")) {
            string = string.substring(1);
        }

        // Step 2: Split the string on the closing square bracket to separate the address from the optional port
        @NotNull String[] parts = string.split("]");
        if (parts.length > 2 || parts.length == 0) {
            return false;
        } else if (parts.length == 2) {
            // Step 3: Validate the port if present
            if (!parts[1].startsWith(":")) {
                return false;
            } else if (!Port.validate(parts[1].substring(1))) {
                return false;
            }
        }

        // Consider only the address part for further validation
        string = parts[0];

        // Step 4: Replace the double colon with the appropriate number of zero groups
        int missing = 7 - string.replace("::", "").split(":").length;
        @NotNull StringBuilder zeros = new StringBuilder();
        for (int row = 0; row < missing; row++) {
            zeros.append("0:");
        }
        string = string.replace("::", ":" + zeros);

        // Step 5: Split the normalized address on colons to separate the hexadecimal groups
        parts = string.split(":");

        // Step 6: Verify that there are exactly eight hexadecimal groups
        if (parts.length != 8) {
            return false;
        } else {
            // Step 7: Validate each hexadecimal group
            for (@NotNull String hex : parts) {
                try {
                    Integer.parseInt(hex, 16);
                } catch (@NotNull NumberFormatException ignore) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Parses a given string into an {@link IPv6Address} instance.
     * <p>
     * The parsing process involves the following steps:
     * <ol>
     *   <li>Validates the input string using the {@link #validate(String)} method.</li>
     *   <li>Removes the starting square bracket '[' if present.</li>
     *   <li>Splits the string on the closing square bracket ']' to separate the address from the optional port.</li>
     *   <li>Normalizes the address by replacing the double colon (::) with the appropriate number of zero groups.</li>
     *   <li>Splits the normalized address on colons (:) to separate the hexadecimal groups.</li>
     *   <li>Parses each hexadecimal group from a string to a short integer.</li>
     *   <li>Creates a new {@link IPv6Address} instance with the parsed groups.</li>
     * </ol>
     * </p>
     *
     * @param string the string to parse.
     * @return the parsed {@link IPv6Address} instance.
     * @throws IllegalArgumentException if the string cannot be parsed as a valid IPv6 address.
     */
    public static @NotNull IPv6Address parse(@NotNull String string) throws IllegalArgumentException {
        // Step 1: Validate the input string
        if (validate(string)) {
            // Step 2: Remove the starting square bracket if present
            if (string.startsWith("[")) {
                string = string.substring(1);
            }

            // Step 3: Split the string on the closing square bracket to separate the address from the optional port
            @NotNull String[] parts = string.split("]");
            string = parts[0];

            // Step 4: Parse the groups
            short[] groups = new short[8];

            // Step 5: Replace the double colon with the appropriate number of zero groups
            int missing = 7 - string.replace("::", "").split(":").length;
            @NotNull StringBuilder zeros = new StringBuilder();
            for (int row = 0; row < missing; row++) {
                zeros.append("0:");
            }
            string = string.replace("::", ":" + zeros);

            // Step 6: Split the normalized address on colons to separate the hexadecimal groups
            @NotNull String[] groupParts = string.split(":");
            for (int index = 0; index < groupParts.length; index++) {
                @NotNull String hex = groupParts[index].replaceFirst("^0+(?!$)", "");
                groups[index] = (short) Integer.parseInt(hex, 16);
            }

            // Step 7: Create a new IPv6Address instance
            return new IPv6Address(groups);
        } else {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as a valid IPv6 address");
        }
    }

    /**
     * Converts a 128-bit integer representation to an IPv6 address.
     * <p>
     * The integer is split into eight 16-bit groups to form the IPv6 address.
     * </p>
     *
     * @param high the high 64-bit integer part of the IPv6 address.
     * @param low the low 64-bit integer part of the IPv6 address.
     * @return the {@link IPv6Address} created from the two 64-bit integers.
     */
    public static @NotNull IPv6Address fromLongs(long high, long low) {
        short[] groups = new short[8];
        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                groups[i] = (short) ((high >> (48 - i * 16)) & 0xFFFF);
            } else {
                groups[i] = (short) ((low >> (48 - (i - 4) * 16)) & 0xFFFF);
            }
        }
        return new IPv6Address(groups);
    }

    // Object

    // This array will always have 8 elements
    private final short @NotNull [] groups;

    /**
     * Constructs an IPv6Address instance with the specified groups.
     *
     * @param groups an array of eight short integers representing the groups of the IPv6 address.
     * @throws IllegalArgumentException if the array does not have exactly eight elements.
     */
    private IPv6Address(short[] groups) {
        this.groups = groups;

        if (groups.length != 8) {
            throw new IllegalArgumentException("An IPv6 address must have eight hexadecimal groups");
        }
    }

    // Getters

    /**
     * Returns the groups of this IPv6 address.
     *
     * @return an array of eight short integers representing the groups of this IPv6 address.
     */
    public short[] getGroups() {
        return groups;
    }

    /**
     * Returns the raw byte values of this IPv6 address.
     *
     * @return a byte array representing the raw byte values of this IPv6 address.
     */
    @Override
    public byte @NotNull [] getBytes() {
        return getName().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns the name representation of this IPv6 address.
     * <p>
     * The name representation of an IPv6 address follows the standard IPv6 notation, which can include leading zero omission and zero abbreviation.
     * For example, the address "2001:0db8:85a3:0000:0000:8a2e:0370:7334" can be represented as "2001:db8:85a3::8a2e:370:7334".
     * </p>
     *
     * @return the name representation of this IPv6 address.
     */
    @Override
    public @NotNull String getName() {
        // Function to convert each group to its hexadecimal representation without leading zeros
        @NotNull Function<Short, String> function = new Function<Short, String>() {
            @Override
            public @NotNull String apply(@NotNull Short group) {
                @NotNull String hex = String.format("%04X", group);
                while (hex.startsWith("0")) hex = hex.substring(1);

                return hex;
            }
        };

        // Build the string representation
        @NotNull StringBuilder builder = new StringBuilder();

        for (int index = 0; index < 8; index++) {
            short group = getGroups()[index];

            // Generate the representation
            @NotNull String representation = function.apply(group);
            if (representation.equals("0000")) representation = "0";

            // Check for zero abbreviation
            if (group == 0 && index != 7 && getGroups()[index + 1] == 0) {
                continue;
            }

            // Add the ":" separator
            if (builder.length() > 0) builder.append(":");
            // Add the group representation
            builder.append(representation);
        }

        return builder.toString();
    }

    /**
     * Returns the raw string representation of this IPv6 address.
     * <p>
     * This method provides the full, uncompressed form of the IPv6 address,
     * with each group of four hexadecimal digits separated by colons. Leading
     * zeros in each group are included.
     * </p>
     *
     * @return the raw string representation of the IPv6 address.
     */
    public @NotNull String getRawName() {
        @NotNull StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            // Format each group as four hexadecimal digits, with leading zeros
            @NotNull String group = String.format("%04X", groups[i]);

            if (i > 0) {
                builder.append(":");
            }
            builder.append(group);
        }

        return builder.toString();
    }

    // Modules

    /**
     * Computes the network address for this IPv6 address given a subnet mask.
     * <p>
     * The network address is obtained by performing a bitwise AND operation between
     * the address and the subnet mask.
     * </p>
     *
     * @param subnetMask the subnet mask as an {@link IPv6Address}.
     * @return the network address as an {@link IPv6Address}.
     */
    public @NotNull IPv6Address getNetworkAddress(@NotNull IPv6Address subnetMask) {
        short[] maskGroups = subnetMask.getGroups();
        short[] networkGroups = new short[8];
        for (int i = 0; i < 8; i++) {
            networkGroups[i] = (short) (groups[i] & maskGroups[i]);
        }
        return new IPv6Address(networkGroups);
    }

    /**
     * Checks if this IPv6 address is within a given range.
     * <p>
     * The range is defined by a start address and an end address. The method verifies
     * if the current address falls within these bounds.
     * </p>
     *
     * @param start the start address of the range as an {@link IPv6Address}.
     * @param end the end address of the range as an {@link IPv6Address}.
     * @return {@code true} if this IPv6 address is within the specified range; {@code false} otherwise.
     */
    public boolean isWithinRange(@NotNull IPv6Address start, @NotNull IPv6Address end) {
        for (int i = 0; i < 8; i++) {
            if (groups[i] < start.getGroups()[i] || groups[i] > end.getGroups()[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Computes the broadcast address for this IPv6 address given a subnet mask.
     * <p>
     * The broadcast address is typically all bits set to 1 for the host portion of the address.
     * </p>
     *
     * @param subnetMask the subnet mask as an {@link IPv6Address}.
     * @return the broadcast address as an {@link IPv6Address}.
     */
    public @NotNull IPv6Address getBroadcastAddress(@NotNull IPv6Address subnetMask) {
        short[] maskGroups = subnetMask.getGroups();
        short[] broadcastGroups = new short[8];
        for (int i = 0; i < 8; i++) {
            broadcastGroups[i] = (short) (groups[i] | ~maskGroups[i] & 0xFFFF);
        }
        return new IPv6Address(broadcastGroups);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof IPv6Address)) return false;
        @NotNull IPv6Address that = (IPv6Address) object;
        return Objects.deepEquals(getGroups(), that.getGroups());
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(getGroups());
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }

    /**
     * Returns a string representation of this IPv6 address with a specified port.
     *
     * @param port the port to be included in the string representation.
     * @return a string representation of this IPv6 address with the specified port.
     */
    @Override
    public @NotNull String toString(@NotNull Port port) {
        return "[" + getName() + "]:" + port;
    }

    /**
     * Converts this IPv6 address to a 128-bit integer representation.
     * <p>
     * The integer representation is split into two 64-bit integers.
     * </p>
     *
     * @return an array of two long values representing the 128-bit integer.
     */
    public long @NotNull [] toLongs() {
        long[] result = new long[2];
        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                result[0] |= ((long) groups[i] & 0xFFFF) << (48 - (i * 16));
            } else {
                result[1] |= ((long) groups[i] & 0xFFFF) << (48 - ((i - 4) * 16));
            }
        }
        return result;
    }

}
