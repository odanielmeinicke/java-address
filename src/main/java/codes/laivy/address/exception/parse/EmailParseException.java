package codes.laivy.address.exception.parse;

import org.jetbrains.annotations.Nullable;

public final class EmailParseException extends IllegalArgumentException {
    public EmailParseException() {
    }
    public EmailParseException(@Nullable String s) {
        super(s);
    }
    public EmailParseException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
    public EmailParseException(@Nullable Throwable cause) {
        super(cause);
    }
}
