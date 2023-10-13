import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Canvas<T extends Comparable<? super T>> {
    private final Map<T, Set<T>> pixels;

    private Canvas(Map<T, Set<T>> pixels) {
        assert pixels != null;
        this.pixels = new TreeMap<>(pixels);
    }

    public static <T extends Comparable<? super T>> Canvas<T> of(Map<T, Set<T>> pixels) {
        return new Canvas<>(Objects.requireNonNull(pixels));
    }

    public Set<T> xSet() {
        return pixels.keySet();
    }

    public Set<T> ySet(T x) {
        return pixels.containsKey(Objects.requireNonNull(x)) ? pixels.get(x) : Set.of();
    }

    public boolean hasPoint(T x, T y) {return ySet(x).contains(Objects.requireNonNull(y));}

    public long pointCount() {
        return pixels.values().stream()
                .map(Set::size)
                .reduce(0, Integer::sum);
    }

    public final boolean add(T x, T y) {
        return isValidPoint(x,y) && (pixels.containsKey(x) ? ySet(x).add(y) : newEntry(x, new HashSet<>(List.of(y))));
    }

    private final boolean isValidPoint (T x, T y) {
        try {
            return !hasPoint(x,y);
        } catch (NullPointerException n) {
            return false;
        }
    }

    private final boolean newEntry(T x, Set<T> y) {
        assert x != null;
        assert y != null;
        assert !pixels.containsKey(x);
        try {
            pixels.put(x, y);
            return true;
        } catch (Exception e) {
            assert false : e;
            return false;
        }
    }

    public final Canvas<T> slice(Rectangle<T> rectangle) {
        Objects.requireNonNull(rectangle);
        return Canvas.of( newPixelMap(
                xSet().stream()
                        .filter(x -> inBoundsOf(rectangle, x, true)),
                x -> ySet(x).stream()
                        .filter(y -> inBoundsOf(rectangle, y, false))
                        .collect(Collectors.toSet())
                )
        );
    }

    private final Map<T, Set<T>> newPixelMap(Stream<T> xValues, Function<T, Set<T>> valueMapper) {
        assert xValues != null;
        assert valueMapper != null;
        return xValues.collect(Collectors.toMap(Function.identity(), valueMapper));
    }

    private final boolean inBoundsOf(Rectangle<T> rectangle, T coordinate, boolean isXCoordinate) {
        assert coordinate != null;
        assert rectangle != null;
        if (isXCoordinate)
            return coordinate.compareTo(rectangle.left()) >= 0 && coordinate.compareTo(rectangle.right()) <= 0;
        else
            return coordinate.compareTo(rectangle.bottom()) >= 0 && coordinate.compareTo(rectangle.top()) <= 0;
    }

    public final long sliceCount(Rectangle<T> rectangle) {
        return slice(rectangle).pointCount();
    }

}
