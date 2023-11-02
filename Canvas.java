import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Canvas<T extends Comparable<? super T>> {
    private final NavigableMap<T, NavigableSet<T>> pixels;

    private Canvas(Map<T, Set<T>> pixels) {
        assert pixels != null;
        assert pixels.values().stream().noneMatch(Objects::isNull);
        assert pixels.values().stream().noneMatch(Set::isEmpty);
        this.pixels = new TreeMap<>(pixels.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new TreeSet<>(entry.getValue()))));
    }

    /**
     * Return a new Canvas
     * @param pixels Map of pixels to create Canvas from
     * @return A new Canvas
     */
    public static <T extends Comparable<? super T>> Canvas<T> of(Map<T, Set<T>> pixels) {
        return new Canvas<>(containsNoNullValues(Objects.requireNonNull(pixels)));
    }

    /**
     * Return an unsorted map from a sorted map
     * @param sortedMap A sorted map to create an unsorted map from
     * @return An unsorted map from a sorted map
     */
    public static <T extends Comparable<? super T>> Map<T, Set<T>> unsortedMap (NavigableMap<T, NavigableSet<T>> sortedMap) {
        return new HashMap<>(Objects.requireNonNull(sortedMap).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new HashSet<>(entry.getValue()))));
    }


    /**
     * Check that map contains no null key values
     * @param map Map to check for null key values
     * @return Same map provided it does not contain null values
     */
    private static <T extends Comparable<? super T>> Map<T,Set<T>> containsNoNullValues(Map<T, Set<T>> map) {
        assert map != null;
        assert map.values().stream().noneMatch(Objects::isNull);
        assert map.values().stream().noneMatch(Set::isEmpty);
        if (map.values().stream().anyMatch(valueSet -> valueSet.stream().anyMatch(Objects::isNull)))
            throw new IllegalArgumentException("Values of map must not contain null values");
        return map;
    }

    /**
     * Return a set of keys for this canvas
     * @return A set of keys for this canvas
     */
    public Set<T> xSet() {
        return pixels.keySet();
    }

    /**
     * Return a set of the values corresponding to this key
     * @param x Key to return values from
     * @return A set of the values corresponding to this key
     */
    public NavigableSet<T> ySet(T x) {
        return pixels.getOrDefault(Objects.requireNonNull(x), new TreeSet<>());
    }

    /**
     * Return if point exists on this canvas
     * @param x X coordinate for point
     * @param y Y coordinate for point
     * @return If point exists on this canvas
     */
    public boolean hasPoint(T x, T y) {return ySet(x).contains(Objects.requireNonNull(y));}

    /**
     * Return number of points on this canvas
     * @return Number of points on this canvas
     */
    public long pointCount() {
        return pixels.values().stream()
                .map(Set::size)
                .reduce(0, Integer::sum);
    }

    /**
     * Add a point to this Canvas
     * @param x X coordinate of new point
     * @param y Y coordinate of new point
     * @return Whether the point was successfully added to map or not
     */
    public final boolean add(T x, T y) {
        return isValidPoint(x,y) && ySet(x).add(y);
    }

    /**
     * Check if point exists or is null
     * @param x X coordinate of point to put into canvas
     * @param y Y coordinate of point to put into canvas
     * @return If point exists or is null
     */
    private final boolean isValidPoint (T x, T y) {
        try {
            return !hasPoint(x,y);
        } catch (NullPointerException n) {
            return false;
        }
    }

    /**
     * Return the number of points in a slice corresponding to the rectangle of the canvas
     * @param rectangle Rectangle to determine slice
     * @return Number of points in a slice corresponding to the rectangle of the canvas
     */
    public final long sliceCount(Rectangle<T> rectangle) {
        return slice(rectangle).pointCount();
    }

    /**
     * Return a sub canvas of this canvas
     * @param xMin Lower bound of this sub canvas
     * @param xMax Upper bound of this sub canvas
     * @return Sub canvas of this canvas
     */
    public final Canvas<T> subCanvas(T xMin, T xMax) {
        return Canvas.of( Canvas.unsortedMap(boundedKeysMap(pixels, Objects.requireNonNull(xMin), Objects.requireNonNull(xMax))));
    }

    /**
     * Return a canvas of all points bounded by rectangle
     * @param rectangle Rectangle to define which points are in new Canvas
     * @return A canvas of all points bounded by rectangle
     */
    public final Canvas<T> slice(Rectangle<T> rectangle) {
        return Canvas.of(
                newPixelMap(
                        boundedKeysMap(pixels, Objects.requireNonNull(rectangle).left(), rectangle.right()).keySet().stream(),
                        key -> boundedValues(boundedKeysMap(pixels, rectangle.left(), rectangle.right()),
                                key, rectangle.bottom(), rectangle.top())
                )
        );
    }

    /**
     * Return a map from a stream of x values and a supplier of y values
     * @param xValues Stream of x values to generate y coordinates
     * @param valueMapper Generator for y values
     * @return A navigable map from a stream of x values and their corresponding y values
     */
    private final Map<T, Set<T>> newPixelMap(Stream<T> xValues, Function<T, NavigableSet<T>> valueMapper) {
        assert xValues != null;
        assert valueMapper != null;
        return xValues.collect(Collectors.toMap(Function.identity(), valueMapper));
    }

    /**
     * Return a new canvas representing a transformation of this canvas
     * @param horizontalMapper Generator for keys
     * @param verticalMapper Generator for values
     * @return A new canvas representing a transformation of this canvas
     */
    public final Canvas<T> transform(Function<T, T> horizontalMapper, Function<T, T> verticalMapper) {
        return Canvas.of(
                newPixelMap(pixels.entrySet(),
                        Objects.requireNonNull(horizontalMapper),
                        Objects.requireNonNull(verticalMapper))
        );
    }

    /**
     * Return a new map with keys and values modified by input x and y functions
     * @param s Set of entries of a map used as inputs for each function to generate new key/value pairs for new map
     * @param horizontalMapper Function to generate new x value
     * @param verticalMapper Function to generate new y value
     * @return A new map with keys and values modified by input x and y functions
     */
    private final Map<T, Set<T>> newPixelMap(Set<Map.Entry<T, NavigableSet<T>>> s,
                                             Function<T, T> horizontalMapper,
                                             Function<T, T> verticalMapper) {
        assert s != null;
        assert !s.isEmpty();
        assert s.stream().noneMatch(entry -> entry.getValue().isEmpty());
        assert horizontalMapper != null;
        assert verticalMapper != null;

        return s.stream()
                .map(entry -> newEntry(entry, horizontalMapper, verticalMapper))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Return a new Entry with newly generated key/value pair
     * @param entry Entry to generate input values for functions
     * @param horizontalMapper Key generator for this new Entry
     * @param verticalMapper Value generator for this new Entry
     * @return A new Entry with newly generated key/value pair
     */
    private final Map.Entry<T, Set<T>> newEntry(Map.Entry<T, NavigableSet<T>> entry,
                                                        Function<T, T> horizontalMapper,
                                                        Function<T, T> verticalMapper) {
        assert entry != null;
        assert !entry.getValue().isEmpty();
        assert horizontalMapper != null;
        assert verticalMapper != null;

        return new AbstractMap.SimpleEntry<>(horizontalMapper.apply(entry.getKey()),
                entry.getValue().stream().map(verticalMapper::apply).collect(Collectors.toSet()));
    }

    /**
     * Return a partially bounded map by keys
     * @param map Map to extract bounded map from
     * @param lowerBound Lower bound for new map
     * @param upperBound Upper bound for new map
     * @return A partially bounded map by keys
     */
    private final NavigableMap<T, NavigableSet<T>> boundedKeysMap(NavigableMap<T, NavigableSet<T>> map, T lowerBound, T upperBound) {
        assert map != null;
        assert !map.isEmpty();
        assert map.values().stream().noneMatch(Objects::isNull);
        assert map.values().stream().noneMatch(Set::isEmpty);
        assert lowerBound != null;
        assert upperBound != null;

        return new TreeMap<>(map.subMap(map.ceilingKey(lowerBound), true, map.floorKey(upperBound), true));
    }

    /**
     * Return a bounded set of values
     * @param map Map to extract values from
     * @param key Key to map values in map from`
     * @param lowerBound Lower bound for new set
     * @param upperBound Upper bound for new set
     * @return A bounded set of values
     */
    private final NavigableSet<T> boundedValues(NavigableMap<T, NavigableSet<T>> map, T key, T lowerBound, T upperBound) {
        assert map != null;
        assert !map.isEmpty();
        assert map.values().stream().noneMatch(Objects::isNull);
        assert map.values().stream().noneMatch(Set::isEmpty);
        assert key != null;
        assert lowerBound != null;
        assert upperBound != null;

        return new TreeSet<>(map.get(key).subSet(map.get(key).ceiling(lowerBound), true, map.get(key).floor(upperBound), true));
    }

    /**
     * Return a new canvas representing a transformation of this canvas
     * @param horizontalMapper Generator for keys
     * @param verticalMapper Generator for values
     * @return A new canvas representing a transformation of this canvas
     */
    public final Canvas<T> transform(BiFunction<T, T, T> horizontalMapper, BiFunction<T, T, T> verticalMapper) {
        return Canvas.of(
                newPixelMap(pixels.entrySet(),
                        Objects.requireNonNull(horizontalMapper),
                        Objects.requireNonNull(verticalMapper))
        );
    }

    /**
     * Return a new map with keys and values modified by input x and y functions
     * @param s Set of entries of a map used as inputs for each function to generate new key/value pairs for new map
     * @param horizontalMapper Function to generate new x value
     * @param verticalMapper Function to generate new y value
     * @return A new map with keys and values modified by input x and y functions
     */
    private final Map<T, Set<T>> newPixelMap(Set<Map.Entry<T, NavigableSet<T>>> s,
                                             BiFunction<T, T, T> horizontalMapper,
                                             BiFunction<T, T, T> verticalMapper) {
        assert s != null;
        assert !s.isEmpty();
        assert s.stream().noneMatch(entry -> entry.getValue().isEmpty());
        assert horizontalMapper != null;
        assert verticalMapper != null;

        return s.stream()
                .flatMap(entry -> newEntrySet(entry, horizontalMapper, verticalMapper).stream())
                .collect(Collectors.groupingBy(
                        Coordinate::x,
                        Collectors.mapping(Coordinate::y, Collectors.toSet())
                ));
    }

    /**
     * Return a new Entry with newly generated key/value pair
     * @param entry Entry to generate input values for functions
     * @param horizontalMapper Key generator for this new Entry
     * @param verticalMapper Value generator for this new Entry
     * @return A new Entry with newly generated key/value pair
     */
    private final Set<Coordinate<T>> newEntrySet(Map.Entry<T, NavigableSet<T>> entry,
                                                 BiFunction<T, T, T> horizontalMapper,
                                                 BiFunction<T, T, T> verticalMapper) {
        assert entry != null;
        assert !entry.getValue().isEmpty();
        assert horizontalMapper != null;
        assert verticalMapper != null;

        return entry.getValue().stream()
                .map(value -> new Coordinate<>(horizontalMapper.apply(entry.getKey(), value),
                        verticalMapper.apply(entry.getKey(), value)))
                .collect(Collectors.toSet());
    }

    private record Coordinate<T>(T x, T y) {}

    class TestHook {
        public Map<T,Set<T>> containsNoNullValues(Map<T, Set<T>> map) {
            return Canvas.containsNoNullValues(map);
        }
        public boolean isValidPoint(T x, T y) {
            return Canvas.this.isValidPoint(x, y);
        }
        public Map<T, Set<T>> newPixelMap(Stream<T> xValues, Function<T, NavigableSet<T>> valueMapper) {
            return Canvas.this.newPixelMap(xValues, valueMapper);
        }

        public NavigableMap<T, NavigableSet<T>> boundedKeysMap(NavigableMap<T, NavigableSet<T>> map,
                                                                       T lowerBound, T upperBound) {
            return Canvas.this.boundedKeysMap(map, lowerBound, upperBound);
        }

        public NavigableSet<T> boundedValues(NavigableMap<T, NavigableSet<T>> map, T key,
                                                     T lowerBound, T upperBound) {
            return Canvas.this.boundedValues(map, key, lowerBound, upperBound);
        }

        public Map<T, Set<T>> newPixelMap(Set<Map.Entry<T, NavigableSet<T>>> s,
                                                           Function<T, T> horizontalMapper,
                                                           Function<T, T> verticalMapper) {
            return Canvas.this.newPixelMap(s, horizontalMapper, verticalMapper);
        }

        public Map.Entry<T, Set<T>> newEntry(Map.Entry<T, NavigableSet<T>> entry,
                                             Function<T, T> horizontalMapper,
                                             Function<T, T> verticalMapper) {
            return Canvas.this.newEntry(entry, horizontalMapper, verticalMapper);
        }

        public Map<T, Set<T>> newPixelMap(Set<Map.Entry<T, NavigableSet<T>>> s,
                                          BiFunction<T, T, T> horizontalMapper,
                                          BiFunction<T, T, T> verticalMapper) {
            return Canvas.this.newPixelMap(s, horizontalMapper, verticalMapper);
        }

        public Set<Coordinate<T>> newEntrySet(Map.Entry<T, NavigableSet<T>> entry,
                                    BiFunction<T, T, T> horizontalMapper,
                                    BiFunction<T, T, T> verticalMapper) {
            return Canvas.this.newEntrySet(entry, horizontalMapper, verticalMapper);
        }

        public Coordinate<T> newCoordinate(T x, T y) {
            return new Coordinate<>(x, y);
        }
    }
}
