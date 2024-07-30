package codes.laivy.address.ip;

import codes.laivy.address.Address;
import codes.laivy.address.utilities.HttpAddress;

/**
 * The {@code IPAddress} interface represents a generic Internet Protocol (IP) address.
 * It serves as a common superinterface for specific types of IP addresses, such as
 * {@link IPv4Address} and {@link IPv6Address}. This interface extends the {@link Address}
 * interface, indicating that an IP address is a specific type of address used for network
 * communication.
 *
 * <p>IP addresses are fundamental to networking, enabling the identification and location
 * of devices on a network. They are crucial for routing traffic across the internet and
 * local networks. This interface does not define specific methods, as the behavior and
 * structure of IP addresses can vary significantly between different IP versions. Instead,
 * it provides a common type that can be used to refer to any kind of IP address.</p>
 *
 * <p>Implementations of this interface, such as {@link IPv4Address} and {@link IPv6Address},
 * should provide methods specific to the characteristics of their respective IP version.
 * For instance, an IPv4 address consists of four octets, while an IPv6 address consists
 * of eight groups of four hexadecimal digits.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * IPAddress ipv4 = new IPv4Address("192.168.1.1");
 * IPAddress ipv6 = new IPv6Address("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
 *
 * // Common usage with IPAddress type
 * Network network = new Network(ipv4);
 * }
 * </pre>
 *
 * <p>Note: This interface is intended to be extended by more specific IP address types.
 * As such, it is generally not intended for direct implementation by user-defined classes.</p>
 *
 * @see Address
 * @see HttpAddress
 * @see IPv4Address
 * @see IPv6Address
 */
public interface IPAddress extends Address, HttpAddress {
}
