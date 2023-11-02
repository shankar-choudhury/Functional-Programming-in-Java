import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CanvasTest {

    @Test
    void rectangleOf() {
        try {
            var r = Rectangle.of(4, -2, -3, 4);
        } catch (Exception e) {
            fail("No exception should be thrown");
        }

        assertThrows(NullPointerException.class, () -> Rectangle.of(7, 2, 1, null));
        assertThrows(IllegalArgumentException.class, () -> Rectangle.of(7, 2, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> Rectangle.of(-7, 2, -1, 1));
    }

    @Test
    void canvasOf() {
        try {
            var c = Canvas.of(new TreeMap<Integer, Set<Integer>>());
        } catch (Exception e) {
            fail("No exception should be thrown");
        } catch (AssertionError a) {
            fail("No assertion should be contradicted");
        }

        assertThrows(NullPointerException.class, () -> Canvas.of(null));

        var i = new ArrayList<Integer>();
        i.add(3);
        i.add(4);
        i.add(null);
        i.add(7);
        var containsNull = new HashSet<>(i);

        assertThrows(IllegalArgumentException.class, () -> Canvas.of(Map.of(1, containsNull, 3, Set.of(7), 8, Set.of(1, 2, 5))));
    }

    @Test
    void xSet() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        var c = Canvas.of(m);
        Set<Integer> s = Set.of(-1, 0, 2);
        assertEquals(s, c.xSet());
    }

    @Test
    void ySet() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        var c = Canvas.of(m);

        var ySetOfNegativeOne = Set.of(0);
        var ySetOfZero = Set.of(-3, 4);
        var ySetOfTwo = Set.of(0);
        assertEquals(ySetOfNegativeOne, c.ySet(-1));
        assertEquals(ySetOfZero, c.ySet(0));
        assertEquals(ySetOfTwo, c.ySet(2));
        assertEquals(0, c.ySet(3).size());

        assertThrows(NullPointerException.class, () -> c.ySet(null));
    }

    @Test
    void hasPoint() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        var c = Canvas.of(m);

        assertTrue(c.hasPoint(-1, 0) );
        assertFalse(c.hasPoint(0, -4));
        assertFalse(c.hasPoint(4,5));
    }

    @Test
    void pointCount() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        var c = Canvas.of(m);
        assertEquals(4L, c.pointCount());
    }

    @Test
    void add() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-1, new HashSet<>(List.of(0)));
        m.put(0, new HashSet<>(List.of(-3, 4)));
        m.put(2, new HashSet<>(List.of(0)));
        var c = Canvas.of(m);

        assertFalse(c.add(null,4));
        assertFalse(c.add(4,null));
        assertFalse(c.add(0,-3));

        assertTrue(c.add(2,1));
        assertTrue(c.add(3,0));
    }

    @Test
    void unsortedMap() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(-1, new TreeSet<>(List.of(0)));
        m.put(0, new TreeSet<>(List.of(-3, 4)));
        m.put(2, new TreeSet<>(List.of(0)));
        var newMap = Canvas.unsortedMap(m);
        assertEquals(HashMap.class, newMap.getClass());
        for (Map.Entry<Integer, Set<Integer>> e : newMap.entrySet())
            assertEquals(HashSet.class, e.getValue().getClass());
    }

    @Test
    void slice() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(4, new HashSet<>(List.of(4,-4)));
        m.put(-4, new HashSet<>(List.of(4,-4)));
        m.put(2, new HashSet<>(List.of(1, -3)));
        m.put(-2, new HashSet<>(List.of(2, 3)));
        var c = Canvas.of(m);

        assertThrows(NullPointerException.class, () -> c.slice(null));

        var r = new Rectangle<>(3, -3, -3, 3);
        var d = c.slice(r);
        assertEquals(Set.of(-2,2), d.xSet());
        assertEquals(Set.of(-3,1), d.ySet(2));
        assertEquals(Set.of(2,3), d.ySet(-2));
    }

    @Test
    void sliceCount() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(4, new TreeSet<>(List.of(4,-4)));
        m.put(-4, new TreeSet<>(List.of(4,-4)));
        m.put(2, new TreeSet<>(List.of(1,-3)));
        m.put(-2, new TreeSet<>(List.of(2,3)));
        var c = Canvas.of(m);
        var r = new Rectangle<>(3, -3, -3, 3);

        assertThrows(NullPointerException.class, () -> c.sliceCount(null));
        assertEquals(4L, c.sliceCount(r));
    }

    @Test
    void subCanvas() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-3, new HashSet<>(List.of(0)));
        m.put(-1, new HashSet<>(List.of(-3, 4)));
        m.put(1, new HashSet<>(List.of(1, 3, 4, 7)));
        m.put(2, new HashSet<>(List.of(0)));
        var c = Canvas.of(m);
        var d = c.subCanvas(-2, 2);

        assertThrows(NullPointerException.class, () -> c.subCanvas(null, 2));
        assertThrows(NullPointerException.class, () -> c.subCanvas(-2, null));

        assertEquals(7L, d.pointCount());
        assertEquals(new HashSet<>(List.of(-3, 4)), d.ySet(-1));
        assertEquals(new HashSet<>(List.of(1, 3, 4, 7)), d.ySet(1));
        assertEquals(new HashSet<>(List.of(0)), d.ySet(2));
    }

    @Test
    void transform() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(4, new TreeSet<>(List.of(4,-4)));
        m.put(-4, new TreeSet<>(List.of(6,-11)));
        var c = Canvas.of(Canvas.unsortedMap(m));

        assertThrows(NullPointerException.class, () -> c.transform(null, value -> value * 8));
        assertThrows(NullPointerException.class, () -> c.transform(key -> key + 2, null));

        var d = c.transform(key -> key + 2, value -> value * 3);
        assertEquals(Set.of(6, -2), d.xSet());
        assertEquals(Set.of(12, -12), d.ySet(6));
        assertEquals(Set.of(18, -33), d.ySet(-2));
    }

    @Test
    void verifyBounds() {
        assertThrows(IllegalArgumentException.class, () -> Rectangle.TestHook.verifyBounds(7, 2));

        try {
            Rectangle.TestHook.verifyBounds(2, 7);
        } catch (Throwable t) {
            fail("Nothing should be thrown");
        }
    }

    @Test
    void containsNoNullValues() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-3, new HashSet<>(List.of(0)));
        m.put(-1, new HashSet<>(List.of(-3, 4)));
        m.put(1, new HashSet<>(List.of(1, 3, 4, 7)));
        m.put(2, new HashSet<>(List.of(0)));
        Canvas<Integer> h = Canvas.of(m);
        Canvas<Integer>.TestHook t = h.new TestHook();

        var i = new ArrayList<Integer>();
        i.add(3);
        i.add(4);
        i.add(null);
        i.add(7);
        var containsNull = new HashSet<>(i);
        var emptySet = new HashSet<Integer>();
        var map = Map.of(1, Set.of(9), 3, Set.of(7), 8, Set.of(1, 2, 5));

        assertThrows(AssertionError.class, () -> t.containsNoNullValues(null));
        assertThrows(AssertionError.class, () -> t.containsNoNullValues(Map.of(1, emptySet, 3, Set.of(7), 8, Set.of(1, 2, 5))));
        assertThrows(IllegalArgumentException.class, () -> t.containsNoNullValues(Map.of(1, containsNull, 3, Set.of(7), 8, Set.of(1, 2, 5))));
        assertEquals(map, t.containsNoNullValues(map));
    }

    @Test
    void isValidPoint() {
        var m = new TreeMap<Integer, Set<Integer>>();
        m.put(-3, new HashSet<>(List.of(0)));
        m.put(-1, new HashSet<>(List.of(-3, 4)));
        m.put(1, new HashSet<>(List.of(1, 3, 4, 7)));
        m.put(2, new HashSet<>(List.of(0)));
        Canvas<Integer> h = Canvas.of(m);
        Canvas<Integer>.TestHook t = h.new TestHook();

        assertFalse(t.isValidPoint(null,4));
        assertFalse(t.isValidPoint(4,null));
        assertFalse(t.isValidPoint(2,0));
        assertTrue(t.isValidPoint(2,1));
    }

    @Test
    void boundedKeysMap() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(-3, new TreeSet<>(List.of(0)));
        m.put(-1, new TreeSet<>(List.of(-3, 4)));
        m.put(1, new TreeSet<>(List.of(1, 3, 4, 7)));
        m.put(2, new TreeSet<>(List.of(0)));
        Canvas<Integer> h = Canvas.of(Canvas.unsortedMap(m));
        Canvas<Integer>.TestHook t = h.new TestHook();

        assertThrows(AssertionError.class, () -> t.boundedKeysMap(null, -1, 1));
        assertThrows(AssertionError.class, () -> t.boundedKeysMap(m, null, 1));
        assertThrows(AssertionError.class, () -> t.boundedKeysMap(m, -1, null));

        m.put(4, null);
        assertThrows(AssertionError.class, () -> t.boundedKeysMap(m, -1, 1));
        m.remove(4);

        assertThrows(AssertionError.class, () -> t.boundedKeysMap(new TreeMap<>(), -1, 1));

        var correctMSubMap = Map.of(-1, new TreeSet<Integer>(List.of(-3, 4)), 1, new TreeSet<>(List.of(1, 3, 4, 7)));
        assertEquals(correctMSubMap, t.boundedKeysMap(m, -1, 1));
    }

    @Test
    void boundedValues() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(-3, new TreeSet<>(List.of(0)));
        m.put(-1, new TreeSet<>(List.of(-3, 4)));
        m.put(1, new TreeSet<>(List.of(1, 3, 4, 7)));
        m.put(2, new TreeSet<>(List.of(0)));
        Canvas<Integer> h = Canvas.of(Canvas.unsortedMap(m));
        Canvas<Integer>.TestHook t = h.new TestHook();

        assertThrows(AssertionError.class, () -> t.boundedValues(new TreeMap<>(), -1, 1, 3));
        assertThrows(AssertionError.class, () -> t.boundedValues(null, -1, 1, 3));
        assertThrows(AssertionError.class, () -> t.boundedValues(m, null, 1, 3));
        assertThrows(AssertionError.class, () -> t.boundedValues(m, -1, null, 3));
        assertThrows(AssertionError.class, () -> t.boundedValues(m, -1, 1, null));

        m.put(4, null);
        assertThrows(AssertionError.class, () -> t.boundedValues(m, -1, 1, 3));

        m.replace(4, new TreeSet<>());
        assertThrows(AssertionError.class, () -> t.boundedValues(m, -1, 1, 3));

        var p = new TreeMap<Integer, NavigableSet<Integer>>(Map.of(-1, new TreeSet<Integer>(List.of(-2, 3, 4)), 1, new TreeSet<>(List.of(1, 3, 4, 7))));
        assertEquals(Set.of(3,4), t.boundedValues(p, -1, 3, 4));
        assertEquals(Set.of(4,7), t.boundedValues(p, 1, 4, 7));
    }

    @Test
    void newPixelMapForSlice() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(4, new TreeSet<>(List.of(4,-4)));
        m.put(-4, new TreeSet<>(List.of(4,-4)));
        m.put(2, new TreeSet<>(List.of(1, -3)));
        m.put(-2, new TreeSet<>(List.of(2, 3)));
        var c = Canvas.of(Canvas.unsortedMap(m));
        var r = new Rectangle<>(3, -3, -3, 3);
        Canvas<Integer>.TestHook t = c.new TestHook();

        assertThrows(AssertionError.class, () ->
                t.newPixelMap(null, key ->
                        t.boundedValues(t.boundedKeysMap(m, r.left(), r.right()),
                                key, r.bottom(), r.top())));
        assertThrows(AssertionError.class, () ->
                t.newPixelMap(t.boundedKeysMap(m, r.left(), r.right()).keySet().stream(), null));

        var p = t.newPixelMap(
                t.boundedKeysMap(m, r.left(), r.right()).keySet().stream(),
                key -> t.boundedValues(t.boundedKeysMap(m, r.left(), r.right()),
                        key, r.bottom(), r.top()));
        assertEquals(Set.of(-2, 2), p.keySet());
        assertEquals(Set.of(1, -3), p.get(2));
        assertEquals(Set.of(2, 3), p.get(-2));
    }

    @Test
    void newPixelMapForSingleFunctionTransform() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(4, new TreeSet<>(List.of(4,-4)));
        m.put(-4, new TreeSet<>(List.of(6,-11)));
        var c = Canvas.of(Canvas.unsortedMap(m));
        Canvas<Integer>.TestHook t = c.new TestHook();

        assertThrows(AssertionError.class, () -> t.newPixelMap(null, xValue -> xValue + 4, yValue -> yValue + 5));
        assertThrows(AssertionError.class, () -> t.newPixelMap(Set.of(), xValue -> xValue + 4, yValue -> yValue + 5));
        m.put(5, new TreeSet<>());
        assertThrows(AssertionError.class, () -> t.newPixelMap(m.entrySet(), xValue -> xValue + 4, yValue -> yValue + 5));
        m.remove(5);
        assertThrows(AssertionError.class, () -> t.newPixelMap(m.entrySet(), null, yValue -> yValue + 5));
        assertThrows(AssertionError.class, () -> t.newPixelMap(m.entrySet(), xValue -> xValue + 4, null));

        var cShifted = t.newPixelMap(m.entrySet(), xValue -> xValue + 4, yValue -> yValue + 5);
        assertEquals(Set.of(8, 0), cShifted.keySet());
        assertEquals(Set.of(9, 1), cShifted.get(8));
        assertEquals(Set.of(11, -6), cShifted.get(0));
    }

    @Test
    void newEntryWithSingleFunction() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(4, new TreeSet<>(List.of(4,-4)));
        m.put(-4, new TreeSet<>(List.of(4,-4)));
        var c = Canvas.of(Canvas.unsortedMap(m));
        Canvas<Integer>.TestHook t = c.new TestHook();
        var badEntry = new AbstractMap.SimpleEntry<Integer, NavigableSet<Integer>>(2, new TreeSet<>(Set.of()));
        var goodEntry = new AbstractMap.SimpleEntry<Integer, NavigableSet<Integer>>(2, new TreeSet<>(Set.of(1, 2)));

        assertThrows(AssertionError.class, () -> t.newEntry(null, x -> x + 2, y -> y + 2));
        assertThrows(AssertionError.class, () -> t.newEntry(badEntry, x -> x + 2, y -> y + 2));
        assertThrows(AssertionError.class, () -> t.newEntry(goodEntry, null, y -> y + 2));
        assertThrows(AssertionError.class, () -> t.newEntry(goodEntry, x -> x + 2, null));

        var expectedEntry = new AbstractMap.SimpleEntry<Integer, NavigableSet<Integer>>(4, new TreeSet<>(Set.of(3, 4)));
        assertEquals(expectedEntry, t.newEntry(goodEntry, x -> x + 2, y -> y + 2));
    }

    @Test
    void newPixelMapForBiFunctionTransform() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(2, new TreeSet<>(List.of(1,-3)));
        m.put(-2, new TreeSet<>(List.of(-1,3)));
        var c = Canvas.of(Canvas.unsortedMap(m));
        Canvas<Integer>.TestHook t = c.new TestHook();

        assertThrows(AssertionError.class, () -> t.newPixelMap(null, (x, y) -> x + y, (x, y) -> x * y));
        assertThrows(AssertionError.class, () -> t.newPixelMap(Set.of(), (x, y) -> x + y, (x, y) -> x * y));
        m.put(5, new TreeSet<>());
        assertThrows(AssertionError.class, () -> t.newPixelMap(m.entrySet(), (x, y) -> x + y, (x, y) -> x * y));
        m.remove(5);
        assertThrows(AssertionError.class, () -> t.newPixelMap(m.entrySet(), null, (x, y) -> x * y));
        assertThrows(AssertionError.class, () -> t.newPixelMap(m.entrySet(), (x, y) -> x + y, null));

        var cBiTransformed = t.newPixelMap(m.entrySet(), (x, y) -> x + y, (x, y) -> x * y);
        assertEquals(Set.of(-1, 1, -3, 3), cBiTransformed.keySet());
        assertEquals(Set.of(-6), cBiTransformed.get(-1));
        assertEquals(Set.of(-6), cBiTransformed.get(1));
        assertEquals(Set.of(2), cBiTransformed.get(-3));
        assertEquals(Set.of(2), cBiTransformed.get(3));
    }

    @Test
    void newEntrySet() {
        var m = new TreeMap<Integer, NavigableSet<Integer>>();
        m.put(4, new TreeSet<>(List.of(4,-4)));
        m.put(-4, new TreeSet<>(List.of(4,-4)));
        var c = Canvas.of(Canvas.unsortedMap(m));
        Canvas<Integer>.TestHook t = c.new TestHook();
        var badEntry = new AbstractMap.SimpleEntry<Integer, NavigableSet<Integer>>(2, new TreeSet<>(Set.of()));
        var goodEntry = new AbstractMap.SimpleEntry<Integer, NavigableSet<Integer>>(2, new TreeSet<>(Set.of(1, 2)));

        assertThrows(AssertionError.class, () -> t.newEntrySet(null, (x, y) -> x + 2, (x, y) -> y + 5));
        assertThrows(AssertionError.class, () -> t.newEntrySet(badEntry, (x, y) -> x + 2, (x, y) -> y + 5));
        assertThrows(AssertionError.class, () -> t.newEntrySet(goodEntry, null, (x, y) -> y + 5));
        assertThrows(AssertionError.class, () -> t.newEntrySet(goodEntry, (x, y) -> x + 2, null));

        var expectedEntry = new HashSet<>(Set.of(t.newCoordinate(4, 6), t.newCoordinate(4, 7)));
        assertEquals(expectedEntry, t.newEntrySet(goodEntry, (x, y) -> x + 2, (x, y) -> y + 5));
    }

    @Test
    void shift() {
        var m = new TreeMap<Double, NavigableSet<Double>>();
        m.put(4.0, new TreeSet<>(List.of(4.0,-4.0)));
        m.put(-4.0, new TreeSet<>(List.of(4.0,-4.0)));
        var c = Canvas.of(Canvas.unsortedMap(m));

        assertThrows(NullPointerException.class, () -> CanvasTransformer.shift(null, 2.0, 5.0));
        assertThrows(NullPointerException.class, () -> CanvasTransformer.shift(c, null, 5.0));
        assertThrows(NullPointerException.class, () -> CanvasTransformer.shift(c, 2.0, null));

        var shiftedC = CanvasTransformer.shift(c, 2.0, 5.0);
        assertEquals(Set.of(6.0, -2.0), shiftedC.xSet());
        assertEquals(Set.of(9.0, 1.0), shiftedC.ySet(6.0));
        assertEquals(Set.of(9.0, 1.0), shiftedC.ySet(-2.0));
    }

    @Test
    void rotate() {
        var m = new TreeMap<Double, NavigableSet<Double>>();
        m.put(4.0, new TreeSet<>(List.of(4.0,-4.0)));
        m.put(-4.0, new TreeSet<>(List.of(4.0,-4.0)));
        var c = Canvas.of(Canvas.unsortedMap(m));

        assertThrows(NullPointerException.class, () -> CanvasTransformer.rotate(null, 1.570796));
        assertThrows(NullPointerException.class, () -> CanvasTransformer.rotate(c, null));

        var rotatedC = CanvasTransformer.rotate(c, 1.570796);

        assertEquals(Set.of(-4.000001307179373, -3.9999986928202, 3.9999986928202, 4.000001307179373), rotatedC.xSet());
        assertEquals(Set.of(-3.9999986928202), rotatedC.ySet(-4.000001307179373));
        assertEquals(Set.of(4.000001307179373), rotatedC.ySet(-3.9999986928202));
        assertEquals(Set.of(-4.000001307179373), rotatedC.ySet(3.9999986928202));
        assertEquals(Set.of(3.9999986928202), rotatedC.ySet(4.000001307179373));
    }

    @Test
    void magnify() {
        var m = new TreeMap<Double, NavigableSet<Double>>();
        m.put(4.0, new TreeSet<>(List.of(4.0,-4.0)));
        m.put(-4.0, new TreeSet<>(List.of(4.0,-4.0)));
        var c = Canvas.of(Canvas.unsortedMap(m));

        assertThrows(NullPointerException.class, () -> CanvasTransformer.magnify(null, 2.0));
        assertThrows(NullPointerException.class, () -> CanvasTransformer.magnify(c, null));

        var shiftedC = CanvasTransformer.magnify(c, 2.0);
        assertEquals(Set.of(8.0, -8.0), shiftedC.xSet());
        assertEquals(Set.of(8.0, -8.0), shiftedC.ySet(8.0));
        assertEquals(Set.of(8.0, -8.0), shiftedC.ySet(-8.0));
    }

}
