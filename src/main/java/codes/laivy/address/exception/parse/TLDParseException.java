package codes.laivy.address.exception.parse;

import org.jetbrains.annotations.Nullable;

public final class TLDParseException extends IllegalArgumentException {
    public TLDParseException() {
    }
    public TLDParseException(@Nullable String s) {
        super(s);
    }
    public TLDParseException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
    public TLDParseException(@Nullable Throwable cause) {
        super(cause);
    }
}
