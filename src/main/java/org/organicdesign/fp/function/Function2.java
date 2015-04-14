// Copyright 2015-04-13 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.organicdesign.fp.function;

import java.util.Comparator;
import java.util.function.BiFunction;

/**
 This is like Java 8's java.util.function.BiFunction, but retrofitted to turn checked exceptions
 into unchecked ones.
 */
@FunctionalInterface
public interface Function2<A,B,R> extends BiFunction<A,B,R> {
    /** Implement this one method and you don't have to worry about checked exceptions. */
    R applyEx(A a, B b) throws Exception;

    /**
     The class that takes a consumer as an argument uses this convenience method so that it
     doesn't have to worry about checked exceptions either.
     */
    @Override default R apply(A a, B b) {
        try {
            return applyEx(a, b);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    Comparator<Comparable<Object>> DEFAULT_COMPARATOR =
            (o1, o2) -> (o1 == o2) ? 0 :
                        (o1 == null) ? -o2.compareTo(o1) :
                        o1.compareTo(o2);

    @SuppressWarnings("unchecked")
    static <T> Comparator<T> defaultComparator() { return (Comparator<T>) DEFAULT_COMPARATOR; }


// Don't think this is necessary.  Is it?
//    default BiFunction<A,B,R> asBiFunction() {
//        return (A a, B b) -> apply(a, b);
//    }
}
