package codes.laivy.address.test;

import codes.laivy.address.Address;
import codes.laivy.address.domain.Domain;
import codes.laivy.address.domain.SLD;
import codes.laivy.address.domain.Subdomain;
import codes.laivy.address.domain.TLD;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.util.Arrays;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class DomainTest {

    public static final @NotNull Matcher[] valids = {
            new Matcher("www.example.com", "example", SLD.parse("example"), TLD.COM, Subdomain.WWW),
            new Matcher("example.com", "example", SLD.parse("example"), TLD.COM),
            new Matcher("sub.example.com", "example", SLD.parse("example"), TLD.COM, Subdomain.create("sub")),
            new Matcher("example.co.uk", "example", SLD.parse("co"), TLD.UK),
            new Matcher("xn--exmple-jua.com", "xn--exmple-jua", SLD.parse("xn--exmple-jua"), TLD.COM),
            new Matcher("example.com.", "example", SLD.parse("example"), TLD.COM),
            new Matcher("example.travel", "example", SLD.parse("example"), TLD.TRAVEL),
            new Matcher("example123.com", "example123", SLD.parse("example123"), TLD.COM),
            new Matcher("ex-ample.com", "ex-ample", SLD.parse("ex-ample"), TLD.COM),
            new Matcher("example.jobs", "example", SLD.parse("example"), TLD.JOBS),
            new Matcher("example.museum", "example", SLD.parse("example"), TLD.MUSEUM),
            new Matcher("sub.example.travel", "example", SLD.parse("example"), TLD.TRAVEL, Subdomain.create("sub")),
            new Matcher("example.com.au", "example", SLD.parse("com"), TLD.AU),
            new Matcher("sub-domain.example.com", "example", SLD.parse("example"), TLD.COM, Subdomain.create("sub-domain")),
            new Matcher("example.name", "example", SLD.parse("example"), TLD.NAME),
            new Matcher("example.app", "example", SLD.parse("example"), TLD.APP),
            new Matcher("example.space", "example", SLD.parse("example"), TLD.SPACE),
            new Matcher("example.news", "example", SLD.parse("example"), TLD.NEWS),
            new Matcher("example.tech", "example", SLD.parse("example"), TLD.TECH),
            new Matcher("example.email", "example", SLD.parse("example"), TLD.EMAIL),
            new Matcher("example.io", "example", SLD.parse("example"), TLD.IO),
            new Matcher("sub.example.info", "example", SLD.parse("example"), TLD.INFO, Subdomain.create("sub")),
            new Matcher("example.biz", "example", SLD.parse("example"), TLD.BIZ),
            new Matcher("example.club", "example", SLD.parse("example"), TLD.CLUB),
            new Matcher("example.co", "example", SLD.parse("example"), TLD.CO),
            new Matcher("example.net", "example", SLD.parse("example"), TLD.NET),
            new Matcher("example.org", "example", SLD.parse("example"), TLD.ORG),
            new Matcher("example.edu", "example", SLD.parse("example"), TLD.EDU),
            new Matcher("example.gov", "example", SLD.parse("example"), TLD.GOV),
            new Matcher("example.jobs", "example", SLD.parse("example"), TLD.JOBS)
    };

    @Test
    @Order(value = 0)
    void validator() {
        for (@NotNull Matcher matcher : valids) {
            matcher.validate();
        }
    }
    @Test
    @Order(value = 1)
    void type() {
        for (@NotNull Matcher matcher : valids) {
            Assertions.assertSame(Domain.class, Address.getType(matcher.input), "cannot validate domain address using address type '" + matcher.input + "'");
        }
    }
    @Test
    @Order(value = 2)
    void parser() {
        for (@NotNull Matcher matcher : valids) {
            try {
                @NotNull Domain address = Domain.parse(matcher.input);
                Assertions.assertEquals(address, Domain.parse(address.toString()), "the address string '" + matcher.input + "' has been parsed into a different domain address '" + Domain.parse(address.toString()) + "'");
            } catch (@NotNull Throwable throwable) {
                throw new IllegalArgumentException("cannot parse '" + matcher.input + "' as a valid domain address", throwable);
            }
        }
    }

    @Test
    void string() {
        for (@NotNull Matcher matcher : valids) {
            @NotNull Domain address = Domain.parse(matcher.input);
            @NotNull Domain clone = Domain.parse(address.toString());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void name() {
        for (@NotNull Matcher matcher : valids) {
            @NotNull Domain address = Domain.parse(matcher.input);
            @NotNull Domain clone = Domain.parse(address.toString());

            Assertions.assertEquals(address.toString(), clone.toString());
        }
    }

    // Classes

    public static final class Matcher {

        private final @NotNull String input;

        private final @NotNull Subdomain @NotNull [] subdomains;
        private final @NotNull String name;
        private final @NotNull SLD sld;
        private final @NotNull TLD tld;

        public Matcher(@NotNull String input, @NotNull String name, @NotNull SLD sld, @NotNull TLD tld, @NotNull Subdomain @NotNull ... subdomains) {
            this.input = input;
            this.name = name;
            this.sld = sld;
            this.tld = tld;
            this.subdomains = subdomains;
        }

        // Getters

        public @NotNull String getInput() {
            return input;
        }
        public @NotNull Subdomain @NotNull [] getSubdomains() {
            return subdomains;
        }
        public @NotNull String getName() {
            return name;
        }
        public @NotNull SLD getSLD() {
            return sld;
        }
        public @NotNull TLD getTLD() {
            return tld;
        }

        // Validators

        private void validate() {
            @NotNull Domain domain = Domain.parse(input);
            Assertions.assertArrayEquals(subdomains, domain.getSubdomains(), "The subdomains doesn't matches for input '" + input + "' and expected '" + Arrays.toString(subdomains) + "'");
            Assertions.assertEquals(name, domain.getName(), "The name doesn't matches for input '" + input + "' and expected '" + name + "'");
            Assertions.assertEquals(sld, domain.getSLD(), "The SLD doesn't matches for input '" + input + "' and expected '" + sld + "'");
            Assertions.assertEquals(tld, domain.getTLD(), "The TLD doesn't matches for input '" + input + "' and expected '" + tld + "'");
        }

    }

}
