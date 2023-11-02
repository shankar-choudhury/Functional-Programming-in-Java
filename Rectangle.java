import java.util.Arrays;
import java.util.Objects;

public record Rectangle<T extends Comparable<? super T>>(T top, T bottom, T left, T right) {

    public static <T extends Comparable<? super T>> Rectangle<T> of(T top, T bottom, T left, T right){
        verifyNonNull(top, bottom, left, right);
        verifyBounds(bottom, top);
        verifyBounds(left, right);
        return new Rectangle<>(top, bottom, left, right);
    }

    public static final void verifyNonNull(Object... args) {
        if (Arrays.stream(args).anyMatch(Objects::isNull))
            throw new NullPointerException();
    }

    private static final <T extends Comparable<? super T>> void verifyBounds(T lowerBound, T upperBound) {
        verifyNonNull(lowerBound, upperBound);
        if (lowerBound.compareTo(upperBound) >= 0)
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
    }

    static class TestHook {
        public static <T extends Comparable<? super T>> void verifyBounds(T lowerBound, T upperBound) {
            Rectangle.verifyBounds(lowerBound, upperBound);
        }
    }

}
