package codes.laivy.address.exception.parse;

import org.jetbrains.annotations.Nullable;

public final class SubdomainParseException extends IllegalArgumentException {
    public SubdomainParseException() {
    }
    public SubdomainParseException(@Nullable String s) {
        super(s);
    }
    public SubdomainParseException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
    public SubdomainParseException(@Nullable Throwable cause) {
        super(cause);
    }
}
