package codes.laivy.address.exception;

import org.jetbrains.annotations.NotNull;

// todo: add some more exceptions like AddressParseException
public class IllegalAddressTypeException extends Exception {
    public IllegalAddressTypeException(@NotNull String message) {
        super(message);
    }
}
