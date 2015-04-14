// Copyright 2014-01-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp;

import org.organicdesign.fp.collections.UnList;
import org.organicdesign.fp.collections.UnMap;
import org.organicdesign.fp.collections.UnSet;
import org.organicdesign.fp.collections.UnSetSorted;
import org.organicdesign.fp.function.Function1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 Calling any of these methods forces eager evaluation of the entire underlying collection.
 This is incomplete, but enough to run some simple experiments with.
 @param <T>
 */
public interface Realizable<T> {
    ArrayList<T> toJavaArrayList();
    UnList<T> toUnList();

    /**
     @return A map with the keys from the given set, mapped to values using the given function.
      * @param f1 Maps keys to values
     */
    <U> HashMap<T,U> toJavaHashMap(Function1<T,U> f1);
    /**
     @return An unmodifiable map with the keys from the given set, mapped to values using the given
     function.
      * @param f1 Maps keys to values
     */
    <U> UnMap<T,U> toUnMap(Function1<T,U> f1);

    /**
     @return A map with the values from the given set, mapped by keys supplied by the given
     function.
      * @param f1 Maps values to keys
     */
    <U> HashMap<U,T> toReverseJavaHashMap(Function1<T,U> f1);

    /**
     @return An unmodifiable map with the values from the given set, mapped by keys supplied by
     the given function.
      * @param f1 Maps values to keys
     */
    <U> UnMap<U,T> toReverseUnMap(Function1<T,U> f1);

    TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator);
    UnSetSorted<T> toUnSetSorted(Comparator<? super T> comparator);

    TreeSet<T> toJavaTreeSet();
    UnSetSorted<T> toUnSetSorted();

    HashSet<T> toJavaHashSet();
    UnSet<T> toUnSet();

    T[] toArray();

    Iterator<T> toIterator();
}
