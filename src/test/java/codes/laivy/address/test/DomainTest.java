package codes.laivy.address.test;

import codes.laivy.address.Address;
import codes.laivy.address.domain.Domain;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class DomainTest {

    public static final @NotNull String[] valids = {
            "www.example.com",              // Simple valid domain with www
            "example.com",                  // Simple valid domain
            "sub.example.com",              // Subdomain
            "example.co.uk",                // Country code top-level domain (ccTLD)
            "xn--exmple-jua.com",           // Internationalized domain name (IDN) with punycode
            "example.com.",                 // Domain with trailing dot
            "example.travel",               // Uncommon top-level domain (TLD)
            "example123.com",               // Domain with numbers
            "ex-ample.com",                 // Domain with hyphen
            "example.jobs",                 // Domain with a new gTLD
            "example.museum",               // Long TLD
            "sub.example.travel",           // Subdomain with uncommon TLD
            "example.com.au",               // Second-level domain under ccTLD
            "sub-domain.example.com",       // Domain with multiple levels of subdomains
            "example.name",                 // Personal name TLD
            "example.app",                  // gTLD for apps
            "example.space",                // gTLD for various uses
            "example.news",                 // gTLD for news sites
            "example.tech",                 // gTLD for tech companies
            "example.email",                // gTLD for email services
            "example.io",                   // Popular gTLD for startups
            "sub.example.info",             // Subdomain with info TLD
            "example.biz",                  // TLD for businesses
            "example.club",                 // TLD for clubs
            "example.co",                   // Short ccTLD often used as an alternative to .com
            "example.net",                  // Common TLD for networks
            "example.org",                  // TLD for organizations
            "example.edu",                  // TLD for educational institutions
            "example.gov",                  // TLD for government entities
            "example.jobs"                  // TLD for employment-related sites
    };

    @Test
    @Order(value = 0)
    void validator() {
        for (@NotNull String string : valids) {
            Assertions.assertTrue(Domain.validate(string), "cannot validate domain address '" + string + "'");
        }
    }
    @Test
    @Order(value = 1)
    void type() {
        for (@NotNull String string : valids) {
            Assertions.assertSame(Domain.class, Address.getType(string), "cannot validate domain address using address type '" + string + "'");
        }
    }
    @Test
    @Order(value = 2)
    void parser() {
        for (@NotNull String string : valids) {
            try {
                @NotNull Domain address = Domain.parse(string);
                Assertions.assertEquals(address, Domain.parse(address.toString()), "the address string '" + string + "' has been parsed into a different domain address '" + Domain.parse(address.getName()) + "'");
            } catch (@NotNull Throwable throwable) {
                throw new IllegalArgumentException("cannot parse '" + string + "' as a valid domain address", throwable);
            }
        }
    }

    @Test
    void string() {
        for (@NotNull String string : valids) {
            @NotNull Domain address = Domain.parse(string);
            @NotNull Domain clone = Domain.parse(address.toString());

            Assertions.assertEquals(address, clone);
        }
    }
    @Test
    void name() {
        for (@NotNull String string : valids) {
            @NotNull Domain address = Domain.parse(string);
            @NotNull Domain clone = Domain.parse(address.getName());

            Assertions.assertEquals(address.getName(), clone.toString());
        }
    }

}
