# Java Address 📬

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/github/license/ItsLaivy/java-address?style=for-the-badge)

## Overview 🌐

`java-address` is a lightweight library for handling internet addresses and communication protocols, including IPv4, IPv6 and Domains. It provides robust utilities for validation, parsing, and manipulation of network addresses and ports.

## Features ✨

- **Validation and Parsing**: Validate and create instances of IPv4 and IPv6 addresses from strings.
- **Port Handling**: Represent and manipulate network ports, ensuring they are within the valid range (0 to 65535).
- **Address Utilities**: Retrieve raw bytes and formatted names of addresses.
- **Domain Names**: A fully complete Domain classes including **SLD**, **TLD** and **Subdomain** classes with all verifications.

## Installation 📦

For now, there's no public artifact at the Maven Central for this.
To use the **Java Address** library.
You should install it manually at your project
using [Maven Guide to installing 3rd party JARs](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html)

## Usage 🛠️

### Basic Example

```java
// Parses the 8080 into a port object
Port port = Port.create(8080);
// Parses the ipv4 into an object
IPv4Address address = IPv4Address.parse("192.168.1.1");
// The Host object includes information about the address and port
Host host = Host.create(address, port);

// Print objects
System.out.println("Port: " + port);
System.out.println("Address: " + address);
System.out.println("Host: " + host);
```

### Advanced Usage

#### Working with Ports

```java
Port wellKnownPort = Port.create(80); // HTTP port
Port registeredPort = Port.create(8080); // Custom application port

System.out.println("Is well-known port: " + wellKnownPort.isWellKnown());
System.out.println("Registered Port: " + registeredPort.isRegistered());
```

## IPv4 Address 🌐

The `IPv4Address` class represents an IPv4 network address. IPv4 addresses are 32-bit numerical labels written in decimal and separated by dots, e.g., `192.168.0.1`.

### Usage Examples

#### Validating an IPv4 Address

```java
boolean isValid = IPv4Address.validate("192.168.0.1");
System.out.println("Is valid: " + isValid); // Returns 'true'

isValid = IPv4Address.validate("255.255.255.255");
System.out.println("Is valid: " + isValid); // Returns 'true'

isValid = IPv4Address.validate("999.999.999.999");
System.out.println("Is valid: " + isValid); // Returns 'false'
```

#### Parsing an IPv4 Address

```java
IPv4Address ipv4Address = IPv4Address.parse("192.168.0.1");
System.out.println("Parsed address: " + ipv4Address.getName());
```

#### Getting Byte Representation

```java
IPv4Address ipv4Address = IPv4Address.parse("192.168.0.1");
System.out.println("Byte representation: " + Arrays.toString(ipv4Address.getBytes()));
```

#### Checking if Address is local

```java
IPv4Address ipv4Address = IPv4Address.parse("127.0.0.1");
System.out.println("Is local: " + ipv4Address.isLocal());
```

## IPv6 Addresses 🌐

The `IPv6Address` class represents an IPv6 network address. IPv6 addresses are 128-bit numerical labels written in hexadecimal and separated by colons, e.g., `2001:0db8:85a3:0000:0000:8a2e:0370:7334`.

### Usage Examples

#### Validating an IPv6 Address

```java
boolean isValid = IPv6Address.validate("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
System.out.println("Is valid: " + isValid); // Prints 'true'

isValid = IPv6Address.validate("::ffff:192.0.2.128");
System.out.println("Is valid: " + isValid); // Prints 'true'

isValid = IPv6Address.validate("2001:0db8:85a3::85a3::7334");
System.out.println("Is valid: " + isValid); // Prints 'false'
```

#### Parsing an IPv6 Address

```java
IPv6Address ipv6Address = IPv6Address.parse("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
System.out.println("Parsed address: " + ipv6Address.getName());
```

#### Getting Byte Representation

```java
IPv6Address ipv6Address = IPv6Address.parse("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
System.out.println("Byte representation: " + Arrays.toString(ipv6Address.getBytes()));
```

#### Checking if Address is local

```java
IPv6Address ipv6Address = IPv6Address.parse("::1");
System.out.println("Is local: " + ipv6Address.isLocal());
```

## Domain Names 🌐

### Domain
The `Domain` class represents a fully qualified domain name (FQDN). It provides methods to parse and validate domain names, ensuring they conform to standard formats.

#### Example
```java
Domain domain = Domain.parse("example.com");
System.out.println("Domain: " + domain);
```

### TLD (Top-Level Domain)
The `TLD` class represents the top-level domain of a domain name, such as `.com`, `.org`, or `.net`. It includes methods for validation and retrieval of the TLD from a given domain.

#### Example
```java
TLD tld = TLD.parse("com");
System.out.println("Top-Level Domain: " + tld);
```

### SLD (Second-Level Domain)
The `SLD` class represents the second-level domain, which is the part of the domain name directly to the left of the TLD. For example, in `example.com`, `example` is the SLD. This class provides methods to parse and validate the SLD.

#### Example
```java
SLD sld = SLD.parse("example");
System.out.println("Second-Level Domain: " + sld);
```

### Subdomain
The `Subdomain` class represents the subdomain part of a domain name, which is the part to the left of the SLD. For example, in `blog.example.com`, `blog` is the subdomain. This class provides methods to parse and validate subdomains.

#### Example
```java
Subdomain subdomain = Subdomain.parse("blog");
System.out.println("Subdomain: " + subdomain);
```

These classes work together to provide a comprehensive toolkit for handling and manipulating domain names in your applications.

## Contributing 🤝

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a pull request

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements 🙏

- Thanks to all the contributors who have helped improve this project.
- Special thanks to the open-source community for their continuous support and contributions.

---

Feel free to reach out if you have any questions or need further assistance!

Happy coding! 🚀