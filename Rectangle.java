import java.util.Arrays;
import java.util.Objects;

public record Rectangle<T>(T top, T bottom, T left, T right) {

    public Rectangle {
        verifyNonNull(top, bottom, left, right);
    }

    public final void verifyNonNull(Object... args) {
        if (Arrays.stream(args).anyMatch(Objects::isNull))
            throw new NullPointerException();
    }

}
