# Laivy's java-address

This library provides robust classes for representing and manipulating network addresses and ports, including IPv4 and IPv6 addresses. It ensures valid formatting, supports various address operations, and offers utilities for common networking tasks.

## Table of Contents

- [Port](#port)
- [Address](#address)
- [IPv4Address](#ipv4address)
- [IPv6Address](#ipv6address)

## Port

The `Port` class represents a network port and ensures it adheres to the valid port number range (0 to 65535). It is an immutable and thread-safe class that extends `Number` and implements `Comparable`.

### Key Features

- **Validation**: Check if a string is a valid port number.
- **Parsing**: Create `Port` instances from strings or integers.
- **Port Type Classification**: Determine if a port is well-known, registered, or dynamic/private.
- **Utilities**: Methods for incrementing, decrementing, and checking port characteristics.

### Example Methods

- `validate(String string)`: Validates if a string is a valid port number.
- `parse(String port)`: Parses a string to create a `Port` object.
- `create(int port)`: Creates a `Port` object from an integer.
- `isWellKnown()`: Checks if the port is a well-known port (0-1023).
- `isRegistered()`: Checks if the port is a registered port (1024-49151).
- `isDynamicPrivate()`: Checks if the port is a dynamic or private port (49152-65535).
- `getPortType()`: Returns the type of the port.

## Address

The `Address` interface represents a network address, providing methods for handling both IPv4 and IPv6 addresses. It includes static methods to validate and parse addresses, and abstract methods to retrieve address bytes and names.

### Key Features

- **Validation**: Validate addresses in IPv4 or IPv6 format.
- **Parsing**: Create address instances from strings.
- **Address Utilities**: Retrieve raw bytes and formatted names of the address.
- **Port Handling**: Methods to convert addresses to strings with ports appended.

### Example Methods

- `validate(String string)`: Validates if a string is a valid IPv4 or IPv6 address.
- `parse(String string)`: Parses a string to create an `Address` object.
- `getBytes()`: Returns the raw byte values of the address.
- `getName()`: Returns the formatted string representation of the address.
- `toString(Port port)`: Converts the address into a string representation with a port appended.

## IPv4Address

The `IPv4Address` class represents an IPv4 network address. IPv4 addresses are 32-bit numerical labels separated by dots, e.g., `192.168.1.1`.

### Key Features

- **Validation**: Ensures the address is a valid IPv4 address.
- **Conversion**: Converts the address to and from its byte representation.
- **Address Utilities**: Methods to check if the address is localhost, or within specific subnets or broadcast addresses.

### Example Methods

- `validate(String string)`: Validates if a string is a valid IPv4 address.
- `parse(String string)`: Parses a string to create an `IPv4Address` object.
- `getOctets()`: Returns the individual octets of the IPv4 address.
- `isLocalHost()`: Checks if the address is a localhost address.
- `isInSubnet(IPv4Address subnetMask)`: Checks if the address is within the range defined by a subnet mask.
- `isBroadcast(IPv4Address subnetMask)`: Checks if the address is a broadcast address for the given subnet mask.

## IPv6Address

The `IPv6Address` class represents an IPv6 network address. IPv6 addresses are 128-bit numerical labels separated by colons, e.g., `2001:0db8:85a3:0000:0000:8a2e:0370:7334`.

### Key Features

- **Validation**: Ensures the address is a valid IPv6 address.
- **Compression Handling**: Handles address compression and decompression.
- **Address Utilities**: Methods to check if the address is a loopback address, or within specific subnets or broadcast addresses.

### Example Methods

- `validate(String string)`: Validates if a string is a valid IPv6 address.
- `parse(String string)`: Parses a string to create an `IPv6Address` object.
- `getGroups()`: Returns the individual groups of the IPv6 address.
- `getName()`: Returns the address in its compressed format.
- `getRawName()`: Returns the raw, uncompressed string representation of the address.
- `isLocalHost()`: Checks if the address is a loopback address.
- `isInSubnet(IPv6Address subnetMask)`: Checks if the address is within the range defined by a subnet mask.
- `isBroadcast(IPv6Address subnetMask)`: Checks if the address is a broadcast address for the given subnet mask.

---

For more details and usage examples, please refer to the API documentation or the source code of the library. Feel free to contribute and enhance the library by opening issues or submitting pull requests.
