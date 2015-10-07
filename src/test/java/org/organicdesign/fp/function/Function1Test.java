package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.Mutable;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.FunctionUtils.ordinal;
import static org.organicdesign.fp.StaticImports.vec;

@RunWith(JUnit4.class)
public class Function1Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function1<Integer,Integer>() {
            @Override public Integer applyEx(Integer o) throws Exception {
                if (o < 10) {
                    throw new IOException("test exception");
                }
                return o;
            }
        }.apply(3);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function1<Integer,Integer>() {
            @Override public Integer applyEx(Integer o) throws Exception {
                if (o < 10) {
                    throw new IllegalStateException("test exception");
                }
                return o;
            }
        }.apply(3);
    }

    @Test
    public void composePredicatesWithAnd() {
//        assertTrue(Function1.andArray() == Function1.accept());
        assertTrue(Function1.and(null) == Function1.accept());
        assertTrue(Function1.and(vec()) == Function1.accept());

//        assertTrue(Function1.andArray(Function1.accept()) == Function1.accept());
        assertTrue(Function1.and(vec(Function1.accept())) ==
                   Function1.accept());

//        assertTrue(Function1.<Object>andArray(Function1.accept(),
//                                                  Function1.accept(),
//                                                  Function1.accept()) ==
//                   Function1.accept());
        assertTrue(Function1.<Object>and(vec(Function1.accept(),
                                             Function1.accept(),
                                             Function1.accept())) ==
                   Function1.accept());

//        assertTrue(Function1.andArray(Function1.reject()) == Function1.reject());
        assertTrue(Function1.and(vec(Function1.reject())) ==
                   Function1.reject());
    }

    @Test
    public void composePredicatesWithOr() {
//        assertTrue(Function1.orArray() == Function1.reject());
        assertTrue(Function1.or(null) == Function1.reject());

//        assertTrue(Function1.orArray(Function1.accept()) == Function1.accept());
        assertTrue(Function1.or(vec(Function1.accept())) ==
                   Function1.accept());

//        assertTrue(Function1.<Object>orArray(Function1.reject(),
//                                             Function1.reject(),
//                                             Function1.reject(),
//                                             Function1.accept()) ==
//                   Function1.accept());
        assertTrue(Function1.<Object>or(vec(Function1.reject(),
                                                     Function1.reject(),
                                                     Function1.reject(),
                                                     Function1.accept())) ==
                   Function1.accept());

//        assertTrue(Function1.<Object>orArray(Function1.accept(),
//                                             Function1.reject(),
//                                             Function1.reject(),
//                                             Function1.reject()) ==
//                   Function1.accept());
        assertTrue(Function1.<Object>or(vec(Function1.accept(),
                                                     Function1.reject(),
                                                     Function1.reject(),
                                                     Function1.reject())) ==
                   Function1.accept());

//        assertTrue(Function1.orArray(Function1.reject()) == Function1.reject());
        assertTrue(Function1.or(vec(Function1.reject())) ==
                Function1.reject());
    }

    @Test public void compose() {
        assertEquals(Function1.IDENTITY,
                     Function1.compose((Iterable<Function1<String,String>>) null));

        assertEquals(Function1.IDENTITY, Function1.compose(vec(null, null, null)));

        assertEquals(Function1.IDENTITY, Function1.compose(vec(null, Function1.identity(), null)));

        assertEquals(Function1.ACCEPT, Function1.compose(vec(null, Function1.identity(), null,
                                                             Function1.accept())));

        Function1<Integer,String> intToStr = new Function1<Integer, String>() {
            @Override
            public String applyEx(Integer i) throws Exception {
                return (i == 0) ? "zero" :
                       (i == 1) ? "one" :
                       (i == 2) ? "two" : "unknown";
            }
        };
        Function1<String,String> wordToOrdinal = new Function1<String, String>() {
            @Override
            public String applyEx(String s) throws Exception {
                return ("one".equals(s)) ? "first" :
                       ("two".equals(s)) ? "second" : s;
            }
        };
        Function1<Integer,String> f = wordToOrdinal.compose(intToStr);
        assertEquals("unknown", f.apply(-1));
        assertEquals("zero", f.apply(0));
        assertEquals("first", f.apply(1));
        assertEquals("second", f.apply(2));
        assertEquals("unknown", f.apply(3));

        Function1<Integer,String> g = intToStr.compose(Function1.identity());
        assertEquals("unknown", g.apply(-1));
        assertEquals("zero", g.apply(0));
        assertEquals("one", g.apply(1));
        assertEquals("two", g.apply(2));
        assertEquals("unknown", g.apply(3));

        Function1<Integer,String> h = Function1.<String>identity().compose(intToStr);
        assertEquals("unknown", h.apply(-1));
        assertEquals("zero", h.apply(0));
        assertEquals("one", h.apply(1));
        assertEquals("two", h.apply(2));
        assertEquals("unknown", h.apply(3));

        Function1<String,String> i = Function1.compose(vec(s -> s.substring(0, s.indexOf(" hundred")),
                                                           wordToOrdinal));
        assertEquals("zillion", i.apply("zillion hundred"));
        assertEquals("zero", i.apply("zero hundred"));
        assertEquals("first", i.apply("one hundred"));
        assertEquals("second", i.apply("two hundred"));
        assertEquals("three", i.apply("three hundred"));
    }

    @Test
    public void filtersOfPredicates() {
        assertArrayEquals(vec(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.<Integer>and((i) -> i > 2,
                                (i) -> i < 6))
                        .toMutableList()
                        .toArray(),
                new Integer[]{3, 4, 5});

        assertArrayEquals(vec(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.or(i -> i < 3,
                                i -> i > 5))
                        .toMutableList()
                        .toArray(),
                new Integer[]{1, 2, 6, 7, 8, 9});

        assertArrayEquals(vec(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .filter(Function1.or(vec(i -> i < 3,
                                i -> i == 4,
                                i -> i > 5)))
                        .toMutableList()
                        .toArray(),
                new Integer[]{1, 2, 4, 6, 7, 8, 9});
    }

    @Test public void testMemoize() {
        final int MAX_INT = 1000;
        Mutable.IntRef ir = Mutable.IntRef.of(0);
        Function1<Integer,String> f = Function1.memoize(i -> {
            ir.increment();
            return ordinal(i);
        });

        assertEquals(0, ir.value());

        // Call function a bunch of times, memoizing the results.
        for (int i = 0; i < MAX_INT; i++) {
            f.apply(i);
        }
        // Assert count of calls equals the actual number.
        assertEquals(MAX_INT, ir.value());

        // Make all those calls again.
        for (int i = 0; i < MAX_INT; i++) {
            f.apply(i);
        }

        // Assert that function has not actually been called again.
        assertEquals(MAX_INT, ir.value());
    }
}
