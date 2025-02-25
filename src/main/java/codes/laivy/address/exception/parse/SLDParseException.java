package codes.laivy.address.exception.parse;

import org.jetbrains.annotations.Nullable;

public final class SLDParseException extends IllegalArgumentException {
    public SLDParseException() {
    }
    public SLDParseException(@Nullable String s) {
        super(s);
    }
    public SLDParseException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
    public SLDParseException(@Nullable Throwable cause) {
        super(cause);
    }
}
