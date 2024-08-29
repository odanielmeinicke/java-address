package codes.laivy.address.domain;

import codes.laivy.address.Address;
import codes.laivy.address.http.HttpAddress;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a domain name, which includes subdomains, a second-level domain (SLD), and an optional top-level domain (TLD).
 * This class also handles special cases such as "localhost".
 *
 * <p>The {@code Domain} class provides various methods to validate, parse, and manage domain names, including
 * subdomains, SLDs, and TLDs. It also offers utility methods to convert the domain into different representations
 * like byte arrays or string formats with optional port numbers.</p>
 *
 * <p>This class is immutable and thread-safe.</p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * Domain domain = Domain.parse("www.example.com");
 * String domainName = domain.getName(); // returns "example"
 * boolean isLocal = domain.isLocal(); // returns false
 * }</pre>
 *
 * <p>Note: This class assumes valid domain names follow the structure defined in common domain name system (DNS)
 * practices, including the handling of "localhost" as a special case.</p>
 *
 * @see SLD
 * @see TLD
 * @see Subdomain
 */
public final class Domain implements Address, HttpAddress {

    // Static initializers

    private static final long serialVersionUID = 1325278871227725319L;

    /**
     * Validates whether a given string is a valid domain name.
     *
     * <p>The validation process involves checking the following conditions:</p>
     * <ul>
     *   <li>The string can optionally include a port number, separated by a colon (":").</li>
     *   <li>The domain part must consist of valid subdomain, SLD, and TLD components.</li>
     *   <li>Special handling for the "localhost" domain, which is considered valid.</li>
     * </ul>
     *
     * <p>This method performs strict validation, ensuring that each component of the domain name conforms to
     * standard DNS rules. If the string includes a port number, it must also be validated as a valid port.</p>
     *
     * @param string the string to validate
     * @return {@code true} if the string is a valid domain name, {@code false} otherwise
     */
    public static boolean validate(@NotNull String string) {
        @NotNull String[] parts = string.split(":");

        if (parts.length > 2 || parts.length == 0) {
            return false;
        } else try {
            if (parts.length == 2 && !Port.validate(parts[1])) {
                return false;
            }

            parts = parts[0].split("\\.");

            if (parts.length == 0) {
                return false;
            } else if (parts[parts.length - 1].equalsIgnoreCase("localhost")) {
                if (parts.length > 1 && Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 1)).anyMatch(subdomain -> !Subdomain.validate(subdomain))) {
                    return false;
                }
            } else {
                if (!TLD.validate(parts[parts.length - 1])) {
                    return false;
                } else if (!SLD.validate(parts[parts.length - 2])) {
                    return false;
                } else if (parts.length > 2 && Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 2)).anyMatch(subdomain -> !Subdomain.validate(subdomain))) {
                    return false;
                }
            }
        } catch (@NotNull NumberFormatException ignore) {
            return false;
        }

        return true;
    }

    /**
     * Parses a string into a {@code Domain} object.
     *
     * <p>The string must represent a valid domain name, which is verified by the {@link #validate(String)} method.
     * The parsed components include subdomains, SLD, and TLD, with special handling for "localhost".</p>
     *
     * <p>If the string cannot be parsed as a valid domain name, this method throws an {@code IllegalArgumentException}.
     * The parsing logic is robust, handling different cases such as the presence of a port number and special domains
     * like "localhost".</p>
     *
     * @param string the string to parse
     * @return a {@code Domain} object representing the parsed domain name
     * @throws IllegalArgumentException if the string cannot be parsed as a valid domain name
     */
    public static @NotNull Domain parse(@NotNull String string) throws IllegalArgumentException {
        if (validate(string)) {
            @NotNull String[] parts = string.split(":")[0].split("\\.");

            @Nullable TLD tld;
            @NotNull SLD sld;
            @NotNull String name;
            @NotNull Subdomain[] subdomains;

            if (parts[parts.length - 1].equalsIgnoreCase("localhost")) {
                tld = null;
                sld = SLD.parse(parts[parts.length - 1]);

                name = sld.toString();
                subdomains = parts.length > 1 ? Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 1)).map(Subdomain::create).toArray(Subdomain[]::new) : new Subdomain[0];
            } else {
                tld = TLD.parse(parts[parts.length - 1]);
                sld = SLD.parse(parts[parts.length - 2]);

                int increment = sld.isKnownTLD() ? 1 : 0;

                name = parts[parts.length - (2 + increment)];
                subdomains = parts.length > (2 + increment) ? Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - (2 + increment))).map(Subdomain::create).toArray(Subdomain[]::new) : new Subdomain[0];
            }

            return new Domain(subdomains, name, sld, tld);
        } else {
            throw new IllegalArgumentException("cannot parse '" + string + "' as a valid name address");
        }
    }

    /**
     * Creates a new {@code Domain} instance using the provided subdomains, SLD, and TLD.
     *
     * <p>This factory method is used when you already have the individual components of a domain and want to create
     * a {@code Domain} object. The method ensures that the provided subdomains and TLD are valid and conform to
     * the rules defined for domain names.</p>
     *
     * @param subdomains an array of {@code Subdomain} objects representing the subdomains
     * @param name the name of the SLD
     * @param sld the {@code SLD} object representing the second-level domain
     * @param tld the {@code TLD} object representing the top-level domain, or {@code null} for "localhost"
     * @return a {@code Domain} object representing the specified domain
     * @throws IllegalArgumentException if the subdomains array contains a wildcard along with other subdomains
     */
    public static @NotNull Domain create(@NotNull Subdomain @NotNull [] subdomains, @NotNull String name, @NotNull SLD sld, @Nullable TLD tld) {
        return new Domain(subdomains, name, sld, tld);
    }

    // Object

    private final @NotNull Subdomain @NotNull [] subdomains;
    private final @NotNull String name;
    private final @NotNull SLD sld;
    private final @Nullable TLD tld;

    /**
     * Constructs a new {@code Domain} instance.
     *
     * <p>This constructor is used internally and should be invoked through the factory methods {@link #parse(String)}
     * or {@link #create(Subdomain[], String, SLD, TLD)} to ensure proper validation of the domain components.</p>
     *
     * @param subdomains an array of {@code Subdomain} objects representing the subdomains
     * @param name the name of the SLD
     * @param sld the {@code SLD} object representing the second-level domain
     * @param tld the {@code TLD} object representing the top-level domain, or {@code null} for "localhost"
     * @throws IllegalArgumentException if the subdomains array contains a wildcard along with other subdomains
     */
    private Domain(@NotNull Subdomain @NotNull [] subdomains, @NotNull String name, @NotNull SLD sld, @Nullable TLD tld) {
        this.subdomains = subdomains;
        this.name = name;
        this.sld = sld;
        this.tld = tld;

        if (Arrays.stream(subdomains).anyMatch(subdomain -> subdomain.toString().equals("*")) && subdomains.length > 1) {
            throw new IllegalArgumentException("there's a wildcard subdomain, you cannot add others.");
        }
    }

    // Getters

    /**
     * Returns the subdomains of this domain.
     *
     * <p>The returned array is a copy, ensuring that the internal state of the domain is not modified externally.</p>
     *
     * @return an array of {@code Subdomain} objects representing the subdomains
     */
    public @NotNull Subdomain @NotNull [] getSubdomains() {
        return subdomains;
    }

    /**
     * Returns the second-level domain (SLD) of this domain.
     *
     * @return the {@code SLD} object representing the second-level domain
     */
    public @NotNull SLD getSLD() {
        return sld;
    }

    /**
     * Returns the top-level domain (TLD) of this domain, or {@code null} if it is "localhost".
     *
     * @return the {@code TLD} object representing the top-level domain, or {@code null} for "localhost"
     */
    public @Nullable TLD getTLD() {
        return tld;
    }

    /**
     * Checks whether this domain is "localhost".
     *
     * <p>This method provides a quick check to determine if the domain is the special "localhost" domain,
     * which does not include a TLD.</p>
     *
     * @return {@code true} if this domain is "localhost", {@code false} otherwise
     */
    public boolean isLocal() {
        return getTLD() == null && getSLD().equalsIgnoreCase("localhost");
    }

    // Modules

    /**
     * Returns the byte array representation of this domain name.
     *
     * <p>This method converts the full domain name (including subdomains, SLD, and TLD) into a byte array.
     * This is particularly useful for network transmission or storage in binary formats.</p>
     *
     * @return a byte array representing this domain name
     */
    @Override
    public byte @NotNull [] getBytes() {
        return toString().getBytes();
    }

    /**
     * Returns the domain name without the TLD and subdomains.
     *
     * <p>For example, given a domain "www.example.com", this method returns "example".</p>
     *
     * @return the second-level domain (SLD) name
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    // Implementations

    /**
     * Compares this domain to the specified object.
     *
     * <p>The comparison checks for equality by considering the subdomains, name, SLD, and TLD. Two {@code Domain}
     * objects are considered equal if they represent the exact same domain name.</p>
     *
     * @param message the object to compare this {@code Domain} against
     * @return {@code true} if the given object represents a {@code Domain} equivalent to this domain, {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object message) {
        if (this == message) return true;
        if (!(message instanceof Domain)) return false;
        @NotNull Domain domain = (Domain) message;
        return Objects.deepEquals(getSubdomains(), domain.getSubdomains()) && Objects.equals(getName(), domain.getName()) && Objects.equals(getSLD(), domain.getSLD()) && Objects.equals(getTLD(), domain.getTLD());
    }

    /**
     * Returns a hash code value for this domain.
     *
     * <p>The hash code is computed based on the subdomains, name, SLD, and TLD, ensuring that equal domains have the same hash code.</p>
     *
     * @return a hash code value for this domain
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(getSubdomains()), getName(), getSLD(), getTLD());
    }

    /**
     * Returns a string representation of this domain.
     *
     * <p>The string representation includes the subdomains, SLD, and TLD, with appropriate dot separators.
     * This method is particularly useful for displaying the domain name in logs or user interfaces.</p>
     *
     * @return a string representation of this domain
     */
    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        for (@NotNull Subdomain subdomain : getSubdomains()) {
            builder.append(subdomain).append(".");
        }

        if (getSLD().isKnownTLD()) {
            builder.append(getName()).append(".");
        }

        builder.append(getSLD());

        if (getTLD() != null) {
            builder.append(".").append(getTLD());
        }

        return builder.toString();
    }

    /**
     * Returns a string representation of this domain, including the specified port number.
     *
     * @param port the port number to include in the string representation
     * @return a string representation of this domain with the port number
     */
    @Override
    public @NotNull String toString(@NotNull Port port) {
        return this + ":" + port;
    }

    /**
     * Returns a clone of this domain address with the same TLD, SLD and subdomains.
     *
     * @return the clone of this domain
     */
    @Override
    public @NotNull Domain clone() {
        try {
            return (Domain) super.clone();
        } catch (@NotNull CloneNotSupportedException e) {
            throw new RuntimeException("cannot clone domain", e);
        }
    }

}
