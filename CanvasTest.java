import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CanvasTest {
    @Test
    void of() {
        Map<Integer, Set<Integer>> l = null;
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        try {
            Canvas<Integer> c = Canvas.of(m);
        } catch (Exception e) {
            fail("No exception should be thrown");
        }

        assertThrows(NullPointerException.class, () -> Canvas.of(l));
    }

    @Test
    void xSet() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        Canvas<Integer> c = Canvas.of(m);
        Set<Integer> s = Set.of(-1, 0, 2);
        assertEquals(s, c.xSet());
    }

    @Test
    void ySet() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        Canvas<Integer> c = Canvas.of(m);

        Set<Integer> ySetOfNegativeOne = Set.of(0);
        Set<Integer> ySetOfZero = Set.of(-3, 4);
        Set<Integer> ySetOfTwo = Set.of(0);
        assertEquals(ySetOfNegativeOne, c.ySet(-1));
        assertEquals(ySetOfZero, c.ySet(0));
        assertEquals(ySetOfTwo, c.ySet(2));
        assertEquals(0, c.ySet(3).size());

        assertThrows(NullPointerException.class, () -> c.ySet(null));
    }

    @Test
    void hasPoint() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        Canvas<Integer> c = Canvas.of(m);

        assertTrue(c.hasPoint(-1, 0) );
        assertFalse(c.hasPoint(0, -4));
        assertFalse(c.hasPoint(4,5));
    }

    @Test
    void pointCount() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        m.put(-1, Set.of(0));
        m.put(0, Set.of(-3, 4));
        m.put(2, Set.of(0));
        Canvas<Integer> c = Canvas.of(m);
        assertEquals(4L, c.pointCount());
    }

    @Test
    void add() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        Set<Integer> ySetOfNegativeOne = new HashSet<>(List.of(0));
        Set<Integer> ySetOfZero = new HashSet<>(List.of(-3, 4));
        Set<Integer> ySetOfTwo = new HashSet<>(List.of(0));
        m.put(-1, ySetOfNegativeOne);
        m.put(0, ySetOfZero);
        m.put(2, ySetOfTwo);
        Canvas<Integer> c = Canvas.of(m);

        assertFalse(c.add(null,4));
        assertFalse(c.add(4,null));
        assertFalse(c.add(0,-3));

        assertTrue(c.add(2,1));
        assertTrue(c.add(3,0));
    }

    @Test
    void slice() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        m.put(4, new HashSet<>(List.of(4,-4)));
        m.put(-4, new HashSet<>(List.of(4,-4)));
        m.put(2, new HashSet<>(List.of(2, -2)));
        m.put(-2, new HashSet<>(List.of(2, -2)));
        Canvas<Integer> c = Canvas.of(m);

        Rectangle<Integer> r = new Rectangle<>(3, -3, -3, 3);
        Canvas<Integer> d = c.slice(r);
        assertEquals(Set.of(-2,2), d.xSet());
        assertEquals(Set.of(-2,2), d.ySet(2));
        assertEquals(Set.of(-2,2), d.ySet(-2));
    }

    @Test
    void sliceCount() {
        Map<Integer, Set<Integer>> m = new TreeMap<>();
        m.put(4, new HashSet<>(List.of(4,-4)));
        m.put(-4, new HashSet<>(List.of(4,-4)));
        m.put(2, new HashSet<>(List.of(2, -2)));
        m.put(-2, new HashSet<>(List.of(2, -2)));
        Canvas<Integer> c = Canvas.of(m);

        Rectangle<Integer> r = new Rectangle<>(3, -3, -3, 3);
        Canvas<Integer> d = c.slice(r);
        assertEquals(4L, c.sliceCount(r));
    }
}