package codes.laivy.address.domain;

import codes.laivy.address.Address;
import codes.laivy.address.port.Port;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class Domain implements Address {

    // Static initializers

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

    public static @NotNull Domain create(@NotNull Subdomain @NotNull [] subdomains, @NotNull SLD sld, @Nullable TLD tld) {
        return new Domain(subdomains, sld, tld);
    }

    // Object

    private final @NotNull Subdomain @NotNull [] subdomains;
    private final @NotNull SLD sld;
    private final @Nullable TLD tld;

    private Domain(@NotNull Subdomain @NotNull [] subdomains, @NotNull SLD sld, @Nullable TLD tld) {
        this.subdomains = subdomains;
        this.sld = sld;
        this.tld = tld;

        if (Arrays.stream(subdomains).anyMatch(subdomain -> subdomain.toString().equals("*")) && subdomains.length > 1) {
            throw new IllegalArgumentException("there's a wildcard subdomain, you cannot add others.");
        }
    }

    // Getters

    public @NotNull Subdomain @NotNull [] getSubdomains() {
        return subdomains;
    }
    public @NotNull SLD getSLD() {
        return sld;
    }
    public @Nullable TLD getTLD() {
        return tld;
    }

    public boolean isLocalhost() {
        return getTLD() == null && getSLD().equalsIgnoreCase("localhost");
    }

    // Modules

    @Override
    public byte @NotNull [] getBytes() {
        return toString().getBytes();
    }
    @Override
    public @NotNull String getName() {
        return getSLD() + (getTLD() != null ? "." + getTLD() : "");
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object message) {
        if (this == message) return true;
        if (!(message instanceof Domain)) return false;
        @NotNull Domain domain = (Domain) message;
        return Objects.deepEquals(getSubdomains(), domain.getSubdomains()) && Objects.equals(getSLD(), domain.getSLD()) && Objects.equals(getTLD(), domain.getTLD());
    }
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(getSubdomains()), getSLD(), getTLD());
    }

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
    @Override
    public @NotNull String toString(@NotNull Port port) {
        return this + ":" + port;
    }

}
