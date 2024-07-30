package codes.laivy.address.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalAddressTypeException extends Exception {
    public IllegalAddressTypeException(@NotNull String message) {
        super(message);
    }
}
