package codes.laivy.address.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Top-Level Domain (TLD) as defined by the Internet Assigned Numbers Authority (IANA).
 *
 * <p>A TLD is the last segment of a domain name, located after the last dot ('.'). It signifies the highest level in the
 * hierarchical Domain Name System (DNS) of the Internet. Examples of TLDs include generic TLDs (gTLDs) like "com", "net",
 * and "org", country code TLDs (ccTLDs) like "uk", "jp", and "br", and new gTLDs like "app", "dev", and "xyz".</p>
 *
 * <p>This class provides mechanisms to validate and handle TLDs, ensuring they conform to known standards and formats.
 * It also supports validation against the list of TLDs officially recognized by IANA.</p>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 *
 * <p>Usage examples:</p>
 * <pre>
 * {@code
 * TLD tld = TLD.parse("com");
 * boolean isValid = TLD.validate("com");
 * boolean exists = TLD.exists("com");
 * }
 * </pre>
 *
 * @see CharSequence
 * @see Objects
 * @see String
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1
 */
@SuppressWarnings("NonAsciiCharacters")
public final class TLD implements CharSequence, Serializable {

    // Static initializers

    private static final long serialVersionUID = -5154560316815508774L;
    private static final @NotNull Map<String, TLD> map = new HashMap<>();

    /**
     * Parses a given string into a {@link TLD} object.
     *
     * @param string the string to parse
     * @return a new {@link TLD} object
     * @throws NullPointerException if the string is not a valid registered TLD
     */
    public static @NotNull TLD parse(@NotNull String string) {
        string = string.replace("-", "_").toLowerCase();

        if (map.containsKey(string)) {
            return map.get(string);
        } else {
            throw new NullPointerException("there's no registered TLD named '" + string + "'");
        }
    }

    /**
     * Validates if the provided TLD name exists (is known by IANA).
     *
     * @param string the string to validate
     * @return {@code true} if the TLD exists and is known by IANA, {@code false} otherwise
     */
    public static boolean isKnown(@NotNull String string) {
        if (validate(string)) {
            return Arrays.stream(TLD.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(string.replace("-", "_").toUpperCase()));
        } else {
            return false;
        }
    }

    /**
     * Validates whether a given string is a valid TLD.
     *
     * <p>A valid TLD is between 2 and 63 characters long, does not contain underscores ('_'), and only contains valid
     * characters. Valid characters include letters, numbers, and hyphens ('-'), with some exceptions for specific Unicode
     * characters.</p>
     *
     * @param string the string to validate
     * @return {@code true} if the string is a valid TLD, {@code false} otherwise
     */
    public static boolean validate(@NotNull String string) {
        return string.length() > 1 && string.length() < 64 && !string.contains("_") && string.matches("^[^0-9@#\"'\\\\$%]*$");
    }

// This method reads all the TLDs com IANA and automatic generates a TLDs.log file
// with all the TLDs already with the Javadocs.
// Must have JSOUP to run.
//    public static void main(@NotNull String @NotNull ... args) throws IOException, InterruptedException {
//        // Create file and writer
//        @NotNull File file = new File("./tlds.log");
//        if (!file.exists() and !file.createNewFile()) {
//            throw new IllegalStateException("cannot create TLDs file");
//        }
//
//        @NotNull FileWriter writer = new FileWriter(file);
//
//        // Functions
//        @NotNull Function<String, String> enumName = new Function<>() {
//            @Override
//            public @NotNull String apply(@NotNull String string) {
//                string = string.replace("-", "_").toUpperCase();
//
//                // Detect illegal characters and replace them
//                @NotNull StringBuilder sb = new StringBuilder();
//                for (char c : string.toCharArray()) {
//                    if (Character.isJavaIdentifierPart(c)) sb.append(c);
//                    else sb.append("_").append(Integer.toHexString(c));
//                }
//
//                // Finish
//                return sb.toString();
//            }
//        };
//
//        // Parse
//        @NotNull Document document = Jsoup.connect("https://www.iana.org/domains/root/db").get();
//        @NotNull List<String> versions = new LinkedList<>();
//
//        // Retrieve the TLDs that are not already included here
//        int total = document.body().getElementsByTag("tr").size();
//        int current = 0;
//
//        for (@NotNull Element element : document.body().getElementsByTag("tr")) try {
//            if (element.getElementsByTag("td").size() != 3) {
//                continue;
//            }
//
//            // Parse code and retrieve information URL
//            @NotNull Element a = Objects.requireNonNull(element.getElementsByTag("a").first());
//            @NotNull String code = a.text().substring(1);
//
//            // Retrieve dates
//            @Nullable String registration = null;
//            @Nullable String update = null;
//
//            @NotNull Document details = Jsoup.connect("https://iana.org" + a.attr("href")).get();
//            @Nullable Integer registrationIndex = null;
//
//            for (@NotNull Element paragraph : details.getElementsByTag("p")) {
//                if (paragraph.text().contains("Registration date")) {
//                    registrationIndex = details.getAllElements().indexOf(paragraph);
//
//                    @NotNull Matcher matcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(paragraph.text());
//                    if (matcher.find()) update = matcher.group();
//                    if (matcher.find()) registration = matcher.group();
//
//                    break;
//                }
//            }
//
//            // Retrieve IANA Reports
//            @NotNull Map<String, String> reports = new LinkedHashMap<>();
//
//            if (details.getElementsByTag("h2").stream().anyMatch(h2 -> h2.text().equals("IANA Reports"))) {
//                for (@NotNull Element ul : details.getElementsByTag("ul")) {
//                    if (ul.getElementsByTag("li").stream().allMatch(li -> li.getAllElements().size() == 2 and li.getElementsByTag("a").size() == 1 and li.getElementsByTag("a").get(0).attr("href").startsWith("/reports/"))) {
//                        for (@NotNull Element li : ul.getElementsByTag("li")) {
//                            a = Objects.requireNonNull(li.getElementsByTag("a").first());
//
//                            @NotNull String name = li.text();
//                            @NotNull String url = "https://iana.org" + a.attr("href");
//
//                            reports.put(url, name);
//                        }
//
//                        break;
//                    }
//                }
//            }
//
//            // Retrieve registry information
//            @NotNull List<String> information = new LinkedList<>();
//
//            if (details.getElementsByTag("h2").stream().anyMatch(h2 -> h2.text().equals("Registry Information"))) {
//                @NotNull Element main = details.getAllElements().stream().filter(h2 -> h2.text().equals("Registry Information")).findFirst().orElseThrow();
//
//                int index = details.getAllElements().indexOf(main) + 1;
//                int maximum = registrationIndex != null ? registrationIndex : index;
//
//                @NotNull Optional<Element> opt = details.getElementsByTag("h2").stream().filter(h2 -> h2.text().equals("IANA Reports")).findFirst();
//                if (opt.isPresent()) maximum = details.getAllElements().indexOf(opt.get()) - 1;
//
//                for (; index < maximum; index++) {
//                    @NotNull Element t = details.getAllElements().get(index);
//
//                    if (!Objects.requireNonNull(t.parent()).toString().startsWith("<main>")) {
//                        continue;
//                    }
//
//                    information.add(t.html().replace("\n", "<br>") + "<br>");
//                }
//            }
//
//            // Parse type
//            @NotNull Type type = Type.valueOf(element.getElementsByTag("td").get(1).text().toUpperCase().replace("-", "_"));
//
//            // Provider
//            @Nullable String provider = element.getElementsByTag("td").get(2).text().replace("\"", "\\\"");
//            if (provider.equalsIgnoreCase("Not Assigned")) provider = null;
//
//            if (Arrays.stream(TLD.class.getDeclaredFields()).noneMatch(field -> field.getName().equals(enumName.apply(code)))) {
//                @NotNull StringBuilder builder = new StringBuilder();
//
//                if (!information.isEmpty()) {
//                    builder.append(" * <h2>Registry Information</h2>\n");
//
//                    for (@NotNull String line : information) {
//                        builder.append(" * ").append(line).append("\n");
//                    }
//
//                    builder.append(" *\n");
//                } if (!reports.isEmpty()) {
//                    builder.append(" * <h2>IANA Reports</h2>\n");
//                    builder.append(" *\n");
//                    builder.append(" * <ul>\n");
//
//                    for (@NotNull Entry<String, String> entry : reports.entrySet()) {
//                        builder.append(" *   <li><a href=\"").append(entry.getKey()).append("\">").append(entry.getValue()).append("</a></li>\n");
//                    }
//
//                    builder.append(" * </ul>\n");
//                    builder.append(" *\n");
//                }
//
//                if (!builder.isEmpty()) {
//                    builder.insert(0, "/**\n");
//                    builder.append(" */\n");
//                }
//
//                builder.append("public static final @NotNull TLD ").append(enumName.apply(code)).append(" = new TLD(\"").append(code.toLowerCase()).append("\", Type.").append(type.name()).append(", ").append(provider != null ? "\"" + provider + "\"" : null).append(" \"").append(provider).append("\", ").append(registration != null ? "LocalDate.parse(\"" + registration + "\")" : null).append(", ").append(update != null ? "LocalDate.parse(\"" + update + "\")" : null).append(");\n\n");
//
//                versions.add(builder.toString());
//
//                // Progress counter
//                current++;
//                System.out.println("Processed '" + code + "' - " + current + "/" + total + " TLDs at IANA (" + new DecimalFormat("#.##").format(((double) current / total * 100D)) + "%).");
//            }
//        } catch (@NotNull Throwable throwable) {
//            throwable.printStackTrace();
//            System.out.println("Cannot process '" + element.toString().replace("\n", "") + "'");
//        }
//
//        // Print the fields to add
//
//        for (@NotNull String field : versions) {
//            writer.append(field);
//        }
//
//        writer.close();
//    }

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.aaa.com">http://www.aaa.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-aaa">Delegation of the .aaa domain to American Automobile Association, Inc. (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AAA = new TLD("aaa", Type.GENERIC, "American Automobile Association, Inc.", LocalDate.parse("2015-08-13"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.aarp<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151030-aarp">Delegation of the .aarp domain to AARP (2015-10-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AARP = new TLD("aarp", Type.GENERIC, "AARP", LocalDate.parse("2015-10-22"), LocalDate.parse("2024-07-08"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-abarth">Delegation of the .abarth domain to Fiat Chrysler Automobiles N.V. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230605-abarth">Revocation of the .abarth domain (2023-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABARTH = new TLD("abarth", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2023-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://abb.com">http://abb.com</a><br><b>WHOIS Server:</b> whois.nic.abb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150421-abb">Delegation of the .abb domain to ABB Ltd (2015-04-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABB = new TLD("abb", Type.GENERIC, "ABB Ltd", LocalDate.parse("2015-04-09"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.abbott.com">http://www.abbott.com</a><br><b>WHOIS Server:</b> whois.nic.abbott<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150304-abbott">Delegation of the .abbott domain to Abbott Laboratories, Inc. (2015-03-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABBOTT = new TLD("abbott", Type.GENERIC, "Abbott Laboratories, Inc.", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.abbvie.com">http://www.abbvie.com</a><br><b>WHOIS Server:</b> whois.nic.abbvie<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160404-abbvie">Delegation of the .abbvie domain to AbbVie Inc. (2016-04-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABBVIE = new TLD("abbvie", Type.GENERIC, "AbbVie Inc.", LocalDate.parse("2016-02-26"), LocalDate.parse("2021-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://abc.com">http://abc.com</a><br><b>WHOIS Server:</b> whois.nic.abc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-abc">Delegation of the .abc domain to Disney Enterprises, Inc. (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABC = new TLD("abc", Type.GENERIC, "Disney Enterprises, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.able.co.jp">http://www.able.co.jp</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160615-able">Delegation of the .able domain to Able Inc. (2016-06-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABLE = new TLD("able", Type.GENERIC, "Able Inc.", LocalDate.parse("2016-03-03"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.abogado/">http://nic.abogado/</a><br><b>WHOIS Server:</b> whois.nic.abogado<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141009-abogado">Delegation of the .abogado domain to Top Level Domain Holdings Limited (2014-10-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-abogado">Transfer of the .abogado domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABOGADO = new TLD("abogado", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-07-10"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.abudhabi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160405-abudhabi">Delegation of the .abudhabi domain to Abu Dhabi Systems and Information Centre (2016-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ABUDHABI = new TLD("abudhabi", Type.GENERIC, "Abu Dhabi Systems and Information Centre", LocalDate.parse("2016-02-26"), LocalDate.parse("2019-11-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ac/">http://www.nic.ac/</a><br><b>WHOIS Server:</b> whois.nic.ac<br>
     *
     */
    public static final @NotNull TLD AC = new TLD("ac", Type.COUNTRY_CODE, "Internet Computer Bureau Limited", LocalDate.parse("1997-12-19"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.academy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-academy">Delegation of the .ACADEMY domain to Half Oaks, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACADEMY = new TLD("academy", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.accenture.com">http://www.accenture.com</a><br><b>WHOIS Server:</b> whois.nic.accenture<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150506-accenture">Delegation of the .accenture domain to Accenture plc (2015-05-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACCENTURE = new TLD("accenture", Type.GENERIC, "Accenture plc", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.accountant">http://nic.accountant</a><br><b>WHOIS Server:</b> whois.nic.accountant<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-accountant">Delegation of the .accountant domain to dot Accountant Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACCOUNTANT = new TLD("accountant", Type.GENERIC, "dot Accountant Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.accountants<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140502-accountants">Delegation of the .accountants domain to Knob Town, LLC (2014-05-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACCOUNTANTS = new TLD("accountants", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.aco.com">http://www.aco.com</a><br><b>WHOIS Server:</b> whois.nic.aco<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150825-aco">Delegation of the .aco domain to ACO Severin Ahlmann GmbH and Co. KG (2015-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACO = new TLD("aco", Type.GENERIC, "ACO Severin Ahlmann GmbH and Co. KG", LocalDate.parse("2015-07-16"), LocalDate.parse("2022-06-22"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140623-active">Delegation of the .active domain to The Active Network, Inc (2014-06-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190215-active">Revocation of the .active domain (2019-02-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACTIVE = new TLD("active", Type.GENERIC, null, LocalDate.parse("2014-06-19"), LocalDate.parse("2019-02-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.actor<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-actor">Delegation of the .actor domain to United TLD Holdco Ltd. (2014-02-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-actor">Transfer of the .actor domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ACTOR = new TLD("actor", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-02-20"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ad">http://www.nic.ad</a><br><br>
     *
     */
    public static final @NotNull TLD AD = new TLD("ad", Type.COUNTRY_CODE, "Andorra Telecom", LocalDate.parse("1996-01-09"), LocalDate.parse("2024-05-16"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160121-adac">Delegation of the .adac domain to Allgemeiner Deutscher Automobil-Club e.V. (ADAC) (2016-01-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221126-adac">Revocation of the .adac domain (2022-11-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ADAC = new TLD("adac", Type.GENERIC, null, LocalDate.parse("2015-11-20"), LocalDate.parse("2022-11-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-ads">Delegation of the .ads domain to Charleston Road Registry Inc. (2015-03-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ADS = new TLD("ads", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-02-05"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.adult">http://nic.adult</a><br><b>WHOIS Server:</b> whois.nic.adult<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141203-adult">Delegation of the .adult domain to ICM Registry AD LLC (2014-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ADULT = new TLD("adult", Type.GENERIC, "ICM Registry AD LLC", LocalDate.parse("2014-11-26"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://aeda.ae/">http://aeda.ae/</a><br><b>WHOIS Server:</b> whois.aeda.net.ae<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2008/ae-report-23jan2008.html">Report on the Redelegation of the .AE Top-Level Domain to the "Telecommunications Regulatory Authority" (2008-01-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AE = new TLD("ae", Type.COUNTRY_CODE, "Telecommunications and Digital Government Regulatory Authority (TDRA)", LocalDate.parse("1992-12-01"), LocalDate.parse("2021-10-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.aeg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-aeg">Delegation of the .aeg domain to Aktiebolaget Electrolux (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AEG = new TLD("aeg", Type.GENERIC, "Aktiebolaget Electrolux", LocalDate.parse("2015-06-11"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .AERO through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.information.aero">http://www.information.aero</a><br><b>WHOIS Server:</b> whois.aero<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/aero-report-19dec01.html">IANA Report on Establishment of the .AERO Top-Level Domain (2001-12-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AERO = new TLD("aero", Type.SPONSORED, "Societe Internationale de Telecommunications Aeronautique (SITA INC USA)", LocalDate.parse("2001-12-21"), LocalDate.parse("2023-08-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.aetna.com">https://www.aetna.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160518-aetna">Delegation of the .aetna domain to Aetna Life Insurance Company (2016-05-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AETNA = new TLD("aetna", Type.GENERIC, "Aetna Life Insurance Company", LocalDate.parse("2016-04-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.af">http://www.nic.af</a><br><b>WHOIS Server:</b> whois.nic.af<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2003/af-report-08jan2003.html">IANA Report on Redelegation of the .AF Top-Level Domain (2003-01-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AF = new TLD("af", Type.COUNTRY_CODE, "Ministry of Communications and IT", LocalDate.parse("1997-10-16"), LocalDate.parse("2022-11-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160726-afamilycompany">Delegation of the .afamilycompany domain to Johnson Shareholdings, Inc. (2016-07-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211203-afamilycompany">Revocation of the .afamilycompany domain (2021-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AFAMILYCOMPANY = new TLD("afamilycompany", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.afl">http://nic.afl</a><br><b>WHOIS Server:</b> whois.nic.afl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150326-afl">Delegation of the .afl domain to Australian Football League (2015-03-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AFL = new TLD("afl", Type.GENERIC, "Australian Football League", LocalDate.parse("2015-02-19"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.africa">http://nic.africa</a><br><b>WHOIS Server:</b> whois.nic.africa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170214-africa">Delegation of the .africa domain to ZA Central Registry NPC trading as Registry.Africa (2017-02-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AFRICA = new TLD("africa", Type.GENERIC, "ZA Central Registry NPC trading as Registry.Africa", LocalDate.parse("2017-02-11"), LocalDate.parse("2022-04-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ag">http://www.nic.ag</a><br><b>WHOIS Server:</b> whois.nic.ag<br>
     *
     */
    public static final @NotNull TLD AG = new TLD("ag", Type.COUNTRY_CODE, "UHSA School of Medicine", LocalDate.parse("1991-09-03"), LocalDate.parse("2021-02-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.akdn.org/">http://www.akdn.org/</a><br><b>WHOIS Server:</b> whois.nic.agakhan<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160413-agakhan">Delegation of the .agakhan domain to Fondation Aga Khan (Aga Khan Foundation) (2016-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AGAKHAN = new TLD("agakhan", Type.GENERIC, "Fondation Aga Khan (Aga Khan Foundation)", LocalDate.parse("2016-03-31"), LocalDate.parse("2023-08-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.agency<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-agency">Delegation of the .AGENCY domain to Steel Falls, LLC (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AGENCY = new TLD("agency", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://whois.ai">http://whois.ai</a><br><b>WHOIS Server:</b> whois.nic.ai<br>
     *
     */
    public static final @NotNull TLD AI = new TLD("ai", Type.COUNTRY_CODE, "Government of Anguilla", LocalDate.parse("1995-02-16"), LocalDate.parse("2024-02-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.aig.com">http://www.aig.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-aig">Delegation of the .aig domain to American International Group, Inc. (2015-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AIG = new TLD("aig", Type.GENERIC, "American International Group, Inc.", LocalDate.parse("2015-04-09"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160812-aigo">Delegation of the .aigo domain to aigo Digital Technology Co,Ltd. (2016-08-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200627-aigo">Revocation of the .aigo domain (2020-06-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AIGO = new TLD("aigo", Type.GENERIC, null, LocalDate.parse("2016-08-04"), LocalDate.parse("2020-06-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.airbus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160607-airbus">Delegation of the .airbus domain to Airbus S.A.S. (2016-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AIRBUS = new TLD("airbus", Type.GENERIC, "Airbus S.A.S.", LocalDate.parse("2016-05-26"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.airforce<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140428-airforce">Delegation of the .airforce domain to United TLD Holdco Ltd. (2014-04-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-airforce">Transfer of the .airforce domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AIRFORCE = new TLD("airforce", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.airtel.in">http://www.airtel.in</a><br><b>WHOIS Server:</b> whois.nic.airtel<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-airtel">Delegation of the .airtel domain to Bharti Airtel Limited (2015-07-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AIRTEL = new TLD("airtel", Type.GENERIC, "Bharti Airtel Limited", LocalDate.parse("2015-06-18"), LocalDate.parse("2023-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.akdn.org/">http://www.akdn.org/</a><br><b>WHOIS Server:</b> whois.nic.akdn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160413-akdn">Delegation of the .akdn domain to Fondation Aga Khan (Aga Khan Foundation) (2016-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AKDN = new TLD("akdn", Type.GENERIC, "Fondation Aga Khan (Aga Khan Foundation)", LocalDate.parse("2016-03-31"), LocalDate.parse("2023-08-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://akep.al/e-aplikime-domain/">https://akep.al/e-aplikime-domain/</a><br><br>
     *
     */
    public static final @NotNull TLD AL = new TLD("al", Type.COUNTRY_CODE, "Electronic and Postal Communications Authority - AKEP", LocalDate.parse("1992-04-21"), LocalDate.parse("2023-10-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-alfaromeo">Delegation of the .alfaromeo domain to Fiat Chrysler Automobiles N.V. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230605-alfaromeo">Revocation of the .alfaromeo domain (2023-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALFAROMEO = new TLD("alfaromeo", Type.GENERIC, null, LocalDate.parse("2016-07-21"), LocalDate.parse("2023-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.alibabagroup.com">http://www.alibabagroup.com</a><br><b>WHOIS Server:</b> whois.nic.alibaba<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160113-alibaba">Delegation of the .alibaba domain to Alibaba Group Holding Limited (2016-01-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALIBABA = new TLD("alibaba", Type.GENERIC, "Alibaba Group Holding Limited", LocalDate.parse("2016-01-08"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.alibabagroup.com">http://www.alibabagroup.com</a><br><b>WHOIS Server:</b> whois.nic.alipay<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160113-alipay">Delegation of the .alipay domain to Alibaba Group Holding Limited (2016-01-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALIPAY = new TLD("alipay", Type.GENERIC, "Alibaba Group Holding Limited", LocalDate.parse("2016-01-08"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.allfinanz-registry.de">http://www.allfinanz-registry.de</a><br><b>WHOIS Server:</b> whois.nic.allfinanz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140926-allfinanz">Delegation of the .allfinanz domain to Allfinanz Deutsche Vermögensberatung Aktiengesellschaft (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALLFINANZ = new TLD("allfinanz", Type.GENERIC, "Allfinanz Deutsche Vermögensberatung Aktiengesellschaft", LocalDate.parse("2014-09-25"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.allstate.com/">https://www.allstate.com/</a><br><b>WHOIS Server:</b> whois.nic.allstate<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160711-allstate">Delegation of the .allstate domain to Allstate Fire and Casualty Insurance Company (2016-07-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALLSTATE = new TLD("allstate", Type.GENERIC, "Allstate Fire and Casualty Insurance Company", LocalDate.parse("2016-02-05"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ally.com">http://www.ally.com</a><br><b>WHOIS Server:</b> whois.nic.ally<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160322-ally">Delegation of the .ally domain to Ally Financial Inc. (2016-03-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALLY = new TLD("ally", Type.GENERIC, "Ally Financial Inc.", LocalDate.parse("2015-10-22"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.mondomaine.alsace">https://www.mondomaine.alsace</a><br><b>WHOIS Server:</b> whois.nic.alsace<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141002-alsace">Delegation of the .alsace domain to REGION D ALSACE (2014-10-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALSACE = new TLD("alsace", Type.GENERIC, "REGION GRAND EST", LocalDate.parse("2014-09-18"), LocalDate.parse("2024-04-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.alstom.com">http://www.alstom.com</a><br><b>WHOIS Server:</b> whois.nic.alstom<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160609-alstom">Delegation of the .alstom domain to Alstom (2016-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ALSTOM = new TLD("alstom", Type.GENERIC, "ALSTOM", LocalDate.parse("2015-12-04"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amnic.net/">https://www.amnic.net/</a><br><b>WHOIS Server:</b> whois.amnic.net<br>
     *
     */
    public static final @NotNull TLD AM = new TLD("am", Type.COUNTRY_CODE, "\"Internet Society\" Non-governmental Organization", LocalDate.parse("1994-08-26"), LocalDate.parse("2022-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.amazon<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200602-amazon">Delegation of the .amazon domain to Amazon Registry Services, Inc. (2020-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMAZON = new TLD("amazon", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2020-05-28"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.americanexpress.com">https://www.americanexpress.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-americanexpress">Delegation of the .americanexpress domain to American Express Travel Related Services Company, Inc. (2016-08-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMERICANEXPRESS = new TLD("americanexpress", Type.GENERIC, "American Express Travel Related Services Company, Inc.", LocalDate.parse("2016-07-22"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.americanfamily<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160720-americanfamily">Delegation of the .americanfamily domain to AmFam, Inc. (2016-07-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMERICANFAMILY = new TLD("americanfamily", Type.GENERIC, "AmFam, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-07-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.americanexpress.com">https://www.americanexpress.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-amex">Delegation of the .amex domain to American Express Travel Related Services Company, Inc. (2016-08-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMEX = new TLD("amex", Type.GENERIC, "American Express Travel Related Services Company, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.amfam<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160720-amfam">Delegation of the .amfam domain to AmFam, Inc. (2016-07-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMFAM = new TLD("amfam", Type.GENERIC, "AmFam, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-07-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amica.com">http://www.amica.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-amica">Delegation of the .amica domain to Amica Mutual Insurance Company (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMICA = new TLD("amica", Type.GENERIC, "Amica Mutual Insurance Company", LocalDate.parse("2015-08-06"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.amsterdam">http://www.nic.amsterdam</a><br><b>WHOIS Server:</b> whois.nic.amsterdam<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141222-amsterdam">Delegation of the .amsterdam domain to Gemeente Amsterdam (2014-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AMSTERDAM = new TLD("amsterdam", Type.GENERIC, "Gemeente Amsterdam", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-07-18"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/cw-report-20111003.html">Report on the Delegation of the .CW domain representing Curaçao to the University of the Netherlands Antilles, and transitional arrangements for the .AN domain representing the Netherlands Antilles (2011-10-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AN = new TLD("an", Type.COUNTRY_CODE, null, LocalDate.parse("1993-09-09"), LocalDate.parse("2015-08-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sas.com">http://www.sas.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151216-analytics">Delegation of the .analytics domain to Campus IP LLC (2015-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ANALYTICS = new TLD("analytics", Type.GENERIC, "Campus IP LLC", LocalDate.parse("2015-11-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141107-android">Delegation of the .android domain to Charleston Road Registry Inc. (2014-11-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ANDROID = new TLD("android", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-10-30"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.teleinfo.cn">http://www.teleinfo.cn</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160325-anquan">Delegation of the .anquan domain to QIHOO 360 Technology Co. Ltd. (2016-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ANQUAN = new TLD("anquan", Type.GENERIC, "QIHOO 360 TECHNOLOGY CO. LTD.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.anz.com/">http://www.anz.com/</a><br><b>WHOIS Server:</b> whois.nic.anz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160615-anz">Delegation of the .anz domain to Australia and New Zealand Banking Group Limited (2016-06-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ANZ = new TLD("anz", Type.GENERIC, "Australia and New Zealand Banking Group Limited", LocalDate.parse("2016-06-09"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dns.ao/">http://www.dns.ao/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2020/ao-report-20200605.html">Transfer of the .AO (Angola) top-level domain to the Ministry of Telecommunications and Information Technologies (2020-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AO = new TLD("ao", Type.COUNTRY_CODE, "Ministry of Telecommunications and Information Technologies (MTTI)", LocalDate.parse("1995-11-15"), LocalDate.parse("2024-06-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.aol.com">http://www.aol.com</a><br><b>WHOIS Server:</b> whois.nic.aol<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161103-aol">Delegation of the .aol domain to AOL Inc. (2016-11-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AOL = new TLD("aol", Type.GENERIC, "Yahoo Inc.", LocalDate.parse("2016-10-27"), LocalDate.parse("2024-05-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.apartments<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150209-apartments">Delegation of the .apartments domain to June Maple, LLC (2015-02-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD APARTMENTS = new TLD("apartments", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-02-05"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150629-app">Delegation of the .app domain to Charleston Road Registry Inc. (2015-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD APP = new TLD("app", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-06-25"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.apple.com/">http://www.apple.com/</a><br><b>WHOIS Server:</b> whois.nic.apple<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151027-apple">Delegation of the .apple domain to Apple Inc. (2015-10-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD APPLE = new TLD("apple", Type.GENERIC, "Apple Inc.", LocalDate.parse("2015-07-02"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD AQ = new TLD("aq", Type.COUNTRY_CODE, "Antarctica Network Information Centre Limited", LocalDate.parse("1992-02-26"), LocalDate.parse("2021-02-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.aquarelle.com">https://www.aquarelle.com</a><br><b>WHOIS Server:</b> whois.nic.aquarelle<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141125-aquarelle">Delegation of the .aquarelle domain to Aquarelle.com (2014-11-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AQUARELLE = new TLD("aquarelle", Type.GENERIC, "Aquarelle.com", LocalDate.parse("2014-11-06"), LocalDate.parse("2024-04-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.ar">https://nic.ar</a><br><b>WHOIS Server:</b> whois.nic.ar<br>
     *
     */
    public static final @NotNull TLD AR = new TLD("ar", Type.COUNTRY_CODE, "Presidencia de la Nación , Secretaría Legal y Técnica", LocalDate.parse("1987-09-23"), LocalDate.parse("2024-04-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.arab<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170522-arab">Delegation of the .arab domain to League of Arab States (2017-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ARAB = new TLD("arab", Type.GENERIC, "League of Arab States", LocalDate.parse("2017-05-11"), LocalDate.parse("2020-05-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.aramco.com">http://www.aramco.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151013-aramco">Delegation of the .aramco domain to Aramco Services Company (2015-10-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ARAMCO = new TLD("aramco", Type.GENERIC, "Aramco Services Company", LocalDate.parse("2015-08-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.archi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-archi">Delegation of the .archi domain to STARTING DOT LIMITED (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190508-archi">Transfer of the .archi domain to Afilias Limited (2019-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ARCHI = new TLD("archi", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-03-20"), LocalDate.parse("2023-08-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.army<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-army">Delegation of the .army domain to United TLD Holdco, Ltd (2014-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-army">Transfer of the .army domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ARMY = new TLD("army", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.iana.org/domains/arpa">https://www.iana.org/domains/arpa</a><br><b>WHOIS Server:</b> whois.iana.org<br>
     *
     */
    public static final @NotNull TLD ARPA = new TLD("arpa", Type.INFRASTRUCTURE, "Internet Architecture Board (IAB)", LocalDate.parse("1985-01-01"), LocalDate.parse("2023-11-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://art.art/">http://art.art/</a><br><b>WHOIS Server:</b> whois.nic.art<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160622-art">Delegation of the .art domain to UK Creative Ideas Limited (2016-06-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ART = new TLD("art", Type.GENERIC, "UK Creative Ideas Limited", LocalDate.parse("2016-06-09"), LocalDate.parse("2024-04-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.arte<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151016-arte">Delegation of the .arte domain to Association Relative à la Télévision Européenne G.E.I.E. (2015-10-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ARTE = new TLD("arte", Type.GENERIC, "Association Relative à la Télévision Européenne G.E.I.E.", LocalDate.parse("2015-09-24"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.as">http://www.nic.as</a><br><b>WHOIS Server:</b> whois.nic.as<br>
     *
     */
    public static final @NotNull TLD AS = new TLD("as", Type.COUNTRY_CODE, "AS Domain Registry", LocalDate.parse("1997-06-12"), LocalDate.parse("2022-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.asda.com">http://www.asda.com</a><br><b>WHOIS Server:</b> whois.nic.asda<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160810-asda">Delegation of the .asda domain to Wal-Mart Stores, Inc. (2016-08-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ASDA = new TLD("asda", Type.GENERIC, "Wal-Mart Stores, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-21"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .ASIA through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.registry.asia">http://www.registry.asia</a><br><b>WHOIS Server:</b> whois.nic.asia<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/asia-report-12apr2007.html">IANA Report on the Delegation of the .ASIA Top-Level Domain (2007-04-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ASIA = new TLD("asia", Type.SPONSORED, "DotAsia Organisation Ltd.", LocalDate.parse("2007-05-02"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.associates<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-associates">Delegation of the .associates domain to Baxter Hill, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ASSOCIATES = new TLD("associates", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.at/">http://www.nic.at/</a><br><b>WHOIS Server:</b> whois.nic.at<br>
     *
     */
    public static final @NotNull TLD AT = new TLD("at", Type.COUNTRY_CODE, "nic.at GmbH", LocalDate.parse("1988-01-20"), LocalDate.parse("2023-04-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gap.com">http://www.gap.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-athleta">Delegation of the .athleta domain to The Gap, Inc. (2016-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ATHLETA = new TLD("athleta", Type.GENERIC, "The Gap, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.attorney<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-attorney">Delegation of the .attorney domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-attorney">Transfer of the .attorney domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ATTORNEY = new TLD("attorney", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.auda.org.au/domains/au-domains/">https://www.auda.org.au/domains/au-domains/</a><br><b>WHOIS Server:</b> whois.auda.org.au<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/au-report-31aug01.html">IANA Report on Request for Redelegation of the .AU Top-Level Domain (2001-08-31)</a></li>
     *   <li><a href="https://iana.org/reports/2001/au-report-19nov01.html">Second IANA Report on Request for Redelegation of the .AU Top-Level Domain (2001-11-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AU = new TLD("au", Type.COUNTRY_CODE, ".au Domain Administration (auDA)", LocalDate.parse("1986-03-05"), LocalDate.parse("2024-03-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.auction<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140714-auction">Delegation of the .auction domain to Sand Galley, LLC (2014-07-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-auction">Transfer of the .auction domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUCTION = new TLD("auction", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-04-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.audi.com">http://www.audi.com</a><br><b>WHOIS Server:</b> whois.nic.audi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151117-audi">Delegation of the .audi domain to Audi Aktiengesellschaft (2015-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUDI = new TLD("audi", Type.GENERIC, "AUDI Aktiengesellschaft", LocalDate.parse("2015-11-12"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.audible<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160603-audible">Delegation of the .audible domain to Amazon Registry Services, Inc. (2016-06-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUDIBLE = new TLD("audible", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.audio">https://nic.audio</a><br><b>WHOIS Server:</b> whois.nic.audio<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140512-audio">Delegation of the .audio domain to Uniregistry, Corp. (2014-05-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-audio">Transfer of the .audio domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUDIO = new TLD("audio", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://auspost.com.au">https://auspost.com.au</a><br><b>WHOIS Server:</b> whois.nic.auspost<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160815-auspost">Delegation of the .auspost domain to Australian Postal Corporation (2016-08-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUSPOST = new TLD("auspost", Type.GENERIC, "Australian Postal Corporation", LocalDate.parse("2016-08-11"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.author<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-author">Delegation of the .author domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUTHOR = new TLD("author", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.auto">https://www.nic.auto</a><br><b>WHOIS Server:</b> whois.nic.auto<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-auto">Delegation of the .auto domain to Uniregistry, Corp. (2015-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200922-auto">Transfer of the .auto domain to XYZ.COM LLC (2020-09-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUTO = new TLD("auto", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.autos">http://nic.autos</a><br><b>WHOIS Server:</b> whois.nic.autos<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140519-autos">Delegation of the .autos domain to DERAutos, LLC (2014-05-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210405-autos">Transfer of the .autos domain to XYZ.COM LLC (2021-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AUTOS = new TLD("autos", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-05-01"), LocalDate.parse("2023-11-27"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160307-avianca">Delegation of the .avianca domain to Aerovias del Continente Americano S.A. Avianca (2016-03-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191211-avianca">Transfer of the .avianca domain to Avianca Holdings S.A. (2019-12-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240327-avianca">Revocation of the .avianca domain (2024-03-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AVIANCA = new TLD("avianca", Type.GENERIC, null, LocalDate.parse("2016-02-26"), LocalDate.parse("2024-03-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.aw<br>
     *
     */
    public static final @NotNull TLD AW = new TLD("aw", Type.COUNTRY_CODE, "SETAR", LocalDate.parse("1996-02-20"), LocalDate.parse("2023-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.aws<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160323-aws">Delegation of the .aws domain to Amazon Registry Services, Inc. (2016-03-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210105-aws">Transfer of the .aws domain to AWS Registry LLC (2021-01-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AWS = new TLD("aws", Type.GENERIC, "AWS Registry LLC", LocalDate.parse("2016-03-18"), LocalDate.parse("2021-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.whois.ax">http://www.whois.ax</a><br><b>WHOIS Server:</b> whois.ax<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2006/ax-report-09jun2006.pdf">IANA Report on the Delegation of the .AX Top-Level Domain (2006-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AX = new TLD("ax", Type.COUNTRY_CODE, "Ålands landskapsregering", LocalDate.parse("2006-06-21"), LocalDate.parse("2017-08-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://domains.axa/">https://domains.axa/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140317-axa">Delegation of the .axa domain to AXA SA (2014-03-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201111-axa">Transfer of the .axa domain to AXA Group Operations SAS (2020-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AXA = new TLD("axa", Type.GENERIC, "AXA Group Operations SAS", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.whois.az/">http://www.whois.az/</a><br><br>
     *
     */
    public static final @NotNull TLD AZ = new TLD("az", Type.COUNTRY_CODE, "IntraNS", LocalDate.parse("1993-08-25"), LocalDate.parse("2022-03-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.microsoft.com">http://www.microsoft.com</a><br><b>WHOIS Server:</b> whois.nic.azure<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150604-azure">Delegation of the .azure domain to Microsoft Corporation (2015-06-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD AZURE = new TLD("azure", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ba">http://www.nic.ba</a><br><br>
     *
     */
    public static final @NotNull TLD BA = new TLD("ba", Type.COUNTRY_CODE, "Universtiy Telinformatic Centre (UTIC)", LocalDate.parse("1996-08-14"), LocalDate.parse("2020-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.baby/">https://nic.baby/</a><br><b>WHOIS Server:</b> whois.nic.baby<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160406-baby">Delegation of the .baby domain to Johnson and Johnson Services, Inc. (2016-04-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190324-baby">Transfer of the .baby domain to XYZ.COM LLC (2019-03-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BABY = new TLD("baby", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2016-01-14"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.zdns.cn">http://www.zdns.cn</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151229-baidu">Delegation of the .baidu domain to Baidu, Inc. (2015-12-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BAIDU = new TLD("baidu", Type.GENERIC, "Baidu, Inc.", LocalDate.parse("2015-12-17"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.citigroup.com">http://www.citigroup.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160726-banamex">Delegation of the .banamex domain to Citigroup Inc. (2016-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BANAMEX = new TLD("banamex", Type.GENERIC, "Citigroup Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-bananarepublic">Delegation of the .bananarepublic domain to The Gap, Inc. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240122-bananarepublic">Revocation of the .bananarepublic domain (2024-01-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BANANAREPUBLIC = new TLD("bananarepublic", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2024-01-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.band<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141002-band">Delegation of the .band domain to United TLD Holdco, Ltd (2014-10-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-band">Transfer of the .band domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BAND = new TLD("band", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-10-01"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.ftld.com/">https://www.ftld.com/</a><br><b>WHOIS Server:</b> whois.nic.bank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150105-bank">Delegation of the .bank domain to fTLD Registry Services, LLC (2015-01-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BANK = new TLD("bank", Type.GENERIC, "fTLD Registry Services, LLC", LocalDate.parse("2014-11-26"), LocalDate.parse("2024-07-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.bar">http://nic.bar</a><br><b>WHOIS Server:</b> whois.nic.bar<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-bar">Delegation of the .bar domain to Punto 2012 Sociedad Anonima Promotora de Inversion de Capital Variable (2014-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BAR = new TLD("bar", Type.GENERIC, "Punto 2012 Sociedad Anonima Promotora de Inversion de Capital Variable", LocalDate.parse("2014-02-13"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.barcelona.cat">http://www.barcelona.cat</a><br><b>WHOIS Server:</b> whois.nic.barcelona<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-barcelona">Delegation of the .barcelona domain to Municipi de Barcelona (2015-07-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BARCELONA = new TLD("barcelona", Type.GENERIC, "Municipi de Barcelona", LocalDate.parse("2015-06-04"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.barclaycard.com/">http://www.barclaycard.com/</a><br><b>WHOIS Server:</b> whois.nic.barclaycard<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-barclaycard">Delegation of the .barclaycard domain to Barclays Bank PLC (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BARCLAYCARD = new TLD("barclaycard", Type.GENERIC, "Barclays Bank PLC", LocalDate.parse("2015-01-08"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.barclays.com/">http://www.barclays.com/</a><br><b>WHOIS Server:</b> whois.nic.barclays<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-barclays">Delegation of the .barclays domain to Barclays Bank PLC (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BARCLAYS = new TLD("barclays", Type.GENERIC, "Barclays Bank PLC", LocalDate.parse("2015-01-08"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.barefootwine.com">http://www.barefootwine.com</a><br><b>WHOIS Server:</b> whois.nic.barefoot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160322-barefoot">Delegation of the .barefoot domain to Gallo Vineyards, Inc. (2016-03-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BAREFOOT = new TLD("barefoot", Type.GENERIC, "Gallo Vineyards, Inc.", LocalDate.parse("2015-10-24"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.bargains<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-bargains">Delegation of the .BARGAINS domain to Half Hallow, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BARGAINS = new TLD("bargains", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mlb.com">http://www.mlb.com</a><br><b>WHOIS Server:</b> whois.nic.baseball<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161028-baseball">Delegation of the .baseball domain to MLB Advanced Media DH, LLC (2016-10-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BASEBALL = new TLD("baseball", Type.GENERIC, "MLB Advanced Media DH, LLC", LocalDate.parse("2016-09-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.fiba.com/">http://www.fiba.com/</a><br><b>WHOIS Server:</b> whois.nic.basketball<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161018-basketball">Delegation of the .basketball domain to Fédération Internationale de Basketball (FIBA) (2016-10-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BASKETBALL = new TLD("basketball", Type.GENERIC, "Fédération Internationale de Basketball (FIBA)", LocalDate.parse("2016-10-13"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bauhaus.info">http://www.bauhaus.info</a><br><b>WHOIS Server:</b> whois.nic.bauhaus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150403-bauhaus">Delegation of the .bauhaus domain to Werkhaus GmbH (2015-04-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BAUHAUS = new TLD("bauhaus", Type.GENERIC, "Werkhaus GmbH", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-07-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.bayern">http://nic.bayern</a><br><b>WHOIS Server:</b> whois.nic.bayern<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140429-bayern">Delegation of the .bayern domain to Bayern Connect GmbH (2014-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BAYERN = new TLD("bayern", Type.GENERIC, "Bayern Connect GmbH", LocalDate.parse("2014-03-13"), LocalDate.parse("2023-06-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.whois.telecoms.gov.bb/">http://www.whois.telecoms.gov.bb/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/bb-report-20nov2007.html">Report on the Redelegation of the .BB Top-Level Domain to the "Ministry of Economic Affairs and Development’s Telecommunications Unit" (2007-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BB = new TLD("bb", Type.COUNTRY_CODE, "Ministry of Innovation, Science and Smart Technology", LocalDate.parse("1991-09-03"), LocalDate.parse("2022-03-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bbc.co.uk">http://www.bbc.co.uk</a><br><b>WHOIS Server:</b> whois.nic.bbc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-bbc">Delegation of the .bbc domain to British Broadcasting Corporation (2015-03-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BBC = new TLD("bbc", Type.GENERIC, "British Broadcasting Corporation", LocalDate.parse("2015-03-12"), LocalDate.parse("2021-11-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://bbt.com">http://bbt.com</a><br><b>WHOIS Server:</b> whois.nic.bbt<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160713-bbt">Delegation of the .bbt domain to BBT Corporation (2016-07-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BBT = new TLD("bbt", Type.GENERIC, "BB&T Corporation", LocalDate.parse("2016-05-12"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.bbva<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150522-bbva">Delegation of the .bbva domain to Banco Bilbao Vizcaya Argentaria, S.A. (2015-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BBVA = new TLD("bbva", Type.GENERIC, "BANCO BILBAO VIZCAYA ARGENTARIA, S.A.", LocalDate.parse("2015-04-23"), LocalDate.parse("2021-11-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bcg.com">http://www.bcg.com</a><br><b>WHOIS Server:</b> whois.nic.bcg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160307-bcg">Delegation of the .bcg domain to The Boston Consulting Group, Inc. (2016-03-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BCG = new TLD("bcg", Type.GENERIC, "The Boston Consulting Group, Inc.", LocalDate.parse("2016-02-11"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.barcelona.cat">http://www.barcelona.cat</a><br><b>WHOIS Server:</b> whois.nic.bcn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-bcn">Delegation of the .bcn domain to Municipi de Barcelona (2015-07-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BCN = new TLD("bcn", Type.GENERIC, "Municipi de Barcelona", LocalDate.parse("2015-06-04"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://domainreg.btcl.com.bd/">http://domainreg.btcl.com.bd/</a><br><br>
     *
     */
    public static final @NotNull TLD BD = new TLD("bd", Type.COUNTRY_CODE, "Posts and Telecommunications Division", LocalDate.parse("1999-05-20"), LocalDate.parse("2024-05-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dnsbelgium.be">https://www.dnsbelgium.be</a><br><b>WHOIS Server:</b> whois.dns.be<br>
     *
     */
    public static final @NotNull TLD BE = new TLD("be", Type.COUNTRY_CODE, "DNS Belgium vzw/asbl", LocalDate.parse("1988-08-05"), LocalDate.parse("2021-03-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://beatsbydre.com">http://beatsbydre.com</a><br><b>WHOIS Server:</b> whois.nic.beats<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151027-beats">Delegation of the .beats domain to Beats Electronics, LLC (2015-10-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BEATS = new TLD("beats", Type.GENERIC, "Beats Electronics, LLC", LocalDate.parse("2015-08-13"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.beauty/">https://nic.beauty/</a><br><b>WHOIS Server:</b> whois.nic.beauty<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160713-beauty">Delegation of the .beauty domain to L'Oréal (2016-07-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200915-beauty">Transfer of the .beauty domain to XYZ.COM LLC (2020-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BEAUTY = new TLD("beauty", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-03-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.beer/">http://nic.beer/</a><br><b>WHOIS Server:</b> whois.nic.beer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140512-beer">Delegation of the .beer domain to Top Level Domain Holdings Limited (2014-05-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-beer">Transfer of the .beer domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BEER = new TLD("beer", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.bentley/">http://www.nic.bentley/</a><br><b>WHOIS Server:</b> whois.nic.bentley<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150707-bentley">Delegation of the .bentley domain to Bentley Motors Limited (2015-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BENTLEY = new TLD("bentley", Type.GENERIC, "Bentley Motors Limited", LocalDate.parse("2015-05-14"), LocalDate.parse("2021-11-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.berlin/">http://nic.berlin/</a><br><b>WHOIS Server:</b> whois.nic.berlin<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140103-berlin">Delegation of the .BERLIN domain to dotBERLIN GmbH and Co. KG (2014-01-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BERLIN = new TLD("berlin", Type.GENERIC, "dotBERLIN GmbH and Co. KG", LocalDate.parse("2013-12-19"), LocalDate.parse("2022-05-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.go.best">https://www.go.best</a><br><b>WHOIS Server:</b> whois.nic.best<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-best">Delegation of the .best domain to BestTLD Pty Ltd (2014-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BEST = new TLD("best", Type.GENERIC, "BestTLD Pty Ltd", LocalDate.parse("2014-02-20"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.bestbuy">http://nic.bestbuy</a><br><b>WHOIS Server:</b> whois.nic.bestbuy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-bestbuy">Delegation of the .bestbuy domain to BBY Solutions, Inc. (2016-07-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BESTBUY = new TLD("bestbuy", Type.GENERIC, "BBY Solutions, Inc.", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-01-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.bet<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150721-bet">Delegation of the .bet domain to Afilias plc (2015-07-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BET = new TLD("bet", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2015-07-16"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registre.bf">https://www.registre.bf</a><br><b>WHOIS Server:</b> whois.registre.bf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/bf-report-07jan2011.html">Report on the Redelegation of the .BF domain representing Burkina Faso to the Autorité de Régulation des Communications Electroniques (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BF = new TLD("bf", Type.COUNTRY_CODE, "Autorité de Régulation des Communications Electroniques et des Postes (ARCEP)", LocalDate.parse("1993-03-29"), LocalDate.parse("2023-10-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.register.bg">http://www.register.bg</a><br><b>WHOIS Server:</b> whois.register.bg<br>
     *
     */
    public static final @NotNull TLD BG = new TLD("bg", Type.COUNTRY_CODE, "Register.BG", LocalDate.parse("1995-01-03"), LocalDate.parse("2021-02-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.bh<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/bh-report-20120108.html">Report on the Redelegation of the .BH domain representing Bahrain to the Telecommunications Regulatory Authority (2012-01-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BH = new TLD("bh", Type.COUNTRY_CODE, "Telecommunications Regulatory Authority (TRA)", LocalDate.parse("1994-02-01"), LocalDate.parse("2024-07-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bharti.com/wps/wcm/connect/BhartiPortal/Bharti/home">http://www.bharti.com/wps/wcm/connect/BhartiPortal/Bharti/home</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150609-bharti">Delegation of the .bharti domain to Bharti Enterprises (Holding) Private Limited (2015-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BHARTI = new TLD("bharti", Type.GENERIC, "Bharti Enterprises (Holding) Private Limited", LocalDate.parse("2015-05-08"), LocalDate.parse("2023-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.bi">http://www.nic.bi</a><br><b>WHOIS Server:</b> whois1.nic.bi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/bi-report-16jul02.html">IANA Report on Redelegation of the .BI Top-Level Domain (2002-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BI = new TLD("bi", Type.COUNTRY_CODE, "Centre National de l'Informatique", LocalDate.parse("1996-10-21"), LocalDate.parse("2022-06-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://get.bible">http://get.bible</a><br><b>WHOIS Server:</b> whois.nic.bible<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150529-bible">Delegation of the .bible domain to American Bible Society (2015-05-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BIBLE = new TLD("bible", Type.GENERIC, "American Bible Society", LocalDate.parse("2015-05-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.bid">http://nic.bid</a><br><b>WHOIS Server:</b> whois.nic.bid<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140226-bid">Delegation of the .bid domain to dot Bid Limited (2014-02-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BID = new TLD("bid", Type.GENERIC, "dot Bid Limited", LocalDate.parse("2014-02-13"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.bike<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-bike">Delegation of the .BIKE domain to Grand Hollow, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BIKE = new TLD("bike", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.microsoft.com">http://www.microsoft.com</a><br><b>WHOIS Server:</b> whois.nic.bing<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150604-bing">Delegation of the .bing domain to Microsoft Corporation (2015-06-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BING = new TLD("bing", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.bingo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150203-bingo">Delegation of the .bingo domain to Sand Cedar, LLC (2015-02-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BINGO = new TLD("bingo", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-01-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.bio<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140529-bio">Delegation of the .bio domain to Starting Dot Limited (2014-05-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190508-bio">Transfer of the .bio domain to Afilias Limited (2019-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BIO = new TLD("bio", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-05-15"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .BIZ through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.nic.biz">http://www.nic.biz</a><br><b>WHOIS Server:</b> whois.nic.biz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/biz-info-report-25jun01.html">IANA Report on Establishment of the .BIZ and .INFO Top-Level Domains (2001-06-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201019-biz">Transfer of the .biz domain to Registry Services, LLC (2020-10-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BIZ = new TLD("biz", Type.GENERIC_RESTRICTED, "Registry Services, LLC", LocalDate.parse("2001-06-26"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.bj">http://www.nic.bj</a><br><b>WHOIS Server:</b> whois.nic.bj<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2019/bj-report-20190607.html">Transfer of the .BJ (Benin) top-level domain to Autorité de Régulation des Communications Electroniques et de la Poste du Bénin (ARCEP BENIN) (2019-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BJ = new TLD("bj", Type.COUNTRY_CODE, "Autorité de Régulation des Communications Electroniques et de la Poste du Bénin (ARCEP BENIN)", LocalDate.parse("1996-01-18"), LocalDate.parse("2023-04-14"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD BL = new TLD("bl", Type.COUNTRY_CODE, null, LocalDate.parse("2007-10-11"), LocalDate.parse("2007-10-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.black<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140325-black">Delegation of the .black domain to Afilias Limited (2014-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLACK = new TLD("black", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-03-20"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.blackfriday/">http://nic.blackfriday/</a><br><b>WHOIS Server:</b> whois.nic.blackfriday<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140418-blackfriday">Delegation of the .blackfriday domain to Uniregistry, Corp. (2014-04-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220607-blackfriday">Transfer of the .blackfriday domain to Registry Services, LLC (2022-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLACKFRIDAY = new TLD("blackfriday", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160616-blanco">Delegation of the .blanco domain to Blanco GmbH + Co. KG (2016-06-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190212-blanco">Revocation of the .blanco domain (2019-02-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLANCO = new TLD("blanco", Type.GENERIC, null, LocalDate.parse("2016-06-03"), LocalDate.parse("2019-02-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com/">http://www.dish.com/</a><br><b>WHOIS Server:</b> whois.nic.blockbuster<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160729-blockbuster">Delegation of the .blockbuster domain to Dish DBS Corporation (2016-07-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLOCKBUSTER = new TLD("blockbuster", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.blog">http://nic.blog</a><br><b>WHOIS Server:</b> whois.nic.blog<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160516-blog">Delegation of the .blog domain to Knock Knock WHOIS There, LLC (2016-05-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLOG = new TLD("blog", Type.GENERIC, "Knock Knock WHOIS There, LLC", LocalDate.parse("2016-05-12"), LocalDate.parse("2023-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.bloomberg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141031-bloomberg">Delegation of the .bloomberg domain to Bloomberg IP Holdings LLC (2014-10-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLOOMBERG = new TLD("bloomberg", Type.GENERIC, "Bloomberg IP Holdings LLC", LocalDate.parse("2014-10-16"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.blue<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140129-blue">Delegation of the .blue domain to Afilias Limited (2014-01-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BLUE = new TLD("blue", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-01-23"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bermudanic.bm">http://www.bermudanic.bm</a><br><b>WHOIS Server:</b> whois.nic.bm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/bm-report-16oct2007.html">Report on the Redelegation of the .BM Top-Level Domain to the "Registrar General of Bermuda" (2007-10-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BM = new TLD("bm", Type.COUNTRY_CODE, "Registry General Department, Ministry of Home Affairs", LocalDate.parse("1993-03-31"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.bms<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150917-bms">Delegation of the .bms domain to Bristol-Myers Squibb Company (2015-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BMS = new TLD("bms", Type.GENERIC, "Bristol-Myers Squibb Company", LocalDate.parse("2015-09-10"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.bmw.com">http://nic.bmw.com</a><br><b>WHOIS Server:</b> whois.nic.bmw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140618-bmw">Delegation of the .bmw domain to Bayerische Motoren Werke Aktiengesellschaft (2014-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BMW = new TLD("bmw", Type.GENERIC, "Bayerische Motoren Werke Aktiengesellschaft", LocalDate.parse("2014-06-05"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bnnic.bn">http://www.bnnic.bn</a><br><b>WHOIS Server:</b> whois.bnnic.bn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/bn-report-20150413.html">Redelegation of the .BN domain representing Brunei Darussalam to Brunei Darussalam Network Information Centre Sdn Bhd (BNNIC) (2015-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BN = new TLD("bn", Type.COUNTRY_CODE, "Authority for Info-communications Technology Industry of Brunei Darussalam (AITI)", LocalDate.parse("1994-06-03"), LocalDate.parse("2024-05-14"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150623-bnl">Delegation of the .bnl domain to Banca Nazionale del Lavoro (2015-06-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190729-bnl">Revocation of the .bnl domain (2019-07-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BNL = new TLD("bnl", Type.GENERIC, null, LocalDate.parse("2015-06-11"), LocalDate.parse("2019-07-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bnpparibas.com/en/about-us">http://www.bnpparibas.com/en/about-us</a><br><b>WHOIS Server:</b> whois.nic.bnpparibas<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140811-bnpparibas">Delegation of the .bnpparibas domain to BNP Paribas (2014-08-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BNPPARIBAS = new TLD("bnpparibas", Type.GENERIC, "BNP Paribas", LocalDate.parse("2014-07-31"), LocalDate.parse("2023-08-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.bo">http://www.nic.bo</a><br><b>WHOIS Server:</b> whois.nic.bo<br>
     *
     */
    public static final @NotNull TLD BO = new TLD("bo", Type.COUNTRY_CODE, "Agencia para el Desarrollo de la Información de la Sociedad en Bolivia", LocalDate.parse("1991-02-26"), LocalDate.parse("2024-05-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.boats">http://nic.boats</a><br><b>WHOIS Server:</b> whois.nic.boats<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150223-boats">Delegation of the .boats domain to DERBoats, LLC (2015-02-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210405-boats">Transfer of the .boats domain to XYZ.COM LLC (2021-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOATS = new TLD("boats", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-02-13"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.boehringer-ingelheim.com/">http://www.boehringer-ingelheim.com/</a><br><b>WHOIS Server:</b> whois.nic.boehringer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151117-boehringer">Delegation of the .boehringer domain to Boehringer Ingelheim International GmbH (2015-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOEHRINGER = new TLD("boehringer", Type.GENERIC, "Boehringer Ingelheim International GmbH", LocalDate.parse("2015-11-06"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://bofa.com">http://bofa.com</a><br><b>WHOIS Server:</b> whois.nic.bofa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-bofa">Delegation of the .bofa domain to NMS Services, Inc. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20171009-bofa">Transfer of the .bofa domain to Bank of America Corporation (2017-10-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOFA = new TLD("bofa", Type.GENERIC, "Bank of America Corporation", LocalDate.parse("2016-07-28"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.gtlds.nic.br<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150925-bom">Delegation of the .bom domain to Núcleo de Informação e Coordenação do Ponto BR - NIC.br (2015-09-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOM = new TLD("bom", Type.GENERIC, "Núcleo de Informação e Coordenação do Ponto BR - NIC.br", LocalDate.parse("2015-05-29"), LocalDate.parse("2023-11-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.bond">http://nic.bond</a><br><b>WHOIS Server:</b> whois.nic.bond<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-bond">Delegation of the .bond domain to Bond University Limited (2015-03-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190920-bond">Transfer of the .bond domain to Shortdot SA (2019-09-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOND = new TLD("bond", Type.GENERIC, "Shortdot SA", LocalDate.parse("2014-10-30"), LocalDate.parse("2024-04-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-boo">Delegation of the .boo domain to Charleston Road Registry Inc. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOO = new TLD("boo", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.book<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-book">Delegation of the .book domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOOK = new TLD("book", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.booking.com">http://www.booking.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160719-booking">Delegation of the .booking domain to Booking.com B.V. (2016-07-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOOKING = new TLD("booking", Type.GENERIC, "Booking.com B.V.", LocalDate.parse("2016-07-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150803-boots">Delegation of the .boots domain to The Boots Company PLC (2015-08-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180405-boots">Revocation of the .boots domain (2018-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOOTS = new TLD("boots", Type.GENERIC, null, LocalDate.parse("2015-06-18"), LocalDate.parse("2018-04-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.bosch<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-bosch">Delegation of the .bosch domain to Robert Bosch GMBH (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOSCH = new TLD("bosch", Type.GENERIC, "Robert Bosch GMBH", LocalDate.parse("2015-12-18"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.bostik.com">https://www.bostik.com</a><br><b>WHOIS Server:</b> whois.nic.bostik<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151120-bostik">Delegation of the .bostik domain to Bostik SA (2015-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOSTIK = new TLD("bostik", Type.GENERIC, "Bostik SA", LocalDate.parse("2015-11-06"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.boston/">http://nic.boston/</a><br><b>WHOIS Server:</b> whois.nic.boston<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161128-boston">Delegation of the .boston domain to Boston TLD Management, LLC (2016-11-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220121-boston">Transfer of the .boston domain to Registry Services, LLC (2022-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOSTON = new TLD("boston", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2016-11-21"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.bot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-bot">Delegation of the .bot domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOT = new TLD("bot", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.boutique<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-boutique">Delegation of the .BOUTIQUE domain to Over Galley, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOUTIQUE = new TLD("boutique", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://intercap.inc">https://intercap.inc</a><br><b>WHOIS Server:</b> whois.nic.box<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-box">Delegation of the .box domain to NS1 Limited (2016-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200723-box">Transfer of the .box domain to .Box Inc. (2020-07-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201009-box">Transfer of the .box domain to Intercap Registry Inc. (2020-10-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BOX = new TLD("box", Type.GENERIC, "Intercap Registry Inc.", LocalDate.parse("2016-10-27"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD BQ = new TLD("bq", Type.COUNTRY_CODE, null, LocalDate.parse("2010-12-20"), LocalDate.parse("2010-12-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://registro.br/">http://registro.br/</a><br><b>WHOIS Server:</b> whois.registro.br<br>
     *
     */
    public static final @NotNull TLD BR = new TLD("br", Type.COUNTRY_CODE, "Comite Gestor da Internet no Brasil", LocalDate.parse("1989-04-18"), LocalDate.parse("2023-11-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.bradesco/">http://www.nic.bradesco/</a><br><b>WHOIS Server:</b> whois.nic.bradesco<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150624-bradesco">Delegation of the .bradesco domain to Banco Bradesco S.A. (2015-06-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BRADESCO = new TLD("bradesco", Type.GENERIC, "Banco Bradesco S.A.", LocalDate.parse("2015-05-29"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bridgestone.com/">http://www.bridgestone.com/</a><br><b>WHOIS Server:</b> whois.nic.bridgestone<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150428-bridgestone">Delegation of the .bridgestone domain to Bridgestone Corporation (2015-04-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BRIDGESTONE = new TLD("bridgestone", Type.GENERIC, "Bridgestone Corporation", LocalDate.parse("2015-04-16"), LocalDate.parse("2023-06-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.broadway">http://nic.broadway</a><br><b>WHOIS Server:</b> whois.nic.broadway<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151116-broadway">Delegation of the .broadway domain to Celebrate Broadway, Inc. (2015-11-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BROADWAY = new TLD("broadway", Type.GENERIC, "Celebrate Broadway, Inc.", LocalDate.parse("2015-10-08"), LocalDate.parse("2024-01-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.broker<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150420-broker">Delegation of the .broker domain to Dotbroker Registry Ltd (2015-04-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210629-broker">Transfer of the .broker domain to Dog Beach, LLC (2021-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BROKER = new TLD("broker", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.brother.com/">http://www.brother.com/</a><br><b>WHOIS Server:</b> whois.nic.brother<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150508-brother">Delegation of the .brother domain to Brother Industries, Ltd. (2015-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BROTHER = new TLD("brother", Type.GENERIC, "Brother Industries, Ltd.", LocalDate.parse("2015-04-09"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.brussels">http://www.nic.brussels</a><br><b>WHOIS Server:</b> whois.nic.brussels<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140612-brussels">Delegation of the .brussels domain to DNS.be vzw (2014-06-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BRUSSELS = new TLD("brussels", Type.GENERIC, "DNS.be vzw", LocalDate.parse("2014-06-05"), LocalDate.parse("2021-01-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.register.bs">http://www.register.bs</a><br><br>
     *
     */
    public static final @NotNull TLD BS = new TLD("bs", Type.COUNTRY_CODE, "University of The Bahamas", LocalDate.parse("1991-09-03"), LocalDate.parse("2019-08-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.bt">http://www.nic.bt</a><br><br>
     *
     */
    public static final @NotNull TLD BT = new TLD("bt", Type.COUNTRY_CODE, "Ministry of Information and Communications", LocalDate.parse("1997-07-16"), LocalDate.parse("2020-03-09"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140919-budapest">Delegation of the .budapest domain to Top Level Domain Holdings Limited (2014-09-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220217-budapest">Revocation of the .budapest domain (2022-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUDAPEST = new TLD("budapest", Type.GENERIC, null, LocalDate.parse("2014-02-28"), LocalDate.parse("2022-02-17"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151117-bugatti">Delegation of the .bugatti domain to Bugatti International SA (2015-11-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221007-bugatti">Revocation of the .bugatti domain (2022-10-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUGATTI = new TLD("bugatti", Type.GENERIC, null, LocalDate.parse("2015-11-12"), LocalDate.parse("2022-10-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://about.build/">https://about.build/</a><br><b>WHOIS Server:</b> whois.nic.build<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-build">Delegation of the .BUILD domain to Plan Bee LLC (2014-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUILD = new TLD("build", Type.GENERIC, "Plan Bee LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.builders<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-builders">Delegation of the .BUILDERS domain to Atomic Madison, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUILDERS = new TLD("builders", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.business<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140821-business">Delegation of the .business domain to Spring Cross, LLC (2014-08-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUSINESS = new TLD("business", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-08-18"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.buy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-buy">Delegation of the .buy domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUY = new TLD("buy", Type.GENERIC, "Amazon Registry Services, INC", LocalDate.parse("2015-11-20"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.buzznames.biz">http://www.buzznames.biz</a><br><b>WHOIS Server:</b> whois.nic.buzz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131216-buzz">Delegation of the .BUZZ domain to DOTSTRATEGY CO. (2013-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BUZZ = new TLD("buzz", Type.GENERIC, "DOTSTRATEGY CO.", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.norid.no/en/omnorid/toppdomenet-bv/">https://www.norid.no/en/omnorid/toppdomenet-bv/</a><br><br>
     *
     */
    public static final @NotNull TLD BV = new TLD("bv", Type.COUNTRY_CODE, "Norid A/S", LocalDate.parse("1997-08-21"), LocalDate.parse("2022-01-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.net.bw">http://nic.net.bw</a><br><b>WHOIS Server:</b> whois.nic.net.bw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/bw-report-20130808.html">Report on the Redelegation of the .BW domain representing Botswana to Botswana Communications Regulatory Authority (2013-08-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BW = new TLD("bw", Type.COUNTRY_CODE, "Botswana Communications Regulatory Authority (BOCRA)", LocalDate.parse("1993-03-19"), LocalDate.parse("2024-05-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://cctld.by">https://cctld.by</a><br><b>WHOIS Server:</b> whois.cctld.by<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2009/by-report-03feb2009.html">Report on the Redelegation of the .BY Top-Level Domain to "Open Contact Ltd" (2009-02-03)</a></li>
     *   <li><a href="https://iana.org/reports/2012/by-report-20120107.html">Report on the Redelegation of the .BY domain representing Belarus to Reliable Software Inc. (2012-01-07)</a></li>
     *   <li><a href="https://iana.org/reports/2022/belarus-report-20220527.html">Transfer of the .BY top-level domain and the .бел (“bel”) top-level domain representing Belarus to Belarusian Cloud Technologies LLC (2022-05-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BY = new TLD("by", Type.COUNTRY_CODE, "Belarusian Cloud Technologies LLC", LocalDate.parse("1994-05-10"), LocalDate.parse("2023-10-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.belizenic.bz">http://www.belizenic.bz</a><br><br>
     *
     */
    public static final @NotNull TLD BZ = new TLD("bz", Type.COUNTRY_CODE, "University of Belize", LocalDate.parse("1991-09-03"), LocalDate.parse("2020-11-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.pik.bzh">https://www.pik.bzh</a><br><b>WHOIS Server:</b> whois.nic.bzh<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140612-bzh">Delegation of the .bzh domain to Association www.bzh (2014-06-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD BZH = new TLD("bzh", Type.GENERIC, "Association www.bzh", LocalDate.parse("2014-05-29"), LocalDate.parse("2024-04-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cira.ca/">http://www.cira.ca/</a><br><b>WHOIS Server:</b> whois.cira.ca<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2000/ca-report-01dec00.html">IANA Report on Request for Redelegation of .CA Top-Level Domain (2000-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CA = new TLD("ca", Type.COUNTRY_CODE, "Canadian Internet Registration Authority (CIRA) Autorité Canadienne pour les enregistrements Internet (ACEI)", LocalDate.parse("1987-05-14"), LocalDate.parse("2023-09-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cab<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-cab">Delegation of the .CAB domain to Half Sunset, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAB = new TLD("cab", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cafe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150403-cafe">Delegation of the .cafe domain to Pioneer Canyon, LLC (2015-04-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAFE = new TLD("cafe", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140910-cal">Delegation of the .cal domain to Charleston Road Registry Inc. (2014-09-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAL = new TLD("cal", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.call<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-call">Delegation of the .call domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CALL = new TLD("call", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pvh.com/">http://www.pvh.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-calvinklein">Delegation of the .calvinklein domain to PVH gTLD Holdings LLC (2016-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CALVINKLEIN = new TLD("calvinklein", Type.GENERIC, "PVH gTLD Holdings LLC", LocalDate.parse("2016-07-28"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.cam/">https://nic.cam/</a><br><b>WHOIS Server:</b> whois.nic.cam<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160613-cam">Delegation of the .cam domain to AC Webconnecting Holding B.V. (2016-06-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220207-cam">Transfer of the .cam domain to CAM Connecting SARL (2022-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAM = new TLD("cam", Type.GENERIC, "CAM Connecting SARL", LocalDate.parse("2016-06-09"), LocalDate.parse("2024-04-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.camera<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-camera">Delegation of the .CAMERA domain to Atomic Maple, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAMERA = new TLD("camera", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.camp<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-camp">Delegation of the .CAMP domain to Delta Dynamite, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAMP = new TLD("camp", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140625-cancerresearch">Delegation of the .cancerresearch domain to Australian Cancer Research Foundation (2014-06-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221006-cancerresearch">Revocation of the .cancerresearch domain (2022-10-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CANCERRESEARCH = new TLD("cancerresearch", Type.GENERIC, null, LocalDate.parse("2014-06-19"), LocalDate.parse("2022-10-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.canon">http://nic.canon</a><br><b>WHOIS Server:</b> whois.nic.canon<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150202-canon">Delegation of the .canon domain to Canon Inc. (2015-02-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CANON = new TLD("canon", Type.GENERIC, "Canon Inc.", LocalDate.parse("2015-01-29"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://registry.net.za">http://registry.net.za</a><br><b>WHOIS Server:</b> whois.nic.capetown<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140617-capetown">Delegation of the .capetown domain to ZA Central Registry NPC trading as ZA Central Registry (2014-06-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAPETOWN = new TLD("capetown", Type.GENERIC, "ZA Central Registry NPC trading as ZA Central Registry", LocalDate.parse("2014-06-05"), LocalDate.parse("2022-05-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.capital<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-capital">Delegation of the .capital domain to Delta Mill, LLC (2014-04-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAPITAL = new TLD("capital", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.capitalone.com">http://www.capitalone.com</a><br><b>WHOIS Server:</b> whois.nic.capitalone<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160808-capitalone">Delegation of the .capitalone domain to Capital One Financial Corporation (2016-08-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAPITALONE = new TLD("capitalone", Type.GENERIC, "Capital One Financial Corporation", LocalDate.parse("2016-08-04"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.car">http://www.nic.car</a><br><b>WHOIS Server:</b> whois.nic.car<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150828-car">Delegation of the .car domain to Cars Registry Limited (2015-08-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200922-car">Transfer of the .car domain to XYZ.COM LLC (2020-09-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAR = new TLD("car", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-08-26"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.caravan.com">http://www.caravan.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140812-caravan">Delegation of the .caravan domain to Caravan International, Inc. (2014-08-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CARAVAN = new TLD("caravan", Type.GENERIC, "Caravan International, Inc.", LocalDate.parse("2014-05-22"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cards<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-cards">Delegation of the .cards domain to Foggy Hollow, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CARDS = new TLD("cards", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.care<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-care">Delegation of the .care domain to Goose Cross (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CARE = new TLD("care", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.career<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-career">Delegation of the .career domain to dotCareer LLC (2014-04-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAREER = new TLD("career", Type.GENERIC, "dotCareer LLC", LocalDate.parse("2014-03-20"), LocalDate.parse("2021-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.careers<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-careers">Delegation of the .CAREERS domain to Wild Corner, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAREERS = new TLD("careers", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.cars">http://nic.cars</a><br><b>WHOIS Server:</b> whois.nic.cars<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-cars">Delegation of the .cars domain to Uniregistry, Corp. (2015-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200922-cars">Transfer of the .cars domain to XYZ.COM LLC (2020-09-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CARS = new TLD("cars", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141209-cartier">Delegation of the .cartier domain to Richemont DNS Inc. (2014-12-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191115-cartier">Revocation of the .cartier domain (2019-11-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CARTIER = new TLD("cartier", Type.GENERIC, null, LocalDate.parse("2014-11-20"), LocalDate.parse("2019-11-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.casa/">http://nic.casa/</a><br><b>WHOIS Server:</b> whois.nic.casa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140919-casa">Delegation of the .casa domain to Top Level Domain Holdings Limited (2014-09-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-casa">Transfer of the .casa domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CASA = new TLD("casa", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-08-18"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.digity.case/case">https://www.digity.case/case</a><br><b>WHOIS Server:</b> whois.nic.case<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161028-case">Delegation of the .case domain to CNH Industrial N.V. (2016-10-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230531-case">Transfer of the .case domain to Digity, LLC (2023-05-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CASE = new TLD("case", Type.GENERIC, "Digity, LLC", LocalDate.parse("2016-10-27"), LocalDate.parse("2023-12-06"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161028-caseih">Delegation of the .caseih domain to CNH Industrial N.V. (2016-10-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210219-caseih">Revocation of the .caseih domain (2021-02-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CASEIH = new TLD("caseih", Type.GENERIC, null, LocalDate.parse("2016-10-27"), LocalDate.parse("2021-02-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cash<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-cash">Delegation of the .cash domain to Delta Lake, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CASH = new TLD("cash", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.casino<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150216-casino">Delegation of the .casino domain to Binky Sky, LLC (2015-02-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CASINO = new TLD("casino", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-02-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .CAT through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.domini.cat">http://www.domini.cat</a><br><b>WHOIS Server:</b> whois.nic.cat<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/cat-report-18nov2005.html">IANA Report on the Delegation of the .CAT Top-Level Domain (2005-11-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CAT = new TLD("cat", Type.SPONSORED, "Fundacio puntCAT", LocalDate.parse("2005-12-19"), LocalDate.parse("2024-03-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.catering<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-catering">Delegation of the .catering domain to New Falls, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CATERING = new TLD("catering", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pccs.va">http://www.pccs.va</a><br><b>WHOIS Server:</b> whois.nic.catholic<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161130-catholic">Delegation of the .catholic domain to Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication) (2016-11-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CATHOLIC = new TLD("catholic", Type.GENERIC, "Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication)", LocalDate.parse("2016-11-16"), LocalDate.parse("2023-12-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.commbank.com.au">http://www.commbank.com.au</a><br><b>WHOIS Server:</b> whois.nic.cba<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-cba">Delegation of the .cba domain to Commonwealth Bank of Australia (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CBA = new TLD("cba", Type.GENERIC, "COMMONWEALTH BANK OF AUSTRALIA", LocalDate.parse("2015-03-26"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cbn.com">http://www.cbn.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150211-cbn">Delegation of the .cbn domain to The Christian Broadcasting Network, Inc. (2015-02-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CBN = new TLD("cbn", Type.GENERIC, "The Christian Broadcasting Network, Inc.", LocalDate.parse("2014-11-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://cbre.com">http://cbre.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160630-cbre">Delegation of the .cbre domain to CBRE, Inc. (2016-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CBRE = new TLD("cbre", Type.GENERIC, "CBRE, Inc.", LocalDate.parse("2016-06-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-cbs">Delegation of the .cbs domain to CBS Domains Inc. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231025-cbs">Revocation of the .cbs domain (2023-10-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CBS = new TLD("cbs", Type.GENERIC, null, LocalDate.parse("2016-07-21"), LocalDate.parse("2023-10-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cc/">http://www.nic.cc/</a><br><b>WHOIS Server:</b> ccwhois.verisign-grs.com<br>
     *
     */
    public static final @NotNull TLD CC = new TLD("cc", Type.COUNTRY_CODE, "eNIC Cocos (Keeling) Islands Pty. Ltd. d/b/a Island Internet Services", LocalDate.parse("1997-10-13"), LocalDate.parse("2023-10-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cd/">http://www.nic.cd/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/cd-report-07jan2011.html">Redelegation of the .CD domain representing the Democratic Republic of the Congo to Office Congolais des Postes et Telecommunications (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CD = new TLD("cd", Type.COUNTRY_CODE, "Office Congolais des Postes et Télécommunications - OCPT", LocalDate.parse("1997-08-20"), LocalDate.parse("2021-02-10"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201209-ceb">Revocation of the .ceb domain (2020-12-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CEB = new TLD("ceb", Type.GENERIC, null, LocalDate.parse("2015-07-02"), LocalDate.parse("2020-12-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.center<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-center">Delegation of the .CENTER domain to Tin Mill, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CENTER = new TLD("center", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.peoplebrowsr.com">http://www.peoplebrowsr.com</a><br><b>WHOIS Server:</b> whois.nic.ceo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131223-ceo">Delegation of the .CEO domain to CEOTLD Pty Ltd (2013-12-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231024-ceo">Transfer of the .ceo domain to XYZ.COM LLC (2023-10-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CEO = new TLD("ceo", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cern.ch">http://www.cern.ch</a><br><b>WHOIS Server:</b> whois.nic.cern<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-cern">Delegation of the .cern domain to European Organization for Nuclear Research ("CERN") (2014-08-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CERN = new TLD("cern", Type.GENERIC, "European Organization for Nuclear Research (\"CERN\")", LocalDate.parse("2014-08-13"), LocalDate.parse("2023-08-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.cf">http://www.dot.cf</a><br><b>WHOIS Server:</b> whois.dot.cf<br>
     *
     */
    public static final @NotNull TLD CF = new TLD("cf", Type.COUNTRY_CODE, "Societe Centrafricaine de Telecommunications (SOCATEL)", LocalDate.parse("1996-04-24"), LocalDate.parse("2015-12-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.cfainstitute.org">https://www.cfainstitute.org</a><br><b>WHOIS Server:</b> whois.nic.cfa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-cfa">Delegation of the .cfa domain to CFA Institute (2015-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CFA = new TLD("cfa", Type.GENERIC, "CFA Institute", LocalDate.parse("2015-04-16"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://bostonivy.co">http://bostonivy.co</a><br><b>WHOIS Server:</b> whois.nic.cfd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150311-cfd">Delegation of the .cfd domain to DotCFD Registry Ltd (2015-03-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240429-cfd">Transfer of the .cfd domain to Shortdot SA (2024-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CFD = new TLD("cfd", Type.GENERIC, "Shortdot SA", LocalDate.parse("2015-03-06"), LocalDate.parse("2024-04-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dnsafrica.net">https://www.dnsafrica.net</a><br><br>
     *
     */
    public static final @NotNull TLD CG = new TLD("cg", Type.COUNTRY_CODE, "Interpoint Switzerland", LocalDate.parse("1997-01-14"), LocalDate.parse("2024-05-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.ch/">https://www.nic.ch/</a><br><b>WHOIS Server:</b> whois.nic.ch<br>
     *
     */
    public static final @NotNull TLD CH = new TLD("ch", Type.COUNTRY_CODE, "SWITCH The Swiss Education and Research Network", LocalDate.parse("1987-05-20"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.chanel.com">http://www.chanel.com</a><br><b>WHOIS Server:</b> whois.nic.chanel<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150730-chanel">Delegation of the .chanel domain to Chanel International B.V. (2015-07-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHANEL = new TLD("chanel", Type.GENERIC, "Chanel International B.V.", LocalDate.parse("2015-06-18"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140910-channel">Delegation of the .channel domain to Charleston Road Registry Inc. (2014-09-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHANNEL = new TLD("channel", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.charity">http://nic.charity</a><br><b>WHOIS Server:</b> whois.nic.charity<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180604-charity">Delegation of the .charity domain to Corn Lake, LLC (2018-06-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210609-charity">Transfer of the .charity domain to Binky Moon, LLC (2021-06-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220825-charity">Transfer of the .charity domain to Public Interest Registry (2022-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHARITY = new TLD("charity", Type.GENERIC, "Public Interest Registry (PIR)", LocalDate.parse("2018-05-17"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.jpmorganchase.com">http://www.jpmorganchase.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160225-chase">Delegation of the .chase domain to JPMorgan Chase and Co. (2016-02-25)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160718-chase">Redelegation of the .chase domain to JPMorgan Chase Bank, National Association (2016-07-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHASE = new TLD("chase", Type.GENERIC, "JPMorgan Chase Bank, National Association", LocalDate.parse("2016-01-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.chat<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150203-chat">Delegation of the .chat domain to Sand Fields, LLC (2015-02-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHAT = new TLD("chat", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-01-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cheap<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-cheap">Delegation of the .CHEAP domain to Sand Cover, LLC (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHEAP = new TLD("cheap", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.chintai">http://nic.chintai</a><br><b>WHOIS Server:</b> whois.nic.chintai<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160603-chintai">Delegation of the .chintai domain to Chintai Corporation (2016-06-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHINTAI = new TLD("chintai", Type.GENERIC, "CHINTAI Corporation", LocalDate.parse("2016-03-03"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150306-chloe">Delegation of the .chloe domain to Richemont DNS Inc. (2015-03-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170929-chloe">Revocation of the .chloe domain (2017-09-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHLOE = new TLD("chloe", Type.GENERIC, null, LocalDate.parse("2015-01-08"), LocalDate.parse("2017-10-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.christmas">https://nic.christmas</a><br><b>WHOIS Server:</b> whois.nic.christmas<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140219-christmas">Delegation of the .christmas domain to Uniregistry, Corp. (2014-02-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-christmas">Transfer of the .christmas domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHRISTMAS = new TLD("christmas", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140910-chrome">Delegation of the .chrome domain to Charleston Road Registry Inc. (2014-09-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHROME = new TLD("chrome", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-chrysler">Delegation of the .chrysler domain to FCA US Llc (2016-07-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191120-chrysler">Revocation of the .chrysler domain (2019-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHRYSLER = new TLD("chrysler", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2019-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.church<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140509-church">Delegation of the .church domain to Holly Fields, LLC (2014-05-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CHURCH = new TLD("church", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://registry.nic.ci/login.jsp">https://registry.nic.ci/login.jsp</a><br><b>WHOIS Server:</b> whois.nic.ci<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/ci-report-20170907.html">Transfer of the .CI (Cote d’Ivoire) top-level domain to Autorité de Régulation des Télécommunications/TIC de Côte d’lvoire (ARTCI) (2017-09-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CI = new TLD("ci", Type.COUNTRY_CODE, "Autorité de Régulation des Télécommunications/TIC de Côte d’lvoire (ARTCI)", LocalDate.parse("1995-02-14"), LocalDate.parse("2022-09-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cipriani.com/">http://www.cipriani.com/</a><br><b>WHOIS Server:</b> whois.nic.cipriani<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151007-cipriani">Delegation of the .cipriani domain to Hotel Cipriani Srl (2015-10-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CIPRIANI = new TLD("cipriani", Type.GENERIC, "Hotel Cipriani Srl", LocalDate.parse("2015-07-30"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.circle<br>
     *
     */
    public static final @NotNull TLD CIRCLE = new TLD("circle", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cisco.com">http://www.cisco.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150513-cisco">Delegation of the .cisco domain to Cisco Technology, Inc. (2015-05-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CISCO = new TLD("cisco", Type.GENERIC, "Cisco Technology, Inc.", LocalDate.parse("2015-04-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.citadel.com">http://www.citadel.com</a><br><b>WHOIS Server:</b> whois.nic.citadel<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160718-citadel">Delegation of the .citadel domain to Citadel Domain LLC (2016-07-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CITADEL = new TLD("citadel", Type.GENERIC, "Citadel Domain LLC", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-10-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.citigroup.com">http://www.citigroup.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160726-citi">Delegation of the .citi domain to Citigroup Inc. (2016-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CITI = new TLD("citi", Type.GENERIC, "Citigroup Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140425-citic">Delegation of the .citic domain to CITIC Group Corporation (2014-04-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CITIC = new TLD("citic", Type.GENERIC, "CITIC Group Corporation", LocalDate.parse("2014-04-17"), LocalDate.parse("2024-03-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.city<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140709-city">Delegation of the .city domain to Snow Sky, LLC (2014-07-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CITY = new TLD("city", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151106-cityeats">Delegation of the .cityeats domain to Lifestyle Domain Holdings, Inc. (2015-11-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231018-cityeats">Revocation of the .cityeats domain (2023-10-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CITYEATS = new TLD("cityeats", Type.GENERIC, null, LocalDate.parse("2015-09-24"), LocalDate.parse("2023-10-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.vodafone.co.ck">http://www.vodafone.co.ck</a><br><br>
     *
     */
    public static final @NotNull TLD CK = new TLD("ck", Type.COUNTRY_CODE, "Telecom Cook Islands Ltd.", LocalDate.parse("1995-08-08"), LocalDate.parse("2020-06-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cl/">http://www.nic.cl/</a><br><b>WHOIS Server:</b> whois.nic.cl<br>
     *
     */
    public static final @NotNull TLD CL = new TLD("cl", Type.COUNTRY_CODE, "NIC Chile (University of Chile)", LocalDate.parse("1987-12-15"), LocalDate.parse("2021-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.claims<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140502-claims">Delegation of the .claims domain to Black Corner, LLC (2014-05-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLAIMS = new TLD("claims", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-01"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cleaning<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-cleaning">Delegation of the .cleaning domain to Fox Shadow, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLEANING = new TLD("cleaning", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.click<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-click">Delegation of the .click domain to Uniregistry, Corp. (2014-08-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221116-click">Transfer of the .click domain to Internet Naming Co. (2022-11-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLICK = new TLD("click", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2014-08-15"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.clinic<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140414-clinic">Delegation of the .clinic domain to Goose Park, LLC (2014-04-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLINIC = new TLD("clinic", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-11"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://elcompanies.com/Pages/Homepage.aspx">http://elcompanies.com/Pages/Homepage.aspx</a><br><b>WHOIS Server:</b> whois.nic.clinique<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151224-clinique">Delegation of the .clinique domain to The Estée Lauder Companies Inc. (2015-12-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLINIQUE = new TLD("clinique", Type.GENERIC, "The Estée Lauder Companies Inc.", LocalDate.parse("2015-12-10"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.clothing<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-clothing">Delegation of the .CLOTHING domain to Steel Lake, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLOTHING = new TLD("clothing", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.get.cloud">https://www.get.cloud</a><br><b>WHOIS Server:</b> whois.nic.cloud<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150623-cloud">Delegation of the .cloud domain to ARUBA S.p.A. (2015-06-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLOUD = new TLD("cloud", Type.GENERIC, "ARUBA PEC S.p.A.", LocalDate.parse("2015-06-18"), LocalDate.parse("2024-05-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.club/">http://nic.club/</a><br><b>WHOIS Server:</b> whois.nic.club<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-club">Delegation of the .CLUB domain to .CLUB Domains, LLC (2014-01-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210817-club">Transfer of the .club domain to Registry Services, LLC (2021-08-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLUB = new TLD("club", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.clubmed<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150929-clubmed">Delegation of the .clubmed domain to Club Méditerranée S.A. (2015-09-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CLUBMED = new TLD("clubmed", Type.GENERIC, "Club Méditerranée S.A.", LocalDate.parse("2015-09-24"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.cm">https://www.nic.cm</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2024/cm-report-20240121.html">Transfer of the .CM (Cameroon) top-level domain to Agence Nationale des Technologies de l'Information et de la Communication (2024-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CM = new TLD("cm", Type.COUNTRY_CODE, "Agence Nationale des Technologies de l'Information et de la Communication (ANTIC)", LocalDate.parse("1995-04-29"), LocalDate.parse("2024-01-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cnnic.cn/">http://www.cnnic.cn/</a><br><b>WHOIS Server:</b> whois.cnnic.cn<br>
     *
     */
    public static final @NotNull TLD CN = new TLD("cn", Type.COUNTRY_CODE, "China Internet Network Information Center (CNNIC)", LocalDate.parse("1990-11-28"), LocalDate.parse("2023-09-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cointernet.com.co">http://www.cointernet.com.co</a><br><b>WHOIS Server:</b> whois.nic.co<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2009/co-report-24nov2009.html">Report on the Redelegation of the .CO Top-Level Domain to ".CO Internet SAS" (2009-11-24)</a></li>
     *   <li><a href="https://iana.org/reports/2020/co-report-20200904.html">Transfer of the .CO (Colombia) top-level domain to the Ministry of Information and Communications Technologies (2020-09-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CO = new TLD("co", Type.COUNTRY_CODE, "Ministry of Information and Communications Technologies (MinTIC)", LocalDate.parse("1991-12-24"), LocalDate.parse("2023-12-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.coach<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141124-coach">Delegation of the .coach domain to Koko Island, LLC (2014-11-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COACH = new TLD("coach", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.codes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-codes">Delegation of the .CODES domain to Puff Willow, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CODES = new TLD("codes", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.coffee<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-coffee">Delegation of the .COFFEE domain to Trixy Cover, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COFFEE = new TLD("coffee", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.college/">https://nic.college/</a><br><b>WHOIS Server:</b> whois.nic.college<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140402-college">Delegation of the .college domain to XYZ.COM LLC (2014-04-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COLLEGE = new TLD("college", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.koeln">https://www.nic.koeln</a><br><b>WHOIS Server:</b> whois.ryce-rsp.com<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140314-cologne">Delegation of the .cologne domain to NetCologne Gesellschaft für Telekommunikation mbH (2014-03-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180430-cologne">Transfer of the .cologne domain to punkt.wien GmbH (2018-04-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180620-cologne">Transfer of the .cologne domain to dotKoeln GmbH (2018-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COLOGNE = new TLD("cologne", Type.GENERIC, "dotKoeln GmbH", LocalDate.parse("2014-03-13"), LocalDate.parse("2021-03-11"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .COM through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.verisign-grs.com<br>
     *
     */
    public static final @NotNull TLD COM = new TLD("com", Type.GENERIC, "VeriSign Global Registry Services", LocalDate.parse("1985-01-01"), LocalDate.parse("2023-12-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160705-comcast">Delegation of the .comcast domain to Comcast IP Holdings I, LLC (2016-07-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-comcast">Revocation of the .comcast domain (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMCAST = new TLD("comcast", Type.GENERIC, null, LocalDate.parse("2016-06-16"), LocalDate.parse("2024-02-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.commbank.com.au">http://www.commbank.com.au</a><br><b>WHOIS Server:</b> whois.nic.commbank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-commbank">Delegation of the .commbank domain to Commonwealth Bank of Australia (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMMBANK = new TLD("commbank", Type.GENERIC, "COMMONWEALTH BANK OF AUSTRALIA", LocalDate.parse("2015-03-26"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.community<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140124-community">Delegation of the .COMMUNITY domain to Fox Orchard, LLC (2014-01-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMMUNITY = new TLD("community", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-23"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.company<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-company">Delegation of the .COMPANY domain to Silver Avenue, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMPANY = new TLD("company", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.compare/">http://nic.compare/</a><br><b>WHOIS Server:</b> whois.nic.compare<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160112-compare">Delegation of the .compare domain to iSelect Ltd (2016-01-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190807-compare">Transfer of the .compare domain to Registry Services, LLC (2019-08-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMPARE = new TLD("compare", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-12-23"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.computer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-computer">Delegation of the .COMPUTER domain to Pine Mill, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMPUTER = new TLD("computer", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.comsec<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151112-comsec">Delegation of the .comsec domain to VeriSign, Inc. (2015-11-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COMSEC = new TLD("comsec", Type.GENERIC, "VeriSign, Inc.", LocalDate.parse("2015-10-08"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.condos<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-condos">Delegation of the .condos domain to Pine House, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CONDOS = new TLD("condos", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.construction<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-construction">Delegation of the .CONSTRUCTION domain to Fox Dynamite, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CONSTRUCTION = new TLD("construction", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.consulting<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-consulting">Transfer of the .consulting domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CONSULTING = new TLD("consulting", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.contact<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151216-contact">Delegation of the .contact domain to Top Level Spectrum, Inc. (2015-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CONTACT = new TLD("contact", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-12-10"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.contractors<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-contractors">Delegation of the .CONTRACTORS domain to Magic Woods, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CONTRACTORS = new TLD("contractors", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.cooking/">http://nic.cooking/</a><br><b>WHOIS Server:</b> whois.nic.cooking<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-cooking">Delegation of the .cooking domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-cooking">Transfer of the .cooking domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COOKING = new TLD("cooking", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160622-cookingchannel">Delegation of the .cookingchannel domain to Lifestyle Domain Holdings, Inc. (2016-06-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230614-cookingchannel">Revocation of the .cookingchannel domain (2023-06-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COOKINGCHANNEL = new TLD("cookingchannel", Type.GENERIC, null, LocalDate.parse("2016-05-05"), LocalDate.parse("2023-06-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cool<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-cool">Delegation of the .COOL domain to Koko Lake, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COOL = new TLD("cool", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .COOP through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.nic.coop">http://www.nic.coop</a><br><b>WHOIS Server:</b> whois.nic.coop<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/coop-report-13dec01.html">IANA Report on Establishment of the .COOP Top-Level Domain (2001-12-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COOP = new TLD("coop", Type.SPONSORED, "DotCooperation LLC", LocalDate.parse("2001-12-15"), LocalDate.parse("2024-04-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://puntu.corsica">https://puntu.corsica</a><br><b>WHOIS Server:</b> whois.nic.corsica<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150512-corsica">Delegation of the .corsica domain to Collectivité Territoriale de Corse (2015-05-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CORSICA = new TLD("corsica", Type.GENERIC, "Collectivité de Corse", LocalDate.parse("2015-04-23"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.country<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-country">Delegation of the .country domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221116-country">Transfer of the .country domain to Internet Naming Co. (2022-11-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COUNTRY = new TLD("country", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-coupon">Delegation of the .coupon domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COUPON = new TLD("coupon", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.coupons<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150511-coupons">Delegation of the .coupons domain to Black Island, LLC (2015-05-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COUPONS = new TLD("coupons", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-05-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.get.courses/">https://www.get.courses/</a><br><b>WHOIS Server:</b> whois.nic.courses<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150224-courses">Delegation of the .courses domain to Open Universities Australia Pty Ltd (2015-02-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220615-courses">Transfer of the .courses domain to Registry Services, LLC (2022-06-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD COURSES = new TLD("courses", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-02-19"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.cpa.com">https://www.cpa.com</a><br><b>WHOIS Server:</b> whois.nic.cpa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190921-cpa">Delegation of the .cpa domain (2019-09-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CPA = new TLD("cpa", Type.GENERIC, "American Institute of Certified Public Accountants", LocalDate.parse("2019-09-11"), LocalDate.parse("2023-12-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cr/">http://www.nic.cr/</a><br><b>WHOIS Server:</b> whois.nic.cr<br>
     *
     */
    public static final @NotNull TLD CR = new TLD("cr", Type.COUNTRY_CODE, "National Academy of Sciences Academia Nacional de Ciencias", LocalDate.parse("1990-09-10"), LocalDate.parse("2024-02-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.credit<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140502-credit">Delegation of the .credit domain to Snow Shadow, LLC (2014-05-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CREDIT = new TLD("credit", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-24"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.creditcard<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140425-creditcard">Delegation of the .creditcard domain to Binky Frostbite, LLC (2014-04-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CREDITCARD = new TLD("creditcard", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-24"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.identity.coop/creditunion">http://www.identity.coop/creditunion</a><br><b>WHOIS Server:</b> whois.nic.creditunion<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151106-creditunion">Delegation of the .creditunion domain to CUNA Performance Resources, LLC (2015-11-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210115-creditunion">Transfer of the .creditunion domain to DotCooperation, LLC (2021-01-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CREDITUNION = new TLD("creditunion", Type.GENERIC, "DotCooperation, LLC", LocalDate.parse("2015-10-15"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.cricket">http://nic.cricket</a><br><b>WHOIS Server:</b> whois.nic.cricket<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141117-cricket">Delegation of the .cricket domain to Blue Sky Registry Limited (2014-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CRICKET = new TLD("cricket", Type.GENERIC, "dot Cricket Limited", LocalDate.parse("2014-11-13"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.crown.com/">http://www.crown.com/</a><br><b>WHOIS Server:</b> whois.nic.crown<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150616-crown">Delegation of the .crown domain to Crown Equipment Corporation (2015-06-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CROWN = new TLD("crown", Type.GENERIC, "Crown Equipment Corporation", LocalDate.parse("2015-06-04"), LocalDate.parse("2024-03-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.crs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141009-crs">Delegation of the .crs domain to Federated Co-operatives Limited (2014-10-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CRS = new TLD("crs", Type.GENERIC, "Federated Co-operatives Limited", LocalDate.parse("2014-08-28"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.vikingrivercruises.com">http://www.vikingrivercruises.com</a><br><b>WHOIS Server:</b> whois.nic.cruise<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-cruise">Delegation of the .cruise domain to Viking River Cruises (Bermuda) Ltd. (2016-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CRUISE = new TLD("cruise", Type.GENERIC, "Viking River Cruises (Bermuda) Ltd.", LocalDate.parse("2016-10-21"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.cruises<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-cruises">Delegation of the .cruises domain to Spring Way, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CRUISES = new TLD("cruises", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-csc">Delegation of the .csc domain to Alliance-One Services, Inc. (2015-08-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220201-csc">Revocation of the .csc domain (2022-02-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CSC = new TLD("csc", Type.GENERIC, null, LocalDate.parse("2015-08-06"), LocalDate.parse("2022-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cu">http://www.nic.cu</a><br><br>
     *
     */
    public static final @NotNull TLD CU = new TLD("cu", Type.COUNTRY_CODE, "CENIAInternet Industria y San Jose Capitolio Nacional", LocalDate.parse("1992-06-03"), LocalDate.parse("2023-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.cuisinella/">http://nic.cuisinella/</a><br><b>WHOIS Server:</b> whois.nic.cuisinella<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140702-cuisinella">Delegation of the .cuisinella domain to SALM S.A.S. (2014-07-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CUISINELLA = new TLD("cuisinella", Type.GENERIC, "SCHMIDT GROUPE S.A.S.", LocalDate.parse("2014-06-19"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dns.cv/">http://www.dns.cv/</a><br><b>WHOIS Server:</b> whois.nic.cv<br>
     *
     */
    public static final @NotNull TLD CV = new TLD("cv", Type.COUNTRY_CODE, "Agência Reguladora Multissectorial da Economia (ARME)", LocalDate.parse("1996-10-21"), LocalDate.parse("2024-02-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.uoc.cw/cw-registry">http://www.uoc.cw/cw-registry</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/cw-report-20111003.html">Report on the Delegation of the .CW domain representing Curaçao to the University of the Netherlands Antilles, and transitional arrangements for the .AN domain representing the Netherlands Antilles (2011-10-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CW = new TLD("cw", Type.COUNTRY_CODE, "University of Curacao", LocalDate.parse("2010-12-20"), LocalDate.parse("2024-02-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://cxda.org.cx">https://cxda.org.cx</a><br><b>WHOIS Server:</b> whois.nic.cx<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2006/cx-report-07mar2006.pdf">IANA Report on the Redelegation of the .CX Top-Level Domain (2006-03-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CX = new TLD("cx", Type.COUNTRY_CODE, "Christmas Island Domain Administration Limited", LocalDate.parse("1997-04-24"), LocalDate.parse("2024-02-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cy">http://www.nic.cy</a><br><br>
     *
     */
    public static final @NotNull TLD CY = new TLD("cy", Type.COUNTRY_CODE, "University of Cyprus", LocalDate.parse("1994-07-26"), LocalDate.parse("2023-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://eincartrefarlein.cymru/">https://eincartrefarlein.cymru/</a><br><b>WHOIS Server:</b> whois.nic.cymru<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140807-cymru">Delegation of the .cymru domain to Nominet UK (2014-08-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CYMRU = new TLD("cymru", Type.GENERIC, "Nominet UK", LocalDate.parse("2014-07-31"), LocalDate.parse("2022-08-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://cy.changyou.com">http://cy.changyou.com</a><br><b>WHOIS Server:</b> whois.nic.cyou<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150331-cyou">Delegation of the .cyou domain to Beijing Gamease Age Digital Technology Co., Ltd. (2015-03-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200514-cyou">Transfer of the .cyou domain to Shortdot SA (2020-05-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD CYOU = new TLD("cyou", Type.GENERIC, "Shortdot SA", LocalDate.parse("2015-03-19"), LocalDate.parse("2024-06-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.cz/">http://www.nic.cz/</a><br><b>WHOIS Server:</b> whois.nic.cz<br>
     *
     */
    public static final @NotNull TLD CZ = new TLD("cz", Type.COUNTRY_CODE, "CZ.NIC, z.s.p.o", LocalDate.parse("1993-01-13"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dabur.com">http://www.dabur.com</a><br><b>WHOIS Server:</b> whois.nic.dabur<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-dabur">Delegation of the .dabur domain to Dabur India Limited (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DABUR = new TLD("dabur", Type.GENERIC, "Dabur India Limited", LocalDate.parse("2015-01-15"), LocalDate.parse("2023-08-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-dad">Delegation of the .dad domain to Charleston Road Registry Inc. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DAD = new TLD("dad", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.dance<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-dance">Delegation of the .DANCE domain to United TLD Holdco Ltd. (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-dance">Transfer of the .dance domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DANCE = new TLD("dance", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dish.com/">https://www.dish.com/</a><br><b>WHOIS Server:</b> whois.nic.data<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161219-data">Delegation of the .data domain to Dish DBS Corporation (2016-12-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DATA = new TLD("data", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-12-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.date">http://nic.date</a><br><b>WHOIS Server:</b> whois.nic.date<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-date">Delegation of the .date domain to dot Date Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DATE = new TLD("date", Type.GENERIC, "dot Date Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.dating<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140124-dating">Delegation of the .DATING domain to Pine Fest, LLC (2014-01-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DATING = new TLD("dating", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-23"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150302-datsun">Delegation of the .datsun domain to Nissan Motor Co., Ltd. (2015-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DATSUN = new TLD("datsun", Type.GENERIC, "NISSAN MOTOR CO., LTD.", LocalDate.parse("2014-07-10"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-day">Delegation of the .day domain to Charleston Road Registry Inc. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DAY = new TLD("day", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-dclk">Delegation of the .dclk domain to Charleston Road Registry Inc. (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DCLK = new TLD("dclk", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-01-08"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.dds/">http://nic.dds/</a><br><b>WHOIS Server:</b> whois.nic.dds<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160428-dds">Delegation of the .dds domain to Minds + Machines Group Limited (2016-04-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-dds">Transfer of the .dds domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DDS = new TLD("dds", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2016-01-08"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.denic.de/">http://www.denic.de/</a><br><b>WHOIS Server:</b> whois.denic.de<br>
     *
     */
    public static final @NotNull TLD DE = new TLD("de", Type.COUNTRY_CODE, "DENIC eG", LocalDate.parse("1986-11-05"), LocalDate.parse("2023-04-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.deal<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-deal">Delegation of the .deal domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DEAL = new TLD("deal", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.dealer">https://get.dealer</a><br><b>WHOIS Server:</b> whois.nic.dealer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-dealer">Delegation of the .dealer domain to Dealer Dot Com, Inc. (2015-12-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191108-dealer">Transfer of the .dealer domain to Intercap Registry Inc. (2019-11-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DEALER = new TLD("dealer", Type.GENERIC, "Intercap Registry Inc.", LocalDate.parse("2015-11-25"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.deals<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140709-deals">Delegation of the .deals domain to Sand Sunset, LLC (2014-07-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DEALS = new TLD("deals", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-03"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.degree<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140527-degree">Delegation of the .degree domain to United TLD Holdco, LTD. (2014-05-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-degree">Transfer of the .degree domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DEGREE = new TLD("degree", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.delivery<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141027-delivery">Delegation of the .delivery domain to Steel Station, LLC (2014-10-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DELIVERY = new TLD("delivery", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-10-16"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dell.com">http://www.dell.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151012-dell">Delegation of the .dell domain to Dell Inc. (2015-10-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DELL = new TLD("dell", Type.GENERIC, "Dell Inc.", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.deloitte">http://nic.deloitte</a><br><b>WHOIS Server:</b> whois.nic.deloitte<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160122-deloitte">Delegation of the .deloitte domain to Deloitte Touche Tohmatsu (2016-01-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DELOITTE = new TLD("deloitte", Type.GENERIC, "Deloitte Touche Tohmatsu", LocalDate.parse("2015-12-23"), LocalDate.parse("2023-11-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.delta.com">http://www.delta.com</a><br><b>WHOIS Server:</b> whois.nic.delta<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150709-delta">Delegation of the .delta domain to Delta Air Lines, Inc. (2015-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DELTA = new TLD("delta", Type.GENERIC, "Delta Air Lines, Inc.", LocalDate.parse("2015-06-25"), LocalDate.parse("2023-08-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.democrat<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-democrat">Delegation of the .DEMOCRAT domain to United TLD Holdco Ltd. (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-democrat">Transfer of the .democrat domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DEMOCRAT = new TLD("democrat", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.dental<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-dental">Delegation of the .dental domain to Tin Birch, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DENTAL = new TLD("dental", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.dentist<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-dentist">Delegation of the .dentist domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-dentist">Transfer of the .dentist domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DENTIST = new TLD("dentist", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.icann.org/resources/pages/ebero-2013-04-02-en">https://www.icann.org/resources/pages/ebero-2013-04-02-en</a><br><b>WHOIS Server:</b> whois.nic.desi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-desi">Delegation of the .desi domain to Desi Networks LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231019-desi">Transfer of the .desi domain to emergency back-end registry operations (2023-10-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DESI = new TLD("desi", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2014-03-06"), LocalDate.parse("2024-07-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.design/">http://nic.design/</a><br><b>WHOIS Server:</b> whois.nic.design<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150120-design">Delegation of the .design domain to Top Level Design, LLC (2015-01-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210831-design">Transfer of the .design domain to Registry Services, LLC (2021-08-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DESIGN = new TLD("design", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-12-18"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141216-dev">Delegation of the .dev domain to Charleston Road Registry Inc. (2014-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DEV = new TLD("dev", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-11-20"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160531-dhl">Delegation of the .dhl domain to Deutsche Post AG (2016-05-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DHL = new TLD("dhl", Type.GENERIC, "Deutsche Post AG", LocalDate.parse("2016-05-26"), LocalDate.parse("2023-11-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.diamonds<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131118-diamonds">Delegation of the .DIAMONDS domain to John Edge, LLC (2013-11-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DIAMONDS = new TLD("diamonds", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.diet">https://nic.diet</a><br><b>WHOIS Server:</b> whois.nic.diet<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-diet">Delegation of the .diet domain to Uniregistry, Corp. (2014-08-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-diet">Transfer of the .diet domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DIET = new TLD("diet", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-08-15"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.digital<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140502-digital">Delegation of the .digital domain to Dash Park, LLC (2014-05-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DIGITAL = new TLD("digital", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.direct<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140624-direct">Delegation of the .direct domain to Half Trail, LLC (2014-06-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DIRECT = new TLD("direct", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.directory<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131116-directory">Delegation of the .DIRECTORY domain to Extra Madison, LLC (2013-11-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DIRECTORY = new TLD("directory", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.discount<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-discount">Delegation of the .discount domain to Holly Hill, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DISCOUNT = new TLD("discount", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.discover.com/">https://www.discover.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-discover">Delegation of the .discover domain to Discover Financial Services (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DISCOVER = new TLD("discover", Type.GENERIC, "Discover Financial Services", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com/">http://www.dish.com/</a><br><b>WHOIS Server:</b> whois.nic.dish<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160808-dish">Delegation of the .dish domain to Dish DBS Corporation (2016-08-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DISH = new TLD("dish", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-08-04"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.diy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160823-diy">Delegation of the .diy domain to Lifestyle Domain Holdings, Inc. (2016-08-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240109-diy">Transfer of the .diy domain to Internet Naming Co. (2024-01-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DIY = new TLD("diy", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.dj">http://www.nic.dj</a><br><br>
     *
     */
    public static final @NotNull TLD DJ = new TLD("dj", Type.COUNTRY_CODE, "Djibouti Telecom S.A", LocalDate.parse("1996-05-22"), LocalDate.parse("2024-07-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://punktum.dk/">https://punktum.dk/</a><br><b>WHOIS Server:</b> whois.punktum.dk<br>
     *
     */
    public static final @NotNull TLD DK = new TLD("dk", Type.COUNTRY_CODE, "Dansk Internet Forum", LocalDate.parse("1987-07-14"), LocalDate.parse("2024-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.dm">http://www.nic.dm</a><br><b>WHOIS Server:</b> whois.dmdomains.dm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/dm-report-31jul2007.html">Report on Redelegation of the .DM Top-Level Domain (2007-07-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DM = new TLD("dm", Type.COUNTRY_CODE, "DotDM Corporation", LocalDate.parse("1991-09-03"), LocalDate.parse("2024-05-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/">http://www.gmoregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.dnp<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140307-dnp">Delegation of the .dnp domain to Dai Nippon Printing Co., Ltd. (2014-03-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DNP = new TLD("dnp", Type.GENERIC, "Dai Nippon Printing Co., Ltd.", LocalDate.parse("2014-02-27"), LocalDate.parse("2019-08-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.do">http://www.nic.do</a><br><b>WHOIS Server:</b> whois.nic.do<br>
     *
     */
    public static final @NotNull TLD DO = new TLD("do", Type.COUNTRY_CODE, "Pontificia Universidad Catolica Madre y Maestra Recinto Santo Tomas de Aquino", LocalDate.parse("1991-08-25"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141216-docs">Delegation of the .docs domain to Charleston Road Registry Inc. (2014-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOCS = new TLD("docs", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-11-20"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.doctor<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-doctor">Delegation of the .doctor domain to Brice Trail, LLC (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOCTOR = new TLD("doctor", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2016-07-14"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-dodge">Delegation of the .dodge domain to FCA US Llc (2016-07-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191120-dodge">Revocation of the .dodge domain (2019-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DODGE = new TLD("dodge", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2019-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.dog<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150422-dog">Delegation of the .dog domain to Koko Mill, LLC (2015-04-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOG = new TLD("dog", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190409-doha">Revocation of the .doha domain (2019-04-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOHA = new TLD("doha", Type.GENERIC, null, LocalDate.parse("2015-02-27"), LocalDate.parse("2019-04-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.domains<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-domains">Delegation of the .DOMAINS domain to Sugar Cross, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOMAINS = new TLD("domains", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141211-doosan">Delegation of the .doosan domain to Doosan Corporation (2014-12-11)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160219-doosan">Revocation of the .doosan domain (2016-02-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOOSAN = new TLD("doosan", Type.GENERIC, null, LocalDate.parse("2014-10-30"), LocalDate.parse("2016-02-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com">http://www.dish.com</a><br><b>WHOIS Server:</b> whois.nic.dot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160516-dot">Delegation of the .dot domain to Dish DBS Corporation (2016-05-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOT = new TLD("dot", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-04-21"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.download">http://nic.download</a><br><b>WHOIS Server:</b> whois.nic.download<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-download">Delegation of the .download domain to dot Support Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DOWNLOAD = new TLD("download", Type.GENERIC, "dot Support Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-drive">Delegation of the .drive domain to Charleston Road Registry Inc. (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DRIVE = new TLD("drive", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-06-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com">http://www.dish.com</a><br><b>WHOIS Server:</b> whois.nic.dtv<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160525-dtv">Delegation of the .dtv domain to Dish DBS Corporation (2016-05-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DTV = new TLD("dtv", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-05-19"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.dubai<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160105-dubai">Delegation of the .dubai domain to Dubai Smart Government Department (2016-01-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DUBAI = new TLD("dubai", Type.GENERIC, "Dubai Smart Government Department", LocalDate.parse("2015-12-17"), LocalDate.parse("2020-05-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-duck">Delegation of the .duck domain to Johnson Shareholdings, Inc. (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211203-duck">Revocation of the .duck domain (2021-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DUCK = new TLD("duck", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.goodyear.com">http://www.goodyear.com</a><br><b>WHOIS Server:</b> whois.nic.dunlop<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160606-dunlop">Delegation of the .dunlop domain to The Goodyear Tire and Rubber Company (2016-06-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DUNLOP = new TLD("dunlop", Type.GENERIC, "The Goodyear Tire and Rubber Company", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160721-duns">Delegation of the .duns domain to The Dun and Bradstreet Corporation (2016-07-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190830-duns">Revocation of the .duns domain (2019-08-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DUNS = new TLD("duns", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2019-08-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dupont.com">https://www.dupont.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160606-dupont">Delegation of the .dupont domain to E. I. du Pont de Nemours and Company (2016-06-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220210-dupont">Transfer of the .dupont domain to DuPont Specialty Products USA, LLC (2022-02-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DUPONT = new TLD("dupont", Type.GENERIC, "DuPont Specialty Products USA, LLC", LocalDate.parse("2016-05-05"), LocalDate.parse("2024-06-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.net.za">http://www.registry.net.za</a><br><b>WHOIS Server:</b> whois.nic.durban<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140617-durban">Delegation of the .durban domain to ZA Central Registry NPC trading as ZA Central Registry (2014-06-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DURBAN = new TLD("durban", Type.GENERIC, "ZA Central Registry NPC trading as ZA Central Registry", LocalDate.parse("2014-06-05"), LocalDate.parse("2022-05-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dvag-registry.de">http://www.dvag-registry.de</a><br><b>WHOIS Server:</b> whois.nic.dvag<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140925-dvag">Delegation of the .dvag domain to Deutsche Vermögensberatung Aktiengesellschaft DVAG (2014-09-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DVAG = new TLD("dvag", Type.GENERIC, "Deutsche Vermögensberatung Aktiengesellschaft DVAG", LocalDate.parse("2014-09-18"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.echostar.com">http://www.echostar.com</a><br><b>WHOIS Server:</b> whois.nic.dvr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20160927-dvr">Delegation of the .dvr domain to Hughes Satellite Systems Corporation (2016-09-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240425-dvr">Transfer of the .dvr domain to DISH Technologies L.L.C. (2024-04-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD DVR = new TLD("dvr", Type.GENERIC, "DISH Technologies L.L.C.", LocalDate.parse("2016-09-22"), LocalDate.parse("2024-04-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.dz">http://www.nic.dz</a><br><b>WHOIS Server:</b> whois.nic.dz<br>
     *
     */
    public static final @NotNull TLD DZ = new TLD("dz", Type.COUNTRY_CODE, "CERIST", LocalDate.parse("1994-01-03"), LocalDate.parse("2019-04-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://domain.earth/">https://domain.earth/</a><br><b>WHOIS Server:</b> whois.nic.earth<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150512-earth">Delegation of the .earth domain to Interlink Co., Ltd. (2015-05-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220719-earth">Transfer of the .earth domain to Interlink Systems Innovation Institute K.K. (2022-07-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EARTH = new TLD("earth", Type.GENERIC, "Interlink Systems Innovation Institute K.K.", LocalDate.parse("2015-04-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-eat">Delegation of the .eat domain to Charleston Road Registry Inc. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EAT = new TLD("eat", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ec">http://www.nic.ec</a><br><b>WHOIS Server:</b> whois.nic.ec<br>
     *
     */
    public static final @NotNull TLD EC = new TLD("ec", Type.COUNTRY_CODE, "ECUADORDOMAIN S.A.", LocalDate.parse("1991-02-01"), LocalDate.parse("2024-04-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://go.eco">https://go.eco</a><br><b>WHOIS Server:</b> whois.nic.eco<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160825-eco">Delegation of the .eco domain to Big Room Inc. (2016-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ECO = new TLD("eco", Type.GENERIC, "Big Room Inc.", LocalDate.parse("2016-08-18"), LocalDate.parse("2024-03-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://edeka.de">http://edeka.de</a><br><b>WHOIS Server:</b> whois.nic.edeka<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160115-edeka">Delegation of the .edeka domain to EDEKA Verband kaufmännischer Genossenschaften e.V. (2016-01-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EDEKA = new TLD("edeka", Type.GENERIC, "EDEKA Verband kaufmännischer Genossenschaften e.V.", LocalDate.parse("2016-01-08"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.educause.edu/edudomain">http://www.educause.edu/edudomain</a><br><b>WHOIS Server:</b> whois.educause.edu<br>
     *
     */
    public static final @NotNull TLD EDU = new TLD("edu", Type.SPONSORED, "EDUCAUSE", LocalDate.parse("1985-01-01"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.education<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-education">Delegation of the .EDUCATION domain to Brice Way, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EDUCATION = new TLD("education", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.internet.ee">http://www.internet.ee</a><br><b>WHOIS Server:</b> whois.tld.ee<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/ee-report-20130512.html">Report on the Redelegation of the .EE domain representing Estonia to Eesti Interneti Sihtasutus (2013-05-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EE = new TLD("ee", Type.COUNTRY_CODE, "Eesti Interneti Sihtasutus (EIS)", LocalDate.parse("1992-06-03"), LocalDate.parse("2023-06-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.egregistry.eg/">http://www.egregistry.eg/</a><br><br>
     *
     */
    public static final @NotNull TLD EG = new TLD("eg", Type.COUNTRY_CODE, "Egyptian Universities Network (EUN) Supreme Council of Universities", LocalDate.parse("1990-11-30"), LocalDate.parse("2024-04-24"));

    public static final @NotNull TLD EH = new TLD("eh", Type.COUNTRY_CODE, null, null, LocalDate.parse("1999-09-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.email<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-email">Delegation of the .EMAIL domain to Spring Madison, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EMAIL = new TLD("email", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.emerck">http://www.nic.emerck</a><br><b>WHOIS Server:</b> whois.nic.emerck<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141017-emerck">Delegation of the .emerck domain to Merck KGaA (2014-10-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EMERCK = new TLD("emerck", Type.GENERIC, "Merck KGaA", LocalDate.parse("2014-09-11"), LocalDate.parse("2023-10-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.energy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141027-energy">Delegation of the .energy domain to Binky Birch, LLC (2014-10-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ENERGY = new TLD("energy", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-10-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.engineer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-engineer">Delegation of the .engineer domain to United TLD Holdco, Ltd (2014-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-engineer">Transfer of the .engineer domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ENGINEER = new TLD("engineer", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.engineering<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-engineering">Delegation of the .engineering domain to Romeo Canyon (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ENGINEERING = new TLD("engineering", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.enterprises<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131118-enterprises">Delegation of the .ENTERPRISES domain to Snow Oaks, LLC (2013-11-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ENTERPRISES = new TLD("enterprises", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-epost">Delegation of the .epost domain to Deutsche Post AG (2016-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190214-epost">Revocation of the .epost domain (2019-02-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EPOST = new TLD("epost", Type.GENERIC, null, LocalDate.parse("2016-05-26"), LocalDate.parse("2019-02-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.epson.com">http://www.epson.com</a><br><b>WHOIS Server:</b> whois.nic.epson<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150227-epson">Delegation of the .epson domain to Seiko Epson Corporation (2015-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EPSON = new TLD("epson", Type.GENERIC, "Seiko Epson Corporation", LocalDate.parse("2015-02-19"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.equipment<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-equipment">Delegation of the .EQUIPMENT domain to Corn Station, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EQUIPMENT = new TLD("equipment", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD ER = new TLD("er", Type.COUNTRY_CODE, "Eritrea Telecommunication Services Corporation (EriTel)", LocalDate.parse("1996-09-24"), LocalDate.parse("2023-11-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.ericsson<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160607-ericsson">Delegation of the .ericsson domain to Telefonaktiebolaget L M Ericsson (2016-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ERICSSON = new TLD("ericsson", Type.GENERIC, "Telefonaktiebolaget L M Ericsson", LocalDate.parse("2016-04-14"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.erni.ch">http://www.erni.ch</a><br><b>WHOIS Server:</b> whois.nic.erni<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150309-erni">Delegation of the .erni domain to ERNI Group Holding AG (2015-03-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ERNI = new TLD("erni", Type.GENERIC, "ERNI Group Holding AG", LocalDate.parse("2015-02-05"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.es/">http://www.nic.es/</a><br><b>WHOIS Server:</b> whois.nic.es<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/es-report-05aug2005.pdf">IANA Report on the Redelegation of the .ES Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ES = new TLD("es", Type.COUNTRY_CODE, "Red.es", LocalDate.parse("1988-04-14"), LocalDate.parse("2023-05-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140827-esq">Delegation of the .esq domain to Charleston Road Registry Inc. (2014-08-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ESQ = new TLD("esq", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.estate<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-estate">Delegation of the .ESTATE domain to Trixy Park, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ESTATE = new TLD("estate", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160718-esurance">Delegation of the .esurance domain to Esurance Insurance Company (2016-07-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200526-esurance">Revocation of the .esurance domain (2020-05-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ESURANCE = new TLD("esurance", Type.GENERIC, null, LocalDate.parse("2016-06-24"), LocalDate.parse("2020-05-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ethiotelecom.et">http://www.ethiotelecom.et</a><br><br>
     *
     */
    public static final @NotNull TLD ET = new TLD("et", Type.COUNTRY_CODE, "Ethio telecom", LocalDate.parse("1995-10-15"), LocalDate.parse("2022-06-02"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170601-etisalat">Delegation of the .etisalat domain to Emirates Telecommunications Corporation (trading as Etisalat) (2017-06-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231117-etisalat">Revocation of the .etisalat domain (2023-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ETISALAT = new TLD("etisalat", Type.GENERIC, null, LocalDate.parse("2017-05-11"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.eurid.eu">http://www.eurid.eu</a><br><b>WHOIS Server:</b> whois.eu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/eu-report-05aug2005.pdf">IANA Report on the Delegation of the .EU Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EU = new TLD("eu", Type.COUNTRY_CODE, "EURid vzw/asbl", LocalDate.parse("2005-04-28"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.ebu.ch/">https://www.ebu.ch/</a><br><b>WHOIS Server:</b> whois.nic.eurovision<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141201-eurovision">Delegation of the .eurovision domain to European Broadcasting Union (EBU) (2014-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EUROVISION = new TLD("eurovision", Type.GENERIC, "European Broadcasting Union (EBU)", LocalDate.parse("2014-08-28"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.domeinuak.eus">https://www.domeinuak.eus</a><br><b>WHOIS Server:</b> whois.nic.eus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140408-eus">Delegation of the .eus domain to Puntueus Fundazioa (2014-04-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EUS = new TLD("eus", Type.GENERIC, "Puntueus Fundazioa", LocalDate.parse("2014-03-06"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.events<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-events">Delegation of the .events domain to Pioneer Maple, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EVENTS = new TLD("events", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141124-everbank">Delegation of the .everbank domain to EverBank (2014-11-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191115-everbank">Revocation of the .everbank domain (2019-11-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EVERBANK = new TLD("everbank", Type.GENERIC, null, LocalDate.parse("2014-07-31"), LocalDate.parse("2019-11-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.exchange<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-exchange">Delegation of the .exchange domain to Spring Falls, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EXCHANGE = new TLD("exchange", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.expert<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-expert">Delegation of the .EXPERT domain to Magic Pass, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EXPERT = new TLD("expert", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.exposed<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-exposed">Delegation of the .exposed domain to Victor Beach, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EXPOSED = new TLD("exposed", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.express<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150403-express">Delegation of the .express domain to Sea Sunset, LLC (2015-04-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EXPRESS = new TLD("express", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.extraspace.com">http://www.extraspace.com</a><br><b>WHOIS Server:</b> whois.nic.extraspace<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160322-extraspace">Delegation of the .extraspace domain to Extra Space Storage LLC (2016-03-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD EXTRASPACE = new TLD("extraspace", Type.GENERIC, "Extra Space Storage LLC", LocalDate.parse("2016-02-26"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://home.fage/">https://home.fage/</a><br><b>WHOIS Server:</b> whois.nic.fage<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150805-fage">Delegation of the .fage domain to Fage International S.A. (2015-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FAGE = new TLD("fage", Type.GENERIC, "Fage International S.A.", LocalDate.parse("2015-06-25"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.fail<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-fail">Delegation of the .fail domain to Atomic Pipe, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FAIL = new TLD("fail", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.fairwindspartners.com">http://www.fairwindspartners.com</a><br><b>WHOIS Server:</b> whois.nic.fairwinds<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151110-fairwinds">Delegation of the .fairwinds domain to FairWinds Partners, LLC (2015-11-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FAIRWINDS = new TLD("fairwinds", Type.GENERIC, "FairWinds Partners, LLC", LocalDate.parse("2015-10-29"), LocalDate.parse("2024-07-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.faith">http://nic.faith</a><br><b>WHOIS Server:</b> whois.nic.faith<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-faith">Delegation of the .faith domain to dot Faith Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FAITH = new TLD("faith", Type.GENERIC, "dot Faith Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.family<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150807-family">Delegation of the .family domain to United TLD Holdco, Ltd. (2015-08-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-family">Transfer of the .family domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FAMILY = new TLD("family", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-08-06"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.fan<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150312-fan">Delegation of the .fan domain to Asiamix Digital Ltd. (2015-03-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210609-fan">Transfer of the .fan domain to Dog Beach, LLC (2021-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FAN = new TLD("fan", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-02-28"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.fans/">http://www.nic.fans/</a><br><b>WHOIS Server:</b> whois.nic.fans<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150217-fans">Delegation of the .fans domain to Asiamix Digital Limited (2015-02-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200309-fans">Transfer of the .fans domain to ZDNS International Limited (2020-03-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FANS = new TLD("fans", Type.GENERIC, "ZDNS International Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2023-12-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.farm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-farm">Delegation of the .FARM domain to Just Maple, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FARM = new TLD("farm", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.farmers.com">https://www.farmers.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160621-farmers">Delegation of the .farmers domain to Farmers Insurance Exchange (2016-06-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FARMERS = new TLD("farmers", Type.GENERIC, "Farmers Insurance Exchange", LocalDate.parse("2016-04-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.fashion/">http://nic.fashion/</a><br><b>WHOIS Server:</b> whois.nic.fashion<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141203-fashion">Delegation of the .fashion domain to Top Level Domain Holdings Limited (2014-12-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-fashion">Transfer of the .fashion domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FASHION = new TLD("fashion", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-10-23"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.fast<br>
     *
     */
    public static final @NotNull TLD FAST = new TLD("fast", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.fedex.com/">http://www.fedex.com/</a><br><b>WHOIS Server:</b> whois.nic.fedex<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160623-fedex">Delegation of the .fedex domain to Federal Express Corporation (2016-06-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FEDEX = new TLD("fedex", Type.GENERIC, "Federal Express Corporation", LocalDate.parse("2016-06-09"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://centralnic.com">http://centralnic.com</a><br><b>WHOIS Server:</b> whois.nic.feedback<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140407-feedback">Delegation of the .feedback domain to Top Level Spectrum, Inc. (2014-04-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FEEDBACK = new TLD("feedback", Type.GENERIC, "Top Level Spectrum, Inc.", LocalDate.parse("2014-03-06"), LocalDate.parse("2024-05-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ferrari.com/en_us/">http://www.ferrari.com/en_us/</a><br><b>WHOIS Server:</b> whois.nic.ferrari<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-ferrari">Delegation of the .ferrari domain to Fiat Chrysler Automobiles N.V. (2016-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FERRARI = new TLD("ferrari", Type.GENERIC, "Fiat Chrysler Automobiles N.V.", LocalDate.parse("2016-07-14"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ferrero.com">http://www.ferrero.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151104-ferrero">Delegation of the .ferrero domain to Ferrero Trading Lux S.A. (2015-11-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FERRERO = new TLD("ferrero", Type.GENERIC, "Ferrero Trading Lux S.A.", LocalDate.parse("2015-09-10"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://domain.fi">https://domain.fi</a><br><b>WHOIS Server:</b> whois.fi<br>
     *
     */
    public static final @NotNull TLD FI = new TLD("fi", Type.COUNTRY_CODE, "Finnish Transport and Communications Agency Traficom", LocalDate.parse("1986-12-17"), LocalDate.parse("2023-08-18"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-fiat">Delegation of the .fiat domain to Fiat Chrysler Automobiles N.V. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230605-fiat">Revocation of the .fiat domain (2023-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIAT = new TLD("fiat", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2023-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://fidelity.com">http://fidelity.com</a><br><b>WHOIS Server:</b> whois.nic.fidelity<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-fidelity">Delegation of the .fidelity domain to Fidelity Brokerage Services LLC (2016-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIDELITY = new TLD("fidelity", Type.GENERIC, "Fidelity Brokerage Services LLC", LocalDate.parse("2016-07-28"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.rogers.com/consumer/home">http://www.rogers.com/consumer/home</a><br><b>WHOIS Server:</b> whois.nic.fido<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160914-fido">Delegation of the .fido domain to Rogers Communications Canada Inc. (2016-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIDO = new TLD("fido", Type.GENERIC, "Rogers Communications Canada Inc.", LocalDate.parse("2016-08-18"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.film">http://nic.film</a><br><b>WHOIS Server:</b> whois.nic.film<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150316-film">Delegation of the .film domain to Motion Picture Domain Registry Pty Ltd (2015-03-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FILM = new TLD("film", Type.GENERIC, "Motion Picture Domain Registry Pty Ltd", LocalDate.parse("2015-02-27"), LocalDate.parse("2024-01-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.gtlds.nic.br<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150925-final">Delegation of the .final domain to Núcleo de Informação e Coordenação do Ponto BR - NIC.br (2015-09-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FINAL = new TLD("final", Type.GENERIC, "Núcleo de Informação e Coordenação do Ponto BR - NIC.br", LocalDate.parse("2015-05-29"), LocalDate.parse("2023-11-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.finance<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140425-finance">Delegation of the .finance domain to Cotton Cypress, LLC (2014-04-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FINANCE = new TLD("finance", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.financial<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-financial">Delegation of the .financial domain to Just Cover, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FINANCIAL = new TLD("financial", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.fire<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-fire">Delegation of the .fire domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIRE = new TLD("fire", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.firestone.com/">http://www.firestone.com/</a><br><b>WHOIS Server:</b> whois.nic.firestone<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160816-firestone">Redelegation of the .firestone domain to Bridgestone Licensing Services, Inc. (2016-08-16)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-firestone">Delegation of the .firestone domain to Bridgestone Corporation (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIRESTONE = new TLD("firestone", Type.GENERIC, "Bridgestone Licensing Services, Inc.", LocalDate.parse("2015-10-29"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.firmdale<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141114-firmdale">Delegation of the .firmdale domain to Firmdale Holdings Limited (2014-11-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIRMDALE = new TLD("firmdale", Type.GENERIC, "Firmdale Holdings Limited", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-01-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.fish<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140214-fish">Delegation of the .fish domain to Fox Woods, LLC (2014-02-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FISH = new TLD("fish", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.fishing/">http://nic.fishing/</a><br><b>WHOIS Server:</b> whois.nic.fishing<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-fishing">Delegation of the .fishing domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-fishing">Transfer of the .fishing domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FISHING = new TLD("fishing", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.fit/">http://nic.fit/</a><br><b>WHOIS Server:</b> whois.nic.fit<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150106-fit">Delegation of the .fit domain to Minds + Machines Group Limited (2015-01-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-fit">Transfer of the .fit domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FIT = new TLD("fit", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-12-18"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.fitness<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140414-fitness">Delegation of the .fitness domain to Brice Orchard, LLC (2014-04-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FITNESS = new TLD("fitness", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-11"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.domains.fj/">https://www.domains.fj/</a><br><b>WHOIS Server:</b> www.whois.fj<br>
     *
     */
    public static final @NotNull TLD FJ = new TLD("fj", Type.COUNTRY_CODE, "The University of the South Pacific IT Services", LocalDate.parse("1992-06-03"), LocalDate.parse("2024-02-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sure.co.fk">http://www.sure.co.fk</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/fk-report-05aug2005.pdf">IANA Report on the Redelegation of the .FK Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FK = new TLD("fk", Type.COUNTRY_CODE, "Falkland Islands Government", LocalDate.parse("1997-03-26"), LocalDate.parse("2023-03-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.flickr">http://nic.flickr</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160209-flickr">Delegation of the .flickr domain to Yahoo! Domain Services Inc. (2016-02-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200709-flickr">Transfer of the .flickr domain to Flickr, Inc. (2020-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLICKR = new TLD("flickr", Type.GENERIC, "Flickr, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.flights<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-flights">Delegation of the .flights domain to Fox Station, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLIGHTS = new TLD("flights", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.flir.com">http://www.flir.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160505-flir">Delegation of the .flir domain to FLIR Systems, Inc. (2016-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLIR = new TLD("flir", Type.GENERIC, "FLIR Systems, Inc.", LocalDate.parse("2016-03-03"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.florist<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-florist">Delegation of the .FLORIST domain to Half Cypress, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLORIST = new TLD("florist", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.flowers">https://nic.flowers</a><br><b>WHOIS Server:</b> whois.nic.flowers<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141222-flowers">Delegation of the .flowers domain to Uniregistry, Corp. (2014-12-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-flowers">Transfer of the .flowers domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLOWERS = new TLD("flowers", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-12-18"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141006-flsmidth">Delegation of the .flsmidth domain to FLSmidth A/S (2014-10-06)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160722-flsmidth">Revocation of the .flsmidth domain (2016-07-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLSMIDTH = new TLD("flsmidth", Type.GENERIC, null, LocalDate.parse("2014-09-25"), LocalDate.parse("2016-07-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140910-fly">Delegation of the .fly domain to Charleston Road Registry Inc. (2014-09-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FLY = new TLD("fly", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dot.fm/">https://www.dot.fm/</a><br><b>WHOIS Server:</b> whois.nic.fm<br>
     *
     */
    public static final @NotNull TLD FM = new TLD("fm", Type.COUNTRY_CODE, "FSM Telecommunications Corporation", LocalDate.parse("1995-04-19"), LocalDate.parse("2021-02-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.fo/">http://www.nic.fo/</a><br><b>WHOIS Server:</b> whois.nic.fo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/fo-report-05aug2005.pdf">IANA Report on the Redelegation of the .FO Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FO = new TLD("fo", Type.COUNTRY_CODE, "FO Council", LocalDate.parse("1993-05-14"), LocalDate.parse("2024-06-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140416-foo">Delegation of the .foo domain to Charleston Road Registry Inc. (2014-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOO = new TLD("foo", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-03-20"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-food">Delegation of the .food domain to Lifestyle Domain Holdings, Inc. (2016-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240109-food">Transfer of the .food domain to Internet Naming Co. (2024-01-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOOD = new TLD("food", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2016-11-04"), LocalDate.parse("2024-05-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160622-foodnetwork">Delegation of the .foodnetwork domain to Lifestyle Domain Holdings, Inc. (2016-06-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230614-foodnetwork">Revocation of the .foodnetwork domain (2023-06-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOODNETWORK = new TLD("foodnetwork", Type.GENERIC, null, LocalDate.parse("2016-05-05"), LocalDate.parse("2023-06-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.football<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150216-football">Delegation of the .football domain to Foggy Farms, LLC (2015-02-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOOTBALL = new TLD("football", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-02-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ford.com">http://www.ford.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151215-ford">Delegation of the .ford domain to Ford Motor Company (2015-12-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FORD = new TLD("ford", Type.GENERIC, "Ford Motor Company", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.forex<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150309-forex">Delegation of the .forex domain to Dotforex Registry Ltd (2015-03-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210629-forex">Transfer of the .forex domain to Dog Beach, LLC (2021-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOREX = new TLD("forex", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.forsale<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-forsale">Transfer of the .forsale domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FORSALE = new TLD("forsale", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-09-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.topspectrum.com">http://www.topspectrum.com</a><br><b>WHOIS Server:</b> whois.nic.forum<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150626-forum">Delegation of the .forum domain to Fegistry, LLC (2015-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FORUM = new TLD("forum", Type.GENERIC, "Fegistry, LLC", LocalDate.parse("2015-06-11"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.foundation">http://nic.foundation</a><br><b>WHOIS Server:</b> whois.nic.foundation<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-foundation">Delegation of the .foundation domain to John Dale, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220825-foundation">Transfer of the .foundation domain to Public Interest Registry (2022-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOUNDATION = new TLD("foundation", Type.GENERIC, "Public Interest Registry (PIR)", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.fox.com">https://www.fox.com</a><br><b>WHOIS Server:</b> whois.nic.fox<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-fox">Delegation of the .fox domain to Fox Registry, LLC (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FOX = new TLD("fox", Type.GENERIC, "FOX Registry, LLC", LocalDate.parse("2015-11-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.fr">https://www.nic.fr</a><br><b>WHOIS Server:</b> whois.nic.fr<br>
     *
     */
    public static final @NotNull TLD FR = new TLD("fr", Type.COUNTRY_CODE, "Association Française pour le Nommage Internet en Coopération (A.F.N.I.C.)", LocalDate.parse("1986-09-02"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.free<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161107-free">Delegation of the .free domain to Amazon Registry Services, Inc. (2016-11-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FREE = new TLD("free", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-10-27"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.freseniusregistry.com">http://www.freseniusregistry.com</a><br><b>WHOIS Server:</b> whois.nic.fresenius<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160105-fresenius">Delegation of the .fresenius domain to Fresenius Immobilien-Verwaltungs-GmbH (2016-01-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FRESENIUS = new TLD("fresenius", Type.GENERIC, "Fresenius Immobilien-Verwaltungs-GmbH", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://registreer.frl">https://registreer.frl</a><br><b>WHOIS Server:</b> whois.nic.frl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-frl">Delegation of the .frl domain to FRLregistry B.V. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FRL = new TLD("frl", Type.GENERIC, "FRLregistry B.V.", LocalDate.parse("2014-08-22"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.frogans/">https://nic.frogans/</a><br><b>WHOIS Server:</b> whois.nic.frogans<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140416-frogans">Delegation of the .frogans domain to OP3FT (2014-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FROGANS = new TLD("frogans", Type.GENERIC, "OP3FT", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-10-16"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160622-frontdoor">Delegation of the .frontdoor domain to Lifestyle Domain Holdings, Inc. (2016-06-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231018-frontdoor">Revocation of the .frontdoor domain (2023-10-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FRONTDOOR = new TLD("frontdoor", Type.GENERIC, null, LocalDate.parse("2016-05-05"), LocalDate.parse("2023-10-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://frontier.com">http://frontier.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160204-frontier">Delegation of the .frontier domain to Frontier Communications Corporation (2016-02-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FRONTIER = new TLD("frontier", Type.GENERIC, "Frontier Communications Corporation", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://frontier.com">http://frontier.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160414-ftr">Delegation of the .ftr domain to Frontier Communications Corporation (2016-04-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FTR = new TLD("ftr", Type.GENERIC, "Frontier Communications Corporation", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160705-fujitsu">Delegation of the .fujitsu domain to Fujitsu Limited (2016-07-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FUJITSU = new TLD("fujitsu", Type.GENERIC, "Fujitsu Limited", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-06-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160713-fujixerox">Delegation of the .fujixerox domain to Xerox DNHC LLC (2016-07-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210326-fujixerox">Revocation of the .fujixerox domain (2021-03-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FUJIXEROX = new TLD("fujixerox", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2021-03-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.radix.website/">https://www.radix.website/</a><br><b>WHOIS Server:</b> whois.nic.fun<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161220-fun">Delegation of the .fun domain to DotSpace, Inc. (2016-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210825-fun">Transfer of the .fun domain to Radix FZC (2021-08-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240208-fun">Transfer of the .fun domain to Radix Technologies Inc. (2024-02-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FUN = new TLD("fun", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2016-11-30"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.fund<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-fund">Delegation of the .fund domain to John Castle, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FUND = new TLD("fund", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.furniture<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-furniture">Delegation of the .furniture domain to Lone Fields, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FURNITURE = new TLD("furniture", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.futbol<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-futbol">Delegation of the .futbol domain to United TLD Holdco, LTD (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-futbol">Transfer of the .futbol domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FUTBOL = new TLD("futbol", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.fyi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150521-fyi">Delegation of the .fyi domain to Silver Tigers, LLC (2015-05-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD FYI = new TLD("fyi", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/ga-report-20130329.html">Report on the Redelegation of the .GA domain representing Gabon to Agence Nationale des Infrastructures Numériques et des Fréquences (2013-03-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GA = new TLD("ga", Type.COUNTRY_CODE, "Agence Nationale des Infrastructures Numériques et des Fréquences (ANINF)", LocalDate.parse("1994-12-12"), LocalDate.parse("2023-06-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://asociacion.dominio.gal/">http://asociacion.dominio.gal/</a><br><b>WHOIS Server:</b> whois.nic.gal<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140408-gal">Delegation of the .gal domain to Asociación puntoGAL (2014-04-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GAL = new TLD("gal", Type.GENERIC, "Asociación puntoGAL", LocalDate.parse("2014-03-13"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.gallery<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-gallery">Delegation of the .GALLERY domain to Sugar House, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GALLERY = new TLD("gallery", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gallo.com">http://www.gallo.com</a><br><b>WHOIS Server:</b> whois.nic.gallo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160315-gallo">Delegation of the .gallo domain to Gallo Vineyards, Inc. (2016-03-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GALLO = new TLD("gallo", Type.GENERIC, "Gallo Vineyards, Inc.", LocalDate.parse("2015-10-24"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://gallup.com">http://gallup.com</a><br><b>WHOIS Server:</b> whois.nic.gallup<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160208-gallup">Delegation of the .gallup domain to Gallup, Inc. (2016-02-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GALLUP = new TLD("gallup", Type.GENERIC, "Gallup, Inc.", LocalDate.parse("2015-10-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.game">https://nic.game</a><br><b>WHOIS Server:</b> whois.nic.game<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-game">Delegation of the .game domain to Uniregistry, Corp. (2015-07-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-game">Transfer of the .game domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GAME = new TLD("game", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-07-02"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.games<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160531-games">Delegation of the .games domain to United TLD Holdco Ltd. (2016-05-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-games">Transfer of the .games domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GAMES = new TLD("games", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2016-05-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gap.com">http://www.gap.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-gap">Delegation of the .gap domain to The Gap, Inc. (2016-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GAP = new TLD("gap", Type.GENERIC, "The Gap, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.garden/">http://nic.garden/</a><br><b>WHOIS Server:</b> whois.nic.garden<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141210-garden">Delegation of the .garden domain to Top Level Domain Holdings Limited (2014-12-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-garden">Transfer of the .garden domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GARDEN = new TLD("garden", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-10-23"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.gay">http://nic.gay</a><br><b>WHOIS Server:</b> whois.nic.gay<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190809-gay">Delegation of the .gay domain to Top Level Design, LLC (2019-08-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230626-gay">Transfer of the .gay domain to Registry Services, LLC (2023-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GAY = new TLD("gay", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2019-07-19"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD GB = new TLD("gb", Type.COUNTRY_CODE, "Reserved Domain - IANA", LocalDate.parse("1985-07-24"), LocalDate.parse("2011-06-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140825-gbiz">Delegation of the .gbiz domain to Charleston Road Registry Inc. (2014-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GBIZ = new TLD("gbiz", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.gd">http://nic.gd</a><br><b>WHOIS Server:</b> whois.nic.gd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2006/gd-report-24jul2006.html">IANA Report on the Redelegation of the .GD Top-Level Domain (2006-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GD = new TLD("gd", Type.COUNTRY_CODE, "The National Telecommunications Regulatory Commission (NTRC)", LocalDate.parse("1992-06-03"), LocalDate.parse("2021-10-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gdn">http://www.nic.gdn</a><br><b>WHOIS Server:</b> whois.nic.gdn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150211-gdn">Delegation of the .gdn domain to Joint Stock Company "Navigation-information systems" (2015-02-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GDN = new TLD("gdn", Type.GENERIC, "Joint Stock Company \"Navigation-information systems\"", LocalDate.parse("2014-12-04"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.ge">http://nic.ge</a><br><b>WHOIS Server:</b> whois.nic.ge<br>
     *
     */
    public static final @NotNull TLD GE = new TLD("ge", Type.COUNTRY_CODE, "Caucasus Online LLC", LocalDate.parse("1992-12-02"), LocalDate.parse("2023-05-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.geagroup.com">http://www.geagroup.com</a><br><b>WHOIS Server:</b> whois.nic.gea<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-gea">Delegation of the .gea domain to GEA Group Aktiengesellschaft (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GEA = new TLD("gea", Type.GENERIC, "GEA Group Aktiengesellschaft", LocalDate.parse("2015-07-09"), LocalDate.parse("2022-06-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gent">http://www.nic.gent</a><br><b>WHOIS Server:</b> whois.nic.gent<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140710-gent">Delegation of the .gent domain to COMBELL GROUP NV/SA (2014-07-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GENT = new TLD("gent", Type.GENERIC, "Combell nv", LocalDate.parse("2014-07-03"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.genting/">http://nic.genting/</a><br><b>WHOIS Server:</b> whois.nic.genting<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-genting">Delegation of the .genting domain to Resorts World Inc Pte. Ltd. (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GENTING = new TLD("genting", Type.GENERIC, "Resorts World Inc. Pte. Ltd.", LocalDate.parse("2015-06-11"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.walmart.com">http://www.walmart.com</a><br><b>WHOIS Server:</b> whois.nic.george<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160816-george">Delegation of the .george domain to Wal-Mart Stores, Inc. (2016-08-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GEORGE = new TLD("george", Type.GENERIC, "Wal-Mart Stores, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2023-09-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dom-enic.com">https://www.dom-enic.com</a><br><b>WHOIS Server:</b> whois.mediaserv.net<br>
     *
     */
    public static final @NotNull TLD GF = new TLD("gf", Type.COUNTRY_CODE, "CANAL+ TELECOM", LocalDate.parse("1996-07-25"), LocalDate.parse("2021-10-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gg">http://www.nic.gg</a><br><b>WHOIS Server:</b> whois.gg<br>
     *
     */
    public static final @NotNull TLD GG = new TLD("gg", Type.COUNTRY_CODE, "Island Networks Ltd.", LocalDate.parse("1996-08-07"), LocalDate.parse("2024-02-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.ggee">http://nic.ggee</a><br><b>WHOIS Server:</b> whois.nic.ggee<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141222-ggee">Delegation of the .ggee domain to GMO Internet, Inc. (2014-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GGEE = new TLD("ggee", Type.GENERIC, "GMO Internet, Inc.", LocalDate.parse("2014-12-18"), LocalDate.parse("2019-08-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gh">http://www.nic.gh</a><br><b>WHOIS Server:</b> whois.nic.gh<br>
     *
     */
    public static final @NotNull TLD GH = new TLD("gh", Type.COUNTRY_CODE, "Network Computer Systems Limited", LocalDate.parse("1995-01-19"), LocalDate.parse("2023-12-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gi">http://www.nic.gi</a><br><b>WHOIS Server:</b> whois.identitydigital.services<br>
     *
     */
    public static final @NotNull TLD GI = new TLD("gi", Type.COUNTRY_CODE, "Sapphire Networks", LocalDate.parse("1995-12-05"), LocalDate.parse("2024-06-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://uniregistry.link">http://uniregistry.link</a><br><b>WHOIS Server:</b> whois.uniregistry.net<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-gift">Delegation of the .GIFT domain to Uniregistry, Corp. (2014-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GIFT = new TLD("gift", Type.GENERIC, "Uniregistry, Corp.", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-05-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.gifts<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140805-gifts">Delegation of the .gifts domain to Goose Sky, LLC (2014-08-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GIFTS = new TLD("gifts", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.gives">http://nic.gives</a><br><b>WHOIS Server:</b> whois.nic.gives<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-gives">Delegation of the .gives domain to United TLD Holdco, Ltd (2014-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-gives">Transfer of the .gives domain to Dog Beach, LLC (2021-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220825-gives">Transfer of the .gives domain to Public Interest Registry (2022-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GIVES = new TLD("gives", Type.GENERIC, "Public Interest Registry (PIR)", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.giving">http://nic.giving</a><br><b>WHOIS Server:</b> whois.nic.giving<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150730-giving">Delegation of the .giving domain to Giving Limited (2015-07-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221013-giving">Transfer of the .giving domain to Public Interest Registry (PIR) (2022-10-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GIVING = new TLD("giving", Type.GENERIC, "Public Interest Registry (PIR)", LocalDate.parse("2015-06-11"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gl/">http://www.nic.gl/</a><br><b>WHOIS Server:</b> whois.nic.gl<br>
     *
     */
    public static final @NotNull TLD GL = new TLD("gl", Type.COUNTRY_CODE, "TELE Greenland A/S", LocalDate.parse("1994-04-08"), LocalDate.parse("2022-11-26"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160720-glade">Delegation of the .glade domain to Johnson Shareholdings, Inc. (2016-07-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211203-glade">Revocation of the .glade domain (2021-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GLADE = new TLD("glade", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.glass<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-glass">Delegation of the .GLASS domain to Black Cover, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GLASS = new TLD("glass", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140909-gle">Delegation of the .gle domain to Charleston Road Registry Inc. (2014-09-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GLE = new TLD("gle", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://donuts.domains/">https://donuts.domains/</a><br><b>WHOIS Server:</b> whois.nic.global<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140605-global">Delegation of the .global domain to Dot GLOBAL AS (2014-06-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240410-global">Transfer of the .global domain to Identity Digital Limited (2024-04-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GLOBAL = new TLD("global", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-05-29"), LocalDate.parse("2024-04-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.globo/">https://nic.globo/</a><br><b>WHOIS Server:</b> whois.gtlds.nic.br<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140429-globo">Delegation of the .globo domain to Globo Comunicação e Participações S.A (2014-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GLOBO = new TLD("globo", Type.GENERIC, "Globo Comunicação e Participações S.A", LocalDate.parse("2014-02-20"), LocalDate.parse("2024-01-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gm">http://www.nic.gm</a><br><br>
     *
     */
    public static final @NotNull TLD GM = new TLD("gm", Type.COUNTRY_CODE, "GM-NIC", LocalDate.parse("1997-03-28"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140825-gmail">Delegation of the .gmail domain to Charleston Road Registry Inc. (2014-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GMAIL = new TLD("gmail", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.gmbh<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160307-gmbh">Delegation of the .gmbh domain to Extra Dynamite, LLC (2016-03-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GMBH = new TLD("gmbh", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2016-03-03"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140429-gmo">Delegation of the .gmo domain to GMO Internet, Inc. (2014-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GMO = new TLD("gmo", Type.GENERIC, "GMO Internet, Inc.", LocalDate.parse("2014-02-27"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://cp.nic.gmx">https://cp.nic.gmx</a><br><b>WHOIS Server:</b> whois.nic.gmx<br>
     *
     */
    public static final @NotNull TLD GMX = new TLD("gmx", Type.GENERIC, "1&1 Mail and Media GmbH", LocalDate.parse("2014-08-07"), LocalDate.parse("2022-09-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://ande.gov.gn/dns-gn/">https://ande.gov.gn/dns-gn/</a><br><b>WHOIS Server:</b> whois.ande.gov.gn<br>
     *
     */
    public static final @NotNull TLD GN = new TLD("gn", Type.COUNTRY_CODE, "Centre National des Sciences Halieutiques de Boussoura", LocalDate.parse("1994-08-09"), LocalDate.parse("2023-12-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.godaddy">http://nic.godaddy</a><br><b>WHOIS Server:</b> whois.nic.godaddy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160701-godaddy">Delegation of the .godaddy domain to Go Daddy East, LLC (2016-07-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GODADDY = new TLD("godaddy", Type.GENERIC, "Go Daddy East, LLC", LocalDate.parse("2016-06-24"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.gold<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-gold">Delegation of the .gold domain to June Edge, LLC (2015-03-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOLD = new TLD("gold", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.goldpoint">http://nic.goldpoint</a><br><b>WHOIS Server:</b> whois.nic.goldpoint<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150217-goldpoint">Delegation of the .goldpoint domain to Yodobashi Camera Co., Ltd. (2015-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOLDPOINT = new TLD("goldpoint", Type.GENERIC, "YODOBASHI CAMERA CO.,LTD.", LocalDate.parse("2015-02-13"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.golf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-golf">Delegation of the .golf domain to Lone Falls, LLC (2015-03-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOLF = new TLD("golf", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150227-goo">Delegation of the .goo domain to NTT Resonant Inc. (2015-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOO = new TLD("goo", Type.GENERIC, "NTT Resonant Inc.", LocalDate.parse("2015-02-13"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160711-goodhands">Delegation of the .goodhands domain to Allstate Fire and Casualty Insurance Company (2016-07-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180920-goodhands">Revocation of the .goodhands domain (2018-09-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOODHANDS = new TLD("goodhands", Type.GENERIC, null, LocalDate.parse("2016-02-05"), LocalDate.parse("2018-09-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.goodyear.com/">https://www.goodyear.com/</a><br><b>WHOIS Server:</b> whois.nic.goodyear<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160606-goodyear">Delegation of the .goodyear domain to The Goodyear Tire and Rubber Company (2016-06-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOODYEAR = new TLD("goodyear", Type.GENERIC, "The Goodyear Tire and Rubber Company", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-goog">Delegation of the .goog domain to Charleston Road Registry Inc. (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOOG = new TLD("goog", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-01-08"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140909-google">Delegation of the .google domain to Charleston Road Registry Inc. (2014-09-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOOGLE = new TLD("google", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://join.gop">http://join.gop</a><br><b>WHOIS Server:</b> whois.nic.gop<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140402-gop">Delegation of the .gop domain to Republican State Leadership Committee, Inc. (2014-04-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GOP = new TLD("gop", Type.GENERIC, "Republican State Leadership Committee, Inc.", LocalDate.parse("2014-03-06"), LocalDate.parse("2024-01-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.got<br>
     *
     */
    public static final @NotNull TLD GOT = new TLD("got", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.gov">https://get.gov</a><br><b>WHOIS Server:</b> whois.dotgov.gov<br>
     *
     */
    public static final @NotNull TLD GOV = new TLD("gov", Type.SPONSORED, "Cybersecurity and Infrastructure Security Agency", LocalDate.parse("1985-01-01"), LocalDate.parse("2024-05-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.gp/">http://www.nic.gp/</a><br><b>WHOIS Server:</b> whois.nic.gp<br>
     *
     */
    public static final @NotNull TLD GP = new TLD("gp", Type.COUNTRY_CODE, "Networking Technologies Group", LocalDate.parse("1996-10-21"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dominio.gq">http://www.dominio.gq</a><br><b>WHOIS Server:</b> whois.dominio.gq<br>
     *
     */
    public static final @NotNull TLD GQ = new TLD("gq", Type.COUNTRY_CODE, "GETESA", LocalDate.parse("1997-07-10"), LocalDate.parse("2023-08-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gr">http://www.gr</a><br><br>
     *
     */
    public static final @NotNull TLD GR = new TLD("gr", Type.COUNTRY_CODE, "ICS-FORTH GR", LocalDate.parse("1989-02-19"), LocalDate.parse("2022-06-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.grainger.com">http://www.grainger.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151110-grainger">Delegation of the .grainger domain to Grainger Registry Services, LLC (2015-11-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GRAINGER = new TLD("grainger", Type.GENERIC, "Grainger Registry Services, LLC", LocalDate.parse("2015-10-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.graphics<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GRAPHICS = new TLD("graphics", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.gratis<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-gratis">Delegation of the .gratis domain to Pioneer Tigers, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GRATIS = new TLD("gratis", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.green<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140617-green">Delegation of the .green domain to Afilias Limited (2014-06-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190508-green">Transfer of the .green domain to Afilias Limited (2019-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GREEN = new TLD("green", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-06-12"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.gripe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-gripe">Delegation of the .gripe domain to Corn Sunset, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GRIPE = new TLD("gripe", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.walmart.com">http://www.walmart.com</a><br><b>WHOIS Server:</b> whois.nic.grocery<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170627-grocery">Delegation of the .grocery domain to Wal-Mart Stores, Inc. (2017-06-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GROCERY = new TLD("grocery", Type.GENERIC, "Wal-Mart Stores, Inc.", LocalDate.parse("2017-06-08"), LocalDate.parse("2023-09-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.group<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150805-group">Delegation of the .group domain to Romeo Town, LLC (2015-08-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GROUP = new TLD("group", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-07-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://secure.nic.gs">http://secure.nic.gs</a><br><b>WHOIS Server:</b> whois.nic.gs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2006/gs-report-07mar2006.pdf">IANA Report on the Redelegation of the .GS Top-Level Domain (2006-03-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GS = new TLD("gs", Type.COUNTRY_CODE, "Government of South Georgia and South Sandwich Islands (GSGSSI)", LocalDate.parse("1997-07-31"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gt">http://www.gt</a><br><br>
     *
     */
    public static final @NotNull TLD GT = new TLD("gt", Type.COUNTRY_CODE, "Universidad del Valle de Guatemala", LocalDate.parse("1992-08-14"), LocalDate.parse("2024-01-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://give.uog.edu/shop/">https://give.uog.edu/shop/</a><br><br>
     *
     */
    public static final @NotNull TLD GU = new TLD("gu", Type.COUNTRY_CODE, "University of Guam", LocalDate.parse("1994-04-15"), LocalDate.parse("2022-08-04"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160511-guardian">Delegation of the .guardian domain to The Guardian Life Insurance Company of America (2016-05-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240305-guardian">Revocation of the .guardian domain (2024-03-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GUARDIAN = new TLD("guardian", Type.GENERIC, null, LocalDate.parse("2016-04-14"), LocalDate.parse("2024-03-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gucci.com">http://www.gucci.com</a><br><b>WHOIS Server:</b> whois.nic.gucci<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151026-gucci">Delegation of the .gucci domain to Guccio Gucci S.p.a. (2015-10-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GUCCI = new TLD("gucci", Type.GENERIC, "Guccio Gucci S.p.a.", LocalDate.parse("2015-03-06"), LocalDate.parse("2021-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150319-guge">Delegation of the .guge domain to Charleston Road Registry Inc. (2015-03-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GUGE = new TLD("guge", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-02-19"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.guide<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140509-guide">Delegation of the .guide domain to Snow Moon, LLC (2014-05-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GUIDE = new TLD("guide", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.guitars">https://nic.guitars</a><br><b>WHOIS Server:</b> whois.nic.guitars<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-guitars">Delegation of the .GUITARS domain to Uniregistry, Corp. (2014-01-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-guitars">Transfer of the .guitars domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GUITARS = new TLD("guitars", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.guru<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-guru">Delegation of the .GURU domain to Pioneer Cypress, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GURU = new TLD("guru", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/gw-report-12apr2007.html">Report on Redelegation of the .GW Top-Level Domain (2007-04-12)</a></li>
     *   <li><a href="https://iana.org/reports/2014/gw-report-20140603.html">Redelegation of the .gw domain to Autoridade Reguladora Nacional das Tecnologias de Informação e Comunicação da Guiné Bissau (2014-06-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD GW = new TLD("gw", Type.COUNTRY_CODE, "Autoridade Reguladora Nacional - Tecnologias de Informação e Comunicação da Guiné-Bissau", LocalDate.parse("1997-02-04"), LocalDate.parse("2022-07-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://registry.gy/">https://registry.gy/</a><br><b>WHOIS Server:</b> whois.registry.gy<br>
     *
     */
    public static final @NotNull TLD GY = new TLD("gy", Type.COUNTRY_CODE, "University of Guyana", LocalDate.parse("1994-09-13"), LocalDate.parse("2024-07-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.hair/">https://nic.hair/</a><br><b>WHOIS Server:</b> whois.nic.hair<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161202-hair">Delegation of the .hair domain to L'Oréal (2016-12-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200915-hair">Transfer of the .hair domain to XYZ.COM LLC (2020-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HAIR = new TLD("hair", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2016-09-16"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.hamburg">http://www.nic.hamburg</a><br><b>WHOIS Server:</b> whois.nic.hamburg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-hamburg">Delegation of the .hamburg domain to Hamburg Top-Level-Domain GmbH (2014-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HAMBURG = new TLD("hamburg", Type.GENERIC, "Hamburg Top-Level-Domain GmbH", LocalDate.parse("2014-05-22"), LocalDate.parse("2022-05-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-hangout">Delegation of the .hangout domain to Charleston Road Registry Inc. (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HANGOUT = new TLD("hangout", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-01-08"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.haus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-haus">Delegation of the .haus domain to United TLD Holdco, Ltd (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-haus">Transfer of the .haus domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HAUS = new TLD("haus", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-03-26"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hbo.com">http://www.hbo.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160810-hbo">Delegation of the .hbo domain to HBO Registry Services, Inc. (2016-08-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HBO = new TLD("hbo", Type.GENERIC, "HBO Registry Services, Inc.", LocalDate.parse("2016-07-08"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.hdfc">http://nic.hdfc</a><br><b>WHOIS Server:</b> whois.nic.hdfc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160812-hdfc">Delegation of the .hdfc domain to Housing Development Finance Corporation Limited (2016-08-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HDFC = new TLD("hdfc", Type.GENERIC, "HOUSING DEVELOPMENT FINANCE CORPORATION LIMITED", LocalDate.parse("2016-07-21"), LocalDate.parse("2023-08-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.hdfcbank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160209-hdfcbank">Delegation of the .hdfcbank domain to HDFC Bank Limited (2016-02-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HDFCBANK = new TLD("hdfcbank", Type.GENERIC, "HDFC Bank Limited", LocalDate.parse("2016-02-05"), LocalDate.parse("2023-08-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dothealth.co">http://www.dothealth.co</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160121-health">Delegation of the .health domain to DotHealth, LLC (2016-01-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230626-health">Transfer of the .health domain to Registry Services, LLC (2023-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HEALTH = new TLD("health", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-09-17"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.healthcare<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140728-healthcare">Delegation of the .healthcare domain to Silver Glen, LLC (2014-07-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HEALTHCARE = new TLD("healthcare", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.help">https://nic.help</a><br><b>WHOIS Server:</b> whois.nic.help<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-help">Delegation of the .help domain to Uniregistry, Corp. (2014-08-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240104-help">Transfer of the .help domain to Innovation Service Ltd (2024-01-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HELP = new TLD("help", Type.GENERIC, "Innovation Service Ltd", LocalDate.parse("2014-08-15"), LocalDate.parse("2024-01-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hel.fi">http://www.hel.fi</a><br><b>WHOIS Server:</b> whois.nic.helsinki<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160121-helsinki">Delegation of the .helsinki domain to City of Helsinki (2016-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HELSINKI = new TLD("helsinki", Type.GENERIC, "City of Helsinki", LocalDate.parse("2015-12-10"), LocalDate.parse("2023-09-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140827-here">Delegation of the .here domain to Charleston Road Registry Inc. (2014-08-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HERE = new TLD("here", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://hermes.com">http://hermes.com</a><br><b>WHOIS Server:</b> whois.nic.hermes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150123-hermes">Delegation of the .hermes domain to Hermes International (2015-01-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HERMES = new TLD("hermes", Type.GENERIC, "Hermes International", LocalDate.parse("2014-10-30"), LocalDate.parse("2023-08-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160622-hgtv">Delegation of the .hgtv domain to Lifestyle Domain Holdings, Inc. (2016-06-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230614-hgtv">Revocation of the .hgtv domain (2023-06-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HGTV = new TLD("hgtv", Type.GENERIC, null, LocalDate.parse("2016-05-05"), LocalDate.parse("2023-06-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.hiphop">https://get.hiphop</a><br><b>WHOIS Server:</b> whois.nic.hiphop<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140512-hiphop">Delegation of the .hiphop domain to Uniregistry, Corp. (2014-05-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220326-hiphop">Transfer of the .hiphop domain to Dot Hip Hop, LLC (2022-03-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HIPHOP = new TLD("hiphop", Type.GENERIC, "Dot Hip Hop, LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2024-04-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160601-hisamitsu">Delegation of the .hisamitsu domain to Hisamitsu Pharmaceutical Co., Inc. (2016-06-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HISAMITSU = new TLD("hisamitsu", Type.GENERIC, "Hisamitsu Pharmaceutical Co.,Inc.", LocalDate.parse("2016-05-13"), LocalDate.parse("2023-06-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150428-hitachi">Delegation of the .hitachi domain to Hitachi, Ltd. (2015-04-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HITACHI = new TLD("hitachi", Type.GENERIC, "Hitachi, Ltd.", LocalDate.parse("2015-04-23"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.hiv<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-hiv">Delegation of the .hiv domain to dotHIV gemeinnuetziger e.V. (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221116-hiv">Transfer of the .hiv domain to Internet Naming Co. (2022-11-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HIV = new TLD("hiv", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2014-05-22"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hkirc.hk">http://www.hkirc.hk</a><br><b>WHOIS Server:</b> whois.hkirc.hk<br>
     *
     */
    public static final @NotNull TLD HK = new TLD("hk", Type.COUNTRY_CODE, "Hong Kong Internet Registration Corporation Ltd.", LocalDate.parse("1990-01-03"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.asia/namesphere/">http://www.dot.asia/namesphere/</a><br><b>WHOIS Server:</b> whois.nic.hkt<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-hkt">Delegation of the .hkt domain to PCCW-HKT DataCom Services Limited (2016-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HKT = new TLD("hkt", Type.GENERIC, "PCCW-HKT DataCom Services Limited", LocalDate.parse("2016-04-15"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.hm">http://www.registry.hm</a><br><b>WHOIS Server:</b> whois.registry.hm<br>
     *
     */
    public static final @NotNull TLD HM = new TLD("hm", Type.COUNTRY_CODE, "HM Domain Registry", LocalDate.parse("1997-07-24"), LocalDate.parse("2023-12-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.hn">https://www.nic.hn</a><br><b>WHOIS Server:</b> whois.nic.hn<br>
     *
     */
    public static final @NotNull TLD HN = new TLD("hn", Type.COUNTRY_CODE, "Red de Desarrollo Sostenible Honduras", LocalDate.parse("1993-04-16"), LocalDate.parse("2024-07-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.hockey<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150504-hockey">Delegation of the .hockey domain to Half Willow, LLC (2015-05-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOCKEY = new TLD("hockey", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-30"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.holdings<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-holdings">Delegation of the .HOLDINGS domain to John Madison, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOLDINGS = new TLD("holdings", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.holiday<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-holiday">Delegation of the .HOLIDAY domain to Goose Woods, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOLIDAY = new TLD("holiday", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.homedepot.com">http://www.homedepot.com</a><br><b>WHOIS Server:</b> whois.nic.homedepot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150602-homedepot">Delegation of the .homedepot domain to Homer TLC, Inc. (2015-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170221-homedepot">Transfer of the .homedepot domain to Home Depot Product Authority, LLC (2017-02-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOMEDEPOT = new TLD("homedepot", Type.GENERIC, "Home Depot Product Authority, LLC", LocalDate.parse("2015-05-29"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-homegoods">Delegation of the .homegoods domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOMEGOODS = new TLD("homegoods", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.homes/">https://nic.homes/</a><br><b>WHOIS Server:</b> whois.nic.homes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140514-homes">Delegation of the .homes domain to DERHomes, LLC (2014-05-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210405-homes">Transfer of the .homes domain to XYZ.COM LLC (2021-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOMES = new TLD("homes", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-05-01"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-homesense">Delegation of the .homesense domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOMESENSE = new TLD("homesense", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://honda.com">http://honda.com</a><br><b>WHOIS Server:</b> whois.nic.honda<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150424-honda">Delegation of the .honda domain to Honda Motor Co., Ltd. (2015-04-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HONDA = new TLD("honda", Type.GENERIC, "Honda Motor Co., Ltd.", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160722-honeywell">Delegation of the .honeywell domain to Honeywell GTLD LLC (2016-07-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190606-honeywell">Revocation of the .honeywell domain (2019-06-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HONEYWELL = new TLD("honeywell", Type.GENERIC, null, LocalDate.parse("2016-06-03"), LocalDate.parse("2019-06-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.horse/">http://nic.horse/</a><br><b>WHOIS Server:</b> whois.nic.horse<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-horse">Delegation of the .horse domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-horse">Transfer of the .horse domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HORSE = new TLD("horse", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.hospital<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161208-hospital">Delegation of the .hospital domain to Ruby Pike, LLC (2016-12-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOSPITAL = new TLD("hospital", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2016-12-02"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.host<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140527-host">Delegation of the .host domain to DotHost Inc. (2014-05-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210825-host">Transfer of the .host domain to Radix FZC (2021-08-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-host">Transfer of the .host domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOST = new TLD("host", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2014-05-22"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.hosting">https://nic.hosting</a><br><b>WHOIS Server:</b> whois.nic.hosting<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-hosting">Delegation of the .hosting domain to Uniregistry, Corp. (2014-08-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-hosting">Transfer of the .hosting domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOSTING = new TLD("hosting", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-08-15"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.hot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160805-hot">Delegation of the .hot domain to Amazon Registry Services, Inc. (2016-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOT = new TLD("hot", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150623-hoteles">Delegation of the .hoteles domain to Travel Reservations SRL (2015-06-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230707-hoteles">Revocation of the .hoteles domain (2023-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOTELES = new TLD("hoteles", Type.GENERIC, null, LocalDate.parse("2015-05-14"), LocalDate.parse("2023-07-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.booking.com">http://www.booking.com</a><br><b>WHOIS Server:</b> whois.nic.hotels<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170403-hotels">Delegation of the .hotels domain to Booking.com B.V. (2017-04-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOTELS = new TLD("hotels", Type.GENERIC, "Booking.com B.V.", LocalDate.parse("2016-09-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.microsoft.com">http://www.microsoft.com</a><br><b>WHOIS Server:</b> whois.nic.hotmail<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150604-hotmail">Delegation of the .hotmail domain to Microsoft Corporation (2015-06-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOTMAIL = new TLD("hotmail", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.house<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-house">Delegation of the .HOUSE domain to Sugar Park, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOUSE = new TLD("house", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-how">Delegation of the .how domain to Charleston Road Registry Inc. (2014-08-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HOW = new TLD("how", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dns.hr">http://www.dns.hr</a><br><b>WHOIS Server:</b> whois.dns.hr<br>
     *
     */
    public static final @NotNull TLD HR = new TLD("hr", Type.COUNTRY_CODE, "CARNet - Croatian Academic and Research Network", LocalDate.parse("1993-02-27"), LocalDate.parse("2023-10-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.hsbc">http://www.nic.hsbc</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150708-hsbc">Delegation of the .hsbc domain to HSBC Holdings PLC (2015-07-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161216-hsbc">Transfer of the .hsbc domain to HSBC Global Services (UK) Limited (2016-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HSBC = new TLD("hsbc", Type.GENERIC, "HSBC Global Services (UK) Limited", LocalDate.parse("2015-01-15"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ht">http://www.nic.ht</a><br><b>WHOIS Server:</b> whois.nic.ht<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2004/ht-report-13jan04.html">IANA Report on Redelegation of the .HT Top-Level Domain (2004-01-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HT = new TLD("ht", Type.COUNTRY_CODE, "Consortium FDS/RDDH", LocalDate.parse("1997-03-06"), LocalDate.parse("2023-03-15"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160329-htc">Delegation of the .htc domain to HTC Corporation (2016-03-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20171009-htc">Revocation of the .htc domain (2017-10-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HTC = new TLD("htc", Type.GENERIC, null, LocalDate.parse("2016-03-03"), LocalDate.parse("2017-10-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.domain.hu">https://www.domain.hu</a><br><b>WHOIS Server:</b> whois.nic.hu<br>
     *
     */
    public static final @NotNull TLD HU = new TLD("hu", Type.COUNTRY_CODE, "Council of Hungarian Internet Providers (CHIP)", LocalDate.parse("1990-11-07"), LocalDate.parse("2024-04-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hughes.com/">http://www.hughes.com/</a><br><b>WHOIS Server:</b> whois.nic.hughes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160808-hughes">Delegation of the .hughes domain to Hughes Satellite Systems Corporation (2016-08-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HUGHES = new TLD("hughes", Type.GENERIC, "Hughes Satellite Systems Corporation", LocalDate.parse("2016-08-04"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hyatt.com">http://www.hyatt.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160726-hyatt">Delegation of the .hyatt domain to Hyatt GTLD, L.L.C. (2016-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HYATT = new TLD("hyatt", Type.GENERIC, "Hyatt GTLD, L.L.C.", LocalDate.parse("2016-07-22"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hyundai.com">http://www.hyundai.com</a><br><b>WHOIS Server:</b> whois.nic.hyundai<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150923-hyundai">Delegation of the .hyundai domain to Hyundai Motor Company (2015-09-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD HYUNDAI = new TLD("hyundai", Type.GENERIC, "Hyundai Motor Company", LocalDate.parse("2015-09-17"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ibm.com">http://www.ibm.com</a><br><b>WHOIS Server:</b> whois.nic.ibm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140926-ibm">Delegation of the .ibm domain to International Business Machines Corporation (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IBM = new TLD("ibm", Type.GENERIC, "International Business Machines Corporation", LocalDate.parse("2014-09-25"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.icbc.com.cn/ICBC/%E5%9F%9F%E5%90%8D%E6%B3%A8%E5%86%8C%E5%B1%80/default.htm">http://www.icbc.com.cn/ICBC/%E5%9F%9F%E5%90%8D%E6%B3%A8%E5%86%8C%E5%B1%80/default.htm</a><br><b>WHOIS Server:</b> whois.nic.icbc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150511-icbc">Delegation of the .icbc domain to Industrial and Commercial Bank of China Limited (2015-05-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ICBC = new TLD("icbc", Type.GENERIC, "Industrial and Commercial Bank of China Limited", LocalDate.parse("2015-04-09"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.ice<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150720-ice">Delegation of the .ice domain to IntercontinentalExchange, Inc. (2015-07-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ICE = new TLD("ice", Type.GENERIC, "IntercontinentalExchange, Inc.", LocalDate.parse("2015-07-09"), LocalDate.parse("2023-09-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.icu/">http://nic.icu/</a><br><b>WHOIS Server:</b> whois.nic.icu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150428-icu">Delegation of the .icu domain to One.com A/S (2015-04-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180215-icu">Transfer of the .icu domain to Shortdot SA (2018-02-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ICU = new TLD("icu", Type.GENERIC, "Shortdot SA", LocalDate.parse("2015-03-06"), LocalDate.parse("2024-06-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://pandi.id">https://pandi.id</a><br><b>WHOIS Server:</b> whois.id<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/id-report-20130524.html">Report on the Redelegation of the .ID domain representing Indonesia to Perkumpulan Pengelola Nama Domain Internet Indonesia (2013-05-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ID = new TLD("id", Type.COUNTRY_CODE, "Perkumpulan Pengelola Nama Domain Internet Indonesia (PANDI)", LocalDate.parse("1993-02-27"), LocalDate.parse("2023-11-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.weare.ie">http://www.weare.ie</a><br><b>WHOIS Server:</b> whois.weare.ie<br>
     *
     */
    public static final @NotNull TLD IE = new TLD("ie", Type.COUNTRY_CODE, "University College Dublin Computing Services Computer Centre", LocalDate.parse("1988-01-27"), LocalDate.parse("2023-06-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ieee.org">http://www.ieee.org</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-ieee">Delegation of the .ieee domain to IEEE Global LLC (2016-07-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IEEE = new TLD("ieee", Type.GENERIC, "IEEE Global LLC", LocalDate.parse("2016-06-09"), LocalDate.parse("2022-03-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ifm.com">http://www.ifm.com</a><br><b>WHOIS Server:</b> whois.nic.ifm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-ifm">Delegation of the .ifm domain to ifm electronic gmbh (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IFM = new TLD("ifm", Type.GENERIC, "ifm electronic gmbh", LocalDate.parse("2015-01-15"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-iinet">Delegation of the .iinet domain to Connect West Pty. Ltd. (2015-07-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161219-iinet">Revocation of the .iinet domain (2016-12-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IINET = new TLD("iinet", Type.GENERIC, null, LocalDate.parse("2014-08-18"), LocalDate.parse("2016-12-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ikano.lu">http://www.ikano.lu</a><br><b>WHOIS Server:</b> whois.nic.ikano<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160628-ikano">Delegation of the .ikano domain to Ikano S.A. (2016-06-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IKANO = new TLD("ikano", Type.GENERIC, "Ikano S.A.", LocalDate.parse("2016-06-03"), LocalDate.parse("2022-05-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.isoc.org.il/domains">http://www.isoc.org.il/domains</a><br><b>WHOIS Server:</b> whois.isoc.org.il<br>
     *
     */
    public static final @NotNull TLD IL = new TLD("il", Type.COUNTRY_CODE, "The Israel Internet Association (RA)", LocalDate.parse("1985-10-24"), LocalDate.parse("2024-07-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.im">http://www.nic.im</a><br><b>WHOIS Server:</b> whois.nic.im<br>
     *
     */
    public static final @NotNull TLD IM = new TLD("im", Type.COUNTRY_CODE, "Isle of Man Government", LocalDate.parse("1996-09-11"), LocalDate.parse("2018-11-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.akdn.org">http://www.akdn.org</a><br><b>WHOIS Server:</b> whois.nic.imamat<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160413-imamat">Delegation of the .imamat domain to Fondation Aga Khan (Aga Khan Foundation) (2016-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IMAMAT = new TLD("imamat", Type.GENERIC, "Fondation Aga Khan (Aga Khan Foundation)", LocalDate.parse("2016-04-07"), LocalDate.parse("2023-08-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.imdb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-imdb">Delegation of the .imdb domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IMDB = new TLD("imdb", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.immo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140822-immo">Delegation of the .immo domain to Auburn Bloom, LLC (2014-08-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IMMO = new TLD("immo", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-08-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.immobilien<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131224-immobilien">Delegation of the .IMMOBILIEN domain to United TLD Holdco Ltd (2013-12-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-immobilien">Transfer of the .immobilien domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IMMOBILIEN = new TLD("immobilien", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.in">http://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2009/in-report-27nov2009.html">Report on the Redelegation of the .IN domain representing India to the National Internet Exchange of India (2009-11-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IN = new TLD("in", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("1989-05-08"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.inc">https://get.inc</a><br><b>WHOIS Server:</b> whois.nic.inc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180716-inc">Delegation of the .inc domain to Intercap Holdings Inc. (2018-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INC = new TLD("inc", Type.GENERIC, "Intercap Registry Inc.", LocalDate.parse("2018-07-03"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.industries<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140214-industries">Delegation of the .industries domain to Outer House, LLC (2014-02-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INDUSTRIES = new TLD("industries", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150302-infiniti">Delegation of the .infiniti domain to Nissan Motor Co., Ltd. (2015-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INFINITI = new TLD("infiniti", Type.GENERIC, "NISSAN MOTOR CO., LTD.", LocalDate.parse("2014-07-10"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .INFO through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.info<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/biz-info-report-25jun01.html">IANA Report on Establishment of the .BIZ and .INFO Top-Level Domains (2001-06-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INFO = new TLD("info", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2001-06-26"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-ing">Delegation of the .ing domain to Charleston Road Registry Inc. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ING = new TLD("ing", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.ink/">http://nic.ink/</a><br><b>WHOIS Server:</b> whois.nic.ink<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230626-ink">Transfer of the .ink domain to Registry Services, LLC (2023-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INK = new TLD("ink", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-27"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.institute<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-institute">Delegation of the .INSTITUTE domain to Outer Maple, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INSTITUTE = new TLD("institute", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.ftld.com/">https://www.ftld.com/</a><br><b>WHOIS Server:</b> whois.nic.insurance<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151125-insurance">Delegation of the .insurance domain to fTLD Registry Services LLC (2015-11-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INSURANCE = new TLD("insurance", Type.GENERIC, "fTLD Registry Services LLC", LocalDate.parse("2015-11-06"), LocalDate.parse("2024-07-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.insure<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140425-insure">Delegation of the .insure domain to Pioneer Willow, LLC (2014-04-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INSURE = new TLD("insure", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-24"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.iana.org/domains/int">https://www.iana.org/domains/int</a><br><b>WHOIS Server:</b> whois.iana.org<br>
     *
     */
    public static final @NotNull TLD INT = new TLD("int", Type.SPONSORED, "Internet Assigned Numbers Authority", LocalDate.parse("1988-11-03"), LocalDate.parse("2024-05-28"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160726-intel">Delegation of the .intel domain to Intel Corporation (2016-07-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201007-intel">Revocation of the .intel domain (2020-10-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INTEL = new TLD("intel", Type.GENERIC, null, LocalDate.parse("2016-04-21"), LocalDate.parse("2020-10-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.international<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INTERNATIONAL = new TLD("international", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.intuit.com/">https://www.intuit.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-intuit">Delegation of the .intuit domain to Intuit Administrative Services, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INTUIT = new TLD("intuit", Type.GENERIC, "Intuit Administrative Services, Inc.", LocalDate.parse("2016-06-09"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.investments<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-investments">Delegation of the .investments domain to Holly Glen, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD INVESTMENTS = new TLD("investments", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.io/">http://www.nic.io/</a><br><b>WHOIS Server:</b> whois.nic.io<br>
     *
     */
    public static final @NotNull TLD IO = new TLD("io", Type.COUNTRY_CODE, "Internet Computer Bureau Limited", LocalDate.parse("1997-09-16"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://ipiranga.com.br">http://ipiranga.com.br</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150724-ipiranga">Delegation of the .ipiranga domain to Ipiranga Produtos de Petroleo S.A. (2015-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IPIRANGA = new TLD("ipiranga", Type.GENERIC, "Ipiranga Produtos de Petroleo S.A.", LocalDate.parse("2015-04-02"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://registrar.cmc.iq">https://registrar.cmc.iq</a><br><b>WHOIS Server:</b> whois.cmc.iq<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/iq-report-05aug2005.pdf">IANA Report on the Redelegation of the .IQ Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IQ = new TLD("iq", Type.COUNTRY_CODE, "Communications and Media Commission (CMC)", LocalDate.parse("1997-05-09"), LocalDate.parse("2023-12-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ir">http://www.nic.ir</a><br><b>WHOIS Server:</b> whois.nic.ir<br>
     *
     */
    public static final @NotNull TLD IR = new TLD("ir", Type.COUNTRY_CODE, "Institute for Research in Fundamental Sciences", LocalDate.parse("1994-04-06"), LocalDate.parse("2023-10-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.irish<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141125-irish">Delegation of the .irish domain to Dot-Irish LLC (2014-11-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170301-irish">Transfer of the .irish domain to Tin Mill LLC (2017-03-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IRISH = new TLD("irish", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-11-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.isnic.is/">https://www.isnic.is/</a><br><b>WHOIS Server:</b> whois.isnic.is<br>
     *
     */
    public static final @NotNull TLD IS = new TLD("is", Type.COUNTRY_CODE, "ISNIC - Internet Iceland ltd.", LocalDate.parse("1987-11-18"), LocalDate.parse("2024-03-08"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160111-iselect">Delegation of the .iselect domain to iSelect Ltd (2016-01-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190805-iselect">Revocation of the .iselect domain (2019-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ISELECT = new TLD("iselect", Type.GENERIC, null, LocalDate.parse("2015-11-20"), LocalDate.parse("2019-08-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.akdn.org/">http://www.akdn.org/</a><br><b>WHOIS Server:</b> whois.nic.ismaili<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160413-ismaili">Delegation of the .ismaili domain to Fondation Aga Khan (Aga Khan Foundation) (2016-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ISMAILI = new TLD("ismaili", Type.GENERIC, "Fondation Aga Khan (Aga Khan Foundation)", LocalDate.parse("2016-04-07"), LocalDate.parse("2023-08-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.istanbul">http://www.nic.istanbul</a><br><b>WHOIS Server:</b> whois.nic.ist<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150709-ist">Delegation of the .ist domain to Istanbul Metropolitan Municipality (2015-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IST = new TLD("ist", Type.GENERIC, "Istanbul Metropolitan Municipality", LocalDate.parse("2015-05-29"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.istanbul">http://www.nic.istanbul</a><br><b>WHOIS Server:</b> whois.nic.istanbul<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150709-istanbul">Delegation of the .istanbul domain to Istanbul Metropolitan Municipality (2015-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ISTANBUL = new TLD("istanbul", Type.GENERIC, "Istanbul Metropolitan Municipality", LocalDate.parse("2015-05-29"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.it/">http://www.nic.it/</a><br><b>WHOIS Server:</b> whois.nic.it<br>
     *
     */
    public static final @NotNull TLD IT = new TLD("it", Type.COUNTRY_CODE, "IIT - CNR", LocalDate.parse("1987-12-23"), LocalDate.parse("2023-01-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.itau.com">http://www.itau.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150720-itau">Delegation of the .itau domain to Itau Unibanco Holding S.A. (2015-07-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ITAU = new TLD("itau", Type.GENERIC, "Itau Unibanco Holding S.A.", LocalDate.parse("2015-05-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.itv.com">http://www.itv.com</a><br><b>WHOIS Server:</b> whois.nic.itv<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160616-itv">Delegation of the .itv domain to ITV Services Limited (2016-06-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ITV = new TLD("itv", Type.GENERIC, "ITV Services Limited", LocalDate.parse("2016-05-19"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161028-iveco">Delegation of the .iveco domain to CNH Industrial N.V. (2016-10-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210421-iveco">Revocation of the .iveco domain (2021-04-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IVECO = new TLD("iveco", Type.GENERIC, null, LocalDate.parse("2016-10-27"), LocalDate.parse("2021-04-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141211-iwc">Delegation of the .iwc domain to Richemont DNS Inc. (2014-12-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180628-iwc">Revocation of the .iwc domain (2018-06-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD IWC = new TLD("iwc", Type.GENERIC, null, LocalDate.parse("2014-11-20"), LocalDate.parse("2018-06-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.jaguar<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151023-jaguar">Delegation of the .jaguar domain to Jaguar Land Rover Ltd (2015-10-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JAGUAR = new TLD("jaguar", Type.GENERIC, "Jaguar Land Rover Ltd", LocalDate.parse("2015-10-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.java<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150227-java">Delegation of the .java domain to Oracle Corporation (2015-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JAVA = new TLD("java", Type.GENERIC, "Oracle Corporation", LocalDate.parse("2015-01-15"), LocalDate.parse("2024-02-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.jcb">http://nic.jcb</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150116-jcb">Delegation of the .jcb domain to JCB Co., Ltd. (2015-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JCB = new TLD("jcb", Type.GENERIC, "JCB Co., Ltd.", LocalDate.parse("2015-01-08"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160324-jcp">Delegation of the .jcp domain to JCP Media, Inc. (2016-03-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201120-jcp">Revocation of the .jcp domain (2020-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JCP = new TLD("jcp", Type.GENERIC, null, LocalDate.parse("2016-03-03"), LocalDate.parse("2020-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.je">http://www.nic.je</a><br><b>WHOIS Server:</b> whois.je<br>
     *
     */
    public static final @NotNull TLD JE = new TLD("je", Type.COUNTRY_CODE, "Island Networks (Jersey) Ltd.", LocalDate.parse("1996-08-08"), LocalDate.parse("2024-02-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.fcausllc.com/">http://www.fcausllc.com/</a><br><b>WHOIS Server:</b> whois.nic.jeep<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-jeep">Delegation of the .jeep domain to FCA US Llc (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JEEP = new TLD("jeep", Type.GENERIC, "FCA US LLC.", LocalDate.parse("2016-07-07"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.jetzt<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140314-jetzt">Delegation of the .jetzt domain to New TLD Company AB (2014-03-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JETZT = new TLD("jetzt", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-03-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.jewelry<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150414-jewelry">Delegation of the .jewelry domain to Wild Bloom, LLC (2015-04-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JEWELRY = new TLD("jewelry", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ril.com">http://www.ril.com</a><br><b>WHOIS Server:</b> whois.nic.jio<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-jio">Delegation of the .jio domain to Affinity Names, Inc. (2016-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240423-jio">Transfer of the .jio domain to Reliance Industries Limited (2024-04-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JIO = new TLD("jio", Type.GENERIC, "Reliance Industries Limited", LocalDate.parse("2016-08-11"), LocalDate.parse("2024-04-23"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150604-jlc">Delegation of the .jlc domain to Richemont DNS Inc. (2015-06-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180917-jlc">Revocation of the .jlc domain (2018-09-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JLC = new TLD("jlc", Type.GENERIC, null, LocalDate.parse("2015-03-12"), LocalDate.parse("2018-09-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.jll.com">http://www.jll.com</a><br><b>WHOIS Server:</b> whois.nic.jll<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150521-jll">Delegation of the .jll domain to Jones Lang LaSalle Incorporated (2015-05-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JLL = new TLD("jll", Type.GENERIC, "Jones Lang LaSalle Incorporated", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD JM = new TLD("jm", Type.COUNTRY_CODE, "University of West Indies", LocalDate.parse("1991-09-24"), LocalDate.parse("2020-03-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.jmp.com">http://www.jmp.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151216-jmp">Delegation of the .jmp domain to Matrix IP LLC (2015-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JMP = new TLD("jmp", Type.GENERIC, "Matrix IP LLC", LocalDate.parse("2015-11-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.jnj.com/">http://www.jnj.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160406-jnj">Delegation of the .jnj domain to Johnson and Johnson Services, Inc. (2016-04-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JNJ = new TLD("jnj", Type.GENERIC, "Johnson and Johnson Services, Inc.", LocalDate.parse("2016-01-14"), LocalDate.parse("2024-07-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dns.jo">https://www.dns.jo</a><br><br>
     *
     */
    public static final @NotNull TLD JO = new TLD("jo", Type.COUNTRY_CODE, "Ministry of Digital Economy and Entrepreneurship (MoDEE)", LocalDate.parse("1994-11-23"), LocalDate.parse("2024-03-04"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .JOBS through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.goto.jobs">http://www.goto.jobs</a><br><b>WHOIS Server:</b> whois.nic.jobs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/jobs-report-31aug2005.html">IANA Report on the Delegation of the .JOBS Top-Level Domain (2005-08-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JOBS = new TLD("jobs", Type.SPONSORED, "Employ Media LLC", LocalDate.parse("2005-09-08"), LocalDate.parse("2021-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.net.za">http://www.registry.net.za</a><br><b>WHOIS Server:</b> whois.nic.joburg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140617-joburg">Delegation of the .joburg domain to ZA Central Registry NPC trading as ZA Central Registry (2014-06-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JOBURG = new TLD("joburg", Type.GENERIC, "ZA Central Registry NPC trading as ZA Central Registry", LocalDate.parse("2014-06-05"), LocalDate.parse("2022-05-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.jot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-jot">Delegation of the .circle domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JOT = new TLD("jot", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.joy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-joy">Delegation of the .joy domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JOY = new TLD("joy", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://jprs.jp/">https://jprs.jp/</a><br><b>WHOIS Server:</b> whois.jprs.jp<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/jp-report-01apr02.html">Second IANA Report on Request for Redelegation of the .JP Top-Level Domain (2002-04-01)</a></li>
     *   <li><a href="https://iana.org/reports/2002/jp-report-08feb02.html">IANA Report on Request for Redelegation of the .JP Top-Level Domain (2002-02-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JP = new TLD("jp", Type.COUNTRY_CODE, "Japan Registry Services Co., Ltd.", LocalDate.parse("1986-08-05"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.jpmorganchase.com">http://www.jpmorganchase.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160225-jpmorgan">Delegation of the .jpmorgan domain to JPMorgan Chase and Co. (2016-02-25)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160718-jpmorgan">Redelegation of the .jpmorgan domain to JPMorgan Chase Bank, National Association (2016-07-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JPMORGAN = new TLD("jpmorgan", Type.GENERIC, "JPMorgan Chase Bank, National Association", LocalDate.parse("2016-01-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.jprs/">https://nic.jprs/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-jprs">Delegation of the .jprs domain to Japan Registry Services Co., Ltd. (2015-07-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JPRS = new TLD("jprs", Type.GENERIC, "Japan Registry Services Co., Ltd.", LocalDate.parse("2014-12-18"), LocalDate.parse("2023-09-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://uniregistry.link">http://uniregistry.link</a><br><b>WHOIS Server:</b> whois.uniregistry.net<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140512-juegos">Delegation of the .juegos domain to Uniregistry, Corp. (2014-05-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230321-juegos">Transfer of the .juegos domain to Internet Naming Company LLC (2023-03-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240411-juegos">Transfer of the .juegos domain to Dog Beach, LLC (2024-04-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JUEGOS = new TLD("juegos", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2024-05-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://juniper.net">http://juniper.net</a><br><b>WHOIS Server:</b> whois.nic.juniper<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-juniper">Delegation of the .juniper domain to Juniper Networks Inc. (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD JUNIPER = new TLD("juniper", Type.GENERIC, "JUNIPER NETWORKS, INC.", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.kaufen<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131224-kaufen">Delegation of the .KAUFEN domain to United TLD Holdco Ltd (2013-12-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-kaufen">Transfer of the .kaufen domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KAUFEN = new TLD("kaufen", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.kddi">http://nic.kddi</a><br><b>WHOIS Server:</b> whois.nic.kddi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141229-kddi">Delegation of the .kddi domain to KDDI Corporation (2014-12-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KDDI = new TLD("kddi", Type.GENERIC, "KDDI CORPORATION", LocalDate.parse("2014-12-18"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kenic.or.ke">http://www.kenic.or.ke</a><br><b>WHOIS Server:</b> whois.kenic.or.ke<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/ke-report-20dec02.html">IANA Report on Redelegation of the .KE Top-Level Domain (2002-12-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KE = new TLD("ke", Type.COUNTRY_CODE, "Kenya Network Information Center (KeNIC)", LocalDate.parse("1993-04-29"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kerryprops.com">http://www.kerryprops.com</a><br><b>WHOIS Server:</b> whois.nic.kerryhotels<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160302-kerryhotels">Delegation of the .kerryhotels domain to Kerry Trading Co. Limited (2016-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KERRYHOTELS = new TLD("kerryhotels", Type.GENERIC, "Kerry Trading Co. Limited", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kerryprops.com">http://www.kerryprops.com</a><br><b>WHOIS Server:</b> whois.nic.kerrylogistics<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160302-kerrylogistics">Delegation of the .kerrylogistics domain to Kerry Trading Co. Limited (2016-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KERRYLOGISTICS = new TLD("kerrylogistics", Type.GENERIC, "Kerry Trading Co. Limited", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kerryprops.com">http://www.kerryprops.com</a><br><b>WHOIS Server:</b> whois.nic.kerryproperties<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160302-kerryproperties">Delegation of the .kerryproperties domain to Kerry Trading Co. Limited (2016-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KERRYPROPERTIES = new TLD("kerryproperties", Type.GENERIC, "Kerry Trading Co. Limited", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://kfh.com">http://kfh.com</a><br><b>WHOIS Server:</b> whois.nic.kfh<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151211-kfh">Delegation of the .kfh domain to Kuwait Finance House (2015-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KFH = new TLD("kfh", Type.GENERIC, "Kuwait Finance House", LocalDate.parse("2015-10-29"), LocalDate.parse("2024-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.cctld.kg/">https://www.cctld.kg/</a><br><b>WHOIS Server:</b> whois.kg<br>
     *
     */
    public static final @NotNull TLD KG = new TLD("kg", Type.COUNTRY_CODE, "AsiaInfo Telecommunication Enterprise", LocalDate.parse("1995-07-12"), LocalDate.parse("2023-12-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tc.com.kh">http://www.tc.com.kh</a><br><br>
     *
     */
    public static final @NotNull TLD KH = new TLD("kh", Type.COUNTRY_CODE, "Telecommunication Regulator of Cambodia (TRC)", LocalDate.parse("1996-02-20"), LocalDate.parse("2019-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ki">http://www.nic.ki</a><br><b>WHOIS Server:</b> whois.nic.ki<br>
     *
     */
    public static final @NotNull TLD KI = new TLD("ki", Type.COUNTRY_CODE, "Ministry of Information, Communications and Transport (MICT)", LocalDate.parse("1995-04-19"), LocalDate.parse("2024-02-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kia.com">http://www.kia.com</a><br><b>WHOIS Server:</b> whois.nic.kia<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150923-kia">Delegation of the .kia domain to Kia Motors Corporation (2015-09-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KIA = new TLD("kia", Type.GENERIC, "KIA MOTORS CORPORATION", LocalDate.parse("2015-09-17"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dotkids.asia">https://www.dotkids.asia</a><br><b>WHOIS Server:</b> whois.nic.kids<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220404-kids">Delegation of the .kids domain to DotKids Foundation Limited (2022-04-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KIDS = new TLD("kids", Type.GENERIC, "DotKids Foundation Limited", LocalDate.parse("2022-03-05"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.kim<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140121-kim">Delegation of the .KIM domain to Afilias Limited (2014-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KIM = new TLD("kim", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151007-kinder">Delegation of the .kinder domain to Ferrero Trading Lux S.A. (2015-10-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231102-kinder">Revocation of the .kinder domain (2023-11-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KINDER = new TLD("kinder", Type.GENERIC, null, LocalDate.parse("2015-09-10"), LocalDate.parse("2023-11-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.kindle<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-kindle">Delegation of the .kindle domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KINDLE = new TLD("kindle", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.kitchen<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131116-kitchen">Delegation of the .KITCHEN domain to Just Goodbye, LLC (2013-11-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KITCHEN = new TLD("kitchen", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://hello.kiwi">http://hello.kiwi</a><br><b>WHOIS Server:</b> whois.nic.kiwi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-kiwi">Delegation of the .KIWI domain to Dot Kiwi Limited (2013-12-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KIWI = new TLD("kiwi", Type.GENERIC, "DOT KIWI LIMITED", LocalDate.parse("2013-11-25"), LocalDate.parse("2024-03-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.domaine.km/">http://www.domaine.km/</a><br><br>
     *
     */
    public static final @NotNull TLD KM = new TLD("km", Type.COUNTRY_CODE, "Comores Telecom", LocalDate.parse("1998-06-08"), LocalDate.parse("2020-06-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.kn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2008/kn-report-29apr2008.html">Report on the Redelegation of the .KN Top-Level Domain to the "Ministry of Finance, Sustainable Development, Information and Technology" (2008-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KN = new TLD("kn", Type.COUNTRY_CODE, "Ministry of Finance, Sustainable Development Information and Technology", LocalDate.parse("1991-09-03"), LocalDate.parse("2022-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.koeln">https://www.nic.koeln</a><br><b>WHOIS Server:</b> whois.ryce-rsp.com<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140304-koeln">Delegation of the .koeln domain to NetCologne Gesellschaft für Telekommunikation mbH (2014-03-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180430-koeln">Transfer of the .koeln domain to punkt.wien GmbH (2018-04-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180620-koeln">Transfer of the .koeln domain to dotKoeln GmbH (2018-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KOELN = new TLD("koeln", Type.GENERIC, "dotKoeln GmbH", LocalDate.parse("2014-02-20"), LocalDate.parse("2021-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.komatsu.com/">http://www.komatsu.com/</a><br><b>WHOIS Server:</b> whois.nic.komatsu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150325-komatsu">Delegation of the .komatsu domain to Komatsu Ltd. (2015-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KOMATSU = new TLD("komatsu", Type.GENERIC, "Komatsu Ltd.", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ok.org/">http://www.ok.org/</a><br><b>WHOIS Server:</b> whois.nic.kosher<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160609-kosher">Delegation of the .kosher domain to Kosher Marketing Assets LLC (2016-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KOSHER = new TLD("kosher", Type.GENERIC, "Kosher Marketing Assets LLC", LocalDate.parse("2016-06-03"), LocalDate.parse("2023-08-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.star.co.kp">http://www.star.co.kp</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/kp-report-11sep2007.html">Report on Delegation of the .KP Top-Level Domain to "Korea Computer Center" (2007-09-11)</a></li>
     *   <li><a href="https://iana.org/reports/2011/kp-report-20110401.html">Report on the Redelegation of the .KP domain representing the Democratic People’s Republic of Korea to Star Joint Venture Company (2011-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KP = new TLD("kp", Type.COUNTRY_CODE, "Star Joint Venture Company", LocalDate.parse("2007-09-24"), LocalDate.parse("2023-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://home.kpmg.com">https://home.kpmg.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160401-kpmg">Delegation of the .kpmg domain to KPMG International Cooperative (KPMG International Genossenschaft) (2016-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KPMG = new TLD("kpmg", Type.GENERIC, "KPMG International Cooperative (KPMG International Genossenschaft)", LocalDate.parse("2016-01-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151211-kpn">Delegation of the .kpn domain to Koninklijke KPN N.V. (2015-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KPN = new TLD("kpn", Type.GENERIC, "Koninklijke KPN N.V.", LocalDate.parse("2015-12-04"), LocalDate.parse("2024-06-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.or.kr/">http://www.nic.or.kr/</a><br><b>WHOIS Server:</b> whois.kr<br>
     *
     */
    public static final @NotNull TLD KR = new TLD("kr", Type.COUNTRY_CODE, "Korea Internet and Security Agency (KISA)", LocalDate.parse("1986-09-29"), LocalDate.parse("2020-07-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://dot.krd">https://dot.krd</a><br><b>WHOIS Server:</b> whois.nic.krd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140716-krd">Delegation of the .krd domain to KRG Department of Information Technology (2014-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KRD = new TLD("krd", Type.GENERIC, "KRG Department of Information Technology", LocalDate.parse("2014-07-10"), LocalDate.parse("2023-12-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.peoplebrowsr.com">http://www.peoplebrowsr.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-kred">Delegation of the .kred domain to KredTLD Pty Ltd (2014-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KRED = new TLD("kred", Type.GENERIC, "KredTLD Pty Ltd", LocalDate.parse("2014-02-20"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kerryprops.com">http://www.kerryprops.com</a><br><b>WHOIS Server:</b> whois.nic.kuokgroup<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160302-kuokgroup">Delegation of the .kuokgroup domain to Kerry Trading Co. Limited (2016-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KUOKGROUP = new TLD("kuokgroup", Type.GENERIC, "Kerry Trading Co. Limited", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.kw">http://www.nic.kw</a><br><br>
     *
     */
    public static final @NotNull TLD KW = new TLD("kw", Type.COUNTRY_CODE, "Communications and Information Technology Regulatory Authority", LocalDate.parse("1992-10-26"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://uniregistry.com">http://uniregistry.com</a><br><b>WHOIS Server:</b> whois.kyregistry.ky<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2003/ky-report-30jun03.html">IANA Report on Redelegation of the .KY Top-Level Domain (2003-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KY = new TLD("ky", Type.COUNTRY_CODE, "Utility Regulation and Competition Office (OfReg)", LocalDate.parse("1995-05-03"), LocalDate.parse("2024-05-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.kyoto">http://nic.kyoto</a><br><b>WHOIS Server:</b> whois.nic.kyoto<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150116-kyoto">Delegation of the .kyoto domain to Academic Institution: Kyoto Jyoho Gakuen (2015-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KYOTO = new TLD("kyoto", Type.GENERIC, "Academic Institution: Kyoto Jyoho Gakuen", LocalDate.parse("2015-01-08"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.kz">http://www.nic.kz</a><br><b>WHOIS Server:</b> whois.nic.kz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/kz-report-05aug2005.pdf">IANA Report on the Redelegation of the .KZ Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD KZ = new TLD("kz", Type.COUNTRY_CODE, "Association of IT Companies of Kazakhstan", LocalDate.parse("1994-09-19"), LocalDate.parse("2023-01-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.la">http://www.la</a><br><b>WHOIS Server:</b> whois.nic.la<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/la-report-11dec02.html">IANA Report on Redelegation of the .LA Top-Level Domain (2002-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LA = new TLD("la", Type.COUNTRY_CODE, "Lao National Internet Center (LANIC), Ministry of Technology and Communications", LocalDate.parse("1996-05-14"), LocalDate.parse("2023-03-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.lacaixa.com">http://www.lacaixa.com</a><br><b>WHOIS Server:</b> whois.nic.lacaixa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140716-lacaixa">Delegation of the .lacaixa domain to CAIXA D'ESTALVIS I PENSIONS DE BARCELONA (2014-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LACAIXA = new TLD("lacaixa", Type.GENERIC, "Fundación Bancaria Caixa d'Estalvis i Pensions de Barcelona, \"la Caixa\"", LocalDate.parse("2014-07-10"), LocalDate.parse("2024-03-22"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160726-ladbrokes">Delegation of the .ladbrokes domain to Ladbrokes International plc (2016-07-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191120-ladbrokes">Revocation of the .ladbrokes domain (2019-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LADBROKES = new TLD("ladbrokes", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2019-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.lamborghini.com">http://www.lamborghini.com</a><br><b>WHOIS Server:</b> whois.nic.lamborghini<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151117-lamborghini">Delegation of the .lamborghini domain to Automobili Lamborghini S.p.A. (2015-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LAMBORGHINI = new TLD("lamborghini", Type.GENERIC, "Automobili Lamborghini S.p.A.", LocalDate.parse("2015-11-12"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://elcompanies.com/Pages/Homepage.aspx">http://elcompanies.com/Pages/Homepage.aspx</a><br><b>WHOIS Server:</b> whois.nic.lamer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-lamer">Delegation of the .lamer domain to The Estée Lauder Companies Inc. (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LAMER = new TLD("lamer", Type.GENERIC, "The Estée Lauder Companies Inc.", LocalDate.parse("2015-12-10"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.lancaster.fr">http://www.lancaster.fr</a><br><b>WHOIS Server:</b> whois.nic.lancaster<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150709-lancaster">Delegation of the .lancaster domain to Lancaster (2015-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LANCASTER = new TLD("lancaster", Type.GENERIC, "LANCASTER", LocalDate.parse("2015-06-18"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-lancia">Delegation of the .lancia domain to Fiat Chrysler Automobiles N.V. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230605-lancia">Revocation of the .lancia domain (2023-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LANCIA = new TLD("lancia", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2023-06-05"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160713-lancome">Delegation of the .lancome domain to L'Oréal (2016-07-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191128-lancome">Revocation of the .lancome domain (2019-11-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LANCOME = new TLD("lancome", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2019-11-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.land<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-land">Delegation of the .LAND domain to Pine Moon, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LAND = new TLD("land", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.landrover<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151026-landrover">Delegation of the .landrover domain to Jaguar Land Rover Ltd (2015-10-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LANDROVER = new TLD("landrover", Type.GENERIC, "Jaguar Land Rover Ltd", LocalDate.parse("2015-10-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.lanxess.com">http://www.lanxess.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160121-lanxess">Delegation of the .lanxess domain to LANXESS Corporation (2016-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LANXESS = new TLD("lanxess", Type.GENERIC, "LANXESS Corporation", LocalDate.parse("2016-01-08"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.jll.com">http://www.jll.com</a><br><b>WHOIS Server:</b> whois.nic.lasalle<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150609-lasalle">Delegation of the .lasalle domain to Jones Lang LaSalle Incorporated (2015-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LASALLE = new TLD("lasalle", Type.GENERIC, "Jones Lang LaSalle Incorporated", LocalDate.parse("2015-06-04"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.lat">https://nic.lat</a><br><b>WHOIS Server:</b> whois.nic.lat<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141229-lat">Delegation of the .lat domain to ECOM-LAC Federaciòn de Latinoamèrica y el Caribe para Internet y el Comercio Electrònico (2014-12-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220922-lat">Transfer of the .lat domain to XYZ.COM LLC (2022-09-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LAT = new TLD("lat", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-12-04"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com/">http://www.dish.com/</a><br><b>WHOIS Server:</b> whois.nic.latino<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160729-latino">Delegation of the .latino domain to Dish DBS Corporation (2016-07-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LATINO = new TLD("latino", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.latrobe">http://nic.latrobe</a><br><b>WHOIS Server:</b> whois.nic.latrobe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141125-latrobe">Delegation of the .latrobe domain to La Trobe University (2014-11-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LATROBE = new TLD("latrobe", Type.GENERIC, "La Trobe University", LocalDate.parse("2014-11-06"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.law/">http://nic.law/</a><br><b>WHOIS Server:</b> whois.nic.law<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150624-law">Delegation of the .law domain to Minds + Machines Group Limited (2015-06-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180703-law">Transfer of the .law domain to LW TLD Limited (2018-07-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-law">Transfer of the .law domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LAW = new TLD("law", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-06-18"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.lawyer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-lawyer">Delegation of the .lawyer domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-lawyer">Transfer of the .lawyer domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LAWYER = new TLD("lawyer", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.lbdr.org.lb">https://www.lbdr.org.lb</a><br><b>WHOIS Server:</b> whois.lbdr.org.lb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2024/lb-report-20240121.html">Transfer of the .LB (Lebanon) top-level domain to the Internet Society Lebanon (2024-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LB = new TLD("lb", Type.COUNTRY_CODE, "Internet Society Lebanon", LocalDate.parse("1993-08-25"), LocalDate.parse("2024-02-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.lc">http://www.nic.lc</a><br><br>
     *
     */
    public static final @NotNull TLD LC = new TLD("lc", Type.COUNTRY_CODE, "University of Puerto Rico", LocalDate.parse("1991-09-03"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.lds.org">https://www.lds.org</a><br><b>WHOIS Server:</b> whois.nic.lds<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141117-lds">Delegation of the .lds domain to IRI Domain Management, LLC (2014-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LDS = new TLD("lds", Type.GENERIC, "IRI Domain Management, LLC", LocalDate.parse("2014-10-16"), LocalDate.parse("2023-08-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.lease<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-lease">Delegation of the .lease domain to Victor Trail, LLC (2014-04-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LEASE = new TLD("lease", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mouvement-leclerc.com">http://www.mouvement-leclerc.com</a><br><b>WHOIS Server:</b> whois.nic.leclerc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150227-leclerc">Delegation of the .leclerc domain to A.C.D. LEC Association des Centres Distributeurs Edouard Leclerc (2015-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LECLERC = new TLD("leclerc", Type.GENERIC, "A.C.D. LEC Association des Centres Distributeurs Edouard Leclerc", LocalDate.parse("2015-01-29"), LocalDate.parse("2024-03-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://lefrak.com">http://lefrak.com</a><br><b>WHOIS Server:</b> whois.nic.lefrak<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160711-lefrak">Delegation of the .lefrak domain to LeFrak Organization, Inc. (2016-07-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LEFRAK = new TLD("lefrak", Type.GENERIC, "LeFrak Organization, Inc.", LocalDate.parse("2016-03-31"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.legal<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141124-legal">Delegation of the .legal domain to Blue Falls, LLC (2014-11-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LEGAL = new TLD("legal", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.lego<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160613-lego">Delegation of the .lego domain to LEGO Juris A/S (2016-06-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LEGO = new TLD("lego", Type.GENERIC, "LEGO Juris A/S", LocalDate.parse("2016-03-24"), LocalDate.parse("2024-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://lexus.jp/">http://lexus.jp/</a><br><b>WHOIS Server:</b> whois.nic.lexus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150724-lexus">Delegation of the .lexus domain to Toyota Motor Corporation (2015-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LEXUS = new TLD("lexus", Type.GENERIC, "TOYOTA MOTOR CORPORATION", LocalDate.parse("2015-07-16"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.lgbt<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140630-lgbt">Delegation of the .lgbt domain to Afilias Limited (2014-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LGBT = new TLD("lgbt", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-06-26"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.li">https://www.nic.li</a><br><b>WHOIS Server:</b> whois.nic.li<br>
     *
     */
    public static final @NotNull TLD LI = new TLD("li", Type.COUNTRY_CODE, "SWITCH The Swiss Education and Research Network", LocalDate.parse("1993-02-26"), LocalDate.parse("2023-11-29"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-liaison">Delegation of the .liaison domain to Liaison Technologies, Incorporated (2015-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200105-liaison">Revocation of the .liaison domain (2020-01-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIAISON = new TLD("liaison", Type.GENERIC, null, LocalDate.parse("2015-03-19"), LocalDate.parse("2020-01-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.lidl">https://www.nic.lidl</a><br><b>WHOIS Server:</b> whois.nic.lidl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141211-lidl">Delegation of the .lidl domain to Schwarz Domains und Services GmbH and Co. KG (2014-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIDL = new TLD("lidl", Type.GENERIC, "Schwarz Domains und Services GmbH and Co. KG", LocalDate.parse("2014-12-04"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.life<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140509-life">Delegation of the .life domain to Trixy Oaks, LLC (2014-05-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIFE = new TLD("life", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://acli.com">http://acli.com</a><br><b>WHOIS Server:</b> whois.nic.lifeinsurance<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160113-lifeinsurance">Delegation of the .lifeinsurance domain to American Council of Life Insurers (2016-01-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIFEINSURANCE = new TLD("lifeinsurance", Type.GENERIC, "American Council of Life Insurers", LocalDate.parse("2015-07-09"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.lifestyle<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151106-lifestyle">Delegation of the .lifestyle domain to Lifestyle Domain Holdings, Inc. (2015-11-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240109-lifestyle">Transfer of the .lifestyle domain to Internet Naming Co. (2024-01-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIFESTYLE = new TLD("lifestyle", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2015-10-01"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.lighting<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-lighting">Delegation of the .LIGHTING domain to John McCook, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIGHTING = new TLD("lighting", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.like<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-like">Delegation of the .like domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIKE = new TLD("like", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.lilly.com">http://www.lilly.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160729-lilly">Delegation of the .lilly domain to Eli Lilly and Company (2016-07-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LILLY = new TLD("lilly", Type.GENERIC, "Eli Lilly and Company", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.limited<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-limited">Delegation of the .limited domain to Big Fest, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIMITED = new TLD("limited", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.limo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-limo">Delegation of the .LIMO domain to Hidden Frostbite, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIMO = new TLD("limo", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.lincoln.com">http://www.lincoln.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151215-lincoln">Delegation of the .lincoln domain to Ford Motor Company (2015-12-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LINCOLN = new TLD("lincoln", Type.GENERIC, "Ford Motor Company", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150914-linde">Delegation of the .linde domain to Linde Aktiengesellschaft (2015-09-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230317-linde">Revocation of the .linde domain (2023-03-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LINDE = new TLD("linde", Type.GENERIC, null, LocalDate.parse("2015-09-03"), LocalDate.parse("2023-03-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://uniregistry.link">http://uniregistry.link</a><br><b>WHOIS Server:</b> whois.uniregistry.net<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-link">Delegation of the .LINK domain to Uniregistry, Corp. (2014-01-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220519-link">Transfer of the .link domain to Nova Registry Ltd. (2022-05-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LINK = new TLD("link", Type.GENERIC, "Nova Registry Ltd.", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-05-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.lipsy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-lipsy">Delegation of the .lipsy domain to Lipsy Ltd (2016-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIPSY = new TLD("lipsy", Type.GENERIC, "Lipsy Ltd", LocalDate.parse("2016-04-21"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.live<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-live">Delegation of the .live domain to United TLD Holdco, Ltd. (2015-07-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-live">Transfer of the .live domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIVE = new TLD("live", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-06-25"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151223-living">Delegation of the .living domain to Lifestyle Domain Holdings, Inc. (2015-12-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240109-living">Transfer of the .living domain to Internet Naming Co. (2024-01-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIVING = new TLD("living", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-05-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-lixil">Delegation of the .lixil domain to LIXIL Group Corporation (2015-07-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211230-lixil">Revocation of the .lixil domain (2021-12-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LIXIL = new TLD("lixil", Type.GENERIC, null, LocalDate.parse("2015-07-16"), LocalDate.parse("2021-12-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.domains.lk/">http://www.domains.lk/</a><br><br>
     *
     */
    public static final @NotNull TLD LK = new TLD("lk", Type.COUNTRY_CODE, "Council for Information Technology LK Domain Registrar", LocalDate.parse("1990-06-15"), LocalDate.parse("2024-02-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.llc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180221-llc">Delegation of the .llc domain to Afilias plc (2018-02-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LLC = new TLD("llc", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2018-02-16"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://intercap.inc">https://intercap.inc</a><br><b>WHOIS Server:</b> whois.nic.llp<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191202-llp">Delegation of the .llp domain (2019-12-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201103-llp">Transfer of the .llp domain to UNR Corp. (2020-11-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220629-llp">Transfer of the .llp domain to Intercap Registry Inc. (2022-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LLP = new TLD("llp", Type.GENERIC, "Intercap Registry Inc.", LocalDate.parse("2019-11-20"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.loan">http://nic.loan</a><br><b>WHOIS Server:</b> whois.nic.loan<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-loan">Delegation of the .loan domain to dot Loan Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOAN = new TLD("loan", Type.GENERIC, "dot Loan Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.loans<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140509-loans">Delegation of the .loans domain to June Woods, LLC (2014-05-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOANS = new TLD("loans", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com">http://www.dish.com</a><br><b>WHOIS Server:</b> whois.nic.locker<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160525-locker">Delegation of the .locker domain to Dish DBS Corporation (2016-05-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240123-locker">Transfer of the .locker domain to Orange Domains LLC (2024-01-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOCKER = new TLD("locker", Type.GENERIC, "Orange Domains LLC", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-05-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.locus">http://nic.locus</a><br><b>WHOIS Server:</b> whois.nic.locus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160307-locus">Delegation of the .locus domain to Locus Analytics LLC (2016-03-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOCUS = new TLD("locus", Type.GENERIC, "Locus Analytics LLC", LocalDate.parse("2016-02-26"), LocalDate.parse("2021-11-25"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-loft">Delegation of the .loft domain to Annco, Inc. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221217-loft">Revocation of the .loft domain (2022-12-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOFT = new TLD("loft", Type.GENERIC, null, LocalDate.parse("2016-07-21"), LocalDate.parse("2022-12-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.lol">https://nic.lol</a><br><b>WHOIS Server:</b> whois.nic.lol<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-lol">Delegation of the .lol domain to Uniregistry, Corp. (2015-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-lol">Transfer of the .lol domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOL = new TLD("lol", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-04-23"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://dotlondondomains.london/">http://dotlondondomains.london/</a><br><b>WHOIS Server:</b> whois.nic.london<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140319-london">Delegation of the .london domain to Dot London Domains Limited (2014-03-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LONDON = new TLD("london", Type.GENERIC, "Dot London Domains Limited", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.lotte">http://nic.lotte</a><br><b>WHOIS Server:</b> whois.nic.lotte<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150112-lotte">Delegation of the .lotte domain to Lotte Holdings Co., Ltd. (2015-01-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOTTE = new TLD("lotte", Type.GENERIC, "Lotte Holdings Co., Ltd.", LocalDate.parse("2015-01-08"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.lotto<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140617-lotto">Delegation of the .lotto domain to Afilias Limited (2014-06-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOTTO = new TLD("lotto", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-06-05"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.love">https://get.love</a><br><b>WHOIS Server:</b> whois.nic.love<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150330-love">Delegation of the .love domain to Merchant Law Group LLP (2015-03-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240620-love">Transfer of the .love domain to Waterford Limited (2024-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LOVE = new TLD("love", Type.GENERIC, "Waterford Limited", LocalDate.parse("2015-03-25"), LocalDate.parse("2024-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.lpl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-lpl">Delegation of the .lpl domain to LPL Holdings, Inc. (2016-07-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LPL = new TLD("lpl", Type.GENERIC, "LPL Holdings, Inc.", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.lplfinancial<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-lplfinancial">Delegation of the .lplfinancial domain to LPL Holdings, Inc. (2016-07-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LPLFINANCIAL = new TLD("lplfinancial", Type.GENERIC, "LPL Holdings, Inc.", LocalDate.parse("2016-06-30"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://psg.com/dns/lr">http://psg.com/dns/lr</a><br><br>
     *
     */
    public static final @NotNull TLD LR = new TLD("lr", Type.COUNTRY_CODE, "Data Technology Solutions, Inc.", LocalDate.parse("1997-04-09"), LocalDate.parse("2024-04-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ls">http://www.nic.ls</a><br><b>WHOIS Server:</b> whois.nic.ls<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2018/ls-report-20180605.html">Transfer of the .LS (Lesotho) top-level domain to Lesotho Network Information Centre Proprietary (LSNIC) (2018-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LS = new TLD("ls", Type.COUNTRY_CODE, "Lesotho Network Information Centre Proprietary (LSNIC)", LocalDate.parse("1993-01-13"), LocalDate.parse("2024-06-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.domreg.lt">http://www.domreg.lt</a><br><b>WHOIS Server:</b> whois.domreg.lt<br>
     *
     */
    public static final @NotNull TLD LT = new TLD("lt", Type.COUNTRY_CODE, "Kaunas University of Technology", LocalDate.parse("1992-06-03"), LocalDate.parse("2023-11-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.ltd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150910-ltd">Delegation of the .ltd domain to Over Corner, LLC (2015-09-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LTD = new TLD("ltd", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-08-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.internetx.info">http://www.internetx.info</a><br><b>WHOIS Server:</b> whois.nic.ltda<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-ltda">Delegation of the .ltda domain to DOMAIN ROBOT SERVICOS DE HOSPEDAGEM NA INTERNET LTDA (2014-08-15)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140828-ltda">Redelegation of the .ltda domain to InterNetX Corp. (2014-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LTDA = new TLD("ltda", Type.GENERIC, "InterNetX Corp.", LocalDate.parse("2014-07-03"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dns.lu">http://www.dns.lu</a><br><b>WHOIS Server:</b> whois.dns.lu<br>
     *
     */
    public static final @NotNull TLD LU = new TLD("lu", Type.COUNTRY_CODE, "RESTENA", LocalDate.parse("1995-01-27"), LocalDate.parse("2023-07-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.lundbeck<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160713-lundbeck">Delegation of the .lundbeck domain to H. Lundbeck A/S (2016-07-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LUNDBECK = new TLD("lundbeck", Type.GENERIC, "H. Lundbeck A/S", LocalDate.parse("2016-06-16"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150514-lupin">Delegation of the .lupin domain to Lupin Limited (2015-05-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201210-lupin">Revocation of the .lupin domain (2020-12-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LUPIN = new TLD("lupin", Type.GENERIC, null, LocalDate.parse("2015-03-19"), LocalDate.parse("2020-12-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.luxe/">http://nic.luxe/</a><br><b>WHOIS Server:</b> whois.nic.luxe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140512-luxe">Delegation of the .luxe domain to Top Level Domain Holdings Limited (2014-05-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-luxe">Transfer of the .luxe domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LUXE = new TLD("luxe", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://join.luxury/">https://join.luxury/</a><br><b>WHOIS Server:</b> whois.nic.luxury<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-luxury">Delegation of the .LUXURY domain to Luxury Partners, LLC (2014-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LUXURY = new TLD("luxury", Type.GENERIC, "Luxury Partners LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-10-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.lv/">http://www.nic.lv/</a><br><b>WHOIS Server:</b> whois.nic.lv<br>
     *
     */
    public static final @NotNull TLD LV = new TLD("lv", Type.COUNTRY_CODE, "University of Latvia Institute of Mathematics and Computer Science Department of Network Solutions (DNS)", LocalDate.parse("1993-04-29"), LocalDate.parse("2021-12-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ly/">http://www.nic.ly/</a><br><b>WHOIS Server:</b> whois.nic.ly<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/ly-report-05aug2005.pdf">IANA Report on the Redelegation of the .LY Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD LY = new TLD("ly", Type.COUNTRY_CODE, "General Post and Telecommunication Company", LocalDate.parse("1997-04-23"), LocalDate.parse("2021-05-10"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registre.ma">http://www.registre.ma</a><br><b>WHOIS Server:</b> whois.registre.ma<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2006/ma-report-24jul2006.html">IANA Report on the Redelegation of the .MA Top-Level Domain (2006-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MA = new TLD("ma", Type.COUNTRY_CODE, "Agence Nationale de Réglementation des Télécommunications (ANRT)", LocalDate.parse("1993-11-26"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-macys">Delegation of the .macys domain to Macys, Inc. (2016-07-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230307-macys">Revocation of the .macys domain (2023-03-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MACYS = new TLD("macys", Type.GENERIC, null, LocalDate.parse("2016-06-24"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://dominios.madrid.org">http://dominios.madrid.org</a><br><b>WHOIS Server:</b> whois.nic.madrid<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141114-madrid">Delegation of the .madrid domain to Comunidad de Madrid (2014-11-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MADRID = new TLD("madrid", Type.GENERIC, "Comunidad de Madrid", LocalDate.parse("2014-10-02"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.maif.fr">http://www.maif.fr</a><br><b>WHOIS Server:</b> whois.nic.maif<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150227-maif">Delegation of the .maif domain to Mutuelle Assurance Instituteur France (MAIF) (2015-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MAIF = new TLD("maif", Type.GENERIC, "Mutuelle Assurance Instituteur France (MAIF)", LocalDate.parse("2015-02-13"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.maison<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-maison">Delegation of the .maison domain to Victor Frostbite, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MAISON = new TLD("maison", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.makeup/">https://nic.makeup/</a><br><b>WHOIS Server:</b> whois.nic.makeup<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160111-makeup">Delegation of the .makeup domain to L'Oréal (2016-01-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200915-makeup">Transfer of the .makeup domain to XYZ.COM LLC (2020-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MAKEUP = new TLD("makeup", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-12-23"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.man.eu">http://www.man.eu</a><br><b>WHOIS Server:</b> whois.nic.man<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150724-man">Delegation of the .man domain to MAN SE (2015-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MAN = new TLD("man", Type.GENERIC, "MAN SE", LocalDate.parse("2015-02-27"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.management<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-management">Delegation of the .MANAGEMENT domain to John Goodbye, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MANAGEMENT = new TLD("management", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mango.com">http://www.mango.com</a><br><b>WHOIS Server:</b> whois.nic.mango<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140212-mango">Delegation of the .mango domain to PUNTO FA S.L. (2014-02-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MANGO = new TLD("mango", Type.GENERIC, "PUNTO FA S.L.", LocalDate.parse("2014-01-23"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170629-map">Delegation of the .map domain to Charleston Road Registry Inc. (2017-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MAP = new TLD("map", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2017-06-08"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.market<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-market">Delegation of the .market domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-market">Transfer of the .market domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MARKET = new TLD("market", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.marketing<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-marketing">Delegation of the .MARKETING domain to Fern Pass, LLC (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MARKETING = new TLD("marketing", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.markets<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150309-markets">Delegation of the .markets domain to Dotmarkets Registry Limited (2015-03-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210629-markets">Transfer of the .markets domain to Dog Beach, LLC (2021-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MARKETS = new TLD("markets", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.marriott.com">http://www.marriott.com</a><br><b>WHOIS Server:</b> whois.nic.marriott<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150108-marriott">Delegation of the .marriott domain to Marriott Worldwide Corporation (2015-01-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MARRIOTT = new TLD("marriott", Type.GENERIC, "Marriott Worldwide Corporation", LocalDate.parse("2014-12-18"), LocalDate.parse("2024-05-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-marshalls">Delegation of the .marshalls domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MARSHALLS = new TLD("marshalls", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-maserati">Delegation of the .maserati domain to Fiat Chrysler Automobiles N.V. (2016-08-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230605-maserati">Revocation of the .maserati domain (2023-06-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MASERATI = new TLD("maserati", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2023-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mattel.com">http://www.mattel.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160526-mattel">Delegation of the .mattel domain to Mattel Sites, Inc. (2016-05-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MATTEL = new TLD("mattel", Type.GENERIC, "Mattel Sites, Inc.", LocalDate.parse("2016-01-22"), LocalDate.parse("2024-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.mba<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150521-mba">Delegation of the .mba domain to Lone Hollow, LLC (2015-05-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MBA = new TLD("mba", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.mc/">https://www.nic.mc/</a><br><br>
     *
     */
    public static final @NotNull TLD MC = new TLD("mc", Type.COUNTRY_CODE, "Direction des Plateformes et des Ressources Numériques", LocalDate.parse("1995-01-20"), LocalDate.parse("2023-06-30"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-mcd">Delegation of the .mcd domain to McDonald’s Corporation (2016-08-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170825-mcd">Revocation of the .mcd domain (2017-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MCD = new TLD("mcd", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2017-09-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-mcdonalds">Delegation of the .mcdonalds domain to McDonald’s Corporation (2016-08-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170825-mcdonalds">Revocation of the .mcdonalds domain (2017-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MCDONALDS = new TLD("mcdonalds", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2017-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mckinsey.com/">http://www.mckinsey.com/</a><br><b>WHOIS Server:</b> whois.nic.mckinsey<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160729-mckinsey">Delegation of the .mckinsey domain to McKinsey Holdings, Inc. (2016-07-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MCKINSEY = new TLD("mckinsey", Type.GENERIC, "McKinsey Holdings, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.md">http://www.nic.md</a><br><b>WHOIS Server:</b> whois.nic.md<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2003/md-report-22oct03.html">IANA Report on Redelegation of the .MD Top-Level Domain (2003-10-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MD = new TLD("md", Type.COUNTRY_CODE, "IP Serviciul Tehnologia Informatiei si Securitate Cibernetica", LocalDate.parse("1994-03-24"), LocalDate.parse("2023-11-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.domain.me">http://www.domain.me</a><br><b>WHOIS Server:</b> whois.nic.me<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/me-report-11sep2007.html">Report on Delegation of the .ME Top-Level Domain to the "Government of Montenegro" (2007-09-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ME = new TLD("me", Type.COUNTRY_CODE, "Government of Montenegro", LocalDate.parse("2007-09-24"), LocalDate.parse("2020-10-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.med<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151125-med">Delegation of the .med domain to Medistry LLC (2015-11-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MED = new TLD("med", Type.GENERIC, "Medistry LLC", LocalDate.parse("2015-11-12"), LocalDate.parse("2021-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.media<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-media">Delegation of the .media domain to Grand Glen, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MEDIA = new TLD("media", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140325-meet">Delegation of the .meet domain to Afilias Limited (2014-03-25)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160727-meet">Redelegation of the .meet domain to Charleston Road Registry Inc. (2016-07-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MEET = new TLD("meet", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-03-20"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.live.melbourne/">https://www.live.melbourne/</a><br><b>WHOIS Server:</b> whois.nic.melbourne<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140707-melbourne">Delegation of the .melbourne domain to The Crown in right of the State of Victoria, represented by its Department of State Development, Business and Innovation (2014-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MELBOURNE = new TLD("melbourne", Type.GENERIC, "The Crown in right of the State of Victoria, represented by its Department of State Development, Business and Innovation", LocalDate.parse("2014-07-03"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     */
    public static final @NotNull TLD MEME = new TLD("meme", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.memorial<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141124-memorial">Delegation of the .memorial domain to Dog Beach, LLC (2014-11-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MEMORIAL = new TLD("memorial", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.men">http://nic.men</a><br><b>WHOIS Server:</b> whois.nic.men<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150518-men">Delegation of the .men domain to Exclusive Registry Limited (2015-05-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MEN = new TLD("men", Type.GENERIC, "Exclusive Registry Limited", LocalDate.parse("2015-05-08"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.menu">http://www.nic.menu</a><br><b>WHOIS Server:</b> whois.nic.menu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131126-menu">Delegation of the .MENU domain to Wedding TLD2, LLC (2013-11-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MENU = new TLD("menu", Type.GENERIC, "Dot Menu Registry LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2024-07-05"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151028-meo">Delegation of the .meo domain to PT Comunicacoes S.A. (2015-10-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180521-meo">Revocation of the .meo domain (2018-05-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MEO = new TLD("meo", Type.GENERIC, null, LocalDate.parse("2015-01-15"), LocalDate.parse("2018-05-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.merckmsd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170623-merckmsd">Delegation of the .merckmsd domain to MSD Registry Holdings, Inc. (2017-06-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MERCKMSD = new TLD("merckmsd", Type.GENERIC, "MSD Registry Holdings, Inc.", LocalDate.parse("2017-06-15"), LocalDate.parse("2024-06-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160505-metlife">Delegation of the .metlife domain to MetLife Services and Solutions, LLC (2016-05-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200907-metlife">Revocation of the .metlife domain (2020-09-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD METLIFE = new TLD("metlife", Type.GENERIC, null, LocalDate.parse("2016-03-18"), LocalDate.parse("2020-09-07"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD MF = new TLD("mf", Type.COUNTRY_CODE, null, LocalDate.parse("2007-10-11"), LocalDate.parse("2008-05-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.mg">http://www.nic.mg</a><br><b>WHOIS Server:</b> whois.nic.mg<br>
     *
     */
    public static final @NotNull TLD MG = new TLD("mg", Type.COUNTRY_CODE, "NIC-MG (Network Information Center Madagascar)", LocalDate.parse("1995-07-25"), LocalDate.parse("2023-04-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.net.mh/">http://www.nic.net.mh/</a><br><br>
     *
     */
    public static final @NotNull TLD MH = new TLD("mh", Type.COUNTRY_CODE, "Office of the Cabinet", LocalDate.parse("1996-08-16"), LocalDate.parse("2013-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.miami">http://nic.miami</a><br><b>WHOIS Server:</b> whois.nic.miami<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-miami">Delegation of the .miami domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220121-miami">Transfer of the .miami domain to Registry Services, LLC (2022-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MIAMI = new TLD("miami", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.microsoft.com">http://www.microsoft.com</a><br><b>WHOIS Server:</b> whois.nic.microsoft<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150604-microsoft">Delegation of the .microsoft domain to Microsoft Corporation (2015-06-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MICROSOFT = new TLD("microsoft", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD MIL = new TLD("mil", Type.SPONSORED, "DoD Network Information Center", LocalDate.parse("1985-01-01"), LocalDate.parse("2023-09-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.mini.com">http://nic.mini.com</a><br><b>WHOIS Server:</b> whois.nic.mini<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140620-mini">Delegation of the .mini domain to Bayerische Motoren Werke Aktiengesellschaft (2014-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MINI = new TLD("mini", Type.GENERIC, "Bayerische Motoren Werke Aktiengesellschaft", LocalDate.parse("2014-06-05"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.intuit.com/">https://www.intuit.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-mint">Delegation of the .mint domain to Intuit Administrative Services, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MINT = new TLD("mint", Type.GENERIC, "Intuit Administrative Services, Inc.", LocalDate.parse("2016-06-09"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mit.edu">http://www.mit.edu</a><br><b>WHOIS Server:</b> whois.nic.mit<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160630-mit">Delegation of the .mit domain to Massachusetts Institute of Technology (2016-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MIT = new TLD("mit", Type.GENERIC, "Massachusetts Institute of Technology", LocalDate.parse("2016-06-24"), LocalDate.parse("2023-08-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160705-mitsubishi">Delegation of the .mitsubishi domain to Mitsubishi Corporation (2016-07-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MITSUBISHI = new TLD("mitsubishi", Type.GENERIC, "Mitsubishi Corporation", LocalDate.parse("2016-06-24"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://marnet.mk/">http://marnet.mk/</a><br><b>WHOIS Server:</b> whois.marnet.mk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2014/mk-report-20140926.html">Redelegation of the .MK domain and Delegation of the .мкд domain representing the Former Yugoslav Republic of Macedonia to Macedonian Academic Research Network Skopje (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MK = new TLD("mk", Type.COUNTRY_CODE, "Macedonian Academic Research Network Skopje", LocalDate.parse("1993-09-23"), LocalDate.parse("2024-02-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ml">http://www.nic.ml</a><br><b>WHOIS Server:</b> whois.nic.ml<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/ml-report-20130211.html">Report on the Redelegation of the .ML domain representing Mali to the Agence des Technologies de l’Information et de la Communication (2013-02-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ML = new TLD("ml", Type.COUNTRY_CODE, "Agence des Technologies de l’Information et de la Communication", LocalDate.parse("1993-09-29"), LocalDate.parse("2023-09-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mlb.com">http://www.mlb.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160520-mlb">Delegation of the .mlb domain to MLB Advanced Media DH, LLC (2016-05-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MLB = new TLD("mlb", Type.GENERIC, "MLB Advanced Media DH, LLC", LocalDate.parse("2016-05-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.mls<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160419-mls">Delegation of the .mls domain to The Canadian Real Estate Association (2016-04-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MLS = new TLD("mls", Type.GENERIC, "The Canadian Real Estate Association", LocalDate.parse("2015-08-20"), LocalDate.parse("2024-03-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ptd.gov.mm/">http://www.ptd.gov.mm/</a><br><b>WHOIS Server:</b> whois.registry.gov.mm<br>
     *
     */
    public static final @NotNull TLD MM = new TLD("mm", Type.COUNTRY_CODE, "Ministry of Transport and Communications", LocalDate.parse("1997-02-04"), LocalDate.parse("2023-03-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.mma.fr">https://www.mma.fr</a><br><b>WHOIS Server:</b> whois.nic.mma<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150312-mma">Delegation of the .mma domain to MMA IARD (2015-03-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MMA = new TLD("mma", Type.GENERIC, "MMA IARD", LocalDate.parse("2015-02-27"), LocalDate.parse("2024-07-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.mn">http://www.nic.mn</a><br><b>WHOIS Server:</b> whois.nic.mn<br>
     *
     */
    public static final @NotNull TLD MN = new TLD("mn", Type.COUNTRY_CODE, "Datacom Co., Ltd.", LocalDate.parse("1995-03-02"), LocalDate.parse("2022-06-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.monic.mo">https://www.monic.mo</a><br><b>WHOIS Server:</b> whois.monic.mo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/mo-report-20120904.html">Report on the Redelegation of the .MO domain representing Macao to the Bureau of Telecommunications Regulation (DSRT) (2012-09-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MO = new TLD("mo", Type.COUNTRY_CODE, "Macao Post and Telecommunications Bureau (CTT)", LocalDate.parse("1992-09-17"), LocalDate.parse("2022-06-08"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .MOBI through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.mobi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/mobi-report-oct2005.html">IANA Report on the Delegation of the .MOBI Top-Level Domain (2005-10-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOBI = new TLD("mobi", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2005-10-17"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dish.com/">https://www.dish.com/</a><br><b>WHOIS Server:</b> whois.nic.mobile<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161219-mobile">Delegation of the .mobile domain to Dish DBS Corporation (2016-12-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOBILE = new TLD("mobile", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-12-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151218-mobily">Delegation of the .mobily domain to GreenTech Consultancy Company W.L.L. (2015-12-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190909-mobily">Revocation of the .mobily domain (2019-09-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOBILY = new TLD("mobily", Type.GENERIC, null, LocalDate.parse("2015-12-03"), LocalDate.parse("2019-09-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.moda<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-moda">Delegation of the .MODA domain to United TLD Holdco Ltd. (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-moda">Transfer of the .moda domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MODA = new TLD("moda", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.moe/">https://get.moe/</a><br><b>WHOIS Server:</b> whois.nic.moe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-moe">Delegation of the .moe domain to Interlink Co., Ltd. (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220719-moe">Transfer of the .moe domain to Interlink Systems Innovation Institute K.K. (2022-07-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOE = new TLD("moe", Type.GENERIC, "Interlink Systems Innovation Institute K.K.", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.moi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151006-moi">Delegation of the .moi domain to Amazon Registry Services, Inc. (2015-10-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOI = new TLD("moi", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-09-10"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.mom">https://nic.mom</a><br><b>WHOIS Server:</b> whois.nic.mom<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150817-mom">Delegation of the .mom domain to Uniregistry, Corp. (2015-08-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220323-mom">Transfer of the .mom domain to XYZ.COM LLC (2022-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOM = new TLD("mom", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-08-13"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.monash">http://nic.monash</a><br><b>WHOIS Server:</b> whois.nic.monash<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-monash">Delegation of the .MONASH domain to Monash University (2014-01-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MONASH = new TLD("monash", Type.GENERIC, "Monash University", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-03-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.money<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141124-money">Delegation of the .money domain to Outer McCook, LLC (2014-11-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MONEY = new TLD("money", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.monster/">https://nic.monster/</a><br><b>WHOIS Server:</b> whois.nic.monster<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160909-monster">Delegation of the .monster domain to Monster Worldwide, Inc. (2016-09-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190201-monster">Transfer of the .monster domain to XYZ.COM LLC (2019-02-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MONSTER = new TLD("monster", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-12-23"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150602-montblanc">Delegation of the .montblanc domain to Richemont DNS Inc. (2015-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170830-montblanc">Revocation of the .montblanc domain (2017-08-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MONTBLANC = new TLD("montblanc", Type.GENERIC, null, LocalDate.parse("2014-11-20"), LocalDate.parse("2017-09-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-mopar">Delegation of the .mopar domain to FCA US Llc (2016-07-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191120-mopar">Revocation of the .mopar domain (2019-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOPAR = new TLD("mopar", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2019-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mormon.org">http://www.mormon.org</a><br><b>WHOIS Server:</b> whois.nic.mormon<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141117-mormon">Delegation of the .mormon domain to IRI Domain Management, LLC (2014-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MORMON = new TLD("mormon", Type.GENERIC, "IRI Domain Management, LLC (\"Applicant\")", LocalDate.parse("2014-10-16"), LocalDate.parse("2023-08-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.mortgage<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-mortgage">Delegation of the .mortgage domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-mortgage">Transfer of the .mortgage domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MORTGAGE = new TLD("mortgage", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.faitid.org">http://www.faitid.org</a><br><b>WHOIS Server:</b> whois.nic.moscow<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140418-moscow">Delegation of the .moscow domain to Foundation for Assistance for Internet Technologies and Infrastructure Development (2014-04-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOSCOW = new TLD("moscow", Type.GENERIC, "Foundation for Assistance for Internet Technologies and Infrastructure Development (FAITID)", LocalDate.parse("2014-04-17"), LocalDate.parse("2019-08-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.motorola.com">http://www.motorola.com</a><br><b>WHOIS Server:</b> whois.nic.moto<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-moto">Delegation of the .moto domain to Motorola Trademark Holdings, LLC (2016-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOTO = new TLD("moto", Type.GENERIC, "Motorola Trademark Holdings, LLC", LocalDate.parse("2016-09-30"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.motorcycles">http://nic.motorcycles</a><br><b>WHOIS Server:</b> whois.nic.motorcycles<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140514-motorcycles">Delegation of the .motorcycles domain to DERMotorcycles, LLC (2014-05-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210405-motorcycles">Transfer of the .motorcycles domain to XYZ.COM LLC (2021-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOTORCYCLES = new TLD("motorcycles", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-05-01"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     */
    public static final @NotNull TLD MOV = new TLD("mov", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.movie<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20150318-movie">Delegation of the .movie domain to New Frostbite, LLC (2015-03-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOVIE = new TLD("movie", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150624-movistar">Delegation of the .movistar domain to Telefónica S.A. (2015-06-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191224-movistar">Revocation of the .movistar domain (2019-12-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MOVISTAR = new TLD("movistar", Type.GENERIC, null, LocalDate.parse("2015-06-18"), LocalDate.parse("2019-12-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://get.mp/">https://get.mp/</a><br><br>
     *
     */
    public static final @NotNull TLD MP = new TLD("mp", Type.COUNTRY_CODE, "Saipan Datacom, Inc.", LocalDate.parse("1996-10-22"), LocalDate.parse("2022-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dom-enic.com">https://www.dom-enic.com</a><br><b>WHOIS Server:</b> whois.mediaserv.net<br>
     *
     */
    public static final @NotNull TLD MQ = new TLD("mq", Type.COUNTRY_CODE, "CANAL+ TELECOM", LocalDate.parse("1997-03-28"), LocalDate.parse("2021-10-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.mr">http://www.nic.mr</a><br><b>WHOIS Server:</b> whois.nic.mr<br>
     *
     */
    public static final @NotNull TLD MR = new TLD("mr", Type.COUNTRY_CODE, "Université de Nouakchott Al Aasriya", LocalDate.parse("1996-04-24"), LocalDate.parse("2021-06-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ms">http://www.nic.ms</a><br><b>WHOIS Server:</b> whois.nic.ms<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2008/ms-report-28aug2008.html">Report on the Redelegation of the .MS Top-Level Domain to "MNI Networks Ltd" (2008-08-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MS = new TLD("ms", Type.COUNTRY_CODE, "MNI Networks Ltd.", LocalDate.parse("1997-03-06"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.msd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160720-msd">Delegation of the .msd domain to MSD Registry Holdings, Inc. (2016-07-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MSD = new TLD("msd", Type.GENERIC, "MSD Registry Holdings, Inc.", LocalDate.parse("2016-06-24"), LocalDate.parse("2024-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.org.mt/">https://www.nic.org.mt/</a><br><br>
     *
     */
    public static final @NotNull TLD MT = new TLD("mt", Type.COUNTRY_CODE, "NIC (Malta)", LocalDate.parse("1992-12-02"), LocalDate.parse("2020-01-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mtn.com">http://www.mtn.com</a><br><b>WHOIS Server:</b> whois.nic.mtn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-mtn">Delegation of the .mtn domain to MTN Dubai Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MTN = new TLD("mtn", Type.GENERIC, "MTN Dubai Limited", LocalDate.parse("2015-03-12"), LocalDate.parse("2021-10-19"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150302-mtpc">Delegation of the .mtpc domain to Mitsubishi Tanabe Pharma Corporation (2015-03-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170512-mtpc">Revocation of the .mtpc domain (2017-05-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MTPC = new TLD("mtpc", Type.GENERIC, null, LocalDate.parse("2015-02-27"), LocalDate.parse("2017-05-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.mtr">http://nic.mtr</a><br><b>WHOIS Server:</b> whois.nic.mtr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151005-mtr">Delegation of the .mtr domain to MTR Corporation Limited (2015-10-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MTR = new TLD("mtr", Type.GENERIC, "MTR Corporation Limited", LocalDate.parse("2015-07-31"), LocalDate.parse("2024-04-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.mu/">http://www.nic.mu/</a><br><b>WHOIS Server:</b> whois.nic.mu<br>
     *
     */
    public static final @NotNull TLD MU = new TLD("mu", Type.COUNTRY_CODE, "Internet Direct Ltd", LocalDate.parse("1995-10-06"), LocalDate.parse("2017-09-14"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .MUSEUM through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="https://about.museum">https://about.museum</a><br><b>WHOIS Server:</b> whois.nic.museum<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/museum-report-30oct01.html">IANA Report on Establishment of the .MUSEUM Top-Level Domain (2001-10-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MUSEUM = new TLD("museum", Type.SPONSORED, "Museum Domain Management Association", LocalDate.parse("2001-10-20"), LocalDate.parse("2024-05-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.music/">https://nic.music/</a><br><b>WHOIS Server:</b> whois.nic.music<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211029-music">Delegation of the .music domain to DotMusic Limited (2021-10-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MUSIC = new TLD("music", Type.GENERIC, "DotMusic Limited", LocalDate.parse("2021-10-14"), LocalDate.parse("2024-07-26"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160329-mutual">Delegation of the .mutual domain to Northwestern Mutual MU TLD Registry, LLC (2016-03-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230801-mutual">Revocation of the .mutual domain (2023-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MUTUAL = new TLD("mutual", Type.GENERIC, null, LocalDate.parse("2015-07-02"), LocalDate.parse("2023-08-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151019-mutuelle">Delegation of the .mutuelle domain to Fédération Nationale de la Mutualité Française (2015-10-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161215-mutuelle">Revocation of the .mutuelle domain (2016-12-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MUTUELLE = new TLD("mutuelle", Type.GENERIC, null, LocalDate.parse("2015-09-24"), LocalDate.parse("2016-12-21"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD MV = new TLD("mv", Type.COUNTRY_CODE, "Dhiraagu Pvt. Ltd. (DHIVEHINET)", LocalDate.parse("1996-09-25"), LocalDate.parse("2021-01-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registrar.mw">http://www.registrar.mw</a><br><b>WHOIS Server:</b> whois.nic.mw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/mw-report-12aug02.html">IANA Report on Redelegation of the .MW Top-Level Domain (2002-08-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD MW = new TLD("mw", Type.COUNTRY_CODE, "Malawi Sustainable Development Network Programme (Malawi SDNP)", LocalDate.parse("1997-01-03"), LocalDate.parse("2022-11-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.mx/">http://www.registry.mx/</a><br><b>WHOIS Server:</b> whois.mx<br>
     *
     */
    public static final @NotNull TLD MX = new TLD("mx", Type.COUNTRY_CODE, "NIC-Mexico ITESM - Campus Monterrey", LocalDate.parse("1989-02-01"), LocalDate.parse("2024-02-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mynic.my">http://www.mynic.my</a><br><b>WHOIS Server:</b> whois.mynic.my<br>
     *
     */
    public static final @NotNull TLD MY = new TLD("my", Type.COUNTRY_CODE, "MYNIC Berhad", LocalDate.parse("1987-06-08"), LocalDate.parse("2024-04-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.mz<br>
     *
     */
    public static final @NotNull TLD MZ = new TLD("mz", Type.COUNTRY_CODE, "Centro de Informatica de Universidade Eduardo Mondlane", LocalDate.parse("1992-09-04"), LocalDate.parse("2020-03-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.na-nic.com.na/">http://www.na-nic.com.na/</a><br><b>WHOIS Server:</b> whois.na-nic.com.na<br>
     *
     */
    public static final @NotNull TLD NA = new TLD("na", Type.COUNTRY_CODE, "Namibian Network Information Center", LocalDate.parse("1991-05-08"), LocalDate.parse("2022-10-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nab.com.au">http://www.nab.com.au</a><br><b>WHOIS Server:</b> whois.nic.nab<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160815-nab">Delegation of the .nab domain to National Australia Bank Limited (2016-08-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NAB = new TLD("nab", Type.GENERIC, "National Australia Bank Limited", LocalDate.parse("2016-08-04"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150429-nadex">Delegation of the .nadex domain to Nadex Domains, Inc. (2015-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200328-nadex">Revocation of the .nadex domain (2020-03-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NADEX = new TLD("nadex", Type.GENERIC, null, LocalDate.parse("2015-04-02"), LocalDate.parse("2020-03-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.nagoya<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140124-nagoya">Delegation of the .NAGOYA domain to GMO Registry, Inc. (2014-01-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NAGOYA = new TLD("nagoya", Type.GENERIC, "GMO Registry, Inc.", LocalDate.parse("2014-01-16"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .NAME through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.nic.name">http://www.nic.name</a><br><b>WHOIS Server:</b> whois.nic.name<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2001/name-report-16aug01.html">IANA Report on Establishment of the .NAME Top-Level Domain (2001-08-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NAME = new TLD("name", Type.GENERIC_RESTRICTED, "VeriSign Information Services, Inc.", LocalDate.parse("2001-08-17"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160708-nationwide">Delegation of the .nationwide domain to Nationwide Mutual Insurance Company (2016-07-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210416-nationwide">Revocation of the .nationwide domain (2021-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NATIONWIDE = new TLD("nationwide", Type.GENERIC, null, LocalDate.parse("2016-04-22"), LocalDate.parse("2021-04-16"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160208-natura">Delegation of the .natura domain to Natura Cosméticos S.A. (2016-02-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240612-natura">Revocation of the .natura domain (2024-06-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NATURA = new TLD("natura", Type.GENERIC, null, LocalDate.parse("2016-01-29"), LocalDate.parse("2024-06-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.navy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-navy">Delegation of the .navy domain to United TLD Holdco, Ltd (2014-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-navy">Transfer of the .navy domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NAVY = new TLD("navy", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nba.com">http://www.nba.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-nba">Delegation of the .nba domain to NBA Registry, LLC (2016-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NBA = new TLD("nba", Type.GENERIC, "NBA REGISTRY, LLC", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.domaine.nc">http://www.domaine.nc</a><br><b>WHOIS Server:</b> whois.nc<br>
     *
     */
    public static final @NotNull TLD NC = new TLD("nc", Type.COUNTRY_CODE, "Office des Postes et Telecommunications", LocalDate.parse("1993-10-13"), LocalDate.parse("2020-03-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.intnet.ne">http://www.intnet.ne</a><br><br>
     *
     */
    public static final @NotNull TLD NE = new TLD("ne", Type.COUNTRY_CODE, "SONITEL", LocalDate.parse("1996-04-24"), LocalDate.parse("2023-11-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nec.com/">http://www.nec.com/</a><br><b>WHOIS Server:</b> whois.nic.nec<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150506-nec">Delegation of the .nec domain to NEC Corporation (2015-05-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEC = new TLD("nec", Type.GENERIC, "NEC Corporation", LocalDate.parse("2015-04-02"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .NET through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.verisign-grs.com<br>
     *
     */
    public static final @NotNull TLD NET = new TLD("net", Type.GENERIC, "VeriSign Global Registry Services", LocalDate.parse("1985-01-01"), LocalDate.parse("2023-11-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.commbank.com.au">http://www.commbank.com.au</a><br><b>WHOIS Server:</b> whois.nic.netbank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-netbank">Delegation of the .netbank domain to Commonwealth Bank of Australia (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NETBANK = new TLD("netbank", Type.GENERIC, "COMMONWEALTH BANK OF AUSTRALIA", LocalDate.parse("2015-03-26"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.netflix.com">http://www.netflix.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160526-netflix">Delegation of the .netflix domain to Netflix, Inc. (2016-05-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NETFLIX = new TLD("netflix", Type.GENERIC, "Netflix, Inc.", LocalDate.parse("2016-05-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.network<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140821-network">Delegation of the .network domain to Trixy Manor, LLC (2014-08-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NETWORK = new TLD("network", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-08-18"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.neustar.biz">http://www.neustar.biz</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140213-neustar">Delegation of the .neustar domain to NeuStar, Inc. (2014-02-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEUSTAR = new TLD("neustar", Type.GENERIC, "NeuStar, Inc.", LocalDate.parse("2014-02-06"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     */
    public static final @NotNull TLD NEW = new TLD("new", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-06-12"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161028-newholland">Delegation of the .newholland domain to CNH Industrial N.V. (2016-10-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210219-newholland">Revocation of the .newholland domain (2021-02-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEWHOLLAND = new TLD("newholland", Type.GENERIC, null, LocalDate.parse("2016-10-27"), LocalDate.parse("2021-02-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.news<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150316-news">Delegation of the .news domain to United TLD Holdco, Ltd (2015-03-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-news">Transfer of the .news domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEWS = new TLD("news", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.next<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-next">Delegation of the .next domain to Next plc (2016-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEXT = new TLD("next", Type.GENERIC, "Next plc", LocalDate.parse("2016-04-21"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.nextdirect<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-nextdirect">Delegation of the .nextdirect domain to Next plc (2016-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEXTDIRECT = new TLD("nextdirect", Type.GENERIC, "Next plc", LocalDate.parse("2016-04-21"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140909-nexus">Delegation of the .nexus domain to Charleston Road Registry Inc. (2014-09-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NEXUS = new TLD("nexus", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.nf">http://nic.nf</a><br><b>WHOIS Server:</b> whois.nic.nf<br>
     *
     */
    public static final @NotNull TLD NF = new TLD("nf", Type.COUNTRY_CODE, "Norfolk Island Data Services", LocalDate.parse("1996-03-18"), LocalDate.parse("2024-02-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nfl.com/">http://www.nfl.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160620-nfl">Delegation of the .nfl domain to NFL Reg Ops LLC (2016-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NFL = new TLD("nfl", Type.GENERIC, "NFL Reg Ops LLC", LocalDate.parse("2016-06-03"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nira.org.ng/">http://www.nira.org.ng/</a><br><b>WHOIS Server:</b> whois.nic.net.ng<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2004/ng-report-10jun04.html">IANA Report on Redelegation of the .NG Top-Level Domain (2004-06-10)</a></li>
     *   <li><a href="https://iana.org/reports/2009/ng-report-07apr2009.html">Report on the Redelegation of the .NG Top-Level Domain to the "Nigeria Internet Registration Association" (2009-04-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NG = new TLD("ng", Type.COUNTRY_CODE, "Nigeria Internet Registration Association", LocalDate.parse("1995-03-15"), LocalDate.parse("2023-03-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.ngo">http://nic.ngo</a><br><b>WHOIS Server:</b> whois.nic.ngo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140716-ngo">Delegation of the .ngo domain to Public Interest Registry (2014-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NGO = new TLD("ngo", Type.GENERIC, "Public Interest Registry", LocalDate.parse("2014-07-10"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.nhk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-nhk">Delegation of the .nhk domain to Japan Broadcasting Corporation (NHK) (2014-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NHK = new TLD("nhk", Type.GENERIC, "Japan Broadcasting Corporation (NHK)", LocalDate.parse("2014-04-24"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ni">http://www.nic.ni</a><br><br>
     *
     */
    public static final @NotNull TLD NI = new TLD("ni", Type.COUNTRY_CODE, "Universidad Nacional del Ingernieria Centro de Computo", LocalDate.parse("1989-10-13"), LocalDate.parse("2023-05-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.nico">http://nic.nico</a><br><b>WHOIS Server:</b> whois.nic.nico<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150209-nico">Delegation of the .nico domain to Dwango Co., Ltd. (2015-02-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NICO = new TLD("nico", Type.GENERIC, "DWANGO Co., Ltd.", LocalDate.parse("2015-02-05"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nike.com">http://www.nike.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160706-nike">Delegation of the .nike domain to Nike, Inc. (2016-07-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NIKE = new TLD("nike", Type.GENERIC, "NIKE, Inc.", LocalDate.parse("2015-10-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nikon.com">http://www.nikon.com</a><br><b>WHOIS Server:</b> whois.nic.nikon<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160126-nikon">Delegation of the .nikon domain to Nikon Corporation (2016-01-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NIKON = new TLD("nikon", Type.GENERIC, "NIKON CORPORATION", LocalDate.parse("2015-10-01"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.ninja<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131224-ninja">Delegation of the .NINJA domain to United TLD Holdco Ltd (2013-12-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-ninja">Transfer of the .ninja domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NINJA = new TLD("ninja", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150302-nissan">Delegation of the .nissan domain to Nissan Motor Co., Ltd. (2015-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NISSAN = new TLD("nissan", Type.GENERIC, "NISSAN MOTOR CO., LTD.", LocalDate.parse("2014-07-10"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nissay.co.jp/english/">http://www.nissay.co.jp/english/</a><br><b>WHOIS Server:</b> whois.nic.nissay<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160325-nissay">Delegation of the .nissay domain to Nippon Life Insurance Company (2016-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NISSAY = new TLD("nissay", Type.GENERIC, "Nippon Life Insurance Company", LocalDate.parse("2016-01-08"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.sidn.nl/">https://www.sidn.nl/</a><br><b>WHOIS Server:</b> whois.domain-registry.nl<br>
     *
     */
    public static final @NotNull TLD NL = new TLD("nl", Type.COUNTRY_CODE, "SIDN (Stichting Internet Domeinregistratie Nederland)", LocalDate.parse("1986-04-25"), LocalDate.parse("2024-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.norid.no">http://www.norid.no</a><br><b>WHOIS Server:</b> whois.norid.no<br>
     *
     */
    public static final @NotNull TLD NO = new TLD("no", Type.COUNTRY_CODE, "Norid A/S", LocalDate.parse("1987-03-17"), LocalDate.parse("2022-01-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.nokia/">http://nic.nokia/</a><br><b>WHOIS Server:</b> whois.nic.nokia<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150713-nokia">Delegation of the .nokia domain to Nokia Corporation (2015-07-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NOKIA = new TLD("nokia", Type.GENERIC, "Nokia Corporation", LocalDate.parse("2015-04-23"), LocalDate.parse("2024-06-26"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160401-northwesternmutual">Delegation of the .northwesternmutual domain to Northwestern Mutual Registry, LLC (2016-04-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230808-northwesternmutual">Revocation of the .northwesternmutual domain (2023-08-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NORTHWESTERNMUTUAL = new TLD("northwesternmutual", Type.GENERIC, null, LocalDate.parse("2015-12-04"), LocalDate.parse("2023-08-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.symantec.com">http://www.symantec.com</a><br><b>WHOIS Server:</b> whois.nic.norton<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-norton">Delegation of the .norton domain to Symantec Corporation (2015-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NORTON = new TLD("norton", Type.GENERIC, "Symantec Corporation", LocalDate.parse("2015-09-24"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.now<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-now">Delegation of the .now domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NOW = new TLD("now", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://icann.org/ebero">https://icann.org/ebero</a><br><b>WHOIS Server:</b> whois.nic.nowruz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-nowruz">Delegation of the .nowruz domain to Asia Green IT System Bilgisayar San. ve Tic. Ltd. Sti. (2015-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NOWRUZ = new TLD("nowruz", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2015-11-25"), LocalDate.parse("2024-07-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.asia/namesphere/">http://www.dot.asia/namesphere/</a><br><b>WHOIS Server:</b> whois.nic.nowtv<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-nowtv">Delegation of the .nowtv domain to Starbucks (HK) Limited (2016-04-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NOWTV = new TLD("nowtv", Type.GENERIC, "Starbucks (HK) Limited", LocalDate.parse("2016-04-15"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mos.com.np">http://www.mos.com.np</a><br><br>
     *
     */
    public static final @NotNull TLD NP = new TLD("np", Type.COUNTRY_CODE, "Mercantile Communications Pvt. Ltd.", LocalDate.parse("1995-01-25"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cenpac.net.nr">http://www.cenpac.net.nr</a><br><br>
     *
     */
    public static final @NotNull TLD NR = new TLD("nr", Type.COUNTRY_CODE, "CENPAC NET", LocalDate.parse("1998-03-30"), LocalDate.parse("2020-03-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://contact.nra.org/contact-us.aspx">http://contact.nra.org/contact-us.aspx</a><br><b>WHOIS Server:</b> whois.nic.nra<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140716-nra">Delegation of the .nra domain to NRA Holdings Company, INC. (2014-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NRA = new TLD("nra", Type.GENERIC, "NRA Holdings Company, INC.", LocalDate.parse("2014-07-10"), LocalDate.parse("2023-08-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://mindsandmachines.com">http://mindsandmachines.com</a><br><b>WHOIS Server:</b> whois.nic.nrw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140709-nrw">Delegation of the .nrw domain to Minds + Machines GmbH (2014-07-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210826-nrw">Transfer of the .nrw domain to Minds + Machines GmbH (2021-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NRW = new TLD("nrw", Type.GENERIC, "Minds + Machines GmbH", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-06-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://group.ntt/en/dotntt/">https://group.ntt/en/dotntt/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150127-ntt">Delegation of the .ntt domain to Nippon Telegraph and Telephone Corporation (2015-01-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NTT = new TLD("ntt", Type.GENERIC, "NIPPON TELEGRAPH AND TELEPHONE CORPORATION", LocalDate.parse("2015-01-08"), LocalDate.parse("2023-12-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.internetstiftelsen.se">https://www.internetstiftelsen.se</a><br><b>WHOIS Server:</b> whois.iis.nu<br>
     *
     */
    public static final @NotNull TLD NU = new TLD("nu", Type.COUNTRY_CODE, "The IUSN Foundation", LocalDate.parse("1997-06-20"), LocalDate.parse("2022-09-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mydotnyc.com">http://www.mydotnyc.com</a><br><b>WHOIS Server:</b> whois.nic.nyc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140314-nyc">Delegation of the .nyc domain to The City of New York by and through the New York City Department of Information Technology and Telecommunications (2014-03-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD NYC = new TLD("nyc", Type.GENERIC, "The City of New York by and through the New York City Department of Information Technology and Telecommunications", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dnc.org.nz/">http://www.dnc.org.nz/</a><br><b>WHOIS Server:</b> whois.irs.net.nz<br>
     *
     */
    public static final @NotNull TLD NZ = new TLD("nz", Type.COUNTRY_CODE, "InternetNZ", LocalDate.parse("1987-01-19"), LocalDate.parse("2024-05-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.obi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150921-obi">Delegation of the .obi domain to OBI Group Holding SE and Co. KGaA (2015-09-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OBI = new TLD("obi", Type.GENERIC, "OBI Group Holding SE and Co. KGaA", LocalDate.parse("2015-09-03"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.observer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160923-observer">Delegation of the .observer domain to Top Level Spectrum, Inc. (2016-09-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210211-observer">Transfer of the .observer domain to Dog Beach, LLC (2021-02-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240223-observer">Transfer of the .observer domain to Fegistry, LLC (2024-02-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OBSERVER = new TLD("observer", Type.GENERIC, "Fegistry, LLC", LocalDate.parse("2016-09-15"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-off">Delegation of the .off domain to Johnson Shareholdings, Inc. (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211203-off">Revocation of the .off domain (2021-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OFF = new TLD("off", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.office.com">http://www.office.com</a><br><b>WHOIS Server:</b> whois.nic.office<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150622-office">Delegation of the .office domain to Microsoft Corporation (2015-06-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OFFICE = new TLD("office", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-06-04"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.okinawa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140227-okinawa">Delegation of the .okinawa domain to BusinessRalliart inc. (2014-02-27)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160613-okinawa">Redelegation of the .okinawa domain to BRregistry, Inc. (2016-06-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OKINAWA = new TLD("okinawa", Type.GENERIC, "BRregistry, Inc.", LocalDate.parse("2014-02-20"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.olayan.com/">http://www.olayan.com/</a><br><b>WHOIS Server:</b> whois.nic.olayan<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-olayan">Delegation of the .olayan domain to Crescent Holding GmbH (2016-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240508-olayan">Transfer of the .olayan domain to Competrol (Luxembourg) Sarl (2024-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OLAYAN = new TLD("olayan", Type.GENERIC, "Competrol (Luxembourg) Sarl", LocalDate.parse("2016-04-21"), LocalDate.parse("2024-05-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.olayan.com/">http://www.olayan.com/</a><br><b>WHOIS Server:</b> whois.nic.olayangroup<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-olayangroup">Delegation of the .olayangroup domain to Crescent Holding GmbH (2016-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240508-olayangroup">Transfer of the .olayangroup domain to Competrol (Luxembourg) Sarl (2024-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OLAYANGROUP = new TLD("olayangroup", Type.GENERIC, "Competrol (Luxembourg) Sarl", LocalDate.parse("2016-04-21"), LocalDate.parse("2024-05-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-oldnavy">Delegation of the .oldnavy domain to The Gap, Inc. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240122-oldnavy">Revocation of the .oldnavy domain (2024-01-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OLDNAVY = new TLD("oldnavy", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2024-01-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com">http://www.dish.com</a><br><b>WHOIS Server:</b> whois.nic.ollo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160525-ollo">Delegation of the .ollo domain to Dish DBS Corporation (2016-05-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OLLO = new TLD("ollo", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-05-19"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.om/om/en/?page_id=197">http://www.registry.om/om/en/?page_id=197</a><br><b>WHOIS Server:</b> whois.registry.om<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/om-report-20110718.html">Report on the Redelegation of the .OM domain representing Oman to the Telecommunications Regulatory Authority (2011-07-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OM = new TLD("om", Type.COUNTRY_CODE, "Telecommunications Regulatory Authority (TRA)", LocalDate.parse("1996-04-11"), LocalDate.parse("2017-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.swatchgroup.com/">http://www.swatchgroup.com/</a><br><b>WHOIS Server:</b> whois.nic.omega<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150624-omega">Delegation of the .omega domain to The Swatch Group Ltd (2015-06-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OMEGA = new TLD("omega", Type.GENERIC, "The Swatch Group Ltd", LocalDate.parse("2015-04-23"), LocalDate.parse("2022-08-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.one.com/">http://www.one.com/</a><br><b>WHOIS Server:</b> whois.nic.one<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150116-one">Delegation of the .one domain to One.com A/S (2015-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ONE = new TLD("one", Type.GENERIC, "One.com A/S", LocalDate.parse("2014-12-18"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.ong">http://nic.ong</a><br><b>WHOIS Server:</b> whois.nic.ong<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140723-ong">Delegation of the .ong domain to Public Interest Registry (2014-07-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ONG = new TLD("ong", Type.GENERIC, "Public Interest Registry", LocalDate.parse("2014-07-10"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.i-registry.com">http://www.i-registry.com</a><br><b>WHOIS Server:</b> whois.nic.onl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-onl">Delegation of the .ONL domain to I-REGISTRY Ltd. (2013-12-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ONL = new TLD("onl", Type.GENERIC, "iRegistry GmbH", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.online<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150312-online">Delegation of the .online domain to DotOnline Inc. (2015-03-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210821-online">Transfer of the .online domain to Radix FZC (2021-08-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-online">Transfer of the .online domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ONLINE = new TLD("online", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2015-03-06"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160713-onyourside">Delegation of the .onyourside domain to Nationwide Mutual Insurance Company (2016-07-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210416-onyourside">Revocation of the .onyourside domain (2021-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ONYOURSIDE = new TLD("onyourside", Type.GENERIC, null, LocalDate.parse("2016-04-22"), LocalDate.parse("2021-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.infibeam.com">http://www.infibeam.com</a><br><b>WHOIS Server:</b> whois.nic.ooo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140812-ooo">Delegation of the .ooo domain to INFIBEAM INCORPORATION LIMITED (2014-08-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OOO = new TLD("ooo", Type.GENERIC, "INFIBEAM AVENUES LIMITED", LocalDate.parse("2014-07-31"), LocalDate.parse("2024-04-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.americanexpress.com">https://www.americanexpress.com</a><br><b>WHOIS Server:</b> whois.nic.open<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-open">Delegation of the .open domain to American Express Travel Related Services Company, Inc. (2016-08-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OPEN = new TLD("open", Type.GENERIC, "American Express Travel Related Services Company, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.oracle<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150227-oracle">Delegation of the .oracle domain to Oracle Corporation (2015-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ORACLE = new TLD("oracle", Type.GENERIC, "Oracle Corporation", LocalDate.parse("2015-01-15"), LocalDate.parse("2024-02-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.orange<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150707-orange">Delegation of the .orange domain to Orange Brand Services Limited (2015-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ORANGE = new TLD("orange", Type.GENERIC, "Orange Brand Services Limited", LocalDate.parse("2015-06-18"), LocalDate.parse("2024-03-31"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .ORG through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://publicinterestregistry.org">http://publicinterestregistry.org</a><br><b>WHOIS Server:</b> whois.publicinterestregistry.org<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/org-report-09dec02.html">IANA Report on the Redelegation of the .ORG Top-Level Domain (2002-12-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ORG = new TLD("org", Type.GENERIC, "Public Interest Registry (PIR)", LocalDate.parse("1985-01-01"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.organic<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140609-organic">Delegation of the .organic domain to Afilias Limited (2014-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ORGANIC = new TLD("organic", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160620-orientexpress">Delegation of the .orientexpress domain to Orient Express (2016-06-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170411-orientexpress">Revocation of the .orientexpress domain (2017-04-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ORIENTEXPRESS = new TLD("orientexpress", Type.GENERIC, null, LocalDate.parse("2016-06-14"), LocalDate.parse("2017-04-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://elcompanies.com/Pages/Homepage.aspx">http://elcompanies.com/Pages/Homepage.aspx</a><br><b>WHOIS Server:</b> whois.nic.origins<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-origins">Delegation of the .origins domain to The Estée Lauder Companies Inc. (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ORIGINS = new TLD("origins", Type.GENERIC, "The Estée Lauder Companies Inc.", LocalDate.parse("2015-12-10"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://domain.osaka/">http://domain.osaka/</a><br><b>WHOIS Server:</b> whois.nic.osaka<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141211-osaka">Delegation of the .osaka domain to Interlink Co., Ltd. (2014-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OSAKA = new TLD("osaka", Type.GENERIC, "Osaka Registry Co., Ltd.", LocalDate.parse("2014-12-04"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.otsuka<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140822-otsuka">Delegation of the .otsuka domain to Otsuka Holdings Co., Ltd. (2014-08-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OTSUKA = new TLD("otsuka", Type.GENERIC, "Otsuka Holdings Co., Ltd.", LocalDate.parse("2014-06-26"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dish.com">http://www.dish.com</a><br><b>WHOIS Server:</b> whois.nic.ott<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160525-ott">Delegation of the .ott domain to Dish DBS Corporation (2016-05-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OTT = new TLD("ott", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-05-19"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.ovh.com">https://www.ovh.com</a><br><b>WHOIS Server:</b> whois.nic.ovh<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140616-ovh">Delegation of the .ovh domain to OVH SAS (2014-06-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD OVH = new TLD("ovh", Type.GENERIC, "OVH SAS", LocalDate.parse("2014-05-01"), LocalDate.parse("2024-04-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.pa/">http://www.nic.pa/</a><br><br>
     *
     */
    public static final @NotNull TLD PA = new TLD("pa", Type.COUNTRY_CODE, "Universidad Tecnologica de Panama", LocalDate.parse("1994-05-25"), LocalDate.parse("2024-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150312-page">Delegation of the .page domain to Charleston Road Registry Inc. (2015-03-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PAGE = new TLD("page", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-02-05"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160119-pamperedchef">Delegation of the .pamperedchef domain to The Pampered Chef, Ltd. (2016-01-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170817-pamperedchef">Revocation of the .pamperedchef domain (2017-08-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PAMPEREDCHEF = new TLD("pamperedchef", Type.GENERIC, null, LocalDate.parse("2015-05-14"), LocalDate.parse("2017-09-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160708-panasonic">Delegation of the .panasonic domain to Panasonic Corporation (2016-07-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PANASONIC = new TLD("panasonic", Type.GENERIC, "Panasonic Corporation", LocalDate.parse("2016-06-24"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-panerai">Delegation of the .panerai domain to Richemont DNS Inc. (2015-03-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180917-panerai">Revocation of the .panerai domain (2018-09-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PANERAI = new TLD("panerai", Type.GENERIC, null, LocalDate.parse("2015-02-19"), LocalDate.parse("2018-09-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://mondomaine.paris.fr/">http://mondomaine.paris.fr/</a><br><b>WHOIS Server:</b> whois.nic.paris<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140416-paris">Delegation of the .paris domain to City of Paris (2014-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PARIS = new TLD("paris", Type.GENERIC, "City of Paris", LocalDate.parse("2014-03-27"), LocalDate.parse("2024-03-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://icann.org/ebero">https://icann.org/ebero</a><br><b>WHOIS Server:</b> whois.nic.pars<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151204-pars">Delegation of the .pars domain to Asia Green IT System Bilgisayar San. ve Tic. Ltd. Sti. (2015-12-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PARS = new TLD("pars", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2015-11-25"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.partners<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-partners">Delegation of the .partners domain to Magic Glen, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PARTNERS = new TLD("partners", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.parts<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-parts">Delegation of the .parts domain to Sea Goodbye, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PARTS = new TLD("parts", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.party">http://nic.party</a><br><b>WHOIS Server:</b> whois.nic.party<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141117-party">Delegation of the .party domain to Blue Sky Registry Limited (2014-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PARTY = new TLD("party", Type.GENERIC, "Blue Sky Registry Limited", LocalDate.parse("2014-10-23"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160229-passagens">Delegation of the .passagens domain to Travel Reservations SRL (2016-02-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230707-passagens">Revocation of the .passagens domain (2023-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PASSAGENS = new TLD("passagens", Type.GENERIC, null, LocalDate.parse("2016-02-19"), LocalDate.parse("2023-07-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.pay<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160805-pay">Delegation of the .pay domain to Amazon Registry Services, Inc. (2016-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PAY = new TLD("pay", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.asia/namesphere/">http://www.dot.asia/namesphere/</a><br><b>WHOIS Server:</b> whois.nic.pccw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160510-pccw">Delegation of the .pccw domain to PCCW Enterprises Limited (2016-05-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PCCW = new TLD("pccw", Type.GENERIC, "PCCW Enterprises Limited", LocalDate.parse("2016-04-15"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.pe">http://www.nic.pe</a><br><b>WHOIS Server:</b> kero.yachay.pe<br>
     *
     */
    public static final @NotNull TLD PE = new TLD("pe", Type.COUNTRY_CODE, "Red Cientifica Peruana", LocalDate.parse("1991-11-25"), LocalDate.parse("2022-07-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.pet<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150721-pet">Delegation of the .pet domain to Afilias plc (2015-07-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PET = new TLD("pet", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2015-07-16"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.registry.pf<br>
     *
     */
    public static final @NotNull TLD PF = new TLD("pf", Type.COUNTRY_CODE, "Gouvernement de la Polynésie française", LocalDate.parse("1996-03-19"), LocalDate.parse("2020-10-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pfizer.com">http://www.pfizer.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160712-pfizer">Delegation of the .pfizer domain to Pfizer Inc. (2016-07-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PFIZER = new TLD("pfizer", Type.GENERIC, "Pfizer Inc.", LocalDate.parse("2016-06-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD PG = new TLD("pg", Type.COUNTRY_CODE, "PNG DNS Administration Vice Chancellors Office The Papua New Guinea University of Technology", LocalDate.parse("1991-09-26"), LocalDate.parse("2021-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://dot.ph">http://dot.ph</a><br><br>
     *
     */
    public static final @NotNull TLD PH = new TLD("ph", Type.COUNTRY_CODE, "PH Domain Foundation", LocalDate.parse("1990-09-14"), LocalDate.parse("2024-01-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nabp.pharmacy/programs/accreditations-inspections/dotpharmacy/">https://nabp.pharmacy/programs/accreditations-inspections/dotpharmacy/</a><br><b>WHOIS Server:</b> whois.nic.pharmacy<br>
     *
     */
    public static final @NotNull TLD PHARMACY = new TLD("pharmacy", Type.GENERIC, "National Association of Boards of Pharmacy", LocalDate.parse("2014-08-28"), LocalDate.parse("2021-10-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170629-phd">Delegation of the .phd domain to Charleston Road Registry Inc. (2017-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHD = new TLD("phd", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2017-06-15"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.philips">http://nic.philips</a><br><b>WHOIS Server:</b> whois.nic.philips<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150506-philips">Delegation of the .philips domain to Koninklijke Philips N.V. (2015-05-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHILIPS = new TLD("philips", Type.GENERIC, "Koninklijke Philips N.V.", LocalDate.parse("2015-02-05"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dish.com/">https://www.dish.com/</a><br><b>WHOIS Server:</b> whois.nic.phone<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161219-phone">Delegation of the .phone domain to Dish DBS Corporation (2016-12-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHONE = new TLD("phone", Type.GENERIC, "Dish DBS Corporation", LocalDate.parse("2016-12-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.photo/">http://nic.photo/</a><br><b>WHOIS Server:</b> whois.nic.photo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-photo">Delegation of the .PHOTO domain to Uniregistry, Corp. (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220607-photo">Transfer of the .photo domain to Registry Services, LLC (2022-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHOTO = new TLD("photo", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.photography<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131118-photography">Delegation of the .PHOTOGRAPHY domain to Sugar Glen, LLC (2013-11-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHOTOGRAPHY = new TLD("photography", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.photos<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-photos">Delegation of the .PHOTOS domain to Sea Corner, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHOTOS = new TLD("photos", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.physio">http://www.registry.physio</a><br><b>WHOIS Server:</b> whois.nic.physio<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140617-physio">Delegation of the .physio domain to PhysBiz Pty Ltd (2014-06-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PHYSIO = new TLD("physio", Type.GENERIC, "PhysBiz Pty Ltd", LocalDate.parse("2014-06-05"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150312-piaget">Delegation of the .piaget domain to Richemont DNS Inc. (2015-03-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191114-piaget">Revocation of the .piaget domain (2019-11-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PIAGET = new TLD("piaget", Type.GENERIC, null, LocalDate.parse("2015-01-08"), LocalDate.parse("2019-11-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.pics">https://nic.pics</a><br><b>WHOIS Server:</b> whois.nic.pics<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-pics">Delegation of the .PICS domain to Uniregistry, Corp. (2014-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PICS = new TLD("pics", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.pictet<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150304-pictet">Delegation of the .pictet domain to Pictet Europe S.A. (2015-03-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PICTET = new TLD("pictet", Type.GENERIC, "Pictet Europe S.A.", LocalDate.parse("2015-01-08"), LocalDate.parse("2023-12-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.pictures<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-pictures">Delegation of the .pictures domain to Foggy Sky, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PICTURES = new TLD("pictures", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.topspectrum.com/">http://www.topspectrum.com/</a><br><b>WHOIS Server:</b> whois.nic.pid<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151217-pid">Delegation of the .pid domain to Top Level Spectrum, Inc. (2015-12-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PID = new TLD("pid", Type.GENERIC, "Top Level Spectrum, Inc.", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-04-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.pin<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-pin">Delegation of the .pin domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PIN = new TLD("pin", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ping.com">http://www.ping.com</a><br><b>WHOIS Server:</b> whois.nic.ping<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151028-ping">Delegation of the .ping domain to Ping Registry Provider, Inc. (2015-10-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PING = new TLD("ping", Type.GENERIC, "Ping Registry Provider, Inc.", LocalDate.parse("2015-09-24"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.pink<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140115-pink">Delegation of the .PINK domain to Afilias Limited (2014-01-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PINK = new TLD("pink", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.pioneer/">http://www.nic.pioneer/</a><br><b>WHOIS Server:</b> whois.nic.pioneer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160601-pioneer">Delegation of the .pioneer domain to Pioneer Corporation (2016-06-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PIONEER = new TLD("pioneer", Type.GENERIC, "Pioneer Corporation", LocalDate.parse("2016-05-26"), LocalDate.parse("2022-09-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.pizza<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140822-pizza">Delegation of the .pizza domain to Foggy Moon, LLC (2014-08-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PIZZA = new TLD("pizza", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-08-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pknic.net.pk/">http://www.pknic.net.pk/</a><br><b>WHOIS Server:</b> whois.pknic.net.pk<br>
     *
     */
    public static final @NotNull TLD PK = new TLD("pk", Type.COUNTRY_CODE, "PKNIC", LocalDate.parse("1992-06-03"), LocalDate.parse("2022-07-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.dns.pl/en/">https://www.dns.pl/en/</a><br><b>WHOIS Server:</b> whois.dns.pl<br>
     *
     */
    public static final @NotNull TLD PL = new TLD("pl", Type.COUNTRY_CODE, "Research and Academic Computer Network", LocalDate.parse("1990-07-30"), LocalDate.parse("2024-01-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.place<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140624-place">Delegation of the .place domain to Snow Galley, LLC (2014-06-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PLACE = new TLD("place", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-06-05"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150617-play">Delegation of the .play domain to Charleston Road Registry Inc. (2015-06-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PLAY = new TLD("play", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2015-06-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.playstation.com">http://www.playstation.com</a><br><b>WHOIS Server:</b> whois.nic.playstation<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151105-playstation">Delegation of the .playstation domain to Sony Computer Entertainment Inc. (2015-11-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PLAYSTATION = new TLD("playstation", Type.GENERIC, "Sony Computer Entertainment Inc.", LocalDate.parse("2015-09-24"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.plumbing<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-plumbing">Delegation of the .PLUMBING domain to Spring Tigers, LLC (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PLUMBING = new TLD("plumbing", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.plus<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-plus">Delegation of the .plus domain to Sugar Mill, LLC (2015-03-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PLUS = new TLD("plus", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.pm">http://www.nic.pm</a><br><b>WHOIS Server:</b> whois.nic.pm<br>
     *
     */
    public static final @NotNull TLD PM = new TLD("pm", Type.COUNTRY_CODE, "Association Française pour le Nommage Internet en Coopération (A.F.N.I.C.)", LocalDate.parse("1997-08-20"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.pn">https://nic.pn</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2000/pn-report-11feb00.html">IANA Report on Request for Redelegation of the .PN Top-Level Domain (2000-02-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PN = new TLD("pn", Type.COUNTRY_CODE, "Pitcairn Island Administration", LocalDate.parse("1997-07-10"), LocalDate.parse("2024-02-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pnc.com">http://www.pnc.com</a><br><b>WHOIS Server:</b> whois.nic.pnc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160628-pnc">Delegation of the .pnc domain to PNC Domain Co., LLC (2016-06-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PNC = new TLD("pnc", Type.GENERIC, "PNC Domain Co., LLC", LocalDate.parse("2015-10-29"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dvag-registry.de">http://www.dvag-registry.de</a><br><b>WHOIS Server:</b> whois.nic.pohl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140925-pohl">Delegation of the .pohl domain to Deutsche Vermögensberatung Aktiengesellschaft DVAG (2014-09-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD POHL = new TLD("pohl", Type.GENERIC, "Deutsche Vermögensberatung Aktiengesellschaft DVAG", LocalDate.parse("2014-09-18"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.poker<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141009-poker">Delegation of the .poker domain to Afilias Domains No. 5 Limited (2014-10-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD POKER = new TLD("poker", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-09-04"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.politie<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160620-politie">Delegation of the .politie domain to Politie Nederland (2016-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD POLITIE = new TLD("politie", Type.GENERIC, "Politie Nederland", LocalDate.parse("2016-06-16"), LocalDate.parse("2023-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.porn">https://nic.porn</a><br><b>WHOIS Server:</b> whois.nic.porn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141203-porn">Delegation of the .porn domain to ICM Registry PN LLC (2014-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PORN = new TLD("porn", Type.GENERIC, "ICM Registry PN LLC", LocalDate.parse("2014-11-26"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.upu.int">http://www.upu.int</a><br><b>WHOIS Server:</b> whois.dotpostregistry.net<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/post-report-20120802.html">Report on the Delegation of the .POST Top-Level Domain (2012-08-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD POST = new TLD("post", Type.SPONSORED, "Universal Postal Union", LocalDate.parse("2012-08-07"), LocalDate.parse("2023-08-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.pr">http://www.nic.pr</a><br><b>WHOIS Server:</b> whois.afilias-srs.net<br>
     *
     */
    public static final @NotNull TLD PR = new TLD("pr", Type.COUNTRY_CODE, "Gauss Research Laboratory Inc.", LocalDate.parse("1989-08-27"), LocalDate.parse("2022-07-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.prudential.com">https://www.prudential.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-pramerica">Delegation of the .pramerica domain to Prudential Financial, Inc. (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRAMERICA = new TLD("pramerica", Type.GENERIC, "Prudential Financial, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.praxi.com">http://www.praxi.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140717-praxi">Delegation of the .praxi domain to Praxi S.p.A. (2014-07-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRAXI = new TLD("praxi", Type.GENERIC, "Praxi S.p.A.", LocalDate.parse("2014-03-27"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.press<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140527-press">Delegation of the .press domain to DotPress Inc. (2014-05-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210825-press">Transfer of the .press domain to Radix FZC (2021-08-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-press">Transfer of the .press domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRESS = new TLD("press", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2014-05-22"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.prime<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-prime">Delegation of the .prime domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRIME = new TLD("prime", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .PRO through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.pro<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/pro-report-06may02.html">IANA Report on Establishment of the .PRO Top-Level Domain (2002-05-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190508-pro">Transfer of the .pro domain to Afilias Limited (2019-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRO = new TLD("pro", Type.GENERIC_RESTRICTED, "Identity Digital Limited", LocalDate.parse("2002-05-08"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140825-prod">Delegation of the .prod domain to Charleston Road Registry Inc. (2014-08-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROD = new TLD("prod", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.productions<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-productions">Delegation of the .productions domain to Magic Birch, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRODUCTIONS = new TLD("productions", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140910-prof">Delegation of the .prof domain to Charleston Road Registry Inc. (2014-09-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROF = new TLD("prof", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-09-04"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.progressive.com">http://www.progressive.com</a><br><b>WHOIS Server:</b> whois.nic.progressive<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160418-progressive">Delegation of the .progressive domain to Progressive Casualty Insurance Company (2016-04-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROGRESSIVE = new TLD("progressive", Type.GENERIC, "Progressive Casualty Insurance Company", LocalDate.parse("2016-02-19"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.promo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151230-promo">Delegation of the .promo domain to Afilias plc (2015-12-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROMO = new TLD("promo", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2015-12-18"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.properties<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-properties">Delegation of the .properties domain to Big Pass, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROPERTIES = new TLD("properties", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.property<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140815-property">Delegation of the .property domain to Uniregistry, Corp. (2014-08-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221116-property">Transfer of the .property domain to Internet Naming Co. (2022-11-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231101-property">Transfer of the .property domain to Digital Property Infrastructure Limited (2023-11-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROPERTY = new TLD("property", Type.GENERIC, "Digital Property Infrastructure Limited", LocalDate.parse("2014-08-15"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.protection/">https://nic.protection/</a><br><b>WHOIS Server:</b> whois.nic.protection<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-protection">Delegation of the .protection domain to XYZ.COM LLC (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PROTECTION = new TLD("protection", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-08-13"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.prudential.com">https://www.prudential.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-pru">Delegation of the .pru domain to Prudential Financial, Inc. (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRU = new TLD("pru", Type.GENERIC, "Prudential Financial, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.prudential.com">https://www.prudential.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-prudential">Delegation of the .prudential domain to Prudential Financial, Inc. (2016-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PRUDENTIAL = new TLD("prudential", Type.GENERIC, "Prudential Financial, Inc.", LocalDate.parse("2016-07-14"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ps">http://www.nic.ps</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2004/ps-report-21jun04.html">IANA Report on the Delegation of the .PS Top-Level Domain (2004-06-21)</a></li>
     *   <li><a href="https://iana.org/reports/2000/ps-report-22mar00.html">IANA Report on Request for Delegation of the .PS Top-Level Domain (2000-03-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PS = new TLD("ps", Type.COUNTRY_CODE, "Ministry Of Telecommunications and Information Technology, Government Computer Center.", LocalDate.parse("2000-03-22"), LocalDate.parse("2021-02-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dns.pt/">http://www.dns.pt/</a><br><b>WHOIS Server:</b> whois.dns.pt<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/pt-report-20130808.html">Report on the Redelegation of the .PT domain representing Portugal to Associação DNS.PT (2013-08-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PT = new TLD("pt", Type.COUNTRY_CODE, "Associação DNS.PT", LocalDate.parse("1988-06-30"), LocalDate.parse("2023-08-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.pub<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-pub">Delegation of the .pub domain to United TLD Holdco Ltd. (2014-02-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-pub">Transfer of the .pub domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PUB = new TLD("pub", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-02-20"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.pw">http://www.registry.pw</a><br><b>WHOIS Server:</b> whois.nic.pw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2003/pw-report-30jun03.html">IANA Report on Redelegation of the .PW Top-Level Domain (2003-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PW = new TLD("pw", Type.COUNTRY_CODE, "Micronesia Investment and Development Corporation", LocalDate.parse("1997-06-12"), LocalDate.parse("2024-02-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://pwc.com">http://pwc.com</a><br><b>WHOIS Server:</b> whois.nic.pwc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160208-pwc">Delegation of the .pwc domain to PricewaterhouseCoopers LLP (2016-02-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD PWC = new TLD("pwc", Type.GENERIC, "PricewaterhouseCoopers LLP", LocalDate.parse("2016-01-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.py">http://www.nic.py</a><br><br>
     *
     */
    public static final @NotNull TLD PY = new TLD("py", Type.COUNTRY_CODE, "NIC-PY", LocalDate.parse("1991-09-09"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.cra.gov.qa/">https://www.cra.gov.qa/</a><br><b>WHOIS Server:</b> whois.registry.qa<br>
     *
     */
    public static final @NotNull TLD QA = new TLD("qa", Type.COUNTRY_CODE, "Communications Regulatory Authority", LocalDate.parse("1996-06-12"), LocalDate.parse("2022-02-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.qpon">http://www.nic.qpon</a><br><b>WHOIS Server:</b> whois.nic.qpon<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140210-qpon">Delegation of the .qpon domain to dotCOOL, Inc. (2014-02-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230224-qpon">Transfer of the .qpon domain to DOTQPON LLC. (2023-02-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD QPON = new TLD("qpon", Type.GENERIC, "DOTQPON LLC.", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registre.quebec">http://www.registre.quebec</a><br><b>WHOIS Server:</b> whois.nic.quebec<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140414-quebec">Delegation of the .quebec domain to PointQuébec Inc (2014-04-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD QUEBEC = new TLD("quebec", Type.GENERIC, "PointQuébec Inc", LocalDate.parse("2014-03-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.quest/">https://nic.quest/</a><br><b>WHOIS Server:</b> whois.nic.quest<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160204-quest">Delegation of the .quest domain to Quest ION Limited (2016-02-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191114-quest">Transfer of the .quest domain to XYZ.COM LLC (2019-11-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD QUEST = new TLD("quest", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-08-06"), LocalDate.parse("2024-03-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160729-qvc">Delegation of the .qvc domain to QVC, Inc. (2016-07-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211007-qvc">Revocation of the .qvc domain (2021-10-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD QVC = new TLD("qvc", Type.GENERIC, null, LocalDate.parse("2016-07-21"), LocalDate.parse("2021-10-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.racing">http://nic.racing</a><br><b>WHOIS Server:</b> whois.nic.racing<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150331-racing">Delegation of the .racing domain to Premier Registry Limited (2015-03-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RACING = new TLD("racing", Type.GENERIC, "Premier Registry Limited", LocalDate.parse("2015-03-26"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.radio/">https://www.nic.radio/</a><br><b>WHOIS Server:</b> whois.nic.radio<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161007-radio">Delegation of the .radio domain to European Broadcasting Union (EBU) (2016-10-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RADIO = new TLD("radio", Type.GENERIC, "European Broadcasting Union (EBU)", LocalDate.parse("2016-09-22"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-raid">Delegation of the .raid domain to Johnson Shareholdings, Inc (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211203-raid">Revocation of the .raid domain (2021-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RAID = new TLD("raid", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.re">http://www.nic.re</a><br><b>WHOIS Server:</b> whois.nic.re<br>
     *
     */
    public static final @NotNull TLD RE = new TLD("re", Type.COUNTRY_CODE, "Association Française pour le Nommage Internet en Coopération (A.F.N.I.C.)", LocalDate.parse("1997-04-07"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.read<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-read">Delegation of the .read domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD READ = new TLD("read", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.get.realtor/">https://www.get.realtor/</a><br><b>WHOIS Server:</b> whois.nic.realestate<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160519-realestate">Delegation of the .realestate domain to dotRealEstate LLC (2016-05-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REALESTATE = new TLD("realestate", Type.GENERIC, "dotRealEstate LLC", LocalDate.parse("2016-04-28"), LocalDate.parse("2024-05-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.get.realtor">https://www.get.realtor</a><br><b>WHOIS Server:</b> whois.nic.realtor<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140728-realtor">Delegation of the .realtor domain to Real Estate Domains LLC (2014-07-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REALTOR = new TLD("realtor", Type.GENERIC, "Real Estate Domains LLC", LocalDate.parse("2014-07-24"), LocalDate.parse("2023-05-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.realty<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150626-realty">Delegation of the .realty domain to Fegistry, LLC (2015-06-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210211-realty">Transfer of the .realty domain to Dog Beach, LLC (2021-02-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240223-realty">Transfer of the .realty domain to Internet Naming Co. (2024-02-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REALTY = new TLD("realty", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2015-06-11"), LocalDate.parse("2024-05-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.recipes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-recipes">Delegation of the .RECIPES domain to Grand Island, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RECIPES = new TLD("recipes", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.red<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-red">Delegation of the .RED domain to Afilias Limited (2014-01-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RED = new TLD("red", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www/redstone-cn.com">http://www/redstone-cn.com</a><br><b>WHOIS Server:</b> whois.nic.redstone<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150326-redstone">Delegation of the .redstone domain to Redstone Haute Couture Co., Ltd. (2015-03-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REDSTONE = new TLD("redstone", Type.GENERIC, "Redstone Haute Couture Co., Ltd.", LocalDate.parse("2015-03-06"), LocalDate.parse("2024-02-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.travelers.com">http://www.travelers.com</a><br><b>WHOIS Server:</b> whois.nic.redumbrella<br>
     *
     */
    public static final @NotNull TLD REDUMBRELLA = new TLD("redumbrella", Type.GENERIC, "Travelers TLD, LLC", LocalDate.parse("2015-11-20"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.rehab<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-rehab">Delegation of the .rehab domain to United TLD Holdco, Ltd (2014-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-rehab">Transfer of the .rehab domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REHAB = new TLD("rehab", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.reise<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140521-reise">Delegation of the .reise domain to dotreise GmbH (2014-05-21)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150612-reise">Redelegation of the .reise domain to Foggy Way, LLC (2015-06-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REISE = new TLD("reise", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-05-15"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.reisen<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-reisen">Delegation of the .reisen domain to New Cypress, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REISEN = new TLD("reisen", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.reit.com">http://www.reit.com</a><br><b>WHOIS Server:</b> whois.nic.reit<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141104-reit">Delegation of the .reit domain to National Association of Real Estate Investment Trusts, Inc. (2014-11-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REIT = new TLD("reit", Type.GENERIC, "National Association of Real Estate Investment Trusts, Inc.", LocalDate.parse("2014-10-23"), LocalDate.parse("2023-09-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ril.com">http://www.ril.com</a><br><b>WHOIS Server:</b> whois.nic.reliance<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-reliance">Delegation of the .reliance domain to Reliance Industries Limited (2016-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RELIANCE = new TLD("reliance", Type.GENERIC, "Reliance Industries Limited", LocalDate.parse("2016-08-11"), LocalDate.parse("2023-08-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.ren/">http://nic.ren/</a><br><b>WHOIS Server:</b> whois.nic.ren<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140321-ren">Delegation of the .ren domain to Beijing Qianxiang Wangjing Technology Development Co., Ltd. (2014-03-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190920-ren">Transfer of the .ren domain to ZDNS International Limited (2019-09-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REN = new TLD("ren", Type.GENERIC, "ZDNS International Limited", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.rent/">https://nic.rent/</a><br><b>WHOIS Server:</b> whois.nic.rent<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150428-rent">Delegation of the .rent domain to XYZ.COM LLC (2015-04-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RENT = new TLD("rent", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-04-02"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.rentals<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-rentals">Delegation of the .rentals domain to Big Hollow,LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RENTALS = new TLD("rentals", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.repair<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-repair">Delegation of the .REPAIR domain to Lone Sunset, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REPAIR = new TLD("repair", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.report<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140131-report">Delegation of the .report domain to Binky Glen, LLC (2014-01-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REPORT = new TLD("report", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.republican<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-republican">Delegation of the .republican domain to United TLD Holdco, Ltd (2014-06-02)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-republican">Transfer of the .republican domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REPUBLICAN = new TLD("republican", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.register.rest/">http://www.register.rest/</a><br><b>WHOIS Server:</b> whois.nic.rest<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140328-rest">Delegation of the .rest domain to Punto 2012 Sociedad Anonima Promotora de Inversion de Capital Variable (2014-03-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REST = new TLD("rest", Type.GENERIC, "Punto 2012 Sociedad Anonima Promotora de Inversion de Capital Variable", LocalDate.parse("2014-03-06"), LocalDate.parse("2024-05-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.restaurant<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140805-restaurant">Delegation of the .restaurant domain to Snow Avenue, LLC (2014-08-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RESTAURANT = new TLD("restaurant", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.review">http://nic.review</a><br><b>WHOIS Server:</b> whois.nic.review<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-review">Delegation of the .review domain to dot Review Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REVIEW = new TLD("review", Type.GENERIC, "dot Review Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.reviews<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-reviews">Delegation of the .reviews domain to United TLD Holdco, LTD (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-reviews">Transfer of the .reviews domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REVIEWS = new TLD("reviews", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.rexroth<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-rexroth">Delegation of the .rexroth domain to Robert Bosch GMBH (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD REXROTH = new TLD("rexroth", Type.GENERIC, "Robert Bosch GMBH", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.i-registry.com">http://www.i-registry.com</a><br><b>WHOIS Server:</b> whois.nic.rich<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-rich">Delegation of the .RICH domain to I-REGISTRY Ltd., Niederlassung Deutschland (2014-01-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RICH = new TLD("rich", Type.GENERIC, "iRegistry GmbH", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.asia/namesphere/">http://www.dot.asia/namesphere/</a><br><b>WHOIS Server:</b> whois.nic.richardli<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160510-richardli">Delegation of the .richardli domain to Pacific Century Asset Management (HK) Limited (2016-05-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RICHARDLI = new TLD("richardli", Type.GENERIC, "Pacific Century Asset Management (HK) Limited", LocalDate.parse("2016-04-15"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ricoh.com">http://www.ricoh.com</a><br><b>WHOIS Server:</b> whois.nic.ricoh<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150618-ricoh">Delegation of the .ricoh domain to Ricoh Company, Ltd. (2015-06-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RICOH = new TLD("ricoh", Type.GENERIC, "Ricoh Company, Ltd.", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-rightathome">Delegation of the .rightathome domain to Johnson Shareholdings, Inc. (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200728-rightathome">Revocation of the .rightathome domain (2020-07-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RIGHTATHOME = new TLD("rightathome", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2020-07-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ril.com">http://www.ril.com</a><br><b>WHOIS Server:</b> whois.nic.ril<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161111-ril">Delegation of the .ril domain to Reliance Industries Limited (2016-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RIL = new TLD("ril", Type.GENERIC, "Reliance Industries Limited", LocalDate.parse("2016-08-11"), LocalDate.parse("2023-08-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.rio/">https://nic.rio/</a><br><b>WHOIS Server:</b> whois.gtlds.nic.br<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140521-rio">Delegation of the .rio domain to Empresa Municipal de Informática SA - IPLANRIO (2014-05-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RIO = new TLD("rio", Type.GENERIC, "Empresa Municipal de Informática SA - IPLANRIO", LocalDate.parse("2014-05-15"), LocalDate.parse("2023-11-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.rip<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141010-rip">Delegation of the .rip domain to United TLD Holdco Ltd. (2014-10-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-rip">Transfer of the .rip domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RIP = new TLD("rip", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-10-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161123-rmit">Delegation of the .rmit domain to Royal Melbourne Institute of Technology (2016-11-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210927-rmit">Revocation of the .rmit domain (2021-09-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RMIT = new TLD("rmit", Type.GENERIC, null, LocalDate.parse("2015-12-23"), LocalDate.parse("2021-09-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.rotld.ro/">http://www.rotld.ro/</a><br><b>WHOIS Server:</b> whois.rotld.ro<br>
     *
     */
    public static final @NotNull TLD RO = new TLD("ro", Type.COUNTRY_CODE, "National Institute for R&D in Informatics", LocalDate.parse("1993-02-26"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151104-rocher">Delegation of the .rocher domain to Ferrero Trading Lux S.A. (2015-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231102-rocher">Revocation of the .rocher domain (2023-11-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ROCHER = new TLD("rocher", Type.GENERIC, null, LocalDate.parse("2015-09-10"), LocalDate.parse("2023-11-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.rocks<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-rocks">Delegation of the .rocks domain to United TLD Holdco, Ltd (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-rocks">Transfer of the .rocks domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ROCKS = new TLD("rocks", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.rodeo/">http://nic.rodeo/</a><br><b>WHOIS Server:</b> whois.nic.rodeo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-rodeo">Delegation of the .rodeo domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-rodeo">Transfer of the .rodeo domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RODEO = new TLD("rodeo", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.rogers.com/consumer/home">http://www.rogers.com/consumer/home</a><br><b>WHOIS Server:</b> whois.nic.rogers<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160914-rogers">Delegation of the .rogers domain to Rogers Communications Canada Inc. (2016-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ROGERS = new TLD("rogers", Type.GENERIC, "Rogers Communications Canada Inc.", LocalDate.parse("2016-08-18"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.room<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-room">Delegation of the .room domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ROOM = new TLD("room", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.rnids.rs">http://www.rnids.rs</a><br><b>WHOIS Server:</b> whois.rnids.rs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/rs-yu-report-11sep2007.html">Report on Delegation of the .RS Top-Level Domain, and Redelegation of the .YU Top-Level Domain, to "Serbian National Register of Internet Domain Names" (2007-09-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RS = new TLD("rs", Type.COUNTRY_CODE, "Serbian National Internet Domain Registry (RNIDS)", LocalDate.parse("2007-09-24"), LocalDate.parse("2021-07-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     */
    public static final @NotNull TLD RSVP = new TLD("rsvp", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cctld.ru/en">http://www.cctld.ru/en</a><br><b>WHOIS Server:</b> whois.tcinet.ru<br>
     *
     */
    public static final @NotNull TLD RU = new TLD("ru", Type.COUNTRY_CODE, "Coordination Center for TLD RU", LocalDate.parse("1994-04-07"), LocalDate.parse("2024-01-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.rugby/">https://nic.rugby/</a><br><b>WHOIS Server:</b> whois.nic.rugby<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170405-rugby">Delegation of the .rugby domain to World Rugby Strategic Developments Limited (2017-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RUGBY = new TLD("rugby", Type.GENERIC, "World Rugby Strategic Developments Limited", LocalDate.parse("2017-03-23"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://dot.ruhr">http://dot.ruhr</a><br><b>WHOIS Server:</b> whois.nic.ruhr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131206-ruhr">Delegation of the .RUHR domain to regiodot GmbH and Co. KG (2013-12-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220831-ruhr">Transfer of the .ruhr domain to dotSaarland GmbH (2022-08-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RUHR = new TLD("ruhr", Type.GENERIC, "dotSaarland GmbH", LocalDate.parse("2013-11-25"), LocalDate.parse("2023-09-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.run<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150504-run">Delegation of the .run domain to Snow Park, LLC (2015-05-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RUN = new TLD("run", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-30"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://registry.ricta.org.rw">https://registry.ricta.org.rw</a><br><b>WHOIS Server:</b> whois.ricta.org.rw<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/rw-report-20120818.html">Report on the Redelegation of the .RW domain representing Rwanda to the Rwanda Information Communication and Technology Association (2012-08-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RW = new TLD("rw", Type.COUNTRY_CODE, "Rwanda Internet Community and Technology Alliance (RICTA) Ltd", LocalDate.parse("1996-10-21"), LocalDate.parse("2023-11-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.rwe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151023-rwe">Delegation of the .rwe domain to RWE AG (2015-10-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RWE = new TLD("rwe", Type.GENERIC, "RWE AG", LocalDate.parse("2015-10-01"), LocalDate.parse("2024-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.ryukyu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140331-ryukyu">Delegation of the .ryukyu domain to BusinessRalliart inc. (2014-03-31)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160613-ryukyu">Redelegation of the .ryukyu domain to BRregistry, Inc. (2016-06-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD RYUKYU = new TLD("ryukyu", Type.GENERIC, "BRregistry, Inc.", LocalDate.parse("2014-03-13"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.net.sa/">http://www.nic.net.sa/</a><br><b>WHOIS Server:</b> whois.nic.net.sa<br>
     *
     */
    public static final @NotNull TLD SA = new TLD("sa", Type.COUNTRY_CODE, "Communications, Space and Technology Commission", LocalDate.parse("1994-05-17"), LocalDate.parse("2024-07-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic-saarland.de">http://www.nic-saarland.de</a><br><b>WHOIS Server:</b> whois.nic.saarland<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140328-saarland">Delegation of the .saarland domain to dotSaarland GmbH (2014-03-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAARLAND = new TLD("saarland", Type.GENERIC, "dotSaarland GmbH", LocalDate.parse("2014-03-20"), LocalDate.parse("2023-09-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.safe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-safe">Delegation of the .safe domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAFE = new TLD("safe", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.safety">https://nic.safety</a><br><b>WHOIS Server:</b> whois.nic.safety<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-safety">Delegation of the .safety domain to Safety Registry Services, LLC. (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAFETY = new TLD("safety", Type.GENERIC, "Safety Registry Services, LLC.", LocalDate.parse("2015-04-23"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://dot-sakura.sakura.ad.jp/">https://dot-sakura.sakura.ad.jp/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150629-sakura">Delegation of the .sakura domain to SAKURA Internet Inc. (2015-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAKURA = new TLD("sakura", Type.GENERIC, "SAKURA internet Inc.", LocalDate.parse("2015-06-04"), LocalDate.parse("2023-12-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.sale<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141222-sale">Delegation of the .sale domain to United TLD Holdco, Ltd (2014-12-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-sale">Transfer of the .sale domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SALE = new TLD("sale", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-12-17"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.salon<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-salon">Delegation of the .salon domain to Outer Orchard, LLC (2015-12-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SALON = new TLD("salon", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-11-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.samsclub.com">http://www.samsclub.com</a><br><b>WHOIS Server:</b> whois.nic.samsclub<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160816-samsclub">Delegation of the .samsclub domain to Wal-Mart Stores, Inc. (2016-08-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAMSCLUB = new TLD("samsclub", Type.GENERIC, "Wal-Mart Stores, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2023-09-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://samsungregistry.com">http://samsungregistry.com</a><br><b>WHOIS Server:</b> whois.nic.samsung<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141205-samsung">Delegation of the .samsung domain to SAMSUNG SDS CO., LTD (2014-12-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAMSUNG = new TLD("samsung", Type.GENERIC, "SAMSUNG SDS CO., LTD", LocalDate.parse("2014-10-30"), LocalDate.parse("2024-01-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.sandvik">http://nic.sandvik</a><br><b>WHOIS Server:</b> whois.nic.sandvik<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150522-sandvik">Delegation of the .sandvik domain to Sandvik AB (2015-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SANDVIK = new TLD("sandvik", Type.GENERIC, "Sandvik AB", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.sandvikcoromant">http://nic.sandvikcoromant</a><br><b>WHOIS Server:</b> whois.nic.sandvikcoromant<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150522-sandvikcoromant">Delegation of the .sandvikcoromant domain to Sandvik AB (2015-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SANDVIKCOROMANT = new TLD("sandvikcoromant", Type.GENERIC, "Sandvik AB", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.sanofi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150722-sanofi">Delegation of the .sanofi domain to Sanofi (2015-07-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SANOFI = new TLD("sanofi", Type.GENERIC, "Sanofi", LocalDate.parse("2015-07-16"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sap.com">http://www.sap.com</a><br><b>WHOIS Server:</b> whois.nic.sap<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150325-sap">Delegation of the .sap domain to SAP AG (2015-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAP = new TLD("sap", Type.GENERIC, "SAP AG", LocalDate.parse("2015-03-19"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151028-sapo">Delegation of the .sapo domain to PT Comunicacoes S.A. (2015-10-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180521-sapo">Revocation of the .sapo domain (2018-05-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAPO = new TLD("sapo", Type.GENERIC, null, LocalDate.parse("2015-01-15"), LocalDate.parse("2018-05-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.sarl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140805-sarl">Delegation of the .sarl domain to Delta Orchard, LLC (2014-08-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SARL = new TLD("sarl", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sas.com">http://www.sas.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151216-sas">Delegation of the .sas domain to Research IP LLC (2015-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAS = new TLD("sas", Type.GENERIC, "Research IP LLC", LocalDate.parse("2015-11-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.save<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-save">Delegation of the .save domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAVE = new TLD("save", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.saxo">http://nic.saxo</a><br><b>WHOIS Server:</b> whois.nic.saxo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150205-saxo">Delegation of the .saxo domain to Saxo Bank A/S (2015-02-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SAXO = new TLD("saxo", Type.GENERIC, "Saxo Bank A/S", LocalDate.parse("2015-01-15"), LocalDate.parse("2024-03-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.net.sb/">http://www.nic.net.sb/</a><br><b>WHOIS Server:</b> whois.nic.net.sb<br>
     *
     */
    public static final @NotNull TLD SB = new TLD("sb", Type.COUNTRY_CODE, "Solomon Telekom Company Limited", LocalDate.parse("1994-04-19"), LocalDate.parse("2022-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.sbi.co.in">https://www.sbi.co.in</a><br><b>WHOIS Server:</b> whois.nic.sbi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160412-sbi">Delegation of the .sbi domain to State Bank of India (2016-04-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SBI = new TLD("sbi", Type.GENERIC, "STATE BANK OF INDIA", LocalDate.parse("2016-03-24"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.icu">http://nic.icu</a><br><b>WHOIS Server:</b> whois.nic.sbs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151027-sbs">Delegation of the .sbs domain to Special Broadcasting Service Corporation (2015-10-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240430-sbs">Transfer of the .sbs domain to Shortdot SA (2024-04-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SBS = new TLD("sbs", Type.GENERIC, "Shortdot SA", LocalDate.parse("2015-09-24"), LocalDate.parse("2024-06-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.sc">http://www.nic.sc</a><br><b>WHOIS Server:</b> whois.nic.sc<br>
     *
     */
    public static final @NotNull TLD SC = new TLD("sc", Type.COUNTRY_CODE, "VCS Pty Ltd", LocalDate.parse("1997-05-09"), LocalDate.parse("2024-04-29"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140811-sca">Delegation of the .sca domain to SVENSKA CELLULOSA AKTIEBOLAGET SCA (publ) (2014-08-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231211-sca">Revocation of the .sca domain (2023-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCA = new TLD("sca", Type.GENERIC, null, LocalDate.parse("2014-07-17"), LocalDate.parse("2023-12-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.scb.co.th/en/personal-banking/top-level-domain.html">https://www.scb.co.th/en/personal-banking/top-level-domain.html</a><br><b>WHOIS Server:</b> whois.nic.scb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140709-scb">Delegation of the .scb domain to The Siam Commercial Bank Public Company Limited ("SCB") (2014-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCB = new TLD("scb", Type.GENERIC, "The Siam Commercial Bank Public Company Limited (\"SCB\")", LocalDate.parse("2014-06-26"), LocalDate.parse("2024-05-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.schaeffler.com">http://www.schaeffler.com</a><br><b>WHOIS Server:</b> whois.afilias-srs.net<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-schaeffler">Delegation of the .schaeffler domain to Schaeffler Technologies AG and Co. KG (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCHAEFFLER = new TLD("schaeffler", Type.GENERIC, "Schaeffler Technologies AG and Co. KG", LocalDate.parse("2015-12-17"), LocalDate.parse("2023-05-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.schmidt/">http://nic.schmidt/</a><br><b>WHOIS Server:</b> whois.nic.schmidt<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140701-schmidt">Delegation of the .schmidt domain to SALM S.A.S. (2014-07-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCHMIDT = new TLD("schmidt", Type.GENERIC, "SCHMIDT GROUPE S.A.S.", LocalDate.parse("2014-06-19"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.scholarships.com">http://www.scholarships.com</a><br><b>WHOIS Server:</b> whois.nic.scholarships<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150330-scholarships">Delegation of the .scholarships domain to Scholarships.com, LLC (2015-03-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCHOLARSHIPS = new TLD("scholarships", Type.GENERIC, "Scholarships.com, LLC", LocalDate.parse("2014-07-10"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.school<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150216-school">Delegation of the .school domain to Little Galley, LLC (2015-02-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCHOOL = new TLD("school", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-02-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.schule<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140414-schule">Delegation of the .schule domain to Outer Moon, LLC (2014-04-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCHULE = new TLD("schule", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-11"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.schwarz">https://www.nic.schwarz</a><br><b>WHOIS Server:</b> whois.nic.schwarz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141210-schwarz">Delegation of the .schwarz domain to Schwarz Domains und Services GmbH and Co. KG (2014-12-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCHWARZ = new TLD("schwarz", Type.GENERIC, "Schwarz Domains und Services GmbH and Co. KG", LocalDate.parse("2014-12-04"), LocalDate.parse("2023-11-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.science">http://nic.science</a><br><b>WHOIS Server:</b> whois.nic.science<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141105-science">Delegation of the .science domain to dot Science Limited (2014-11-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCIENCE = new TLD("science", Type.GENERIC, "dot Science Limited", LocalDate.parse("2014-10-23"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-scjohnson">Delegation of the .scjohnson domain to Johnson Shareholdings, Inc. (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211203-scjohnson">Revocation of the .scjohnson domain (2021-12-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCJOHNSON = new TLD("scjohnson", Type.GENERIC, null, LocalDate.parse("2016-07-14"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD SCOR = new TLD("scor", Type.GENERIC, null, LocalDate.parse("2015-04-30"), LocalDate.parse("2020-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://dot.scot/">https://dot.scot/</a><br><b>WHOIS Server:</b> whois.nic.scot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140610-scot">Delegation of the .scot domain to Dot Scot Registry Limited (2014-06-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SCOT = new TLD("scot", Type.GENERIC, "Dot Scot Registry Limited", LocalDate.parse("2014-06-05"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.isoc.sd">http://www.isoc.sd</a><br><b>WHOIS Server:</b> whois.nic.sd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2002/sd-report-20dec02.html">IANA Report on Redelegation of the .SD Top-Level Domain (2002-12-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SD = new TLD("sd", Type.COUNTRY_CODE, "Sudan Internet Society", LocalDate.parse("1997-03-06"), LocalDate.parse("2024-01-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.internetstiftelsen.se">https://www.internetstiftelsen.se</a><br><b>WHOIS Server:</b> whois.iis.se<br>
     *
     */
    public static final @NotNull TLD SE = new TLD("se", Type.COUNTRY_CODE, "The Internet Infrastructure Foundation", LocalDate.parse("1986-09-04"), LocalDate.parse("2020-06-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170629-search">Delegation of the .search domain to Charleston Road Registry Inc. (2017-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEARCH = new TLD("search", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2017-06-08"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.seat.com/content/com/com/en/contact/abuse.html">http://www.seat.com/content/com/com/en/contact/abuse.html</a><br><b>WHOIS Server:</b> whois.nic.seat<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150416-seat">Delegation of the .seat domain to SEAT, S.A. (Sociedad Unipersonal) (2015-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEAT = new TLD("seat", Type.GENERIC, "SEAT, S.A. (Sociedad Unipersonal)", LocalDate.parse("2015-03-06"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.secure<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160805-secure">Delegation of the .secure domain to Amazon Registry Services, Inc. (2016-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SECURE = new TLD("secure", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.security/">https://nic.security/</a><br><b>WHOIS Server:</b> whois.nic.security<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150915-security">Delegation of the .security domain to XYZ.COM LLC (2015-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SECURITY = new TLD("security", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-09-03"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.seek.com.au/about/">https://www.seek.com.au/about/</a><br><b>WHOIS Server:</b> whois.nic.seek<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150810-seek">Delegation of the .seek domain to Seek Limited (2015-08-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEEK = new TLD("seek", Type.GENERIC, "Seek Limited", LocalDate.parse("2015-04-16"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.select/">http://nic.select/</a><br><b>WHOIS Server:</b> whois.nic.select<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160112-select">Delegation of the .select domain to iSelect Ltd (2016-01-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190807-select">Transfer of the .select domain to Registry Services, LLC (2019-08-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SELECT = new TLD("select", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-12-23"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://sener.es">http://sener.es</a><br><b>WHOIS Server:</b> whois.nic.rwe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150428-sener">Delegation of the .sener domain to Sener Ingeniería y Sistemas, S.A. (2015-04-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SENER = new TLD("sener", Type.GENERIC, "Sener Ingeniería y Sistemas, S.A.", LocalDate.parse("2015-04-02"), LocalDate.parse("2024-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.services<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-services">Delegation of the .services domain to Fox Castle, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SERVICES = new TLD("services", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160706-ses">Delegation of the .ses domain to SES (2016-07-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221216-ses">Revocation of the .ses domain (2022-12-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SES = new TLD("ses", Type.GENERIC, null, LocalDate.parse("2016-05-05"), LocalDate.parse("2022-12-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.seven/">http://nic.seven/</a><br><b>WHOIS Server:</b> whois.nic.seven<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150925-seven">Delegation of the .seven domain to Seven West Media Ltd (2015-09-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEVEN = new TLD("seven", Type.GENERIC, "Seven West Media Ltd", LocalDate.parse("2015-09-23"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sew-eurodrive.com/">http://www.sew-eurodrive.com/</a><br><b>WHOIS Server:</b> whois.nic.sew<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141210-sew">Delegation of the .sew domain to SEW-EURODRIVE GmbH and Co. KG (2014-12-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEW = new TLD("sew", Type.GENERIC, "SEW-EURODRIVE GmbH and Co KG", LocalDate.parse("2014-11-06"), LocalDate.parse("2023-08-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.sex">http://nic.sex</a><br><b>WHOIS Server:</b> whois.nic.sex<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150416-sex">Delegation of the .sex domain to ICM Registry SX LLC (2015-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEX = new TLD("sex", Type.GENERIC, "ICM Registry SX LLC", LocalDate.parse("2015-04-09"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.sexy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-sexy">Delegation of the .SEXY domain to Uniregistry, Corp. (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221116-sexy">Transfer of the .sexy domain to Internet Naming Co. (2022-11-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SEXY = new TLD("sexy", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2013-11-08"), LocalDate.parse("2024-05-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.sfr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151124-sfr">Delegation of the .sfr domain to Societe Francaise du Radiotelephone - SFR (2015-11-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SFR = new TLD("sfr", Type.GENERIC, "Societe Francaise du Radiotelephone - SFR", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sgnic.sg">http://www.sgnic.sg</a><br><b>WHOIS Server:</b> whois.sgnic.sg<br>
     *
     */
    public static final @NotNull TLD SG = new TLD("sg", Type.COUNTRY_CODE, "Singapore Network Information Centre (SGNIC) Pte Ltd", LocalDate.parse("1988-10-19"), LocalDate.parse("2024-03-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.sh/">http://www.nic.sh/</a><br><b>WHOIS Server:</b> whois.nic.sh<br>
     *
     */
    public static final @NotNull TLD SH = new TLD("sh", Type.COUNTRY_CODE, "Government of St. Helena", LocalDate.parse("1997-09-23"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.shangri-la.com/">http://www.shangri-la.com/</a><br><b>WHOIS Server:</b> whois.nic.shangrila<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160630-shangrila">Delegation of the .shangrila domain to Shangri-La International Hotel Management Limited (2016-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHANGRILA = new TLD("shangrila", Type.GENERIC, "Shangri-La International Hotel Management Limited", LocalDate.parse("2016-06-24"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-sharp">Delegation of the .sharp domain to Sharp Corporation (2015-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHARP = new TLD("sharp", Type.GENERIC, "Sharp Corporation", LocalDate.parse("2015-11-25"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160315-shaw">Delegation of the .shaw domain to Shaw Cablesystems G.P. (2016-03-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240709-shaw">Revocation of the .shaw domain (2024-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHAW = new TLD("shaw", Type.GENERIC, null, LocalDate.parse("2016-03-10"), LocalDate.parse("2024-07-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.shell.com">http://www.shell.com</a><br><b>WHOIS Server:</b> whois.nic.shell<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151208-shell">Delegation of the .shell domain to Shell Information Technology International Inc (2015-12-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHELL = new TLD("shell", Type.GENERIC, "Shell Information Technology International Inc", LocalDate.parse("2015-11-06"), LocalDate.parse("2023-12-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://icann.org/ebero">https://icann.org/ebero</a><br><b>WHOIS Server:</b> whois.nic.shia<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-shia">Delegation of the .shia domain to Asia Green IT System Bilgisayar San. ve Tic. Ltd. Sti. (2015-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHIA = new TLD("shia", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2015-11-25"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.shiksha<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-shiksha">Delegation of the .SHIKSHA domain to Afilias Limited (2014-01-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHIKSHA = new TLD("shiksha", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.shoes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-shoes">Delegation of the .SHOES domain to Binky Galley, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHOES = new TLD("shoes", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.shop<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160509-shop">Delegation of the .shop domain to GMO Registry, Inc. (2016-05-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHOP = new TLD("shop", Type.GENERIC, "GMO Registry, Inc.", LocalDate.parse("2016-05-05"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.shopping<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160613-shopping">Delegation of the .shopping domain to Over Keep, LLC (2016-06-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHOPPING = new TLD("shopping", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2016-06-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.teleinfo.cn">http://www.teleinfo.cn</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160325-shouji">Delegation of the .shouji domain to QIHOO 360 Technology Co. Ltd. (2016-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHOUJI = new TLD("shouji", Type.GENERIC, "QIHOO 360 TECHNOLOGY CO. LTD.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.show<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150414-show">Delegation of the .show domain to Snow Beach, LLC (2015-04-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHOW = new TLD("show", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160801-showtime">Delegation of the .showtime domain to CBS Domains Inc. (2016-08-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231025-showtime">Revocation of the .showtime domain (2023-10-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHOWTIME = new TLD("showtime", Type.GENERIC, null, LocalDate.parse("2016-07-21"), LocalDate.parse("2023-10-25"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141224-shriram">Delegation of the .shriram domain to Shriram Capital Ltd. (2014-12-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201124-shriram">Revocation of the .shriram domain (2020-11-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SHRIRAM = new TLD("shriram", Type.GENERIC, null, LocalDate.parse("2014-12-04"), LocalDate.parse("2020-11-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.si/">https://www.registry.si/</a><br><b>WHOIS Server:</b> whois.register.si<br>
     *
     */
    public static final @NotNull TLD SI = new TLD("si", Type.COUNTRY_CODE, "Academic and Research Network of Slovenia (ARNES)", LocalDate.parse("1992-04-01"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.silk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-silk">Delegation of the .silk domain to Amazon Registry Services, Inc. (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SILK = new TLD("silk", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-02-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sina.com">http://www.sina.com</a><br><b>WHOIS Server:</b> whois.nic.sina<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160328-sina">Delegation of the .sina domain to Sina Corporation (2016-03-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SINA = new TLD("sina", Type.GENERIC, "Sina Corporation", LocalDate.parse("2016-02-29"), LocalDate.parse("2023-08-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.singles<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-singles">Delegation of the .SINGLES domain to Fern Madison, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SINGLES = new TLD("singles", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.site<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150312-site">Delegation of the .site domain to DotSite Inc. (2015-03-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210831-site">Transfer of the .site domain to Radix FZC (2021-08-31)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-site">Transfer of the .site domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SITE = new TLD("site", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2015-03-06"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.norid.no/en/omnorid/toppdomenet-sj/">https://www.norid.no/en/omnorid/toppdomenet-sj/</a><br><br>
     *
     */
    public static final @NotNull TLD SJ = new TLD("sj", Type.COUNTRY_CODE, "Norid A/S", LocalDate.parse("1997-08-21"), LocalDate.parse("2022-01-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sk-nic.sk">http://www.sk-nic.sk</a><br><b>WHOIS Server:</b> whois.sk-nic.sk<br>
     *
     */
    public static final @NotNull TLD SK = new TLD("sk", Type.COUNTRY_CODE, "SK-NIC, a.s.", LocalDate.parse("1993-03-29"), LocalDate.parse("2024-01-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.ski<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150527-ski">Delegation of the .ski domain to STARTING DOT LIMITED (2015-05-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190508-ski">Transfer of the .ski domain to Afilias Limited (2019-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SKI = new TLD("ski", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.skin/">https://nic.skin/</a><br><b>WHOIS Server:</b> whois.nic.skin<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160111-skin">Delegation of the .skin domain to L'Oréal (2016-01-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200915-skin">Transfer of the .skin domain to XYZ.COM LLC (2020-09-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SKIN = new TLD("skin", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-12-23"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.sky">http://nic.sky</a><br><b>WHOIS Server:</b> whois.nic.sky<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141205-sky">Delegation of the .sky domain to Sky IP International Ltd (2014-12-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SKY = new TLD("sky", Type.GENERIC, "Sky International AG", LocalDate.parse("2014-10-02"), LocalDate.parse("2023-06-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.skype.com">http://www.skype.com</a><br><b>WHOIS Server:</b> whois.nic.skype<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150622-skype">Delegation of the .skype domain to Microsoft Corporation (2015-06-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SKYPE = new TLD("skype", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-03-19"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.sl">https://www.nic.sl</a><br><br>
     *
     */
    public static final @NotNull TLD SL = new TLD("sl", Type.COUNTRY_CODE, "Sierratel", LocalDate.parse("1997-05-09"), LocalDate.parse("2021-10-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.echostar.com/">http://www.echostar.com/</a><br><b>WHOIS Server:</b> whois.nic.sling<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160808-sling">Delegation of the .sling domain to Hughes Satellite Systems Corporation (2016-08-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240425-sling">Transfer of the .sling domain to DISH Technologies L.L.C. (2024-04-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SLING = new TLD("sling", Type.GENERIC, "DISH Technologies L.L.C.", LocalDate.parse("2016-08-04"), LocalDate.parse("2024-04-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.sm">https://www.nic.sm</a><br><b>WHOIS Server:</b> whois.nic.sm<br>
     *
     */
    public static final @NotNull TLD SM = new TLD("sm", Type.COUNTRY_CODE, "Telecom Italia San Marino S.p.A.", LocalDate.parse("1995-08-16"), LocalDate.parse("2020-11-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.smart<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160712-smart">Delegation of the .smart domain to Smart Communications, Inc. (SMART) (2016-07-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SMART = new TLD("smart", Type.GENERIC, "Smart Communications, Inc. (SMART)", LocalDate.parse("2016-04-21"), LocalDate.parse("2024-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.smile<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-smile">Delegation of the .smile domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SMILE = new TLD("smile", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nicsenegal.sn">https://www.nicsenegal.sn</a><br><b>WHOIS Server:</b> whois.nic.sn<br>
     *
     */
    public static final @NotNull TLD SN = new TLD("sn", Type.COUNTRY_CODE, "Universite Cheikh Anta Diop", LocalDate.parse("1993-03-19"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sncf.com">http://www.sncf.com</a><br><b>WHOIS Server:</b> whois.nic.sncf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150529-sncf">Delegation of the .sncf domain to SNCF (Société Nationale des Chemins de fer Francais) (2015-05-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SNCF = new TLD("sncf", Type.GENERIC, "Société Nationale SNCF", LocalDate.parse("2015-05-08"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.so/">http://www.nic.so/</a><br><b>WHOIS Server:</b> whois.nic.so<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2009/so-report-03feb2009.html">Report on the Redelegation of the .SO Top-Level Domain to the "Ministry of Post and Telecommunications of the Transitional Federal Government of Somalia" (2009-02-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SO = new TLD("so", Type.COUNTRY_CODE, "Ministry of Post and Telecommunications", LocalDate.parse("1997-08-28"), LocalDate.parse("2016-04-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.soccer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150511-soccer">Delegation of the .soccer domain to Foggy Shadow, LLC (2015-05-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOCCER = new TLD("soccer", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-05-08"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.social<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-social">Delegation of the .SOCIAL domain to United TLD Holdco Ltd. (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-social">Transfer of the .social domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOCIAL = new TLD("social", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://softbank.com/">http://softbank.com/</a><br><b>WHOIS Server:</b> whois.nic.softbank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160113-softbank">Delegation of the .softbank domain to SoftBank Corp. (2016-01-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOFTBANK = new TLD("softbank", Type.GENERIC, "SoftBank Group Corp.", LocalDate.parse("2015-12-23"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.software<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-software">Delegation of the .software domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-software">Transfer of the .software domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOFTWARE = new TLD("software", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140321-sohu">Delegation of the .sohu domain to Sohu.com Limited (2014-03-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOHU = new TLD("sohu", Type.GENERIC, "Sohu.com Limited", LocalDate.parse("2014-02-27"), LocalDate.parse("2024-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.solar<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-solar">Delegation of the .SOLAR domain to Ruby Town, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOLAR = new TLD("solar", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.solutions<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-solutions">Delegation of the .SOLUTIONS domain to Silver Cover, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOLUTIONS = new TLD("solutions", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-song">Delegation of the .song domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SONG = new TLD("song", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sony.com">http://www.sony.com</a><br><b>WHOIS Server:</b> whois.nic.sony<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150414-sony">Delegation of the .sony domain to Sony Corporation (2015-04-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SONY = new TLD("sony", Type.GENERIC, "Sony Corporation", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140416-soy">Delegation of the .soy domain to Charleston Road Registry Inc. (2014-04-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SOY = new TLD("soy", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-03-20"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.spa">https://nic.spa</a><br><b>WHOIS Server:</b> whois.nic.spa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201016-spa">Delegation of the .spa domain to Asia Spa and Wellness Promotion Council Limited (2020-10-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SPA = new TLD("spa", Type.GENERIC, "Asia Spa and Wellness Promotion Council Limited", LocalDate.parse("2020-09-25"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.space<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140527-space">Delegation of the .space domain to DotSpace Inc. (2014-05-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210826-space">Transfer of the .space domain to Radix FZC (2021-08-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-space">Transfer of the .space domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SPACE = new TLD("space", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2014-05-22"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140716-spiegel">Delegation of the .spiegel domain to SPIEGEL-Verlag Rudolf Augstein GmbH and Co. KG (2014-07-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20181214-spiegel">Revocation of the .spiegel domain (2018-12-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SPIEGEL = new TLD("spiegel", Type.GENERIC, null, LocalDate.parse("2014-06-19"), LocalDate.parse("2018-12-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://join.sport/">https://join.sport/</a><br><b>WHOIS Server:</b> whois.nic.sport<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180108-sport">Delegation of the .sport domain to Global Association of International Sports Federations (GAISF) (2018-01-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230714-sport">Transfer of the .sport domain to SportAccord (2023-07-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SPORT = new TLD("sport", Type.GENERIC, "SportAccord", LocalDate.parse("2018-01-05"), LocalDate.parse("2023-07-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.spot<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-spot">Delegation of the .spot domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SPOT = new TLD("spot", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150311-spreadbetting">Delegation of the .spreadbetting domain to Dotspreadbetting Registry Ltd (2015-03-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210421-spreadbetting">Revocation of the .spreadbetting domain (2021-04-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SPREADBETTING = new TLD("spreadbetting", Type.GENERIC, null, LocalDate.parse("2015-03-06"), LocalDate.parse("2021-04-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://isp.datasur.sr/">https://isp.datasur.sr/</a><br><br>
     *
     */
    public static final @NotNull TLD SR = new TLD("sr", Type.COUNTRY_CODE, "Telesur", LocalDate.parse("1991-09-03"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.internetx.info">http://www.internetx.info</a><br><b>WHOIS Server:</b> whois.nic.srl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150722-srl">Delegation of the .srl domain to InterNetX, Corp. (2015-07-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SRL = new TLD("srl", Type.GENERIC, "InterNetX Corp.", LocalDate.parse("2015-07-09"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-srt">Delegation of the .srt domain to FCA US Llc (2016-07-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191120-srt">Revocation of the .srt domain (2019-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SRT = new TLD("srt", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2019-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.ss/">https://nic.ss/</a><br><b>WHOIS Server:</b> whois.nic.ss<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2019/ss-report-20190108.html">Delegation of the .ss domain (2019-01-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SS = new TLD("ss", Type.COUNTRY_CODE, "National Communication Authority (NCA)", LocalDate.parse("2011-08-31"), LocalDate.parse("2024-04-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.st">http://www.nic.st</a><br><b>WHOIS Server:</b> whois.nic.st<br>
     *
     */
    public static final @NotNull TLD ST = new TLD("st", Type.COUNTRY_CODE, "Tecnisys", LocalDate.parse("1997-11-07"), LocalDate.parse("2022-11-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stada.com">http://www.stada.com</a><br><b>WHOIS Server:</b> whois.nic.stada<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150909-stada">Delegation of the .stada domain to STADA Arzneimittel AG (2015-09-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STADA = new TLD("stada", Type.GENERIC, "STADA Arzneimittel AG", LocalDate.parse("2015-08-13"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.staples.com/">http://www.staples.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160712-staples">Delegation of the .staples domain to Staples, Inc. (2016-07-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STAPLES = new TLD("staples", Type.GENERIC, "Staples, Inc.", LocalDate.parse("2016-06-09"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.startv.com">http://www.startv.com</a><br><b>WHOIS Server:</b> whois.nic.star<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151218-star">Delegation of the .star domain to Star India Private Limited (2015-12-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STAR = new TLD("star", Type.GENERIC, "Star India Private Limited", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-05-03"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150616-starhub">Delegation of the .starhub domain to StarHub Limited (2015-06-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190803-starhub">Revocation of the .starhub domain (2019-08-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STARHUB = new TLD("starhub", Type.GENERIC, null, LocalDate.parse("2015-05-08"), LocalDate.parse("2019-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.sbi.co.in">https://www.sbi.co.in</a><br><b>WHOIS Server:</b> whois.nic.statebank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160412-statebank">Delegation of the .statebank domain to State Bank of India (2016-04-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STATEBANK = new TLD("statebank", Type.GENERIC, "STATE BANK OF INDIA", LocalDate.parse("2016-03-24"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.statefarm.com">http://www.statefarm.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151222-statefarm">Delegation of the .statefarm domain to State Farm Mutual Automobile Insurance Company (2015-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STATEFARM = new TLD("statefarm", Type.GENERIC, "State Farm Mutual Automobile Insurance Company", LocalDate.parse("2015-11-06"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150616-statoil">Delegation of the .statoil domain to Statoil ASA (2015-06-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20181003-statoil">Revocation of the .statoil domain (2018-10-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STATOIL = new TLD("statoil", Type.GENERIC, null, LocalDate.parse("2015-06-11"), LocalDate.parse("2018-10-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stc.com.sa">http://www.stc.com.sa</a><br><b>WHOIS Server:</b> whois.nic.stc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-stc">Delegation of the .stc domain to Saudi Telecom Company (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STC = new TLD("stc", Type.GENERIC, "Saudi Telecom Company", LocalDate.parse("2015-08-20"), LocalDate.parse("2024-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stc.com.sa">http://www.stc.com.sa</a><br><b>WHOIS Server:</b> whois.nic.stcgroup<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-stcgroup">Delegation of the .stcgroup domain to Saudi Telecom Company (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STCGROUP = new TLD("stcgroup", Type.GENERIC, "Saudi Telecom Company", LocalDate.parse("2015-08-20"), LocalDate.parse("2024-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stockholm.se/">http://www.stockholm.se/</a><br><b>WHOIS Server:</b> whois.nic.stockholm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150925-stockholm">Delegation of the .stockholm domain to Stockholms Kommun (2015-09-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STOCKHOLM = new TLD("stockholm", Type.GENERIC, "Stockholms kommun", LocalDate.parse("2015-09-17"), LocalDate.parse("2023-08-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.storage/">https://nic.storage/</a><br><b>WHOIS Server:</b> whois.nic.storage<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151215-storage">Delegation of the .storage domain to Self Storage Company LLC (2015-12-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STORAGE = new TLD("storage", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.store<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-store">Delegation of the .store domain to DotStore Inc. (2016-02-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210826-store">Transfer of the .store domain to Radix FZC (2021-08-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-store">Transfer of the .store domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STORE = new TLD("store", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2016-02-11"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.stream">http://nic.stream</a><br><b>WHOIS Server:</b> whois.nic.stream<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160316-stream">Delegation of the .stream domain to dot Stream Limited (2016-03-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STREAM = new TLD("stream", Type.GENERIC, "dot Stream Limited", LocalDate.parse("2016-02-26"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.studio<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150706-studio">Delegation of the .studio domain to United TLD Holdco, Ltd. (2015-07-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-studio">Transfer of the .studio domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STUDIO = new TLD("studio", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-06-25"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.study">http://nic.study</a><br><b>WHOIS Server:</b> whois.nic.study<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150224-study">Delegation of the .study domain to Open Universities Australia Pty Ltd (2015-02-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220615-study">Transfer of the .study domain to Registry Services, LLC (2022-06-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STUDY = new TLD("study", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-02-19"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.style<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150203-style">Delegation of the .style domain to Binky Moon, LLC (2015-02-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD STYLE = new TLD("style", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-01-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.tcinet.ru<br>
     *
     */
    public static final @NotNull TLD SU = new TLD("su", Type.COUNTRY_CODE, "Russian Institute for Development of Public Networks (ROSNIIROS)", LocalDate.parse("1990-09-19"), LocalDate.parse("2024-07-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.voxpopregistry.com">http://www.voxpopregistry.com</a><br><b>WHOIS Server:</b> whois.nic.sucks<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150224-sucks">Delegation of the .sucks domain to Vox Populi Registry Inc. (2015-02-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SUCKS = new TLD("sucks", Type.GENERIC, "Vox Populi Registry Ltd.", LocalDate.parse("2015-02-19"), LocalDate.parse("2023-12-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.supplies<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140214-supplies">Delegation of the .supplies domain to Atomic Fields, LLC (2014-02-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SUPPLIES = new TLD("supplies", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.supply<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140214-supply">Delegation of the .supply domain to Half Falls, LLC (2014-02-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SUPPLY = new TLD("supply", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.support<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-support">Delegation of the .SUPPORT domain to Grand Orchard, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SUPPORT = new TLD("support", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.surf/">http://nic.surf/</a><br><b>WHOIS Server:</b> whois.nic.surf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140611-surf">Delegation of the .surf domain to Top Level Domain Holdings Limited (2014-06-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-surf">Transfer of the .surf domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SURF = new TLD("surf", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.surgery<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-surgery">Delegation of the .surgery domain to Tin Avenue, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SURGERY = new TLD("surgery", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.suzuki<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140630-suzuki">Delegation of the .suzuki domain to Suzuki Motor Corporation (2014-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SUZUKI = new TLD("suzuki", Type.GENERIC, "SUZUKI MOTOR CORPORATION", LocalDate.parse("2014-06-26"), LocalDate.parse("2019-08-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.svnet.sv">http://www.svnet.sv</a><br><br>
     *
     */
    public static final @NotNull TLD SV = new TLD("sv", Type.COUNTRY_CODE, "SVNet", LocalDate.parse("1994-11-04"), LocalDate.parse("2023-09-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.swatchgroup.com/">http://www.swatchgroup.com/</a><br><b>WHOIS Server:</b> whois.nic.swatch<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150624-swatch">Delegation of the .swatch domain to The Swatch Group Ltd (2015-06-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SWATCH = new TLD("swatch", Type.GENERIC, "The Swatch Group Ltd", LocalDate.parse("2015-04-23"), LocalDate.parse("2022-08-15"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-swiftcover">Delegation of the .swiftcover domain to Swiftcover Insurance Services Limited (2016-07-15)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211006-swiftcover">Revocation of the .swiftcover domain (2021-10-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SWIFTCOVER = new TLD("swiftcover", Type.GENERIC, null, LocalDate.parse("2016-06-09"), LocalDate.parse("2021-10-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.swiss">http://www.nic.swiss</a><br><b>WHOIS Server:</b> whois.nic.swiss<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150423-swiss">Delegation of the .swiss domain to Swiss Confederation (2015-04-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SWISS = new TLD("swiss", Type.GENERIC, "Swiss Confederation", LocalDate.parse("2015-04-16"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://registry.sx">http://registry.sx</a><br><b>WHOIS Server:</b> whois.sx<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/sx-report-20111114.html">Report on the Delegation of the .SX domain representing Sint Maarten (Dutch part) to SX Registry SA B.V. (2011-11-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SX = new TLD("sx", Type.COUNTRY_CODE, "SX Registry SA B.V.", LocalDate.parse("2010-12-20"), LocalDate.parse("2024-03-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://tld.sy">http://tld.sy</a><br><b>WHOIS Server:</b> whois.tld.sy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/sy-report-07jan2011.html">Redelegation of the .SY domain representing the Syrian Arab Republic to the National Agency for Network Services (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SY = new TLD("sy", Type.COUNTRY_CODE, "National Agency for Network Services (NANS)", LocalDate.parse("1996-02-20"), LocalDate.parse("2023-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://iconic.sydney">http://iconic.sydney</a><br><b>WHOIS Server:</b> whois.nic.sydney<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141031-sydney">Delegation of the .sydney domain to State of New South Wales, Department of Premier and Cabinet (2014-10-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SYDNEY = new TLD("sydney", Type.GENERIC, "State of New South Wales, Department of Premier and Cabinet", LocalDate.parse("2014-10-30"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-symantec">Delegation of the .symantec domain to Symantec Corporation (2015-12-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200717-symantec">Revocation of the .symantec domain (2020-07-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SYMANTEC = new TLD("symantec", Type.GENERIC, null, LocalDate.parse("2015-09-24"), LocalDate.parse("2020-07-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.systems<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-systems">Delegation of the .SYSTEMS domain to Dash Cypress, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD SYSTEMS = new TLD("systems", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sispa.org.sz/">http://www.sispa.org.sz/</a><br><br>
     *
     */
    public static final @NotNull TLD SZ = new TLD("sz", Type.COUNTRY_CODE, "University of Swaziland Department of Computer Science", LocalDate.parse("1993-07-19"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.tab">http://nic.tab</a><br><b>WHOIS Server:</b> whois.nic.tab<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151111-tab">Delegation of the .tab domain to Tabcorp Holdings Limited (2015-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TAB = new TLD("tab", Type.GENERIC, "Tabcorp Holdings Limited", LocalDate.parse("2015-09-04"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nictaipei.net">http://www.nictaipei.net</a><br><b>WHOIS Server:</b> whois.nic.taipei<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141021-taipei">Delegation of the .taipei domain to Taipei City Government (2014-10-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TAIPEI = new TLD("taipei", Type.GENERIC, "Taipei City Government", LocalDate.parse("2014-09-25"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.talk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160323-talk">Delegation of the .talk domain to Amazon Registry Services, Inc. (2016-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TALK = new TLD("talk", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.alibabagroup.com">http://www.alibabagroup.com</a><br><b>WHOIS Server:</b> whois.nic.taobao<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160119-taobao">Delegation of the .taobao domain to Alibaba Group Holding Limited (2016-01-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TAOBAO = new TLD("taobao", Type.GENERIC, "Alibaba Group Holding Limited", LocalDate.parse("2016-01-08"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.target.com">http://www.target.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160802-target">Delegation of the .target domain to Target Domain Holdings, LLC (2016-08-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TARGET = new TLD("target", Type.GENERIC, "Target Domain Holdings, LLC", LocalDate.parse("2016-07-21"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.tatamotors<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150722-tatamotors">Delegation of the .tatamotors domain to Tata Motors Ltd (2015-07-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TATAMOTORS = new TLD("tatamotors", Type.GENERIC, "Tata Motors Ltd", LocalDate.parse("2015-07-16"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dottatar.ru">http://www.dottatar.ru</a><br><b>WHOIS Server:</b> whois.nic.tatar<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140804-tatar">Delegation of the .tatar domain to Limited Liability Company "Coordination Center of Regional Domain of Tatarstan Republic" (2014-08-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TATAR = new TLD("tatar", Type.GENERIC, "Limited Liability Company \"Coordination Center of Regional Domain of Tatarstan Republic\"", LocalDate.parse("2014-07-03"), LocalDate.parse("2024-01-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.tattoo/">http://nic.tattoo/</a><br><b>WHOIS Server:</b> whois.nic.tattoo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-tattoo">Delegation of the .TATTOO domain to Uniregistry, Corp. (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20220810-tattoo">Transfer of the .tattoo domain to Top Level Design, LLC (2022-08-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230626-tattoo">Transfer of the .tattoo domain to Registry Services, LLC (2023-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TATTOO = new TLD("tattoo", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tax<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-tax">Delegation of the .tax domain to Storm Orchard, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TAX = new TLD("tax", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.taxi<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150504-taxi">Delegation of the .taxi domain to Pine Falls, LLC (2015-05-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TAXI = new TLD("taxi", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="HTTPS://WWW.NIC.TC">HTTPS://WWW.NIC.TC</a><br><b>WHOIS Server:</b> whois.nic.tc<br>
     *
     */
    public static final @NotNull TLD TC = new TLD("tc", Type.COUNTRY_CODE, "Melrex TC", LocalDate.parse("1997-01-27"), LocalDate.parse("2021-02-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://icann.org/ebero">https://icann.org/ebero</a><br><b>WHOIS Server:</b> whois.nic.tci<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151201-tci">Delegation of the .tci domain to Asia Green IT System Bilgisayar San. ve Tic. Ltd. Sti. (2015-12-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TCI = new TLD("tci", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2015-11-25"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.td">http://www.nic.td</a><br><b>WHOIS Server:</b> whois.nic.td<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2018/td-report-20180227.html">Transfer of the .TD (Chad) top-level domain to l'Agence de Développement des Technologies de l'Information et de la Communication (ADETIC) (2018-02-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TD = new TLD("td", Type.COUNTRY_CODE, "l'Agence de Développement des Technologies de l'Information et de la Communication (ADETIC)", LocalDate.parse("1997-11-03"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.tdk">http://nic.tdk</a><br><b>WHOIS Server:</b> whois.nic.tdk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160602-tdk">Delegation of the .tdk domain to TDK Corporation (2016-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TDK = new TLD("tdk", Type.GENERIC, "TDK Corporation", LocalDate.parse("2016-03-10"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.team<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150414-team">Delegation of the .team domain to Atomic Lake, LLC (2015-04-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TEAM = new TLD("team", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-09"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.tech<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-tech">Delegation of the .tech domain to Dot Tech LLC (2015-03-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210201-tech">Transfer of the .tech domain to Personals TLD Inc. (2021-02-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210826-tech">Transfer of the .tech domain to Radix FZC (2021-08-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-tech">Transfer of the .tech domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TECH = new TLD("tech", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2015-03-12"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.technology<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131111-technology">Delegation of the .TECHNOLOGY domain to Auburn Falls (2013-11-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TECHNOLOGY = new TLD("technology", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-08"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .TEL through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://www.nic.tel">http://www.nic.tel</a><br><b>WHOIS Server:</b> whois.nic.tel<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/tel-report-22jan2007.html">IANA Report on the Delegation of the .TEL Top-Level Domain (2007-01-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170503-tel">Transfer of the .tel domain to Telnames Ltd (2017-05-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TEL = new TLD("tel", Type.SPONSORED, "Telnames Ltd.", LocalDate.parse("2007-03-01"), LocalDate.parse("2024-03-18"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160223-telecity">Delegation of the .telecity domain to TelecityGroup International Limited (2016-02-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180810-telecity">Revocation of the .telecity domain (2018-08-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TELECITY = new TLD("telecity", Type.GENERIC, null, LocalDate.parse("2016-02-11"), LocalDate.parse("2018-08-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150624-telefonica">Delegation of the .telefonica domain to Telefónica S.A. (2015-06-24)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191224-telefonica">Revocation of the .telefonica domain (2019-12-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TELEFONICA = new TLD("telefonica", Type.GENERIC, null, LocalDate.parse("2015-06-18"), LocalDate.parse("2019-12-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tennis<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150203-tennis">Delegation of the .tennis domain to Cotton Bloom, LLC (2015-02-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TENNIS = new TLD("tennis", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-01-29"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.teva">http://nic.teva</a><br><b>WHOIS Server:</b> whois.nic.teva<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160412-teva">Delegation of the .teva domain to Teva Pharmaceutical Industries Limited (2016-04-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TEVA = new TLD("teva", Type.GENERIC, "Teva Pharmaceutical Industries Limited", LocalDate.parse("2016-02-19"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tf">http://www.nic.tf</a><br><b>WHOIS Server:</b> whois.nic.tf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/tf-report-05aug2005.pdf">IANA Report on the Redelegation of the .TF Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TF = new TLD("tf", Type.COUNTRY_CODE, "Association Française pour le Nommage Internet en Coopération (A.F.N.I.C.)", LocalDate.parse("1997-08-26"), LocalDate.parse("2024-03-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tg">http://www.nic.tg</a><br><b>WHOIS Server:</b> whois.nic.tg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2016/tg-report-20160118.html">Redelegation of the .TG domain representing Togo to the Autorite de Reglementation des Secteurs de Postes et de Telecommunications (ARTP) (2016-01-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TG = new TLD("tg", Type.COUNTRY_CODE, "Autorité de Régulation des Communications Electroniques et des Postes (ARCEP)", LocalDate.parse("1996-09-05"), LocalDate.parse("2021-03-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.thnic.co.th">http://www.thnic.co.th</a><br><b>WHOIS Server:</b> whois.thnic.co.th<br>
     *
     */
    public static final @NotNull TLD TH = new TLD("th", Type.COUNTRY_CODE, "Thai Network Information Center Foundation", LocalDate.parse("1988-09-07"), LocalDate.parse("2024-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.homedepot.com">http://www.homedepot.com</a><br><b>WHOIS Server:</b> whois.nic.thd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150521-thd">Delegation of the .thd domain to Homer TLC, Inc. (2015-05-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170221-thd">Transfer of the .thd domain to Home Depot Product Authority, LLC (2017-02-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD THD = new TLD("thd", Type.GENERIC, "Home Depot Product Authority, LLC", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.theater<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150501-theater">Delegation of the .theater domain to Blue Tigers, LLC (2015-05-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD THEATER = new TLD("theater", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-04-30"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.theatre/">https://nic.theatre/</a><br><b>WHOIS Server:</b> whois.nic.theatre<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-theatre">Delegation of the .theatre domain to XYZ.COM LLC (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD THEATRE = new TLD("theatre", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-08-13"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://tiaa.org">http://tiaa.org</a><br><b>WHOIS Server:</b> whois.nic.tiaa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160715-tiaa">Delegation of the .tiaa domain to Teachers Insurance and Annuity Association of America (2016-07-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TIAA = new TLD("tiaa", Type.GENERIC, "Teachers Insurance and Annuity Association of America", LocalDate.parse("2016-03-24"), LocalDate.parse("2024-03-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://tickets.tickets">http://tickets.tickets</a><br><b>WHOIS Server:</b> whois.nic.tickets<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-tickets">Delegation of the .tickets domain to Accent Media Limited (2015-03-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210528-tickets">Transfer of the .tickets domain to XYZ.COM LLC (2021-05-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TICKETS = new TLD("tickets", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2015-03-19"), LocalDate.parse("2024-04-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tienda<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-tienda">Delegation of the .TIENDA domain to Victor Manor, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TIENDA = new TLD("tienda", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160119-tiffany">Delegation of the .tiffany domain to Tiffany and Company (2016-01-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230725-tiffany">Revocation of the .tiffany domain (2023-07-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TIFFANY = new TLD("tiffany", Type.GENERIC, null, LocalDate.parse("2015-12-17"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tips<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131118-tips">Delegation of the .TIPS domain to Corn Willow, LLC (2013-11-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TIPS = new TLD("tips", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tires<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141212-tires">Delegation of the .tires domain to Dog Edge, LLC (2014-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TIRES = new TLD("tires", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-12-11"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tirol">http://www.nic.tirol</a><br><b>WHOIS Server:</b> whois.nic.tirol<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140602-tirol">Delegation of the .tirol domain to punkt Tirol GmbH (2014-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TIROL = new TLD("tirol", Type.GENERIC, "punkt Tirol GmbH", LocalDate.parse("2014-05-29"), LocalDate.parse("2021-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tj">http://www.nic.tj</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2003/tj-report-30jun03.html">IANA Report on Redelegation of the .TJ Top-Level Domain (2003-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TJ = new TLD("tj", Type.COUNTRY_CODE, "Information Technology Center", LocalDate.parse("1997-12-11"), LocalDate.parse("2020-03-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-tjmaxx">Delegation of the .tjmaxx domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TJMAXX = new TLD("tjmaxx", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-tjx">Delegation of the .tjx domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TJX = new TLD("tjx", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.tk">http://www.dot.tk</a><br><b>WHOIS Server:</b> whois.dot.tk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2006/tk-report-31jan2006.pdf">IANA Report on the Redelegation of the .TK Top-Level Domain (2006-01-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TK = new TLD("tk", Type.COUNTRY_CODE, "Telecommunication Tokelau Corporation (Teletok)", LocalDate.parse("1997-11-07"), LocalDate.parse("2019-02-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-tkmaxx">Delegation of the .tkmaxx domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TKMAXX = new TLD("tkmaxx", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tl">http://www.nic.tl</a><br><b>WHOIS Server:</b> whois.nic.tl<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/tl-report-05aug05.pdf">IANA Report on the Delegation of the .TL Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TL = new TLD("tl", Type.COUNTRY_CODE, "Autoridade Nacional de Comunicações", LocalDate.parse("2005-03-23"), LocalDate.parse("2022-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tm/">http://www.nic.tm/</a><br><b>WHOIS Server:</b> whois.nic.tm<br>
     *
     */
    public static final @NotNull TLD TM = new TLD("tm", Type.COUNTRY_CODE, "TM Domain Registry Ltd", LocalDate.parse("1997-05-30"), LocalDate.parse("2020-02-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.alibabagroup.com">http://www.alibabagroup.com</a><br><b>WHOIS Server:</b> whois.nic.tmall<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160119-tmall">Delegation of the .tmall domain to Alibaba Group Holding Limited (2016-01-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TMALL = new TLD("tmall", Type.GENERIC, "Alibaba Group Holding Limited", LocalDate.parse("2016-01-08"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://whois.ati.tn">http://whois.ati.tn</a><br><b>WHOIS Server:</b> whois.ati.tn<br>
     *
     */
    public static final @NotNull TLD TN = new TLD("tn", Type.COUNTRY_CODE, "Agence Tunisienne d'Internet", LocalDate.parse("1991-05-17"), LocalDate.parse("2023-12-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tonic.to/">http://www.tonic.to/</a><br><b>WHOIS Server:</b> whois.tonic.to<br>
     *
     */
    public static final @NotNull TLD TO = new TLD("to", Type.COUNTRY_CODE, "Government of the Kingdom of Tonga H.R.H. Crown Prince Tupouto'a c/o Consulate of Tonga", LocalDate.parse("1995-12-18"), LocalDate.parse("2023-08-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.today<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131116-today">Delegation of the .TODAY domain to Pearl Woods, LLC (2013-11-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TODAY = new TLD("today", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-13"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.tokyo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140124-tokyo">Delegation of the .TOKYO domain to GMO Registry, Inc. (2014-01-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOKYO = new TLD("tokyo", Type.GENERIC, "GMO Registry, Inc.", LocalDate.parse("2014-01-16"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tools<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-tools">Delegation of the .TOOLS domain to Pioneer North, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOOLS = new TLD("tools", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.top">http://www.nic.top</a><br><b>WHOIS Server:</b> whois.nic.top<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140801-top">Delegation of the .top domain to Jiangsu Bangning Science and Technology Co.,Ltd. (2014-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOP = new TLD("top", Type.GENERIC, ".TOP Registry", LocalDate.parse("2014-07-24"), LocalDate.parse("2024-04-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.toray.com">http://www.toray.com</a><br><b>WHOIS Server:</b> whois.nic.toray<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150428-toray">Delegation of the .toray domain to Toray Industries, Inc. (2015-04-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TORAY = new TLD("toray", Type.GENERIC, "Toray Industries, Inc.", LocalDate.parse("2015-04-16"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.toshiba">http://nic.toshiba</a><br><b>WHOIS Server:</b> whois.nic.toshiba<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150202-toshiba">Delegation of the .toshiba domain to Toshiba Corporation (2015-02-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOSHIBA = new TLD("toshiba", Type.GENERIC, "TOSHIBA Corporation", LocalDate.parse("2014-10-09"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.total">http://www.nic.total</a><br><b>WHOIS Server:</b> whois.nic.total<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160225-total">Delegation of the .total domain to Total SA (2016-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOTAL = new TLD("total", Type.GENERIC, "TotalEnergies SE", LocalDate.parse("2016-02-05"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.tours<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150318-tours">Delegation of the .tours domain to Sugar Station, LLC (2015-03-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOURS = new TLD("tours", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.town<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-town">Delegation of the .town domain to Koko Moon, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOWN = new TLD("town", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://toyota.jp/">http://toyota.jp/</a><br><b>WHOIS Server:</b> whois.nic.toyota<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150724-toyota">Delegation of the .toyota domain to Toyota Motor Corporation (2015-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOYOTA = new TLD("toyota", Type.GENERIC, "TOYOTA MOTOR CORPORATION", LocalDate.parse("2015-07-16"), LocalDate.parse("2023-06-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.toys<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-toys">Delegation of the .toys domain to Pioneer Orchard, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TOYS = new TLD("toys", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/tp-report-20150126.html">Removal of the .TP top-level domain representing Portuguese Timor (2015-01-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TP = new TLD("tp", Type.COUNTRY_CODE, null, LocalDate.parse("1997-05-09"), LocalDate.parse("2015-03-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.trabis.gov.tr">http://www.trabis.gov.tr</a><br><b>WHOIS Server:</b> whois.trabis.gov.tr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2019/tr-report-20190417.html">Transfer of the .TR (Turkey) country-code top-level domain to Bilgi Teknolojileri ve İletişim Kurumu (BTK) (2019-04-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TR = new TLD("tr", Type.COUNTRY_CODE, "Bilgi Teknolojileri ve İletişim Kurumu (BTK)", LocalDate.parse("1990-09-17"), LocalDate.parse("2024-04-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.trade">http://nic.trade</a><br><b>WHOIS Server:</b> whois.nic.trade<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140314-trade">Delegation of the .trade domain to Elite Registry Limited (2014-03-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRADE = new TLD("trade", Type.GENERIC, "Elite Registry Limited", LocalDate.parse("2014-03-06"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.trading<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150311-trading">Delegation of the .trading domain to Dottrading Registry Ltd (2015-03-11)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210629-trading">Transfer of the .trading domain to Dog Beach, LLC (2021-06-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRADING = new TLD("trading", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2015-03-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.training<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131220-training">Delegation of the .TRAINING domain to Wild Willow, LLC (2013-12-20)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRAINING = new TLD("training", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-19"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .TRAVEL through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.travel<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/travel-report-05aug2005.pdf">IANA Report on the Delegation of the .TRAVEL Top-Level Domain (2005-08-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180222-travel">Transfer of the .travel domain to Dog Beach, LLC (2018-02-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRAVEL = new TLD("travel", Type.SPONSORED, "Dog Beach, LLC", LocalDate.parse("2005-07-27"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160622-travelchannel">Delegation of the .travelchannel domain to Lifestyle Domain Holdings, Inc. (2016-06-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230614-travelchannel">Revocation of the .travelchannel domain (2023-06-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRAVELCHANNEL = new TLD("travelchannel", Type.GENERIC, null, LocalDate.parse("2016-05-05"), LocalDate.parse("2023-06-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.travelers.com">http://www.travelers.com</a><br><b>WHOIS Server:</b> whois.nic.travelers<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-travelers">Delegation of the .travelers domain to Travelers TLD, LLC (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRAVELERS = new TLD("travelers", Type.GENERIC, "Travelers TLD, LLC", LocalDate.parse("2015-11-25"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.travelers.com">http://www.travelers.com</a><br><b>WHOIS Server:</b> whois.nic.travelersinsurance<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151208-travelersinsurance">Delegation of the .travelersinsurance domain to Travelers TLD, LLC (2015-12-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRAVELERSINSURANCE = new TLD("travelersinsurance", Type.GENERIC, "Travelers TLD, LLC", LocalDate.parse("2015-11-25"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://internetnaming.co">https://internetnaming.co</a><br><b>WHOIS Server:</b> whois.nic.trust<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141201-trust">Delegation of the .trust domain to Artemis Internet Inc (2014-12-01)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210118-trust">Transfer of the .trust domain to UNR Corp. (2021-01-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221116-trust">Transfer of the .trust domain to Internet Naming Co. (2022-11-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRUST = new TLD("trust", Type.GENERIC, "Internet Naming Co.", LocalDate.parse("2014-11-26"), LocalDate.parse("2024-05-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.travelers.com">http://www.travelers.com</a><br><b>WHOIS Server:</b> whois.nic.trv<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151208-trv">Delegation of the .trv domain to Travelers TLD, LLC (2015-12-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TRV = new TLD("trv", Type.GENERIC, "Travelers TLD, LLC", LocalDate.parse("2015-11-25"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.tt">http://www.nic.tt</a><br><br>
     *
     */
    public static final @NotNull TLD TT = new TLD("tt", Type.COUNTRY_CODE, "University of the West Indies Faculty of Engineering", LocalDate.parse("1991-09-03"), LocalDate.parse("2022-12-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://tubetld.com">http://tubetld.com</a><br><b>WHOIS Server:</b> whois.nic.tube<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160107-tube">Delegation of the .tube domain to Latin American Telecom LLC (2016-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TUBE = new TLD("tube", Type.GENERIC, "Latin American Telecom LLC", LocalDate.parse("2015-12-23"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tuiregistry.com">http://www.tuiregistry.com</a><br><b>WHOIS Server:</b> whois.nic.tui<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140926-tui">Delegation of the .tui domain to TUI AG (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TUI = new TLD("tui", Type.GENERIC, "TUI AG", LocalDate.parse("2014-09-18"), LocalDate.parse("2024-05-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.tunes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-tunes">Delegation of the .tunes domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TUNES = new TLD("tunes", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.tushu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151208-tushu">Delegation of the .tushu domain to Amazon Registry Services, Inc. (2015-12-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TUSHU = new TLD("tushu", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://turnon.tv/">https://turnon.tv/</a><br><b>WHOIS Server:</b> whois.nic.tv<br>
     *
     */
    public static final @NotNull TLD TV = new TLD("tv", Type.COUNTRY_CODE, "Ministry of Justice, Communications and Foreign Affairs", LocalDate.parse("1996-03-18"), LocalDate.parse("2024-05-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tvs.in">http://www.tvs.in</a><br><b>WHOIS Server:</b> whois.nic.tvs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160209-tvs">Delegation of the .tvs domain to T V Sundram Iyengar and Sons Private Limited (2016-02-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TVS = new TLD("tvs", Type.GENERIC, "T V SUNDRAM IYENGAR and SONS PRIVATE LIMITED", LocalDate.parse("2016-02-05"), LocalDate.parse("2020-10-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://rs.twnic.net.tw">http://rs.twnic.net.tw</a><br><b>WHOIS Server:</b> whois.twnic.net.tw<br>
     *
     */
    public static final @NotNull TLD TW = new TLD("tw", Type.COUNTRY_CODE, "Taiwan Network Information Center (TWNIC)", LocalDate.parse("1989-07-31"), LocalDate.parse("2024-02-23"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://karibu.tz/">https://karibu.tz/</a><br><b>WHOIS Server:</b> whois.tznic.or.tz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/tz-report-01apr2010.html">Report on the Redelegation of the .TZ Top-Level Domain to "Tanzania Network Information Centre Ltd." (2010-04-01)</a></li>
     *   <li><a href="https://iana.org/reports/2019/tz-report-20191014.html">Transfer of the .TZ (United Republic of Tanzania) domain to the Tanzania Communications Regulatory Authority (2019-10-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD TZ = new TLD("tz", Type.COUNTRY_CODE, "Tanzania Communications Regulatory Authority", LocalDate.parse("1995-07-14"), LocalDate.parse("2023-07-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.hostmaster.ua/">https://www.hostmaster.ua/</a><br><b>WHOIS Server:</b> whois.ua<br>
     *
     */
    public static final @NotNull TLD UA = new TLD("ua", Type.COUNTRY_CODE, "Hostmaster Ltd.", LocalDate.parse("1992-12-01"), LocalDate.parse("2024-03-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ubank.com.au/">http://www.ubank.com.au/</a><br><b>WHOIS Server:</b> whois.nic.ubank<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160815-ubank">Delegation of the .ubank domain to National Australia Bank Limited (2016-08-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UBANK = new TLD("ubank", Type.GENERIC, "National Australia Bank Limited", LocalDate.parse("2016-08-04"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.ubs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150709-ubs">Delegation of the .ubs domain to UBS AG (2015-07-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UBS = new TLD("ubs", Type.GENERIC, "UBS AG", LocalDate.parse("2015-06-11"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160725-uconnect">Delegation of the .uconnect domain to FCA US Llc (2016-07-25)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191120-uconnect">Revocation of the .uconnect domain (2019-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UCONNECT = new TLD("uconnect", Type.GENERIC, null, LocalDate.parse("2016-07-07"), LocalDate.parse("2019-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.co.ug">http://www.registry.co.ug</a><br><b>WHOIS Server:</b> whois.co.ug<br>
     *
     */
    public static final @NotNull TLD UG = new TLD("ug", Type.COUNTRY_CODE, "Uganda Online Ltd.", LocalDate.parse("1995-03-08"), LocalDate.parse("2012-10-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.uk/">http://www.nic.uk/</a><br><b>WHOIS Server:</b> whois.nic.uk<br>
     *
     */
    public static final @NotNull TLD UK = new TLD("uk", Type.COUNTRY_CODE, "Nominet UK", LocalDate.parse("1985-07-24"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/um-report-10jan2007.html">Report on Revocation of the .UM Top-Level Domain (2007-01-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UM = new TLD("um", Type.COUNTRY_CODE, null, LocalDate.parse("2008-04-21"), LocalDate.parse("2008-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.chinaunicom.cn">http://www.chinaunicom.cn</a><br><b>WHOIS Server:</b> whois.nic.unicom<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160201-unicom">Delegation of the .unicom domain to China United Network Communications Corporation Limited (2016-02-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UNICOM = new TLD("unicom", Type.GENERIC, "China United Network Communications Corporation Limited", LocalDate.parse("2016-01-22"), LocalDate.parse("2024-03-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.university<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140409-university">Delegation of the .university domain to Little Station, LLC (2014-04-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UNIVERSITY = new TLD("university", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-03"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.uno<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131126-uno">Delegation of the .UNO domain to Dot Latin LLC (2013-11-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191023-uno">Transfer of the .uno domain to DotSite Inc. (2019-10-23)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210826-uno">Transfer of the .uno domain to Radix FZC (2021-08-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-uno">Transfer of the .uno domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UNO = new TLD("uno", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2013-11-13"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.uol/">https://nic.uol/</a><br><b>WHOIS Server:</b> whois.gtlds.nic.br<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140814-uol">Delegation of the .uol domain to UBN Internet Ltda. (2014-08-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UOL = new TLD("uol", Type.GENERIC, "UBN INTERNET LTDA.", LocalDate.parse("2014-08-07"), LocalDate.parse("2023-11-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ups.com">http://www.ups.com</a><br><b>WHOIS Server:</b> whois.nic.ups<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160526-ups">Delegation of the .ups domain to UPS Market Driver, Inc. (2016-05-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UPS = new TLD("ups", Type.GENERIC, "UPS Market Driver, Inc.", LocalDate.parse("2016-04-14"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.us">http://www.nic.us</a><br><b>WHOIS Server:</b> whois.nic.us<br>
     *
     */
    public static final @NotNull TLD US = new TLD("us", Type.COUNTRY_CODE, "Registry Services, LLC", LocalDate.parse("1985-02-15"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.org.uy/">http://www.nic.org.uy/</a><br><b>WHOIS Server:</b> whois.nic.org.uy<br>
     *
     */
    public static final @NotNull TLD UY = new TLD("uy", Type.COUNTRY_CODE, "SeCIU - Universidad de la Republica", LocalDate.parse("1990-09-10"), LocalDate.parse("2022-12-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cctld.uz/">http://www.cctld.uz/</a><br><b>WHOIS Server:</b> whois.cctld.uz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2003/uz-report-10apr03.html">IANA Report on Redelegation of the .UZ Top-Level Domain (2003-04-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD UZ = new TLD("uz", Type.COUNTRY_CODE, "Single Integrator for Creation and Support of State Information Systems UZINFOCOM", LocalDate.parse("1995-04-29"), LocalDate.parse("2024-02-14"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD VA = new TLD("va", Type.COUNTRY_CODE, "Holy See - Vatican City State", LocalDate.parse("1995-09-11"), LocalDate.parse("2024-01-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.vacations<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140214-vacations">Delegation of the .vacations domain to Atomic Tigers, LLC (2014-02-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VACATIONS = new TLD("vacations", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.vana">https://nic.vana</a><br><b>WHOIS Server:</b> whois.nic.vana<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151106-vana">Delegation of the .vana domain to Lifestyle Domain Holdings, Inc. (2015-11-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240109-vana">Transfer of the .vana domain to Internet Naming Co. (2024-01-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240716-vana">Transfer of the .vana domain to D3 Registry LLC (2024-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VANA = new TLD("vana", Type.GENERIC, "D3 Registry LLC", LocalDate.parse("2015-09-24"), LocalDate.parse("2024-07-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://vanguard.com">http://vanguard.com</a><br><b>WHOIS Server:</b> whois.nic.vanguard<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160824-vanguard">Delegation of the .vanguard domain to The Vanguard Group, Inc. (2016-08-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VANGUARD = new TLD("vanguard", Type.GENERIC, "The Vanguard Group, Inc.", LocalDate.parse("2016-08-18"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.identitydigital.services<br>
     *
     */
    public static final @NotNull TLD VC = new TLD("vc", Type.COUNTRY_CODE, "Ministry of Telecommunications, Science, Technology and Industry", LocalDate.parse("1991-09-03"), LocalDate.parse("2024-07-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.ve/">https://nic.ve/</a><br><b>WHOIS Server:</b> whois.nic.ve<br>
     *
     */
    public static final @NotNull TLD VE = new TLD("ve", Type.COUNTRY_CODE, "Comisión Nacional de Telecomunicaciones (CONATEL)", LocalDate.parse("1991-03-07"), LocalDate.parse("2024-05-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.vegas">http://www.nic.vegas</a><br><b>WHOIS Server:</b> whois.nic.vegas<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-vegas">Delegation of the .vegas domain to Dot Vegas, Inc. (2014-03-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VEGAS = new TLD("vegas", Type.GENERIC, "Dot Vegas, Inc.", LocalDate.parse("2014-03-20"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.ventures<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-ventures">Delegation of the .VENTURES domain to Binky Lake, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VENTURES = new TLD("ventures", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisign.com/">http://www.verisign.com/</a><br><b>WHOIS Server:</b> whois.nic.verisign<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151120-verisign">Delegation of the .verisign domain to VeriSign, Inc. (2015-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VERISIGN = new TLD("verisign", Type.GENERIC, "VeriSign, Inc.", LocalDate.parse("2015-11-06"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.versicherung">http://nic.versicherung</a><br><b>WHOIS Server:</b> whois.nic.versicherung<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140521-versicherung">Delegation of the .versicherung domain to dotversicherung-registry GmbH (2014-05-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170306-versicherung">Transfer of the .versicherung domain to TLD-BOX Registrydienstleistungen GmbH (2017-03-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VERSICHERUNG = new TLD("versicherung", Type.GENERIC, "tldbox GmbH", LocalDate.parse("2014-05-15"), LocalDate.parse("2022-11-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.vet<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140528-vet">Delegation of the .vet domain to United TLD Holdco, Ltd (2014-05-28)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-vet">Transfer of the .vet domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VET = new TLD("vet", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-05-22"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.vg">http://nic.vg</a><br><b>WHOIS Server:</b> whois.nic.vg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2014/vg-report-20140203.html">Redelegation of the .vg domain representing the British Virgin Islands to the Telecommunications Regulatory Commission (2014-02-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VG = new TLD("vg", Type.COUNTRY_CODE, "Telecommunications Regulatory Commission of the Virgin Islands", LocalDate.parse("1997-02-20"), LocalDate.parse("2021-12-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://secure.nic.vi">https://secure.nic.vi</a><br><b>WHOIS Server:</b> virgil.nic.vi<br>
     *
     */
    public static final @NotNull TLD VI = new TLD("vi", Type.COUNTRY_CODE, "Virgin Islands Public Telecommunications System, Inc.", LocalDate.parse("1995-08-31"), LocalDate.parse("2024-02-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.viajes<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131212-viajes">Delegation of the .VIAJES domain to Black Madison, LLC (2013-12-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIAJES = new TLD("viajes", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-12-12"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.video<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141222-video">Delegation of the .video domain to United TLD Holdco, Ltd (2014-12-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210602-video">Transfer of the .video domain to Dog Beach, LLC (2021-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIDEO = new TLD("video", Type.GENERIC, "Dog Beach, LLC", LocalDate.parse("2014-12-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.vig.com">http://www.vig.com</a><br><b>WHOIS Server:</b> whois.nic.vig<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160401-vig">Delegation of the .vig domain to Vienna Insurance Group AG Wiener Versicherung Gruppe (2016-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIG = new TLD("vig", Type.GENERIC, "VIENNA INSURANCE GROUP AG Wiener Versicherung Gruppe", LocalDate.parse("2015-11-25"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.vikingrivercruises.com/">http://www.vikingrivercruises.com/</a><br><b>WHOIS Server:</b> whois.nic.viking<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-viking">Delegation of the .viking domain to Viking River Cruises (Bermuda) Ltd. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIKING = new TLD("viking", Type.GENERIC, "Viking River Cruises (Bermuda) Ltd.", LocalDate.parse("2015-10-15"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.villas<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-villas">Delegation of the .villas domain to New Sky, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VILLAS = new TLD("villas", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.vin<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150730-vin">Delegation of the .vin domain to Holly Shadow, LLC (2015-07-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIN = new TLD("vin", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-07-23"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.vip/">http://nic.vip/</a><br><b>WHOIS Server:</b> whois.nic.vip<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151117-vip">Delegation of the .vip domain to Minds + Machines Group Limited (2015-11-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-vip">Transfer of the .vip domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIP = new TLD("vip", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2015-07-30"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.virgin.com">http://www.virgin.com</a><br><b>WHOIS Server:</b> whois.nic.virgin<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151005-virgin">Delegation of the .virgin domain to Virgin Enterprises Limited (2015-10-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIRGIN = new TLD("virgin", Type.GENERIC, "Virgin Enterprises Limited", LocalDate.parse("2015-09-10"), LocalDate.parse("2021-10-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://visa.com">http://visa.com</a><br><b>WHOIS Server:</b> whois.nic.visa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160722-visa">Delegation of the .visa domain to Visa Worldwide Pte. Limited (2016-07-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VISA = new TLD("visa", Type.GENERIC, "Visa Worldwide Pte. Limited", LocalDate.parse("2016-06-30"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.vision<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140207-vision">Delegation of the .vision domain to Koko Station, LLC (2014-02-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VISION = new TLD("vision", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150616-vista">Delegation of the .vista domain to Vistaprint Limited (2015-06-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180912-vista">Revocation of the .vista domain (2018-09-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VISTA = new TLD("vista", Type.GENERIC, null, LocalDate.parse("2015-06-04"), LocalDate.parse("2018-09-13"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150616-vistaprint">Delegation of the .vistaprint domain to Vistaprint Limited (2015-06-16)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200314-vistaprint">Revocation of the .vistaprint domain (2020-03-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VISTAPRINT = new TLD("vistaprint", Type.GENERIC, null, LocalDate.parse("2015-06-04"), LocalDate.parse("2020-03-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stc.com.sa">http://www.stc.com.sa</a><br><b>WHOIS Server:</b> whois.nic.viva<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150826-viva">Delegation of the .viva domain to Saudi Telecom Company (2015-08-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIVA = new TLD("viva", Type.GENERIC, "Saudi Telecom Company", LocalDate.parse("2015-08-20"), LocalDate.parse("2024-05-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.vivo.com.br">http://www.vivo.com.br</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160712-vivo">Delegation of the .vivo domain to Telefonica Brasil S.A. (2016-07-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VIVO = new TLD("vivo", Type.GENERIC, "Telefonica Brasil S.A.", LocalDate.parse("2016-05-26"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.vlaanderen">http://www.nic.vlaanderen</a><br><b>WHOIS Server:</b> whois.nic.vlaanderen<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140612-vlaanderen">Delegation of the .vlaanderen domain to DNS.be vzw (2014-06-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VLAANDEREN = new TLD("vlaanderen", Type.GENERIC, "DNS.be vzw", LocalDate.parse("2014-06-05"), LocalDate.parse("2021-02-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.vnnic.vn/">https://www.vnnic.vn/</a><br><br>
     *
     */
    public static final @NotNull TLD VN = new TLD("vn", Type.COUNTRY_CODE, "Viet Nam Internet Network Information Center (VNNIC)", LocalDate.parse("1994-04-14"), LocalDate.parse("2023-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.vodka/">http://nic.vodka/</a><br><b>WHOIS Server:</b> whois.nic.vodka<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-vodka">Delegation of the .vodka domain to Top Level Domain Holdings Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-vodka">Transfer of the .vodka domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VODKA = new TLD("vodka", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-28"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160106-volkswagen">Delegation of the .volkswagen domain to Volkswagen Group of America Inc. (2016-01-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231120-volkswagen">Revocation of the .volkswagen domain (2023-11-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VOLKSWAGEN = new TLD("volkswagen", Type.GENERIC, null, LocalDate.parse("2015-12-23"), LocalDate.parse("2023-11-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.volvo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161024-volvo">Delegation of the .volvo domain to Volvo Holding Sverige Aktiebolag (2016-10-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VOLVO = new TLD("volvo", Type.GENERIC, "Volvo Holding Sverige Aktiebolag", LocalDate.parse("2016-01-29"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.vote<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-vote">Delegation of the .vote domain to Monolith Registry LLC (2014-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VOTE = new TLD("vote", Type.GENERIC, "Monolith Registry LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.voting">http://www.nic.voting</a><br><b>WHOIS Server:</b> whois.nic.voting<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140124-voting">Delegation of the .VOTING domain to Valuetainment Corp. (2014-01-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VOTING = new TLD("voting", Type.GENERIC, "Valuetainment Corp.", LocalDate.parse("2014-01-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.voto<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140225-voto">Delegation of the .voto domain to Monolith Registry LLC (2014-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VOTO = new TLD("voto", Type.GENERIC, "Monolith Registry LLC", LocalDate.parse("2014-02-13"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.voyage<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131104-voyage">Delegation of the .VOYAGE domain to Ruby House, LLC (2013-11-04)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VOYAGE = new TLD("voyage", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-31"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hello.vu">http://www.hello.vu</a><br><b>WHOIS Server:</b> whois.dnrs.vu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2019/vu-report-20190225.html">Transfer of the .vu domain to Telecommunications Radiocommunications and Broadcasting Regulator (TRBR) (2019-02-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VU = new TLD("vu", Type.COUNTRY_CODE, "Telecommunications Radiocommunications and Broadcasting Regulator (TRBR)", LocalDate.parse("1995-04-10"), LocalDate.parse("2024-05-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160229-vuelos">Delegation of the .vuelos domain to Travel Reservations SRL (2016-02-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230707-vuelos">Revocation of the .vuelos domain (2023-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VUELOS = new TLD("vuelos", Type.GENERIC, null, LocalDate.parse("2016-02-19"), LocalDate.parse("2023-07-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://ourhomeonline.wales/">https://ourhomeonline.wales/</a><br><b>WHOIS Server:</b> whois.nic.wales<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140805-wales">Delegation of the .wales domain to Nominet UK (2014-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WALES = new TLD("wales", Type.GENERIC, "Nominet UK", LocalDate.parse("2014-07-31"), LocalDate.parse("2022-08-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.walmart.com">http://www.walmart.com</a><br><b>WHOIS Server:</b> whois.nic.walmart<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160816-walmart">Delegation of the .walmart domain to Wal-Mart Stores, Inc. (2016-08-16)</a></li>
     * </ul>
     */
    public static final @NotNull TLD WALMART = new TLD("walmart", Type.GENERIC, "Wal-Mart Stores, Inc.", LocalDate.parse("2016-07-21"), LocalDate.parse("2023-09-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.walter">http://nic.walter</a><br><b>WHOIS Server:</b> whois.nic.walter<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150522-walter">Delegation of the .walter domain to Sandvik AB (2015-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WALTER = new TLD("walter", Type.GENERIC, "Sandvik AB", LocalDate.parse("2015-05-14"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.wang">http://www.nic.wang</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131231-wang">Delegation of the .WANG domain to Zodiac Leo Limited (2013-12-31)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140616-wang">Redelegation of the .wang domain to Zodiac Registry Limited (2014-06-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WANG = new TLD("wang", Type.GENERIC, "Zodiac Wang Limited", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-04-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.wanggou<br>
     *
     */
    public static final @NotNull TLD WANGGOU = new TLD("wanggou", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-warman">Delegation of the .warman domain to Weir Group IP Limited (2016-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20191119-warman">Revocation of the .warman domain (2019-11-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WARMAN = new TLD("warman", Type.GENERIC, null, LocalDate.parse("2016-04-14"), LocalDate.parse("2019-11-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.watch<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-watch">Delegation of the .WATCH domain to Sand Shadow, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WATCH = new TLD("watch", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.watches<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151207-watches">Delegation of the .watches domain to Richemont DNS Inc. (2015-12-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210308-watches">Transfer of the .watches domain to Afilias Limited (2021-03-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WATCHES = new TLD("watches", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2015-11-12"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.weather.com">http://www.weather.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160107-weather">Delegation of the .weather domain to The Weather Channel, LLC (2016-01-07)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170728-weather">Transfer of the .weather domain to International Business Machines (2017-07-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEATHER = new TLD("weather", Type.GENERIC, "International Business Machines Corporation", LocalDate.parse("2015-12-23"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.weather.com">http://www.weather.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160126-weatherchannel">Delegation of the .weatherchannel domain to The Weather Channel, LLC (2016-01-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170728-weatherchannel">Transfer of the .weatherchannel domain to International Business Machines (2017-07-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEATHERCHANNEL = new TLD("weatherchannel", Type.GENERIC, "International Business Machines Corporation", LocalDate.parse("2016-01-22"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.webcam">http://nic.webcam</a><br><b>WHOIS Server:</b> whois.nic.webcam<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140314-webcam">Delegation of the .webcam domain to dot Webcam Limited (2014-03-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEBCAM = new TLD("webcam", Type.GENERIC, "dot Webcam Limited", LocalDate.parse("2014-03-06"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.weber<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151218-weber">Delegation of the .weber domain to Saint-Gobain Weber SA (2015-12-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEBER = new TLD("weber", Type.GENERIC, "Saint-Gobain Weber SA", LocalDate.parse("2015-12-10"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://radix.website/">https://radix.website/</a><br><b>WHOIS Server:</b> whois.nic.website<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140527-website">Delegation of the .website domain to DotWebsite Inc. (2014-05-27)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210826-website">Transfer of the .website domain to Radix FZC (2021-08-26)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-website">Transfer of the .website domain to Radix Technologies Inc. (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEBSITE = new TLD("website", Type.GENERIC, "Radix Technologies Inc.", LocalDate.parse("2014-05-22"), LocalDate.parse("2024-03-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.icann.org/resources/pages/ebero-2013-04-02-en">https://www.icann.org/resources/pages/ebero-2013-04-02-en</a><br><b>WHOIS Server:</b> whois.nic.wed<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-wed">Delegation of the .WED domain to Atgron, Inc. (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20171208-wed">Transfer of the .wed domain to emergency back-end registry operations (2017-12-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WED = new TLD("wed", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-07-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.wedding/">http://nic.wedding/</a><br><b>WHOIS Server:</b> whois.nic.wedding<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141009-wedding">Delegation of the .wedding domain to Top Level Domain Holdings Limited (2014-10-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-wedding">Transfer of the .wedding domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEDDING = new TLD("wedding", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-07-10"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sina.com">http://www.sina.com</a><br><b>WHOIS Server:</b> whois.nic.weibo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160325-weibo">Delegation of the .weibo domain to Sina Corporation (2016-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEIBO = new TLD("weibo", Type.GENERIC, "Sina Corporation", LocalDate.parse("2016-02-29"), LocalDate.parse("2023-08-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://weir.co.uk/">http://weir.co.uk/</a><br><b>WHOIS Server:</b> whois.nic.weir<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150413-weir">Delegation of the .weir domain to Weir Group IP Limited (2015-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WEIR = new TLD("weir", Type.GENERIC, "Weir Group IP Limited", LocalDate.parse("2015-04-02"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.wf">http://www.nic.wf</a><br><b>WHOIS Server:</b> whois.nic.wf<br>
     *
     */
    public static final @NotNull TLD WF = new TLD("wf", Type.COUNTRY_CODE, "Association Française pour le Nommage Internet en Coopération (A.F.N.I.C.)", LocalDate.parse("1997-11-14"), LocalDate.parse("2024-03-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.whoswho">http://nic.whoswho</a><br><b>WHOIS Server:</b> whois.nic.whoswho<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140716-whoswho">Delegation of the .whoswho domain to Who's Who Registry (2014-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WHOSWHO = new TLD("whoswho", Type.GENERIC, "Who's Who Registry", LocalDate.parse("2014-06-12"), LocalDate.parse("2023-06-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.nic.wien">https://www.nic.wien</a><br><b>WHOIS Server:</b> whois.nic.wien<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131227-wien">Delegation of the .WIEN domain to punkt.wien GmbH (2013-12-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WIEN = new TLD("wien", Type.GENERIC, "punkt.wien GmbH", LocalDate.parse("2013-12-19"), LocalDate.parse("2021-03-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.wiki">http://nic.wiki</a><br><b>WHOIS Server:</b> whois.nic.wiki<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140213-wiki">Delegation of the .wiki domain to Top Level Design, LLC (2014-02-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20230626-wiki">Transfer of the .wiki domain to Registry Services, LLC (2023-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WIKI = new TLD("wiki", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.williamhill.com/">http://www.williamhill.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140724-williamhill">Delegation of the .williamhill domain to William Hill Organization Limited (2014-07-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WILLIAMHILL = new TLD("williamhill", Type.GENERIC, "William Hill Organization Limited", LocalDate.parse("2014-07-03"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.win">http://nic.win</a><br><b>WHOIS Server:</b> whois.nic.win<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150323-win">Delegation of the .win domain to First Registry Limited (2015-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WIN = new TLD("win", Type.GENERIC, "First Registry Limited", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.microsoft.com">http://www.microsoft.com</a><br><b>WHOIS Server:</b> whois.nic.windows<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150604-windows">Delegation of the .windows domain to Microsoft Corporation (2015-06-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WINDOWS = new TLD("windows", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.wine<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150730-wine">Delegation of the .wine domain to June Station, LLC (2015-07-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WINE = new TLD("wine", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2015-07-23"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.tjx.com">http://www.tjx.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160707-winners">Delegation of the .winners domain to The TJX Companies, Inc. (2016-07-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WINNERS = new TLD("winners", Type.GENERIC, "The TJX Companies, Inc.", LocalDate.parse("2016-04-07"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.wmeentertainment.com">http://www.wmeentertainment.com</a><br><b>WHOIS Server:</b> whois.nic.wme<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140905-wme">Delegation of the .wme domain to William Morris Endeavor Entertainment, LLC (2014-09-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WME = new TLD("wme", Type.GENERIC, "William Morris Endeavor Entertainment, LLC", LocalDate.parse("2014-08-22"), LocalDate.parse("2023-09-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://wolterskluwer.com">http://wolterskluwer.com</a><br><b>WHOIS Server:</b> whois.nic.wolterskluwer<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160209-wolterskluwer">Delegation of the .wolterskluwer domain to Wolters Kluwer N.V. (2016-02-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WOLTERSKLUWER = new TLD("wolterskluwer", Type.GENERIC, "Wolters Kluwer N.V.", LocalDate.parse("2016-02-05"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.woodside.com.au/">http://www.woodside.com.au/</a><br><b>WHOIS Server:</b> whois.nic.woodside<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160620-woodside">Delegation of the .woodside domain to Woodside Petroleum Limited (2016-06-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WOODSIDE = new TLD("woodside", Type.GENERIC, "Woodside Petroleum Limited", LocalDate.parse("2016-01-08"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.work/">http://nic.work/</a><br><b>WHOIS Server:</b> whois.nic.work<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140919-work">Delegation of the .work domain to Top Level Domain Holdings Limited (2014-09-19)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-work">Transfer of the .work domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WORK = new TLD("work", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-08-18"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.works<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-works">Delegation of the .WORKS domain to Little Dynamite, LLC (2014-01-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WORKS = new TLD("works", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-16"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.world<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140912-world">Delegation of the .world domain to Bitter Fields, LLC (2014-09-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WORLD = new TLD("world", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-09-11"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.wow<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20160921-wow">Delegation of the .wow domain to Amazon Registry Services, Inc. (2016-09-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WOW = new TLD("wow", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-09-16"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.website.ws">http://www.website.ws</a><br><b>WHOIS Server:</b> whois.website.ws<br>
     *
     */
    public static final @NotNull TLD WS = new TLD("ws", Type.COUNTRY_CODE, "Government of Samoa Ministry of Foreign Affairs and Trade", LocalDate.parse("1995-07-14"), LocalDate.parse("2021-05-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.wtc/">http://nic.wtc/</a><br><b>WHOIS Server:</b> whois.nic.wtc<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140424-wtc">Delegation of the .wtc domain to World Trade Centers Association, Inc. (2014-04-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WTC = new TLD("wtc", Type.GENERIC, "World Trade Centers Association, Inc.", LocalDate.parse("2014-04-17"), LocalDate.parse("2024-03-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.wtf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140421-wtf">Delegation of the .wtf domain to Hidden Way, LLC (2014-04-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD WTF = new TLD("wtf", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-04-17"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.microsoft.com">http://www.microsoft.com</a><br><b>WHOIS Server:</b> whois.nic.xbox<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150602-xbox">Delegation of the .xbox domain to Microsoft Corporation (2015-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XBOX = new TLD("xbox", Type.GENERIC, "Microsoft Corporation", LocalDate.parse("2015-04-30"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.xerox<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150414-xerox">Delegation of the .xerox domain to Xerox DNHC LLC (2015-04-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XEROX = new TLD("xerox", Type.GENERIC, "Xerox DNHC LLC", LocalDate.parse("2015-04-02"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160705-xfinity">Delegation of the .xfinity domain to Comcast IP Holdings I, LLC (2016-07-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240207-xfinity">Revocation of the .xfinity domain (2024-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XFINITY = new TLD("xfinity", Type.GENERIC, null, LocalDate.parse("2016-06-16"), LocalDate.parse("2024-02-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.teleinfo.cn">http://www.teleinfo.cn</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160325-xihuan">Delegation of the .xihuan domain to QIHOO 360 Technology Co. Ltd. (2016-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XIHUAN = new TLD("xihuan", Type.GENERIC, "QIHOO 360 TECHNOLOGY CO. LTD.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dotxin.org">http://www.dotxin.org</a><br><b>WHOIS Server:</b> whois.nic.xin<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150302-xin">Delegation of the .xin domain to Elegant Leader Limited (2015-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XIN = new TLD("xin", Type.GENERIC, "Elegant Leader Limited", LocalDate.parse("2015-02-19"), LocalDate.parse("2023-08-14"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 测试 = new TLD("测试", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--11b4c3d<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--11b4c3d">Delegation of the .कॉम domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD कॉम = new TLD("कॉम", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD परीक्षा = new TLD("परीक्षा", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-xn--1ck2e1b">Delegation of the .セール domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD セール = new TLD("セール", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.yu-wei.cn/">http://www.yu-wei.cn/</a><br><b>WHOIS Server:</b> whois.ngtld.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140811-xn--1qqw23a">Delegation of the .佛山 domain to Guangzhou YU Wei Information Technology Co., Ltd. (2014-08-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 佛山 = new TLD("佛山", Type.GENERIC, "Guangzhou YU Wei Information Technology Co., Ltd.", LocalDate.parse("2014-07-17"), LocalDate.parse("2022-04-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ಭಾರತ = new TLD("ಭಾರತ", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-04-18"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.newgtld.cn">http://www.newgtld.cn</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150325-xn--30rr7y">Delegation of the .慈善 domain to Excellent First Limited (2015-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 慈善 = new TLD("慈善", Type.GENERIC, "Excellent First Limited", LocalDate.parse("2015-03-12"), LocalDate.parse("2024-03-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.newgtld.cn">http://www.newgtld.cn</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131231-xn--3bst00m">Delegation of the .集团 domain to Eagle Horizon Limited (2013-12-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 集团 = new TLD("集团", Type.GENERIC, "Eagle Horizon Limited", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-03-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://internetregistry.info/">http://internetregistry.info/</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131227-xn--3ds443g">Delegation of the .在线 domain to TLD Registry Limited (2013-12-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 在线 = new TLD("在线", Type.GENERIC, "TLD REGISTRY LIMITED", LocalDate.parse("2013-12-19"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.or.kr">http://www.nic.or.kr</a><br><b>WHOIS Server:</b> whois.kr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/hanguk-report-07jan2011.html">Delegation of the .한국 ("Hanguk") domain representing the Republic of Korea in Korean (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 한국 = new TLD("한국", Type.COUNTRY_CODE, "KISA (Korea Internet and Security Agency)", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ଭାରତ = new TLD("ଭାରତ", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-04-18"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160812-xn--3oq18vl8pn36a">Delegation of the .大众汽车 domain to Volkswagen (China) Investment Co., Ltd. (2016-08-12)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211027-xn--3oq18vl8pn36a">Revocation of the .大众汽车 domain (2021-10-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 大众汽车 = new TLD("大众汽车", Type.GENERIC, null, LocalDate.parse("2016-07-21"), LocalDate.parse("2021-10-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--3pxu8k<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--3pxu8k">Delegation of the .点看 domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 点看 = new TLD("点看", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--42c2d9a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--42c2d9a">Delegation of the .คอม domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD คอม = new TLD("คอม", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ভাৰত = new TLD("ভাৰত", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-04-18"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ভারত = new TLD("ভারত", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.bagua.wang">http://www.bagua.wang</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141117-xn--45q11c">Delegation of the .八卦 domain to Zodiac Scorpio Limited (2014-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 八卦 = new TLD("八卦", Type.GENERIC, "Zodiac Gemini Ltd", LocalDate.parse("2014-11-06"), LocalDate.parse("2024-03-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://en.isoc.org.il/il-cctld/accredited-registrars/domain-name-registrars">http://en.isoc.org.il/il-cctld/accredited-registrars/domain-name-registrars</a><br><b>WHOIS Server:</b> whois.isoc.org.il<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2021/israel-report-20210114.html">Delegation of the ישראל. (“Israel”) domain representing Israel in Hebrew script to the Israel Internet Association (2021-01-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eישראל‎ = new TLD(".ישראל‎", Type.COUNTRY_CODE, "The Israel Internet Association (RA)", LocalDate.parse("2020-06-16"), LocalDate.parse("2024-05-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dotmawqe.com">http://www.dotmawqe.com</a><br><b>WHOIS Server:</b> whois.nic.xn--4gbrim<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140522-xn--4gbrim">Delegation of the .موقع domain to Suhub Electronic Establishment (2014-05-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20211124-xn--4gbrim">Transfer of the .موقع domain to Helium TLDs Ltd (2021-11-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eموقع‎ = new TLD(".موقع‎", Type.GENERIC, "Helium TLDs Ltd", LocalDate.parse("2014-05-15"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://domainreg.btcl.com.bd/">http://domainreg.btcl.com.bd/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2016/bangla-report-20160902.html">Delegation of the .বাংলা (“bangla”) domain representing Bangladesh in Bengali script to the Ministry of Posts, Telecommunications and Information Technology, Posts and Telecommunications Division (2016-09-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD বাংলা = new TLD("বাংলা", Type.COUNTRY_CODE, "Posts and Telecommunications Division", LocalDate.parse("2011-03-30"), LocalDate.parse("2024-05-29"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.conac.cn">http://www.conac.cn</a><br><b>WHOIS Server:</b> whois.conac.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131213-xn--55qw42g">Delegation of the .公益 domain to China Organizational Name Administration Center (2013-12-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 公益 = new TLD("公益", Type.GENERIC, "China Organizational Name Administration Center", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cnnic.cn">http://www.cnnic.cn</a><br><b>WHOIS Server:</b> whois.ngtld.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-xn--55qx5d">Delegation of the .公司 domain to Computer Network Information Center of Chinese Academy of Sciences (2014-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 公司 = new TLD("公司", Type.GENERIC, "China Internet Network Information Center (CNNIC)", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-10-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.shangri-la.com/">http://www.shangri-la.com/</a><br><b>WHOIS Server:</b> whois.nic.xn--5su34j936bgsg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160630-xn--5su34j936bgsg">Delegation of the .香格里拉 domain to Shangri-La International Hotel Management Limited (2016-06-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 香格里拉 = new TLD("香格里拉", Type.GENERIC, "Shangri-La International Hotel Management Limited", LocalDate.parse("2016-06-24"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://donuts.domains/">https://donuts.domains/</a><br><b>WHOIS Server:</b> whois.nic.xn--5tzm5g<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160413-xn--5tzm5g">Delegation of the .网站 domain to Global Website TLD Asia Limited (2016-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 网站 = new TLD("网站", Type.GENERIC, "Global Website TLD Asia Limited", LocalDate.parse("2015-12-04"), LocalDate.parse("2023-01-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.xn--6frz82g<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140129-xn--6frz82g">Delegation of the .移动 domain to Afilias Limited (2014-01-29)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 移动 = new TLD("移动", Type.GENERIC, "Identity Digital Limited", LocalDate.parse("2014-01-23"), LocalDate.parse("2023-09-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.newgtld.cn">http://www.newgtld.cn</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131231-xn--6qq986b3xl">Delegation of the .我爱你 domain to Tycoon Treasure Limited (2013-12-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 我爱你 = new TLD("我爱你", Type.GENERIC, "Tycoon Treasure Limited", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-03-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.faitid.org">http://www.faitid.org</a><br><b>WHOIS Server:</b> whois.nic.xn--80adxhks<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140418-xn--80adxhks">Delegation of the .москва domain to Foundation for Assistance for Internet Technologies and Infrastructure Development (2014-04-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD МОСКВА = new TLD("москва", Type.GENERIC, "Foundation for Assistance for Internet Technologies and Infrastructure Development (FAITID)", LocalDate.parse("2014-04-17"), LocalDate.parse("2019-08-30"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ИСПЫТАНИЕ = new TLD("испытание", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.kz/">http://www.nic.kz/</a><br><b>WHOIS Server:</b> whois.nic.kz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/kaz-report-20120109.html">Report on the Delegation of .қаз ("kaz") representing Kazakhstan in Cyrillic (2012-01-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ҚАЗ = new TLD("қаз", Type.COUNTRY_CODE, "Association of IT Companies of Kazakhstan", LocalDate.parse("2011-09-15"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pccs.va">http://www.pccs.va</a><br><b>WHOIS Server:</b> whois.nic.xn--80aqecdr1a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161130-xn--80aqecdr1a">Delegation of the .католик domain to Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication) (2016-11-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD КАТОЛИК = new TLD("католик", Type.GENERIC, "Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication)", LocalDate.parse("2016-11-16"), LocalDate.parse("2023-12-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://corenic.org">http://corenic.org</a><br><b>WHOIS Server:</b> whois.nic.xn--80asehdb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131022-xn--80asehdb">Delegation of the .онлайн domain to CORE Association (2013-10-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ОНЛАЙН = new TLD("онлайн", Type.GENERIC, "CORE Association", LocalDate.parse("2013-10-21"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://corenic.org">http://corenic.org</a><br><b>WHOIS Server:</b> whois.nic.xn--80aswg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131022-xn--80aswg">Delegation of the .сайт domain to CORE Association (2013-10-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD САЙТ = new TLD("сайт", Type.GENERIC, "CORE Association", LocalDate.parse("2013-10-21"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.chinaunicom.cn">http://www.chinaunicom.cn</a><br><b>WHOIS Server:</b> whois.nic.xn--8y0a063a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160203-xn--8y0a063a">Delegation of the .联通 domain to China United Network Communications Corporation Limited (2016-02-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 联通 = new TLD("联通", Type.GENERIC, "China United Network Communications Corporation Limited", LocalDate.parse("2015-12-10"), LocalDate.parse("2024-03-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.rnids.rs/">http://www.rnids.rs/</a><br><b>WHOIS Server:</b> whois.rnids.rs<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/serbia-report-20110401.html">Report on the Delegation of the .срб (“srb”) domain representing Serbia in Cyrillic (2011-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD СРБ = new TLD("срб", Type.COUNTRY_CODE, "Serbian National Internet Domain Registry (RNIDS)", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.imena.bg">http://www.imena.bg</a><br><b>WHOIS Server:</b> whois.imena.bg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2016/bg-report-20160610.html">Delegation of the .бг (“bg”) domain representing Bulgaria in Bulgarian language to Imena.BG AD (2016-06-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD БГ = new TLD("бг", Type.COUNTRY_CODE, "Imena.BG AD", LocalDate.parse("2016-03-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://cctld.by">https://cctld.by</a><br><b>WHOIS Server:</b> whois.cctld.by<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/bel-report-20150127.html">Delegation of the .бел domain to Reliable Software Inc. (2015-01-27)</a></li>
     *   <li><a href="https://iana.org/reports/2022/belarus-report-20220527.html">Transfer of the .BY top-level domain and the .бел (“bel”) top-level domain representing Belarus to Belarusian Cloud Technologies LLC (2022-05-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD БЕЛ = new TLD("бел", Type.COUNTRY_CODE, "Belarusian Cloud Technologies LLC", LocalDate.parse("2014-09-29"), LocalDate.parse("2023-10-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--9dbq2a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--9dbq2a">Delegation of the .קום domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eקום‎ = new TLD(".קום‎", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.newgtld.cn">http://www.newgtld.cn</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150325-xn--9et52u">Delegation of the .时尚 domain to Rise Victory Limited (2015-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 时尚 = new TLD("时尚", Type.GENERIC, "RISE VICTORY LIMITED", LocalDate.parse("2015-03-12"), LocalDate.parse("2024-03-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sina.com">http://www.sina.com</a><br><b>WHOIS Server:</b> whois.nic.xn--9krt00a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160328-xn--9krt00a">Delegation of the .微博 domain to Sina Corporation (2016-03-28)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 微博 = new TLD("微博", Type.GENERIC, "Sina Corporation", LocalDate.parse("2016-02-29"), LocalDate.parse("2023-08-08"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 테스트 = new TLD("테스트", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.temasek.com.sg/nicdan4ma3xi1">http://www.temasek.com.sg/nicdan4ma3xi1</a><br><b>WHOIS Server:</b> whois.nic.xn--b4w605ferd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-xn--b4w605ferd">Delegation of the .淡马锡 domain to Temasek Holdings (Private) Limited (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 淡马锡 = new TLD("淡马锡", Type.GENERIC, "Temasek Holdings (Private) Limited", LocalDate.parse("2014-12-18"), LocalDate.parse("2023-08-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-xn--bck1b9a5dre4c">Delegation of the .ファッション domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ファッション = new TLD("ファッション", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.xn--c1avg">http://nic.xn--c1avg</a><br><b>WHOIS Server:</b> whois.nic.xn--c1avg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140304-xn--c1avg">Delegation of the .орг domain to Public Interest Registry (2014-03-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ОРГ = new TLD("орг", Type.GENERIC, "Public Interest Registry", LocalDate.parse("2014-02-27"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--c2br7g<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--c2br7g">Delegation of the .नेट domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD नेट = new TLD("नेट", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-xn--cck2b3b">Delegation of the .ストア domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ストア = new TLD("ストア", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.xn--cckwcxetd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200602-xn--cckwcxetd">Delegation of the .アマゾン domain to Amazon Registry Services, Inc. (2020-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD アマゾン = new TLD("アマゾン", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2020-05-28"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://samsungregistry.com">http://samsungregistry.com</a><br><b>WHOIS Server:</b> whois.kr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140217-xn--cg4bki">Delegation of the .삼성 domain to SAMSUNG SDS CO., LTD (2014-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 삼성 = new TLD("삼성", Type.GENERIC, "SAMSUNG SDS CO., LTD", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-01-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sgnic.sg/">http://www.sgnic.sg/</a><br><b>WHOIS Server:</b> whois.ta.sgnic.sg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/singapore-report-07jan2011.html">Delegation of the .新加坡 ("Singapore") domain, and the .சிங்கப்பூர் ("Singapore") domain, representing Singapore in Chinese and Tamil (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD சிங்கப்பூர் = new TLD("சிங்கப்பூர்", Type.COUNTRY_CODE, "Singapore Network Information Centre (SGNIC) Pte Ltd", LocalDate.parse("2011-02-05"), LocalDate.parse("2024-03-28"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140515-xn--czr694b">Delegation of the .商标 domain to Huyi Global Information Resources (Holding) Company Hong Kong Limited (2014-05-15)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 商标 = new TLD("商标", Type.GENERIC, "Internet DotTrademark Organisation Limited", LocalDate.parse("2014-05-08"), LocalDate.parse("2024-02-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.xn--czrs0t<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141203-xn--czrs0t">Delegation of the .商店 domain to Wild Island, LLC (2014-12-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 商店 = new TLD("商店", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-11-20"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.shangcheng.wang">http://www.shangcheng.wang</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140327-xn--czru2d">Delegation of the .商城 domain to Zodiac Capricorn Limited (2014-03-27)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140723-xn--czru2d">Redelegation of the .商城 domain to Zodiac Aquarius Limited (2014-07-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 商城 = new TLD("商城", Type.GENERIC, "Zodiac Aquarius Limited", LocalDate.parse("2014-02-27"), LocalDate.parse("2024-03-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dotdeti.ru">http://www.dotdeti.ru</a><br><b>WHOIS Server:</b> whois.nic.xn--d1acj3b<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140224-xn--d1acj3b">Delegation of the .дети domain to The Foundation for Network Initiatives “The Smart Internet” (2014-02-24)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ДЕТИ = new TLD("дети", Type.GENERIC, "The Foundation for Network Initiatives “The Smart Internet”", LocalDate.parse("2014-02-13"), LocalDate.parse("2024-07-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://marnet.mk/">http://marnet.mk/</a><br><b>WHOIS Server:</b> whois.marnet.mk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2014/mk-report-20140926.html">Redelegation of the .MK domain and Delegation of the .мкд domain representing the Former Yugoslav Republic of Macedonia to Macedonian Academic Research Network Skopje (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD МКД = new TLD("мкд", Type.COUNTRY_CODE, "Macedonian Academic Research Network Skopje", LocalDate.parse("2014-04-21"), LocalDate.parse("2024-02-21"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eטעסט‎ = new TLD(".טעסט‎", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.eu">http://www.registry.eu</a><br><b>WHOIS Server:</b> whois.eu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2016/eu-report-20160118.html">Delegation of the ею (“eu”) domain representing the European Union in Cyrillic script to EURid vzw/asbl (2016-01-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ЕЮ = new TLD("ею", Type.COUNTRY_CODE, "EURid vzw", LocalDate.parse("2015-12-08"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151208-xn--eckvdtc9d">Delegation of the .ポイント domain to Amazon Registry Services, Inc. (2015-12-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ポイント = new TLD("ポイント", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-25"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gd.xinhua.org">http://www.gd.xinhua.org</a><br><b>WHOIS Server:</b> whois.nic.xn--efvy88h<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150817-xn--efvy88h">Delegation of the .新闻 domain to Xinhua News Agency Guangdong Branch 新华通讯社广东分社 (2015-08-17)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180326-xn--efvy88h">Transfer of the .新闻 domain to Guangzhou YU Wei Information Technology Co., Ltd (2018-03-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 新闻 = new TLD("新闻", Type.GENERIC, "Guangzhou YU Wei Information and Technology Co.,Ltd", LocalDate.parse("2015-08-06"), LocalDate.parse("2024-04-16"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150505-xn--estv75g">Delegation of the .工行 domain to Industrial and Commercial Bank of China Limited (2015-05-05)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200401-xn--estv75g">Revocation of the .工行 domain (2020-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 工行 = new TLD("工行", Type.GENERIC, null, LocalDate.parse("2015-04-23"), LocalDate.parse("2020-04-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160323-xn--fct429k">Delegation of the .家電 domain to Amazon Registry Services, Inc. (2016-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 家電 = new TLD("家電", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--fhbei<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--fhbei">Delegation of the .كوم domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eكوم‎ = new TLD(".كوم‎", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://internetregistry.info/">http://internetregistry.info/</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131227-xn--fiq228c5hs">Delegation of the .中文网 domain to TLD Registry Limited (2013-12-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 中文网 = new TLD("中文网", Type.GENERIC, "TLD REGISTRY LIMITED", LocalDate.parse("2013-12-19"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140117-xn--fiq64b">Delegation of the .中信 domain to CITIC Group Corporation (2014-01-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 中信 = new TLD("中信", Type.GENERIC, "CITIC Group Corporation", LocalDate.parse("2014-01-09"), LocalDate.parse("2024-03-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cnnic.cn">http://www.cnnic.cn</a><br><b>WHOIS Server:</b> cwhois.cnnic.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/zhongguo-report-07jun2010.html">Report on the Delegation of the .中国 and .中國 (“Zhongguo”) domains representing China in Chinese to China Internet Network Information Center (2010-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 中国 = new TLD("中国", Type.COUNTRY_CODE, "China Internet Network Information Center (CNNIC)", LocalDate.parse("2010-07-09"), LocalDate.parse("2023-09-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cnnic.cn">http://www.cnnic.cn</a><br><b>WHOIS Server:</b> cwhois.cnnic.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/zhongguo-report-07jun2010.html">Report on the Delegation of the .中国 and .中國 (“Zhongguo”) domains representing China in Chinese to China Internet Network Information Center (2010-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 中國 = new TLD("中國", Type.COUNTRY_CODE, "China Internet Network Information Center (CNNIC)", LocalDate.parse("2010-07-09"), LocalDate.parse("2023-06-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.xn--fjq720a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150506-xn--fjq720a">Delegation of the .娱乐 domain to Will Bloom, LLC (2015-05-06)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 娱乐 = new TLD("娱乐", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-07-17"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141113-xn--flw351e">Delegation of the .谷歌 domain to Charleston Road Registry Inc. (2014-11-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 谷歌 = new TLD("谷歌", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-10-09"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD భారత్ = new TLD("భారత్", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.lk">http://www.nic.lk</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/lanka-report-16jul2010.html">Report on the Delegation of the .ලංකා (“Lanka”) domain representing Sri Lanka in Sinhalese, and இலங்கை (“Ilangai”) domain representing Sri Lanka in Tamil, to L K Domain Registry (2010-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ලංකා = new TLD("ලංකා", Type.COUNTRY_CODE, "LK Domain Registry", LocalDate.parse("2010-08-19"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dot.asia/namesphere/">http://www.dot.asia/namesphere/</a><br><b>WHOIS Server:</b> whois.nic.xn--fzys8d69uvgm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160510-xn--fzys8d69uvgm">Delegation of the .電訊盈科 domain to PCCW Enterprises Limited (2016-05-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 電訊盈科 = new TLD("電訊盈科", Type.GENERIC, "PCCW Enterprises Limited", LocalDate.parse("2016-04-15"), LocalDate.parse("2023-08-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nicgouwu.com/">http://nicgouwu.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160113-xn--g2xx48c">Delegation of the .购物 domain to Minds + Machines Group Limited (2016-01-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20201111-xn--g2xx48c">Transfer of the .xn--g2xx48c domain to Nawang Heli(Xiamen) Network Service Co., LTD. (2020-11-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 购物 = new TLD("购物", Type.GENERIC, "Nawang Heli(Xiamen) Network Service Co., LTD.", LocalDate.parse("2016-01-08"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 測試 = new TLD("測試", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-xn--gckr3f0f">Delegation of the .クラウド domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD クラウド = new TLD("クラウド", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ભારત = new TLD("ભારત", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com/">http://www.amazonregistry.com/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20160921-xn--gk3at1e">Delegation of the .通販 domain to Amazon Registry Services, Inc. (2016-09-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 通販 = new TLD("通販", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-09-16"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD भारतम् = new TLD("भारतम्", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-08-15"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD भारत = new TLD("भारत", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD भारोत = new TLD("भारोत", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-08-15"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eآزمایشی‎ = new TLD(".آزمایشی‎", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD பரிட்சை = new TLD("பரிட்சை", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.wangdian.wang">http://www.wangdian.wang</a><br><b>WHOIS Server:</b> whois.gtld.knet.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141126-xn--hxt814e">Delegation of the .网店 domain to Zodiac Libra Limited (2014-11-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 网店 = new TLD("网店", Type.GENERIC, "Zodiac Taurus Ltd.", LocalDate.parse("2014-11-20"), LocalDate.parse("2024-03-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.xn--i1b6b1a6a2e">http://nic.xn--i1b6b1a6a2e</a><br><b>WHOIS Server:</b> whois.nic.xn--i1b6b1a6a2e<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140304-xn--i1b6b1a6a2e">Delegation of the .संगठन domain to Public Interest Registry (2014-03-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD संगठन = new TLD("संगठन", Type.GENERIC, "Public Interest Registry", LocalDate.parse("2014-02-27"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150522-xn--imr513n">Delegation of the .餐厅 domain to Hu Yi Global Information Resources (Holding) Company. Hong Kong Limited (2015-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 餐厅 = new TLD("餐厅", Type.GENERIC, "Internet DotTrademark Organisation Limited", LocalDate.parse("2015-05-08"), LocalDate.parse("2024-02-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cnnic.cn">http://www.cnnic.cn</a><br><b>WHOIS Server:</b> whois.ngtld.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140116-xn--io0a7i">Delegation of the .网络 domain to Computer Network Information Center of Chinese Academy of Sciences (2014-01-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 网络 = new TLD("网络", Type.GENERIC, "China Internet Network Information Center (CNNIC)", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-10-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--j1aef<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--j1aef">Delegation of the .ком domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD КОМ = new TLD("ком", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://namestore.u-registry.com">https://namestore.u-registry.com</a><br><b>WHOIS Server:</b> whois.dotukr.com<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/ukr-report-20130219.html">Report on the Delegation of the .укр (“ukr”) domain representing Ukraine in Cyrillic (2013-02-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD УКР = new TLD("укр", Type.COUNTRY_CODE, "Ukrainian Network Information Centre (UANIC), Inc.", LocalDate.parse("2011-03-01"), LocalDate.parse("2024-02-27"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.hkirc.hk">http://www.hkirc.hk</a><br><b>WHOIS Server:</b> whois.hkirc.hk<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/hongkong-report-07jun2010.html">Report on the Delegation of the .香港 (“Hong Kong”) domain representing Hong Kong in Chinese to Hong Kong Internet Registration Corporation Limited (2010-06-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 香港 = new TLD("香港", Type.COUNTRY_CODE, "Hong Kong Internet Registration Corporation Ltd.", LocalDate.parse("2010-07-12"), LocalDate.parse("2024-04-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.xn--jlq480n2rg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200602-xn--jlq480n2rg">Delegation of the .亚马逊 domain to Amazon Registry Services, Inc. (2020-06-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 亚马逊 = new TLD("亚马逊", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2020-05-28"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20221206-xn--jlq61u9w7b">Revocation of the .诺基亚 domain (2022-12-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 诺基亚 = new TLD("诺基亚", Type.GENERIC, null, LocalDate.parse("2015-04-23"), LocalDate.parse("2022-12-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-xn--jvr189m">Delegation of the .食品 domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 食品 = new TLD("食品", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ΔΟΚΙΜΉ = new TLD("δοκιμή", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.xn--kcrx77d1x4a">http://nic.xn--kcrx77d1x4a</a><br><b>WHOIS Server:</b> whois.nic.xn--kcrx77d1x4a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150403-xn--kcrx77d1x4a">Delegation of the .飞利浦 domain to Koninklijke Philips N.V. (2015-04-03)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 飞利浦 = new TLD("飞利浦", Type.GENERIC, "Koninklijke Philips N.V.", LocalDate.parse("2015-02-13"), LocalDate.parse("2023-11-30"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eإختبار‎ = new TLD(".إختبار‎", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://rs.twnic.net.tw">http://rs.twnic.net.tw</a><br><b>WHOIS Server:</b> whois.twnic.net.tw<br>
     *
     */
    public static final @NotNull TLD 台湾 = new TLD("台湾", Type.COUNTRY_CODE, "Taiwan Network Information Center (TWNIC)", LocalDate.parse("2010-07-14"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://rs.twnic.net.tw">http://rs.twnic.net.tw</a><br><b>WHOIS Server:</b> whois.twnic.net.tw<br>
     *
     */
    public static final @NotNull TLD 台灣 = new TLD("台灣", Type.COUNTRY_CODE, "Taiwan Network Information Center (TWNIC)", LocalDate.parse("2010-07-14"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151207-xn--kpu716f">Delegation of the .手表 domain to Richemont DNS Inc. (2015-12-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200626-xn--kpu716f">Revocation of the .手表 domain (2020-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 手表 = new TLD("手表", Type.GENERIC, null, LocalDate.parse("2015-11-12"), LocalDate.parse("2020-06-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.rntd.cn">http://www.rntd.cn</a><br><b>WHOIS Server:</b> whois.nic.xn--kput3i<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140613-xn--kput3i">Delegation of the .手机 domain to Beijing RITT-Net Technology Development Co., Ltd (2014-06-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 手机 = new TLD("手机", Type.GENERIC, "Beijing RITT-Net Technology Development Co., Ltd", LocalDate.parse("2014-06-05"), LocalDate.parse("2023-01-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mon.mn/">http://www.mon.mn/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/mon-report-20130525.html">Report on the Delegation of the .мон (“mon”) domain representing Mongolia in Cyrillic to Datacom LLC (2013-05-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD МОН = new TLD("мон", Type.COUNTRY_CODE, "Datacom Co.,Ltd", LocalDate.parse("2012-06-21"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.dz">http://www.nic.dz</a><br><b>WHOIS Server:</b> whois.nic.dz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/algeria-report-20110401.html">Report on the Delegation of the .الجزائر ("al-Jazair") domain representing Algeria in Arabic (2011-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eالجزائر‎ = new TLD(".الجزائر‎", Type.COUNTRY_CODE, "CERIST", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registry.om">http://www.registry.om</a><br><b>WHOIS Server:</b> whois.registry.om<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/oman-report-20120612.html">Report on the Delegation of .عمان ("Oman") representing Oman in Arabic (2012-06-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eعمان‎ = new TLD(".عمان‎", Type.COUNTRY_CODE, "Telecommunications Regulatory Authority (TRA)", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.aramco.com">http://www.aramco.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151013-xn--mgba3a3ejt">Delegation of the .ارامكو domain to Aramco Services Company (2015-10-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eارامكو‎ = new TLD(".ارامكو‎", Type.GENERIC, "Aramco Services Company", LocalDate.parse("2015-08-20"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.ir">http://www.nic.ir</a><br><b>WHOIS Server:</b> whois.nic.ir<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2013/iran-report-20130913.html">Report on the Delegation of the ایران. (“Iran”) domain representing the Islamic Republic of Iran in Arabic (2013-09-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eایران‎ = new TLD(".ایران‎", Type.COUNTRY_CODE, "Institute for Research in Fundamental Sciences (IPM)", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-08-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.olayan.com/">http://www.olayan.com/</a><br><b>WHOIS Server:</b> whois.nic.xn--mgba7c0bbn0a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160429-xn--mgba7c0bbn0a">Delegation of the .العليان domain to Crescent Holding GmbH (2016-04-29)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240508-xn--mgba7c0bbn0a">Transfer of the .العليان domain to Competrol (Luxembourg) Sarl (2024-05-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eالعليان‎ = new TLD(".العليان‎", Type.GENERIC, "Competrol (Luxembourg) Sarl", LocalDate.parse("2016-04-21"), LocalDate.parse("2024-05-31"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170609-xn--mgbaakc7dvf">Delegation of the .اتصالات domain to Emirates Telecommunications Corporation (trading as Etisalat) (2017-06-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20231117-xn--mgbaakc7dvf">Revocation of the .اتصالات domain (2023-11-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eاتصالات‎ = new TLD(".اتصالات‎", Type.GENERIC, null, LocalDate.parse("2017-05-18"), LocalDate.parse("2023-11-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://aeda.ae/">http://aeda.ae/</a><br><b>WHOIS Server:</b> whois.aeda.net.ae<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/emarat-report-07apr2010.html">Report on the Delegation of the امارات. (“Emarat”) domain representing United Arab Emirates in Arabic to "Telecommunications Regulatory Authority" (2010-04-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eامارات‎ = new TLD(".امارات‎", Type.COUNTRY_CODE, "Telecommunications and Digital Government Regulatory Authority (TDRA)", LocalDate.parse("2010-05-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://corenic.org">http://corenic.org</a><br><b>WHOIS Server:</b> whois.nic.xn--mgbab2bd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140214-xn--mgbab2bd">Delegation of the .بازار domain to CORE Association (2014-02-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eبازار‎ = new TLD(".بازار‎", Type.GENERIC, "CORE Association", LocalDate.parse("2014-02-06"), LocalDate.parse("2022-01-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.mr">http://www.nic.mr</a><br><b>WHOIS Server:</b> whois.nic.mr<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2019/xn--mgbah1a3hjkrd-report-20190108.html">Delegation of the موريتانيا. domain to Université de Nouakchott Al Aasriya (2019-01-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eموريتانيا‎ = new TLD(".موريتانيا‎", Type.COUNTRY_CODE, "Université de Nouakchott Al Aasriya", LocalDate.parse("2017-11-28"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/pakistan-report-20170117.html">Delegation of the .پاکستان (“Pakistan”) domain representing Pakistan in Arabic Script to National Telecommunication Corporation (2017-01-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eپاکستان‎ = new TLD(".پاکستان‎", Type.COUNTRY_CODE, "National Telecommunication Corporation", LocalDate.parse("2011-02-28"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://dns.jo/">https://dns.jo/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/alordon-report-16jul2010.html">Report on the Delegation of the الاردن. (“al-Ordon”) domain representing Jordan in Arabic to the National Information Technology Center (2010-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eالاردن‎ = new TLD(".الاردن‎", Type.COUNTRY_CODE, "Ministry of Digital Economy and Entrepreneurship (MoDEE)", LocalDate.parse("2010-08-20"), LocalDate.parse("2024-05-28"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151218-xn--mgbb9fbpob">Delegation of the .موبايلي domain to GreenTech Consultancy Company W.L.L. (2015-12-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190909-xn--mgbb9fbpob">Revocation of the .موبايلي domain (2019-09-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eموبايلي‎ = new TLD(".موبايلي‎", Type.GENERIC, null, LocalDate.parse("2015-12-03"), LocalDate.parse("2019-09-09"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eبارت‎ = new TLD(".بارت‎", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-08-15"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eبھارت‎ = new TLD(".بھارت‎", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.registre.ma">http://www.registre.ma</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/morocco-report-20110401.html">Report on the Delegation of the domain المغرب. (“al-Maghrib”) representing Morocco in Arabic (2011-04-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eالمغرب‎ = new TLD(".المغرب‎", Type.COUNTRY_CODE, "Agence Nationale de Réglementation des Télécommunications (ANRT)", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.xn--mgbca7dzdo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160405-xn--mgbca7dzdo">Delegation of the .ابوظبي domain to Abu Dhabi Systems and Information Centre (2016-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eابوظبي‎ = new TLD(".ابوظبي‎", Type.GENERIC, "Abu Dhabi Systems and Information Centre", LocalDate.parse("2016-03-03"), LocalDate.parse("2019-11-26"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2020/xn--mgbcpq6gpa1a-report-20200110.html">Delegation of the البحرين. (“albahrain”) domain representing Bahrain in Arabic script to the Telecommunications Regulatory Authority (TRA) (2020-01-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eالبحرين‎ = new TLD(".البحرين‎", Type.COUNTRY_CODE, "Telecommunications Regulatory Authority (TRA)", LocalDate.parse("2019-06-13"), LocalDate.parse("2024-07-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.net.sa/">http://www.nic.net.sa/</a><br><b>WHOIS Server:</b> whois.nic.net.sa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/alsaudiah-report-07apr2010.html">Report on the Delegation of the السعودية. (“al-Saudiah”) domain representing Saudi Arabia in Arabic to the "Communications and Information Technology Commission" (2010-04-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eالسعودية‎ = new TLD(".السعودية‎", Type.COUNTRY_CODE, "Communications, Space and Technology Commission", LocalDate.parse("2010-05-05"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eڀارت‎ = new TLD(".ڀارت‎", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-08-15"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pccs.va">http://www.pccs.va</a><br><b>WHOIS Server:</b> whois.nic.xn--mgbi4ecexp<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161130-xn--mgbi4ecexp">Delegation of the .كاثوليك domain to Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication) (2016-11-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eكاثوليك‎ = new TLD(".كاثوليك‎", Type.GENERIC, "Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication)", LocalDate.parse("2016-11-16"), LocalDate.parse("2023-12-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://domains.sd">http://domains.sd</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/sudan-report-20150414.html">Delegation of the سودان (“sudan”) country code top-level domain representing Sudan in Arabic script to Sudan Internet Society (2015-04-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eسودان‎ = new TLD(".سودان‎", Type.COUNTRY_CODE, "Sudan Internet Society", LocalDate.parse("2012-11-20"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://icann.org/ebero">https://icann.org/ebero</a><br><b>WHOIS Server:</b> whois.nic.xn--mgbt3dhd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151204-xn--mgbt3dhd">Delegation of the .همراه domain to Asia Green IT System Bilgisayar San. ve Tic. Ltd. Sti. (2015-12-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eهمراه‎ = new TLD(".همراه‎", Type.GENERIC, "Emergency Back-End Registry Operator Program - ICANN", LocalDate.parse("2015-11-25"), LocalDate.parse("2024-07-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.cmc.iq/en/iq.html">http://www.cmc.iq/en/iq.html</a><br><b>WHOIS Server:</b> whois.cmc.iq<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/iraq-report-20151005.html">Delegation of the عراق (“Iraq”) domain representing Iraq in Arabic script to Communications and Media Commission (CMC) (2015-10-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eعراق‎ = new TLD(".عراق‎", Type.COUNTRY_CODE, "Communications and Media Commission (CMC)", LocalDate.parse("2014-09-29"), LocalDate.parse("2023-12-18"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.mynic.my">http://www.mynic.my</a><br><b>WHOIS Server:</b> whois.mynic.my<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2012/malaysia-report-20120809.html">Report on the Delegation of the مليسيا domain representing Malaysia in Arabic (2012-08-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eمليسيا‎ = new TLD(".مليسيا‎", Type.COUNTRY_CODE, "MYNIC Berhad", LocalDate.parse("2011-10-22"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.monic.mo">https://www.monic.mo</a><br><b>WHOIS Server:</b> whois.monic.mo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2016/macao-report-20160118.html">Delegation of the 澳門 (“Macao”) domain representing Macao in Traditional Chinese script to the Bureau of Telecommunications Regulation (DSRT) (2016-01-18)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 澳門 = new TLD("澳門", Type.COUNTRY_CODE, "Macao Post and Telecommunications Bureau (CTT)", LocalDate.parse("2015-04-21"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--mk1bu44c<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--mk1bu44c">Delegation of the .닷컴 domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 닷컴 = new TLD("닷컴", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.netc.tw">http://www.netc.tw</a><br><b>WHOIS Server:</b> whois.nic.xn--mxtq1m<br>
     *
     */
    public static final @NotNull TLD 政府 = new TLD("政府", Type.GENERIC, "Net-Chinese Co., Ltd.", LocalDate.parse("2015-01-08"), LocalDate.parse("2021-01-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dotshabaka.com">http://www.dotshabaka.com</a><br><b>WHOIS Server:</b> whois.nic.xn--ngbc5azd<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131021-xn--ngbc5azd">Delegation of the .شبكة domain to International Domain Registry Pty. Ltd. (2013-10-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eشبكة‎ = new TLD(".شبكة‎", Type.GENERIC, "International Domain Registry Pty. Ltd.", LocalDate.parse("2013-10-21"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://kfh.com">http://kfh.com</a><br><b>WHOIS Server:</b> whois.nic.xn--ngbe9e0a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151211-xn--ngbe9e0a">Delegation of the .بيتك domain to Kuwait Finance House (2015-12-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eبيتك‎ = new TLD(".بيتك‎", Type.GENERIC, "Kuwait Finance House", LocalDate.parse("2015-10-29"), LocalDate.parse("2024-06-05"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.nic.xn--ngbrx<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20170522-xn--ngbrx">Delegation of the .عرب domain to League of Arab States (2017-05-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eعرب‎ = new TLD(".عرب‎", Type.GENERIC, "League of Arab States", LocalDate.parse("2017-05-11"), LocalDate.parse("2020-05-14"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://xn--lodaehvb5cdik4g.xn--node">http://xn--lodaehvb5cdik4g.xn--node</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2014/georgia-report-20140926.html">Delegation of the გე (“ge”) domain representing Georgia in Georgian (Mkhedruli) script to the Information Technologies Development Center (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _GE = new TLD("გე", Type.COUNTRY_CODE, "Information Technologies Development Center (ITDC)", LocalDate.parse("2011-02-11"), LocalDate.parse("2024-06-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.xn--nqv7f">http://nic.xn--nqv7f</a><br><b>WHOIS Server:</b> whois.nic.xn--nqv7f<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140304-xn--nqv7f">Delegation of the .机构 domain to Public Interest Registry (2014-03-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _机构 = new TLD("机构", Type.GENERIC, "Public Interest Registry", LocalDate.parse("2014-02-27"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.xn--nqv7fs00ema">http://nic.xn--nqv7fs00ema</a><br><b>WHOIS Server:</b> whois.nic.xn--nqv7fs00ema<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140304-xn--nqv7fs00ema">Delegation of the .组织机构 domain to Public Interest Registry (2014-03-04)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 组织机构 = new TLD("组织机构", Type.GENERIC, "Public Interest Registry", LocalDate.parse("2014-02-27"), LocalDate.parse("2022-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stabletone.com">http://www.stabletone.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150331-xn--nyqy26a">Delegation of the .健康 domain to Stable Tone Limited (2015-03-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 健康 = new TLD("健康", Type.GENERIC, "Stable Tone Limited", LocalDate.parse("2015-03-12"), LocalDate.parse("2023-01-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.thnic.co.th">http://www.thnic.co.th</a><br><b>WHOIS Server:</b> whois.thnic.co.th<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/thai-report-16jul2010.html">Report on the Delegation of the .ไทย (“Thai”) domain representing Thailand in Thai to Thai Network Information Center Foundation (2010-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ไทย = new TLD("ไทย", Type.COUNTRY_CODE, "Thai Network Information Center Foundation", LocalDate.parse("2010-08-19"), LocalDate.parse("2024-06-03"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://tld.sy">http://tld.sy</a><br><b>WHOIS Server:</b> whois.tld.sy<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/syria-report-07jan2011.html">Delegation of the .سورية ("Sourya") domain representing the Syrian Arab Republic in Arabic (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eسورية‎ = new TLD(".سورية‎", Type.COUNTRY_CODE, "National Agency for Network Services (NANS)", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180118-xn--otu796d">Delegation of the .招聘 domain to Dot Trademark TLD Holding Company Limited (2018-01-18)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20240422-xn--otu796d">Transfer of the .招聘 domain to Jiang Yu Liang Cai Technology Company Limited (2024-04-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 招聘 = new TLD("招聘", Type.GENERIC, "Jiang Yu Liang Cai Technology Company Limited", LocalDate.parse("2017-12-21"), LocalDate.parse("2024-04-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://rusnames.ru/">http://rusnames.ru/</a><br><b>WHOIS Server:</b> whois.nic.xn--p1acf<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140926-xn--p1acf">Delegation of the .рус domain to Rusnames Limited (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD РУС = new TLD("рус", Type.GENERIC, "Rusnames Limited", LocalDate.parse("2014-07-03"), LocalDate.parse("2022-11-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://cctld.ru/en">http://cctld.ru/en</a><br><b>WHOIS Server:</b> whois.tcinet.ru<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/rf-report-07apr2010.html">Report on the Delegation of the .рф (“R.F.”) domain representing the Russian Federation in Cyrillic to "Coordination Center for TLD RU" (2010-04-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD РФ = new TLD("рф", Type.COUNTRY_CODE, "Coordination Center for TLD RU", LocalDate.parse("2010-05-12"), LocalDate.parse("2024-01-17"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151207-xn--pbt977c">Delegation of the .珠宝 domain to Richemont DNS Inc. (2015-12-08)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200626-xn--pbt977c">Revocation of the .珠宝 domain (2020-06-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 珠宝 = new TLD("珠宝", Type.GENERIC, null, LocalDate.parse("2015-11-12"), LocalDate.parse("2020-06-26"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.ati.tn">http://www.ati.tn</a><br><b>WHOIS Server:</b> whois.ati.tn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/tunis-report-16jul2010.html">Report on the Delegation of تونس. ("Tunis") representing Tunisia in Arabic to Agence Tunisienne d’Internet (2010-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eتونس‎ = new TLD(".تونس‎", Type.COUNTRY_CODE, "Agence Tunisienne d'Internet", LocalDate.parse("2010-08-19"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--pssy2u<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--pssy2u">Delegation of the .大拿 domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 大拿 = new TLD("大拿", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.la">https://www.la</a><br><b>WHOIS Server:</b> whois.nic.la<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2020/xn--q7ce6a-report-20200110.html">Delegation of the .ລາວ (“Lao”) domain representing the Lao People’s Democratic Republic in Lao script to the Lao National Internet Center (LANIC) (2020-01-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ລາວ = new TLD("ລາວ", Type.COUNTRY_CODE, "Lao National Internet Center (LANIC), Ministry of Technology and Communications", LocalDate.parse("2019-06-10"), LocalDate.parse("2023-03-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131119-xn--q9jyb4c">Delegation of the .みんな domain to Charleston Road Registry Inc. (2013-11-19)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD みんな = new TLD("みんな", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2013-11-13"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141113-xn--qcka1pmc">Delegation of the .グーグル domain to Charleston Road Registry Inc. (2014-11-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD グーグル = new TLD("グーグル", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-10-09"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.eu">https://www.registry.eu</a><br><b>WHOIS Server:</b> whois.eu<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2019/xn--qxa6a-report-20190822.html">Delegation of the .ευ domain representing the European Union in Greek script to EURid vzw/asbl (2019-08-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ΕΥ = new TLD("ευ", Type.COUNTRY_CODE, "EURid vzw", LocalDate.parse("2019-07-18"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gr">http://www.gr</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/greece-report-20151005.html">Delegation of the ελ (“el”) domain representing Greece in Greek script to ICS-FORTH GR (2015-10-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ΕΛ = new TLD("ελ", Type.COUNTRY_CODE, "ICS-FORTH GR", LocalDate.parse("2015-05-06"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.stabletone.com">http://www.stabletone.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140310-xn--rhqv96g">Delegation of the .世界 domain to Stable Tone Limited (2014-03-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 世界 = new TLD("世界", Type.GENERIC, "Stable Tone Limited", LocalDate.parse("2014-03-06"), LocalDate.parse("2023-01-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160217-xn--rovu88b">Delegation of the .書籍 domain to Amazon Registry Services, Inc. (2016-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 書籍 = new TLD("書籍", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2017/india-report-20170609.html">Delegation of eight top-level domains representing India in various languages to the National Internet Exchange of India (2017-06-09)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ഭാരതം = new TLD("ഭാരതം", Type.COUNTRY_CODE, "National Internet eXchange of India", LocalDate.parse("2016-04-18"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ਭਾਰਤ = new TLD("ਭਾਰਤ", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.xn--ses554g/">http://nic.xn--ses554g/</a><br><b>WHOIS Server:</b> whois.nic.xn--ses554g<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140404-xn--ses554g">Delegation of the .网址 domain to Hu Yi Global Information Resources (Holding) Company (2014-04-04)</a></li>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150121-xn--ses554g">Redelegation of the .网址 domain to KNET Co., Ltd (2015-01-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 网址 = new TLD("网址", Type.GENERIC, "KNET Co., Ltd", LocalDate.parse("2014-03-13"), LocalDate.parse("2024-04-08"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.verisigninc.com">http://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--t60b56a<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--t60b56a">Delegation of the .닷넷 domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 닷넷 = new TLD("닷넷", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.verisigninc.com">https://www.verisigninc.com</a><br><b>WHOIS Server:</b> whois.nic.xn--tckwe<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150726-xn--tckwe">Delegation of the .コム domain to VeriSign Sarl (2015-07-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD コム = new TLD("コム", Type.GENERIC, "VeriSign Sarl", LocalDate.parse("2015-07-02"), LocalDate.parse("2020-10-01"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pccs.va">http://www.pccs.va</a><br><b>WHOIS Server:</b> whois.nic.xn--tiq49xqyj<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/tld-transfer/20161130-xn--tiq49xqyj">Delegation of the .天主教 domain to Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication) (2016-11-30)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 天主教 = new TLD("天主教", Type.GENERIC, "Pontificium Consilium de Comunicationibus Socialibus (PCCS) (Pontifical Council for Social Communication)", LocalDate.parse("2016-11-16"), LocalDate.parse("2023-12-21"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.xn--unup4y<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131022-xn--unup4y">Delegation of the .游戏 domain to Spring Fields, LLC (2013-10-22)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 游戏 = new TLD("游戏", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-10-21"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dvag-registry.de">http://www.dvag-registry.de</a><br><b>WHOIS Server:</b> whois.nic.xn--vermgensberater-ctb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140926-xn--vermgensberater-ctb">Delegation of the .vermögensberater domain to Deutsche Vermögensberatung Aktiengesellschaft DVAG (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VERMÖGENSBERATER = new TLD("vermögensberater", Type.GENERIC, "Deutsche Vermögensberatung Aktiengesellschaft DVAG", LocalDate.parse("2014-09-18"), LocalDate.parse("2023-10-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.dvag-registry.de">http://www.dvag-registry.de</a><br><b>WHOIS Server:</b> whois.nic.xn--vermgensberatung-pwb<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140926-xn--vermgensberatung-pwb">Delegation of the .vermögensberatung domain to Deutsche Vermögensberatung Aktiengesellschaft DVAG (2014-09-26)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD VERMÖGENSBERATUNG = new TLD("vermögensberatung", Type.GENERIC, "Deutsche Vermögensberatung Aktiengesellschaft DVAG", LocalDate.parse("2014-09-18"), LocalDate.parse("2023-10-16"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.xn--vhquv<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140821-xn--vhquv">Delegation of the .企业 domain to Dash McCook, LLC (2014-08-21)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 企业 = new TLD("企业", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2013-11-25"), LocalDate.parse("2023-09-13"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.teleinfo.cn">http://www.teleinfo.cn</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150313-xn--vuq861b">Delegation of the .信息 domain to Beijing Tele-info Network Technology Co., Ltd. (2015-03-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 信息 = new TLD("信息", Type.GENERIC, "Beijing Tele-info Technology Co., Ltd.", LocalDate.parse("2015-02-05"), LocalDate.parse("2024-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kerryprops.com">http://www.kerryprops.com</a><br><b>WHOIS Server:</b> whois.nic.xn--w4r85el8fhu5dnra<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160302-xn--w4r85el8fhu5dnra">Delegation of the .嘉里大酒店 domain to Kerry Trading Co. Limited (2016-03-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 嘉里大酒店 = new TLD("嘉里大酒店", Type.GENERIC, "Kerry Trading Co. Limited", LocalDate.parse("2016-02-05"), LocalDate.parse("2023-01-19"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.kerryprops.com">http://www.kerryprops.com</a><br><b>WHOIS Server:</b> whois.nic.xn--w4rs40l<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160512-xn--w4rs40l">Delegation of the .嘉里 domain to Kerry Trading Co. Limited (2016-05-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 嘉里 = new TLD("嘉里", Type.GENERIC, "Kerry Trading Co. Limited", LocalDate.parse("2016-02-05"), LocalDate.parse("2023-01-19"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/misr-report-21apr2010.html">Report on the Delegation of the مصر. (“Misr”) domain representing Egypt in Arabic to the "National Telecommunication Regulatory Authority" (2010-04-21)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eمصر‎ = new TLD(".مصر‎", Type.COUNTRY_CODE, "National Telecommunication Regulatory Authority - NTRA", LocalDate.parse("2010-05-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.cra.gov.qa/">https://www.cra.gov.qa/</a><br><b>WHOIS Server:</b> whois.registry.qa<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/qatar-report-10oct2010.html">Delegation of the قطر. (“Qatar”) domain representing Qatar in Arabic (2010-10-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eقطر‎ = new TLD(".قطر‎", Type.COUNTRY_CODE, "Communications Regulatory Authority", LocalDate.parse("2010-12-24"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.yu-wei.cn/">http://www.yu-wei.cn/</a><br><b>WHOIS Server:</b> whois.ngtld.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140811-xn--xhq521b">Delegation of the .广东 domain to Guangzhou YU Wei Information Technology Co., Ltd. (2014-08-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 广东 = new TLD("广东", Type.GENERIC, "Guangzhou YU Wei Information Technology Co., Ltd.", LocalDate.parse("2014-07-24"), LocalDate.parse("2022-04-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.lk">http://www.nic.lk</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/lanka-report-16jul2010.html">Report on the Delegation of the .ලංකා (“Lanka”) domain representing Sri Lanka in Sinhalese, and இலங்கை (“Ilangai”) domain representing Sri Lanka in Tamil, to L K Domain Registry (2010-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD இலங்கை = new TLD("இலங்கை", Type.COUNTRY_CODE, "LK Domain Registry", LocalDate.parse("2010-08-19"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.in">https://www.registry.in</a><br><b>WHOIS Server:</b> whois.registry.in<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/india-report-07jan2011.html">Delegation of the seven top-level domains representing India in various languages (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD இந்தியா = new TLD("இந்தியா", Type.COUNTRY_CODE, "National Internet Exchange of India", LocalDate.parse("2011-02-05"), LocalDate.parse("2023-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amnic.net/">https://www.amnic.net/</a><br><b>WHOIS Server:</b> whois.amnic.net<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2015/armenia-report-20150413.html">Delegation of the .հայ (“hye”) domain representing Armenia in Armenian script to Internet Society of Armenia (2015-04-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ՀԱՅ = new TLD("հայ", Type.COUNTRY_CODE, "\"Internet Society\" Non-governmental Organization", LocalDate.parse("2014-11-26"), LocalDate.parse("2023-03-07"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.sgnic.sg/">http://www.sgnic.sg/</a><br><b>WHOIS Server:</b> whois.zh.sgnic.sg<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/singapore-report-07jan2011.html">Delegation of the .新加坡 ("Singapore") domain, and the .சிங்கப்பூர் ("Singapore") domain, representing Singapore in Chinese and Tamil (2011-01-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 新加坡 = new TLD("新加坡", Type.COUNTRY_CODE, "Singapore Network Information Centre (SGNIC) Pte Ltd", LocalDate.parse("2011-02-05"), LocalDate.parse("2024-03-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.pnina.ps">http://www.pnina.ps</a><br><b>WHOIS Server:</b> whois.pnina.ps<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2010/falasteen-report-16jul2010.html">Report on the Delegation of فلسطين. ("Falasteen") representing the Occupied Palestinian Territory in Arabic (2010-07-16)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD _2eفلسطين‎ = new TLD(".فلسطين‎", Type.COUNTRY_CODE, "Ministry of Telecom and Information Technology (MTIT)", LocalDate.parse("2010-08-20"), LocalDate.parse("2024-02-05"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2007/testetal-report-01aug2007.html">Report on Delegation of Eleven Evaluative Internationalised Top-Level Domains (2007-08-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD テスト = new TLD("テスト", Type.TEST, null, LocalDate.parse("2007-10-19"), LocalDate.parse("2020-04-02"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.conac.cn">http://www.conac.cn</a><br><b>WHOIS Server:</b> whois.conac.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20131213-xn--zfr164b">Delegation of the .政务 domain to China Organizational Name Administration Center (2013-12-13)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD 政务 = new TLD("政务", Type.GENERIC, "China Organizational Name Administration Center", LocalDate.parse("2013-12-12"), LocalDate.parse("2024-07-18"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150803-xperia">Delegation of the .xperia domain to Sony Mobile Communications AB (2015-08-03)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180711-xperia">Revocation of the .xperia domain (2018-07-11)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XPERIA = new TLD("xperia", Type.GENERIC, null, LocalDate.parse("2015-07-23"), LocalDate.parse("2018-07-21"));

    /**
     * <h2>Registry Information</h2>
     * This domain is managed under ICANN's registrar system. You may register domains in .XXX through an ICANN accredited registrar. The official list of ICANN accredited registrars is available <a href="http://www.icann.org/registrars/accredited-list.html">on ICANN's website</a>.<br>
     * <b>URL for registration services:</b> <a href="http://nic.xxx">http://nic.xxx</a><br><b>WHOIS Server:</b> whois.nic.xxx<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2011/xxx-report-20110407.pdf">Report on the Delegation of the .XXX Top-Level Domain (2011-04-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XXX = new TLD("xxx", Type.SPONSORED, "ICM Registry LLC", LocalDate.parse("2011-04-15"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.xyz">https://nic.xyz</a><br><b>WHOIS Server:</b> whois.nic.xyz<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140212-xyz">Delegation of the .xyz domain to XYZ.COM LLC (2014-02-12)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD XYZ = new TLD("xyz", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-02-06"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.yachts">http://nic.yachts</a><br><b>WHOIS Server:</b> whois.nic.yachts<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140514-yachts">Delegation of the .yachts domain to DERYachts, LLC (2014-05-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210405-yachts">Transfer of the .yachts domain to XYZ.COM LLC (2021-04-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YACHTS = new TLD("yachts", Type.GENERIC, "XYZ.COM LLC", LocalDate.parse("2014-05-01"), LocalDate.parse("2024-02-28"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.yahoo">http://nic.yahoo</a><br><b>WHOIS Server:</b> whois.nic.yahoo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160209-yahoo">Delegation of the .yahoo domain to Yahoo! Domain Services Inc. (2016-02-09)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210708-yahoo">Transfer of the .yahoo domain to OATH INC (2021-07-08)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YAHOO = new TLD("yahoo", Type.GENERIC, "Yahoo Inc.", LocalDate.parse("2016-01-29"), LocalDate.parse("2024-07-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.yamaxun<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151006-yamaxun">Delegation of the .yamaxun domain to Amazon Registry Services, Inc. (2015-10-06)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YAMAXUN = new TLD("yamaxun", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-09-10"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.yandex.ru/">http://www.yandex.ru/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140714-yandex">Delegation of the .yandex domain to YANDEX, LLC (2014-07-14)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20200207-yandex">Transfer of the .yandex domain to Yandex Europe B.V. (2020-02-07)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YANDEX = new TLD("yandex", Type.GENERIC, "Yandex Europe B.V.", LocalDate.parse("2014-07-10"), LocalDate.parse("2024-06-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>WHOIS Server:</b> whois.y.net.ye<br>
     *
     */
    public static final @NotNull TLD YE = new TLD("ye", Type.COUNTRY_CODE, "TeleYemen", LocalDate.parse("1996-08-19"), LocalDate.parse("2024-07-22"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmoregistry.com/en/">http://www.gmoregistry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.gmo<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20150217-yodobashi">Delegation of the .yodobashi domain to Yodobashi Camera Co., Ltd. (2015-02-17)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YODOBASHI = new TLD("yodobashi", Type.GENERIC, "YODOBASHI CAMERA CO.,LTD.", LocalDate.parse("2015-02-13"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://nic.yoga/">http://nic.yoga/</a><br><b>WHOIS Server:</b> whois.nic.yoga<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141013-yoga">Delegation of the .yoga domain to Top Level Domain Holdings Limited (2014-10-13)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20210914-yoga">Transfer of the .yoga domain to Registry Services, LLC (2021-09-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YOGA = new TLD("yoga", Type.GENERIC, "Registry Services, LLC", LocalDate.parse("2014-08-15"), LocalDate.parse("2024-04-17"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.gmo-registry.com/en/">http://www.gmo-registry.com/en/</a><br><b>WHOIS Server:</b> whois.nic.yokohama<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140331-yokohama">Delegation of the .yokohama domain to GMO Registry, Inc. (2014-03-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YOKOHAMA = new TLD("yokohama", Type.GENERIC, "GMO Registry, Inc.", LocalDate.parse("2014-03-27"), LocalDate.parse("2019-08-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com">https://www.amazonregistry.com</a><br><b>WHOIS Server:</b> whois.nic.you<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160323-you">Delegation of the .you domain to Amazon Registry Services, Inc. (2016-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YOU = new TLD("you", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140827-youtube">Delegation of the .youtube domain to Charleston Road Registry Inc. (2014-08-27)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YOUTUBE = new TLD("youtube", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.nic.yt">http://www.nic.yt</a><br><b>WHOIS Server:</b> whois.nic.yt<br>
     *
     */
    public static final @NotNull TLD YT = new TLD("yt", Type.COUNTRY_CODE, "Association Française pour le Nommage Internet en Coopération (A.F.N.I.C.)", LocalDate.parse("1997-11-17"), LocalDate.parse("2024-03-25"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.teleinfo.cn">http://www.teleinfo.cn</a><br><b>WHOIS Server:</b> whois.teleinfo.cn<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160325-yun">Delegation of the .yun domain to QIHOO 360 Technology Co. Ltd. (2016-03-25)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD YUN = new TLD("yun", Type.GENERIC, "QIHOO 360 TECHNOLOGY CO. LTD.", LocalDate.parse("2016-03-18"), LocalDate.parse("2024-05-06"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.zadna.org.za/">http://www.zadna.org.za/</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2005/za-report-05aug05.pdf">IANA Report on the Redelegation of the .ZA Top-Level Domain (2005-08-05)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZA = new TLD("za", Type.COUNTRY_CODE, "ZA Domain Name Authority", LocalDate.parse("1990-11-07"), LocalDate.parse("2020-01-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.amazonregistry.com/">https://www.amazonregistry.com/</a><br><b>WHOIS Server:</b> whois.nic.zappos<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160531-zappos">Delegation of the .zappos domain to Amazon Registry Services, Inc. (2016-05-31)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZAPPOS = new TLD("zappos", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2016-05-19"), LocalDate.parse("2024-01-31"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.zara.com">http://www.zara.com</a><br><b>WHOIS Server:</b> whois.nic.zara<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151020-zara">Delegation of the .zara domain to Industria de Diseño Textil, S.A. (INDITEX, S.A.) (2015-10-20)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZARA = new TLD("zara", Type.GENERIC, "Industria de Diseño Textil, S.A. (INDITEX, S.A.)", LocalDate.parse("2015-09-10"), LocalDate.parse("2023-08-04"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="http://www.amazonregistry.com">http://www.amazonregistry.com</a><br><br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20151202-zero">Delegation of the .zero domain to Amazon Registry Services, Inc. (2015-12-02)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZERO = new TLD("zero", Type.GENERIC, "Amazon Registry Services, Inc.", LocalDate.parse("2015-11-12"), LocalDate.parse("2024-05-11"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.registry.google">https://www.registry.google</a><br><b>WHOIS Server:</b> whois.nic.google<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140910-zip">Delegation of the .zip domain to Charleston Road Registry Inc. (2014-09-10)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZIP = new TLD("zip", Type.GENERIC, "Charleston Road Registry Inc.", LocalDate.parse("2014-08-23"), LocalDate.parse("2020-04-20"));

    /**
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20160630-zippo">Delegation of the .zippo domain to Zadco Company (2016-06-30)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20190214-zippo">Revocation of the .zippo domain (2019-02-14)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZIPPO = new TLD("zippo", Type.GENERIC, null, LocalDate.parse("2016-05-26"), LocalDate.parse("2019-02-15"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://registry.zicta.zm">https://registry.zicta.zm</a><br><b>WHOIS Server:</b> whois.zicta.zm<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/2014/zm-report-20140201.html">Redelegation of the .zm domain to Zambia Information and Communications Technology Authority (2014-02-01)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZM = new TLD("zm", Type.COUNTRY_CODE, "Zambia Information and Communications Technology Authority (ZICTA)", LocalDate.parse("1994-03-25"), LocalDate.parse("2024-04-24"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://www.identity.digital/">https://www.identity.digital/</a><br><b>WHOIS Server:</b> whois.nic.zone<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20140110-zone">Delegation of the .ZONE domain to Outer Falls, LLC (2014-01-10)</a></li>
     *   <li><a href="https://iana.org/reports/tld-transfer/20180323-binkymoon">Transfer of the .academy domain and 195 others (2018-03-23)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZONE = new TLD("zone", Type.GENERIC, "Binky Moon, LLC", LocalDate.parse("2014-01-09"), LocalDate.parse("2023-09-12"));

    /**
     * <h2>Registry Information</h2>
     * <b>URL for registration services:</b> <a href="https://nic.zuerich">https://nic.zuerich</a><br><b>WHOIS Server:</b> whois.nic.zuerich<br>
     *
     * <h2>IANA Reports</h2>
     *
     * <ul>
     *   <li><a href="https://iana.org/reports/c.2.9.2.d/20141222-zuerich">Delegation of the .zuerich domain to Kanton Zürich (2014-12-22)</a></li>
     * </ul>
     *
     */
    public static final @NotNull TLD ZUERICH = new TLD("zuerich", Type.GENERIC, "Kanton Zürich (Canton of Zurich)", LocalDate.parse("2014-12-18"), LocalDate.parse("2023-10-16"));

    /**
     * <h2>Registry Information</h2>
     * <br>
     *
     */
    public static final @NotNull TLD ZW = new TLD("zw", Type.COUNTRY_CODE, "Postal and Telecommunications Regulatory Authority of Zimbabwe (POTRAZ)", LocalDate.parse("1991-11-06"), LocalDate.parse("2021-03-11"));

    // Load all these TLDs into a map to optimize retrieving
    static {
        for (@NotNull Field field : TLD.class.getDeclaredFields()) try {
            field.setAccessible(true);

            if (field.getType() == TLD.class && Modifier.isStatic(field.getModifiers())) {
                @NotNull TLD tld = (TLD) field.get(null);
                map.put(tld.code.toLowerCase(), tld);
            }
        } catch (@NotNull Throwable throwable) {
            throw new RuntimeException("cannot load TLD field value '" + field + "'", throwable);
        }
    }

    // Object

    private final @NotNull String code;
    private final @NotNull Type type;
    private final @Nullable String provider;

    private final @UnknownNullability LocalDate registration;
    private final @NotNull LocalDate update;

    /**
     * Constructs a {@link TLD} object with the given string.
     *
     * @param code the code representing the TLD (e.g., "com", "net", or "org")
     * @param type the TLD type
     * @param provider the TLD provider or null if not assigned
     * @param registration the local date this TLD was registered at IANA. Can be null if the TLD does not provide one (e.g., .eh)
     * @param update the local date this TLD was last updated at IANA
     * @throws IllegalArgumentException if the code is not a valid TLD pattern
     */
    private TLD(@NotNull String code, @NotNull Type type, @Nullable String provider, @Nullable LocalDate registration, @NotNull LocalDate update) {
        this.code = code.toLowerCase();
        this.type = type;
        this.provider = provider;
        this.registration = registration;
        this.update = update;

        if (!validate(code)) {
            throw new IllegalArgumentException("The code '" + code + "' cannot be parsed as a valid TLD pattern");
        }
    }

    // Getters

    /**
     * Returns the TLD type.
     *
     * <p>The TLD type represents the category of the TLD, such as generic, sponsored, country code, infrastructure, generic restricted, or test.</p>
     *
     * @return the TLD type
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Returns the TLD provider.
     *
     * <p>The TLD provider is the entity that manages the TLD. This can be null for some TLDs where the provider is not assigned.</p>
     *
     * @return the assigned TLD provider, or null if not assigned
     */
    public @Nullable String getProvider() {
        return provider;
    }

    /**
     * Returns the registration date of the TLD.
     *
     * <p>This method represents the date the TLD was registered and created at IANA. It can be null for certain TLDs.</p>
     *
     * @return the local date the TLD was created, or null if not available
     */
    public @UnknownNullability LocalDate getRegistration() {
        return registration;
    }

    /**
     * Returns the last update date of the TLD.
     *
     * <p>This method represents the date the TLD was last updated at IANA.</p>
     *
     * @return the local date the TLD was last updated
     */
    public @NotNull LocalDate getLastUpdate() {
        return update;
    }

    // CharSequence Implementations

    /**
     * Returns the length of the TLD string.
     *
     * @return the length of the TLD string
     */
    @Override
    public int length() {
        return toString().length();
    }

    /**
     * Returns the character at the specified index.
     *
     * @param index the index of the character to return
     * @return the character at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    /**
     * Returns a subsequence of the TLD string.
     *
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if start or end are out of range
     */
    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    /**
     * Compares this TLD to another string, ignoring case considerations.
     *
     * @param string the string to compare to
     * @return {@code true} if the strings are equal ignoring case, {@code false} otherwise
     */
    public boolean equalsIgnoreCase(@NotNull String string) {
        return toString().equalsIgnoreCase(string);
    }

    // Modules

    /**
     * Validates if this TLD name exists (is known by IANA).
     *
     * @return {@code true} if the TLD exists and is known by IANA, {@code false} otherwise
     */
    public boolean isKnown() {
        return isKnown(toString());
    }

    // Implementations

    /**
     * Indicates whether TLD is equal by its case-insensitive code.
     *
     * @param object the reference object with which to compare
     * @return {@code true} if this object is the same as the TLD argument; {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull TLD tld = (TLD) object;
        return code.equalsIgnoreCase(tld.code);
    }

    /**
     * Returns a hash code value for the object based on the TLD code (lower case meaning case insensitive).
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(code.toLowerCase());
    }

    /**
     * Returns the string representation of the TLD.
     *
     * @return the string representation of the TLD
     */
    @Override
    public @NotNull String toString() {
        return code;
    }

    // Classes

    /**
     * Represents the types of Top-Level Domains (TLDs) as classified by the Internet Assigned Numbers Authority (IANA).
     *
     * <p>Each TLD is categorized into one of the following types based on its purpose and the policies governing its use.</p>
     *
     * <p>The types are:</p>
     * <ul>
     *   <li>{@link #GENERIC GENERIC} - Generic TLDs intended for general use without restrictions.</li>
     *   <li>{@link #SPONSORED SPONSORED} - Sponsored TLDs associated with a particular community or interest group, governed by specific rules.</li>
     *   <li>{@link #COUNTRY_CODE COUNTRY_CODE} - Country Code TLDs (ccTLDs) assigned to specific countries or territories.</li>
     *   <li>{@link #INFRASTRUCTURE INFRASTRUCTURE} - Infrastructure TLDs used for technical infrastructure purposes.</li>
     *   <li>{@link #GENERIC_RESTRICTED GENERIC_RESTRICTED} - Generic restricted TLDs with specific restrictions on registration and use.</li>
     *   <li>{@link #TEST TEST} - Test TLDs used for testing purposes, not intended for production use.</li>
     * </ul>
     */
    public enum Type {
        /**
         * Generic Top-Level Domains (gTLDs).
         *
         * <p>gTLDs are intended for general use and are not restricted to any particular community or geographic region.
         * These domains can be used by anyone for any purpose, subject to availability. Examples include:</p>
         * <ul>
         *   <li><b>.com</b> - Originally intended for commercial entities, now used broadly.</li>
         *   <li><b>.net</b> - Originally intended for network-related entities, now used broadly.</li>
         *   <li><b>.org</b> - Originally intended for non-profit organizations, now used broadly.</li>
         *   <li><b>.info</b> - Intended for informational sites.</li>
         * </ul>
         */
        GENERIC,

        /**
         * Sponsored Top-Level Domains (sTLDs).
         *
         * <p>sTLDs are associated with a specific community or interest group and are governed by policies set by a sponsor representing that community.
         * Registration and use of these domains are subject to restrictions and eligibility requirements. Examples include:</p>
         * <ul>
         *   <li><b>.gov</b> - Restricted to U.S. government entities.</li>
         *   <li><b>.edu</b> - Restricted to accredited post-secondary educational institutions.</li>
         *   <li><b>.mil</b> - Restricted to U.S. military entities.</li>
         *   <li><b>.aero</b> - Restricted to members of the air transport industry.</li>
         * </ul>
         */
        SPONSORED,

        /**
         * Country Code Top-Level Domains (ccTLDs).
         *
         * <p>ccTLDs are assigned to specific countries or territories and are identified by two-letter codes, typically corresponding to ISO 3166-1 alpha-2 codes.
         * These domains are intended for use by individuals and entities within the respective countries or territories, though some ccTLDs may have more flexible registration policies.
         * Examples include:</p>
         * <ul>
         *   <li><b>.us</b> - United States.</li>
         *   <li><b>.uk</b> - United Kingdom.</li>
         *   <li><b>.jp</b> - Japan.</li>
         *   <li><b>.br</b> - Brazil.</li>
         * </ul>
         */
        COUNTRY_CODE,

        /**
         * Infrastructure Top-Level Domains.
         *
         * <p>Infrastructure TLDs are used for technical infrastructure purposes related to the functioning of the Internet. Currently, the only TLD in this category is:</p>
         * <ul>
         *   <li><b>.arpa</b> - Used for technical infrastructure purposes, such as reverse DNS lookups.</li>
         * </ul>
         */
        INFRASTRUCTURE,

        /**
         * Generic Restricted Top-Level Domains (grTLDs).
         *
         * <p>grTLDs are generic TLDs with specific registration restrictions. These restrictions are intended to ensure that the TLD is used for its intended purpose. Examples include:</p>
         * <ul>
         *   <li><b>.biz</b> - Restricted to business use.</li>
         *   <li><b>.name</b> - Intended for personal use by individuals.</li>
         *   <li><b>.pro</b> - Restricted to certified professionals and related entities.</li>
         * </ul>
         */
        GENERIC_RESTRICTED,

        /**
         * Test Top-Level Domains.
         *
         * <p>Test TLDs are used for testing purposes and are not intended for production use on the global Internet. These domains are typically used in controlled environments to test DNS-related technologies and configurations. Examples include:</p>
         * <ul>
         *   <li><b>.test</b> - Used for testing and development purposes.</li>
         *   <li><b>.example</b> - Reserved for use in examples and documentation.</li>
         *   <li><b>.invalid</b> - Reserved for use in invalid domain names.</li>
         *   <li><b>.localhost</b> - Reserved for use in local testing environments.</li>
         * </ul>
         */
        TEST
    }

}