package codes.laivy.address.email;

import codes.laivy.address.domain.SLD;
import codes.laivy.address.domain.Subdomain;
import codes.laivy.address.domain.TLD;
import codes.laivy.address.exception.parse.EmailParseException;
import codes.laivy.address.exception.parse.SLDParseException;
import codes.laivy.address.exception.parse.TLDParseException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class Email implements CharSequence {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        string = string.replace("(dot)", ".").replace("(at)", "@");

        if (string.length() > 254) {
            return false;
        } else {
            @NotNull String[] parts = string.split("@");

            if (parts.length != 2) {
                return false;
            } else {
                if (!parts[0].matches("[a-zA-Z0-9._%+-]{1,64}")) {
                    return false;
                }

                parts = parts[1].split("\\.");

                @NotNull String[] subdomains = parts.length > 2 ? Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 2)).toArray(String[]::new) : new String[0];
                @NotNull String sld = parts[parts.length - 2];
                @NotNull String tld = parts[parts.length - 1];

                return Arrays.stream(subdomains).allMatch(Subdomain::validate) && SLD.validate(sld) && TLD.validate(tld);
            }
        }
    }

    public static @NotNull Email parse(@NotNull String string) throws EmailParseException {
        if (string.length() > 2048) {
            throw new EmailParseException("email too long!");
        }

        string = string.replace("(dot)", ".").replace("(at)", "@");

        if (validate(string)) try {
            @NotNull String[] parts = string.split("@");

            @NotNull String username = parts[0];
            parts = parts[1].split("\\.");

            @NotNull Subdomain[] subdomains = parts.length > 2 ? Arrays.stream(Arrays.copyOfRange(parts, 0, parts.length - 2)).map(Subdomain::create).toArray(Subdomain[]::new) : new Subdomain[0];
            @NotNull SLD sld;
            @NotNull TLD tld;

            // Parse SLD and TLD
            try {
                sld = SLD.parse(parts[parts.length - 2]);
            } catch (@NotNull SLDParseException e) {
                throw new EmailParseException("cannot parse SLD '" + (parts[parts.length - 2]) + "'", e);
            }
            try {
                tld = TLD.parse(parts[parts.length - 1]);
            } catch (@NotNull TLDParseException e) {
                throw new EmailParseException("cannot parse TLD '" + (parts[parts.length - 1]) + "'", e);
            }

            // Finish
            return create(username, subdomains, sld, tld);
        } catch (@NotNull EmailParseException e) {
            throw e;
        } catch (@NotNull Throwable throwable) {
            throw new EmailParseException("cannot parse email '" + string + "'", throwable);
        }
        else {
            throw new EmailParseException("cannot parse '" + string + "' as a valid e-mail address");
        }
    }

    public static @NotNull Email create(
            final @NotNull String username,

            final @NotNull Subdomain @NotNull [] subdomains,
            final @NotNull SLD sld,
            final @NotNull TLD tld
    ) throws IllegalArgumentException {
        @NotNull String string = username.replace("(dot)", ".");
        return new Email(username, subdomains, sld, tld);
    }

    // Object

    private final @NotNull String username;

    private final @NotNull Subdomain @NotNull [] subdomains;
    private final @NotNull SLD sld;
    private final @NotNull TLD tld;

    protected Email(@NotNull String username, @NotNull Subdomain @NotNull [] subdomains, @NotNull SLD sld, @NotNull TLD tld) {
        if (!username.matches("[a-zA-Z0-9._%+-]{1,64}")) {
            throw new IllegalArgumentException("invalid email username format '" + username + "'");
        }

        this.username = username;
        this.subdomains = subdomains;
        this.sld = sld;
        this.tld = tld;
    }

    // Getters

    public final @NotNull String getUsername() {
        return username;
    }

    public @NotNull Subdomain @NotNull [] getSubdomains() {
        return subdomains;
    }

    public @NotNull SLD getSLD() {
        return sld;
    }
    public @NotNull TLD getTLD() {
        return tld;
    }

    // Implementations

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Email)) return false;
        Email email = (Email) object;
        return Objects.equals(getUsername(), email.getUsername()) && Objects.deepEquals(getSubdomains(), email.getSubdomains()) && Objects.equals(getSLD(), email.getSLD()) && Objects.equals(getTLD(), email.getTLD());
    }
    @Override
    public final int hashCode() {
        return Objects.hash(getUsername(), Arrays.hashCode(getSubdomains()), getSLD(), getTLD());
    }

    @Override
    public final @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder(getUsername());
        builder.append("@");

        for (@NotNull Subdomain subdomain : getSubdomains()) {
            builder.append(subdomain).append(".");
        }
        builder.append(getSLD()).append(".").append(getTLD());

        return builder.toString();
    }

    // CharSequence Implementations

    @Override
    public final int length() {
        return toString().length();
    }
    @Override
    public final char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public final @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

}