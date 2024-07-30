package codes.laivy.address.domain;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import codes.laivy.address.utilities.HttpAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * The {@code Domain} class represents a fully qualified domain name (FQDN) in the Domain Name System (DNS).
 * A domain name typically consists of a sequence of labels separated by dots, such as "www.example.com".
 * This class encapsulates the components of a domain name, including subdomains, second-level domains (SLDs), and top-level domains (TLDs).
 *
 * <p>Domain names are hierarchical, with each label in the name representing a node in the domain namespace.
 * The hierarchy starts from the rightmost label, known as the top-level domain (TLD), and progresses to the left.
 * For example, in "www.example.com", "com" is the TLD, "example" is the SLD, and "www" is a subdomain.</p>
 *
 * <p>This class provides methods for validating, parsing, and creating domain names, as well as retrieving their components.
 * It also implements the {@code Address} interface, allowing domain names to be used as network addresses.</p>
 *
 * <h2>Static Initializers</h2>
 * <p>The class includes static methods for validating and parsing domain names:</p>
 * <ul>
 *   <li>{@link #validate(String)} - Validates whether a given string is a valid domain name.</li>
 *   <li>{@link #parse(String)} - Parses a string into a {@code Domain} object.</li>
 *   <li>{@link #create(Subdomain[], SLD, TLD)} - Creates a {@code Domain} object from its components.</li>
 * </ul>
 *
 * @see HttpAddress
 * @author Daniel Meinicke (Laivy)
 * @since 1.1
 */
public final class Domain implements Address, HttpAddress {

    // Static initializers

    private static final long serialVersionUID = -3053834275420857080L;

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
     * @param string the string to parse
     * @return a {@code Domain} object representing the parsed domain name
     * @throws IllegalArgumentException if the string cannot be parsed as a valid domain name
     */
    public static @NotNull Domain parse(@NotNull String string) throws IllegalArgumentException {
        @NotNull String[] parts = string.split(":");

        if (validate(string)) {
            parts = parts[0].split("\\.");

            @Nullable TLD tld;
            @NotNull SLD sld;
            @NotNull Subdomain[] subdomains;

            if (parts[parts.length - 1].equalsIgnoreCase("localhost")) {
                tld = null;

                sld = SLD.parse(parts[parts.length - 1]);
                subdomains = parts.length > 1 ? Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 1)).map(Subdomain::create).toArray(Subdomain[]::new) : new Subdomain[0];
            } else {
                tld = TLD.parse(parts[parts.length - 1]);
                sld = SLD.parse(parts[parts.length - 2]);
                subdomains = parts.length > 2 ? Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 2)).map(Subdomain::create).toArray(Subdomain[]::new) : new Subdomain[0];
            }

            return new Domain(subdomains, sld, tld);
        } else {
            throw new IllegalArgumentException("cannot parse '" + string + "' as a valid name address");
        }
    }

    /**
     * Creates a {@code Domain} object from its components.
     *
     * <p>This method constructs a domain name from the provided subdomains, SLD, and TLD components.
     * The constructed domain name is validated to ensure it adheres to DNS naming conventions.</p>
     *
     * @param subdomains an array of subdomains
     * @param sld the second-level domain
     * @param tld the top-level domain (nullable for "localhost")
     * @return a {@code Domain} object representing the constructed domain name
     * @throws IllegalArgumentException if the constructed domain name is not valid
     */
    public static @NotNull Domain create(@NotNull Subdomain @NotNull [] subdomains, @NotNull SLD sld, @Nullable TLD tld) {
        return new Domain(subdomains, sld, tld);
    }

    // Object

    private final @NotNull Subdomain @NotNull [] subdomains;
    private final @NotNull SLD sld;
    private final @Nullable TLD tld;

    /**
     * Constructs a {@code Domain} object.
     *
     * <p>This constructor is private and is intended to be called by the static {@link #create(Subdomain[], SLD, TLD)} method.
     * It initializes the domain name components and performs validation to ensure the domain name is valid.</p>
     *
     * @param subdomains an array of subdomains
     * @param sld the second-level domain
     * @param tld the top-level domain (nullable for "localhost")
     * @throws IllegalArgumentException if the domain name is not valid
     */
    private Domain(@NotNull Subdomain @NotNull [] subdomains, @NotNull SLD sld, @Nullable TLD tld) {
        this.subdomains = subdomains;
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
     * @return {@code true} if this domain is "localhost", {@code false} otherwise
     */
    public boolean isLocalhost() {
        return getTLD() == null && getSLD().equalsIgnoreCase("localhost");
    }

    // Modules

    /**
     * Returns the byte array representation of this domain name.
     *
     * @return a byte array representing this domain name
     */
    @Override
    public byte @NotNull [] getBytes() {
        return toString().getBytes();
    }

    /**
     * Returns the name of this domain, consisting of the SLD and TLD.
     *
     * @return a string representing the domain name
     */
    @Override
    public @NotNull String getName() {
        return getSLD() + (getTLD() != null ? "." + getTLD() : "");
    }

    // Implementations

    /**
     * Compares this domain to the specified object. The result is {@code true} if and only if the argument is not {@code null}
     * and is a {@code Domain} object that represents the same domain name as this object.
     *
     * @param message the object to compare this {@code Domain} against
     * @return {@code true} if the given object represents a {@code Domain} equivalent to this domain, {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object message) {
        if (this == message) return true;
        if (!(message instanceof Domain)) return false;
        @NotNull Domain domain = (Domain) message;
        return Objects.deepEquals(getSubdomains(), domain.getSubdomains()) && Objects.equals(getSLD(), domain.getSLD()) && Objects.equals(getTLD(), domain.getTLD());
    }

    /**
     * Returns a hash code value for this domain.
     *
     * @return a hash code value for this domain
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(getSubdomains()), getSLD(), getTLD());
    }

    /**
     * Returns a string representation of this domain. The string representation consists of the subdomains, SLD, and TLD.
     *
     * @return a string representation of this domain
     */
    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        for (@NotNull Subdomain subdomain : getSubdomains()) {
            builder.append(subdomain).append(".");
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
