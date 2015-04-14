/**
 Copyright (c) Rich Hickey. All rights reserved. The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html
 at the root of this distribution. By using this software in any fashion, you are agreeing to be bound by the terms of
 this license. You must not remove this notice, or any other, from this software.
 */

/* rich May 20, 2006 */
package org.organicdesign.fp.collections;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Persistent Red Black Tree
 * Note that instances of this class are constant values
 * i.e. add/remove etc return new values
 * <p/>
 * See Okasaki, Kahrs, Larsen et al
 *
 * @author Rich Hickey (Primary author)
 * @author Glen Peterson (Java-centric editor)
 */

public class PersistentTreeMap<K,V> implements ImMapSorted<K,V> {

    private final Comparator<K> comp;
    private final Node<K,V> tree;
    private final int size;

    final static public PersistentTreeMap EMPTY = new PersistentTreeMap();

    @SuppressWarnings("unchecked")
    public static <K, V> PersistentTreeMap<K,V> empty() {
        return (PersistentTreeMap<K,V>) EMPTY;
    }

//    static public <K, V> PersistentTreeMap<K,V> create(Map<K,V> other) {
//        PersistentTreeMap<K,V> ret = empty();
//        for (Map.Entry<K,V> e : other.entrySet()) {
//            ret = ret.assoc(e.getKey(), e.getValue());
//        }
//        return ret;
//    }

    /**
     Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but that
     return signature is illegal in Java, so you'll just have to remember.
     */
    @Override
    public ImSet<Entry<K,V>> entrySet() {
        return null; // TODO: Fix this!
    }

    /** This is correct, but O(n). */
    // Does this need to be compatible with java.util.AbstractList?
    @Override public int hashCode() { return (size() == 0) ? 0 : UnIterable.hashCode(this); }

    /** This is correct, but definitely O(n), same as java.util.ArrayList. */
    // Does this need to be compatible with java.util.AbstractList?
    @Override public boolean equals(Object other) {
        return (other != null) &&
                (other instanceof UnMapSorted) &&
                (this.size() == ((UnMapSorted) other).size()) &&
                UnIterable.equals(this, (UnMapSorted) other);
    }

    /** Returns a view of the keys contained in this map. */
    @Override public ImSet<K> keySet() { return PersistentTreeSet.of(this); }

    /**
     {@inheritDoc}

     @param fromKey
     @param toKey
     */
    @Override
    public ImMapSorted<K,V> subMap(K fromKey, K toKey) {
        if (comp.compare(fromKey, toKey) > 0) {
            throw new IllegalArgumentException("fromKey is greater than toKey");
        }
        ImMapSorted<K,V> ret = this;
        while (comp.compare(this.first().get().getKey(), fromKey) < 0) {
            remove(
        }
    }

    /** {@inheritDoc} */
    @Override
    public UnCollection<V> values() {
        return this.map((e) -> e.getValue()).toUnSetSorted();
    }

    @Override public Option<UnEntry<K,V>> first() {
        Node<K,V> t = tree;
        if (t != null) {
            while (t.left() != null) {
                t = t.left();
            }
        }
        return t == null ? Option.none() : Option.of(t);
    }

    @Override
    public Sequence<UnEntry<K,V>> rest() {
        Option<UnEntry<K,V>> first = first();
        return first.isSome() ? without(first.get().getKey()) : Sequence.emptySequence();
    }

    // TODO: What use is this?
    private class Box<E> {
        public E val;
        public Box(E val) { this.val = val; }
    }

    private PersistentTreeMap(Comparator<K> c, Node<K,V> t, int n) {
        comp = (c == null) ? Function2.defaultComparator() : c;
        tree = t;
        size = n;
    }
    public PersistentTreeMap(Comparator<K> c) { this(c, null, 0); }
    public PersistentTreeMap() { this(null, null, 0); }

//    @SuppressWarnings("unchecked")
//    static public <S, K extends S, V extends S> PersistentTreeMap<K,V> create(ISeq<S> items) {
//        PersistentTreeMap<K,V> ret = empty();
//        for (; items != null; items = items.next().next()) {
//            if (items.next() == null)
//                throw new IllegalArgumentException(String.format("No value supplied for key: %s", items.first()));
//            ret = ret.assoc((K) items.first(), (V) RT.second(items));
//        }
//        return ret;
//    }

//    @SuppressWarnings("unchecked")
//    static public <S, K extends S, V extends S>
//    PersistentTreeMap<K,V> create(Comparator<K> comp, ISeq<S> items) {
//        PersistentTreeMap<K,V> ret = new PersistentTreeMap<>(comp);
//        for (; items != null; items = items.next().next()) {
//            if (items.next() == null)
//                throw new IllegalArgumentException(String.format("No value supplied for key: %s", items.first()));
//            ret = ret.assoc((K) items.first(), (V) RT.second(items));
//        }
//        return ret;
//    }

    @SuppressWarnings("unchecked")
    @Override public boolean containsKey(Object key) {
        return entryAt((K) key) != null;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean containsValue(Object value) {
        return false; // TODO: implement this!
    }

    /**
     Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
     key.
     <p/>
     <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such that {@code
    (key==null ? k==null : key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.
     (There can be at most one such mapping.)
     <p/>
     <p>If this map permits null values, then a return value of {@code null} does not <i>necessarily</i> indicate that
     the map contains no mapping for the key; it's also possible that the map explicitly maps the key to {@code null}.
     The {@link #containsKey containsKey} operation may be used to distinguish these two cases.

     @param key the key whose associated value is to be returned
     @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
     @throws ClassCastException   if the key is of an inappropriate type for this map (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     @throws NullPointerException if the specified key is null and this map does not permit null keys (<a
     href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public V get(Object key) {
        return null;
    }

// public PersistentTreeMap<K,V> assocEx(K key, V val) {
// Inherits default implementation of assocEx from IPersistentMap

    @Override
    public PersistentTreeMap<K,V> assoc(K key, V val) {
        Box<Node<K,V>> found = new Box<>(null);
        Node<K,V> t = add(tree, key, val, found);
        if (t == null)   //null == already contains key
        {
            Node<K,V> foundNode = found.val;
            if (foundNode.val() == val)  //note only get same collection on identity of val, not equals()
                return this;
            return new PersistentTreeMap<>(comp, replace(tree, key, val), size);
        }
        return new PersistentTreeMap<>(comp, t.blacken(), size + 1);
    }


    @Override
    public PersistentTreeMap<K,V> without(K key) {
        Box<Node<K,V>> found = new Box<>(null);
        Node<K,V> t = remove(tree, key, found);
        if (t == null) {
            if (found.val == null)//null == doesn't contain key
                return this;
            //empty
            return new PersistentTreeMap<>(comp);
        }
        return new PersistentTreeMap<>(comp, t.blacken(), size - 1);
    }

//    @Override
//    public ISeq<Map.Entry<K,V>> seq() {
//        if (size > 0)
//            return Seq.create(tree, true, size);
//        return null;
//    }
//
//    @Override
//    public ISeq<Map.Entry<K,V>> rseq() {
//        if (size > 0)
//            return Seq.create(tree, false, size);
//        return null;
//    }

    @Override
    public Comparator<? super K> comparator() { return comp; }

//    @Override
//    public Object entryKey(Map.Entry<K,V> entry) {
//        return entry.getKey();
//    }

//    // I don't know what to do with this.
//// The other methods on Sorted seem to care only about the key, and the implementations of them
//// here work that way.  This one, however, returns a sequence of Map.Entry<K,V> or Node<K,V>
//// If I understood why, maybe I could do better.
//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<Map.Entry<K,V>> seq(boolean ascending) {
//        if (size > 0)
//            return Seq.create(tree, ascending, size);
//        return null;
//    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<Map.Entry<K,V>> seqFrom(Object key, boolean ascending) {
//        if (size > 0) {
//            ISeq<Node<K,V>> stack = null;
//            Node<K,V> t = tree;
//            while (t != null) {
//                int c = doCompare((K) key, t.key);
//                if (c == 0) {
//                    stack = RT.cons(t, stack);
//                    return new Seq<>(stack, ascending);
//                } else if (ascending) {
//                    if (c < 0) {
//                        stack = RT.cons(t, stack);
//                        t = t.left();
//                    } else
//                        t = t.right();
//                } else {
//                    if (c > 0) {
//                        stack = RT.cons(t, stack);
//                        t = t.right();
//                    } else
//                        t = t.left();
//                }
//            }
//            if (stack != null)
//                return new Seq<>(stack, ascending);
//        }
//        return null;
//    }

//    @Override
//    public UnIterator<UnMap.UnEntry<K,V>> iterator() {
//        return new NodeIterator<>(tree, true);
//    }

    public NodeIterator<K,V> reverseIterator() { return new NodeIterator<>(tree, false); }

    @Override public K firstKey() {
        Option<UnEntry<K,V>> min = first();
        if (min.isSome()) {
            return min.get().getKey();
        }
        throw new NoSuchElementException("this map is empty");
    }

    @Override public K lastKey() {
        Node<K,V> max = last();
        if (max == null) {
            throw new NoSuchElementException("this map is empty");
        }
        return max.getKey();
    }

    public Node<K,V> last() {
        Node<K,V> t = tree;
        if (t != null) {
            while (t.right() != null)
                t = t.right();
        }
        return t;
    }

//    public int depth() {
//        return depth(tree);
//    }

//    int depth(Node<K,V> t) {
//        if (t == null)
//            return 0;
//        return 1 + Math.max(depth(t.left()), depth(t.right()));
//    }

// public Object valAt(Object key){
// Default implementation now inherited from ILookup

    @SuppressWarnings("UnusedDeclaration")
    public int capacity() {
        return size;
    }

    @Override public int size() { return size; }

    public Node<K,V> entryAt(K key) {
        Node<K,V> t = tree;
        while (t != null) {
            int c = doCompare(key, t.key);
            if (c == 0)
                return t;
            else if (c < 0)
                t = t.left();
            else
                t = t.right();
        }
        return null; // t; // t is always null
    }

    public int doCompare(K k1, K k2) {
//	if(comp != null)
        return comp.compare(k1, k2);
//	return ((Comparable) k1).compareTo(k2);
    }

    Node<K,V> add(Node<K,V> t, K key, V val, Box<Node<K,V>> found) {
        if (t == null) {
            if (val == null)
                return new Red<>(key);
            return new RedVal<>(key, val);
        }
        int c = doCompare(key, t.key);
        if (c == 0) {
            found.val = t;
            return null;
        }
        Node<K,V> ins = c < 0 ? add(t.left(), key, val, found) : add(t.right(), key, val, found);
        if (ins == null) //found below
            return null;
        if (c < 0)
            return t.addLeft(ins);
        return t.addRight(ins);
    }

    Node<K,V> remove(Node<K,V> t, K key, Box<Node<K,V>> found) {
        if (t == null)
            return null; //not found indicator
        int c = doCompare(key, t.key);
        if (c == 0) {
            found.val = t;
            return append(t.left(), t.right());
        }
        Node<K,V> del = c < 0 ? remove(t.left(), key, found) : remove(t.right(), key, found);
        if (del == null && found.val == null) //not found below
            return null;
        if (c < 0) {
            if (t.left() instanceof Black)
                return balanceLeftDel(t.key, t.val(), del, t.right());
            else
                return red(t.key, t.val(), del, t.right());
        }
        if (t.right() instanceof Black)
            return balanceRightDel(t.key, t.val(), t.left(), del);
        return red(t.key, t.val(), t.left(), del);
//		return t.removeLeft(del);
//	return t.removeRight(del);
    }

    //static <K,V, K1 extends K, K2 extends K, V1 extends V, V2 extends V>
//Node<K,V> append(Node<K1,V1> left, Node<K2,V2> right){
    @SuppressWarnings("unchecked")
    static <K, V> Node<K,V> append(Node<? extends K,? extends V> left,
                                   Node<? extends K,? extends V> right) {
        if (left == null)
            return (Node<K,V>) right;
        else if (right == null)
            return (Node<K,V>) left;
        else if (left instanceof Red) {
            if (right instanceof Red) {
                Node<K,V> app = append(left.right(), right.left());
                if (app instanceof Red)
                    return red(app.key, app.val(),
                               red(left.key, left.val(), left.left(), app.left()),
                               red(right.key, right.val(), app.right(), right.right()));
                else
                    return red(left.key, left.val(), left.left(), red(right.key, right.val(), app, right.right()));
            } else
                return red(left.key, left.val(), left.left(), append(left.right(), right));
        } else if (right instanceof Red)
            return red(right.key, right.val(), append(left, right.left()), right.right());
        else //black/black
        {
            Node<K,V> app = append(left.right(), right.left());
            if (app instanceof Red)
                return red(app.key, app.val(),
                           black(left.key, left.val(), left.left(), app.left()),
                           black(right.key, right.val(), app.right(), right.right()));
            else
                return balanceLeftDel(left.key, left.val(), left.left(), black(right.key, right.val(), app, right.right()));
        }
    }

    static <K, V, K1 extends K, V1 extends V>
    Node<K,V> balanceLeftDel(K1 key, V1 val,
                             Node<? extends K,? extends V> del,
                             Node<? extends K,? extends V> right) {
        if (del instanceof Red)
            return red(key, val, del.blacken(), right);
        else if (right instanceof Black)
            return rightBalance(key, val, del, right.redden());
        else if (right instanceof Red && right.left() instanceof Black)
            return red(right.left().key, right.left().val(),
                       black(key, val, del, right.left().left()),
                       rightBalance(right.key, right.val(), right.left().right(), right.right().redden()));
        else
            throw new UnsupportedOperationException("Invariant violation");
    }

    static <K, V, K1 extends K, V1 extends V>
    Node<K,V> balanceRightDel(K1 key, V1 val,
                              Node<? extends K,? extends V> left,
                              Node<? extends K,? extends V> del) {
        if (del instanceof Red)
            return red(key, val, left, del.blacken());
        else if (left instanceof Black)
            return leftBalance(key, val, left.redden(), del);
        else if (left instanceof Red && left.right() instanceof Black)
            return red(left.right().key, left.right().val(),
                       leftBalance(left.key, left.val(), left.left().redden(), left.right().left()),
                       black(key, val, left.right().right(), del));
        else
            throw new UnsupportedOperationException("Invariant violation");
    }

    static <K, V, K1 extends K, V1 extends V>
    Node<K,V> leftBalance(K1 key, V1 val,
                          Node<? extends K,? extends V> ins,
                          Node<? extends K,? extends V> right) {
        if (ins instanceof Red && ins.left() instanceof Red)
            return red(ins.key, ins.val(), ins.left().blacken(), black(key, val, ins.right(), right));
        else if (ins instanceof Red && ins.right() instanceof Red)
            return red(ins.right().key, ins.right().val(),
                       black(ins.key, ins.val(), ins.left(), ins.right().left()),
                       black(key, val, ins.right().right(), right));
        else
            return black(key, val, ins, right);
    }


    static <K, V, K1 extends K, V1 extends V>
    Node<K,V> rightBalance(K1 key, V1 val,
                           Node<? extends K,? extends V> left,
                           Node<? extends K,? extends V> ins) {
        if (ins instanceof Red && ins.right() instanceof Red)
            return red(ins.key, ins.val(), black(key, val, left, ins.left()), ins.right().blacken());
        else if (ins instanceof Red && ins.left() instanceof Red)
            return red(ins.left().key, ins.left().val(),
                       black(key, val, left, ins.left().left()),
                       black(ins.key, ins.val(), ins.left().right(), ins.right()));
        else
            return black(key, val, left, ins);
    }

    Node<K,V> replace(Node<K,V> t, K key, V val) {
        int c = doCompare(key, t.key);
        return t.replace(t.key,
                         c == 0 ? val : t.val(),
                         c < 0 ? replace(t.left(), key, val) : t.left(),
                         c > 0 ? replace(t.right(), key, val) : t.right());
    }

    @SuppressWarnings({"unchecked", "RedundantCast", "Convert2Diamond"})
    static <K, V, K1 extends K, V1 extends V>
    Red<K,V> red(K1 key, V1 val,
                 Node<? extends K,? extends V> left,
                 Node<? extends K,? extends V> right) {
        if (left == null && right == null) {
            if (val == null)
                return new Red<K,V>(key);
            return new RedVal<K,V>(key, val);
        }
        if (val == null)
            return new RedBranch<K,V>((K) key, (Node<K,V>) left, (Node<K,V>) right);
        return new RedBranchVal<K,V>((K) key, (V) val, (Node<K,V>) left, (Node<K,V>) right);
    }

    @SuppressWarnings({"unchecked", "RedundantCast", "Convert2Diamond"})
    static <K, V, K1 extends K, V1 extends V>
    Black<K,V> black(K1 key, V1 val,
                     Node<? extends K,? extends V> left,
                     Node<? extends K,? extends V> right) {
        if (left == null && right == null) {
            if (val == null)
                return new Black<>(key);
            return new BlackVal<K,V>(key, val);
        }
        if (val == null)
            return new BlackBranch<K,V>((K) key, (Node<K,V>) left, (Node<K,V>) right);
        return new BlackBranchVal<K,V>((K) key, (V) val, (Node<K,V>) left, (Node<K,V>) right);
    }

//    public static class Reduced<A> {
//        public final A val;
//        private Reduced(A a) { val = a; }
//    }

    static abstract class Node<K, V> implements UnEntry<K,V> {
        final K key;

        Node(K key) { this.key = key; }

        public K key() { return key; }

        public V val() { return null; }

        @Override
        public K getKey() { return key(); }

        @Override
        public V getValue() { return val(); }

        Node<K,V> left() { return null; }

        Node<K,V> right() { return null; }

        abstract Node<K,V> addLeft(Node<K,V> ins);

        abstract Node<K,V> addRight(Node<K,V> ins);

        @SuppressWarnings("UnusedDeclaration")
        abstract Node<K,V> removeLeft(Node<K,V> del);

        @SuppressWarnings("UnusedDeclaration")
        abstract Node<K,V> removeRight(Node<K,V> del);

        abstract Node<K,V> blacken();

        abstract Node<K,V> redden();

        Node<K,V> balanceLeft(Node<K,V> parent) { return black(parent.key, parent.val(), this, parent.right()); }

        Node<K,V> balanceRight(Node<K,V> parent) { return black(parent.key, parent.val(), parent.left(), this); }

        abstract Node<K,V> replace(K key, V val, Node<K,V> left, Node<K,V> right);

//        public <R> R kvreduce(Function3<R,K,V,R> f, R init) {
//            if (left() != null) {
//                init = left().kvreduce(f, init);
//                if (init instanceof Reduced)
//                    return init;
//            }
//            init = f.apply(init, key(), val());
//            if (init instanceof Reduced)
//                return init;
//
//            if (right() != null) {
//                init = right().kvreduce(f, init);
//            }
//            return init;
//        }
    } // end class Node.

    static class Black<K, V> extends Node<K,V> {
        public Black(K key) { super(key); }

        @Override Node<K,V> addLeft(Node<K,V> ins) { return ins.balanceLeft(this); }

        @Override Node<K,V> addRight(Node<K,V> ins) { return ins.balanceRight(this); }

        @Override Node<K,V> removeLeft(Node<K,V> del) { return balanceLeftDel(key, val(), del, right()); }

        @Override Node<K,V> removeRight(Node<K,V> del) { return balanceRightDel(key, val(), left(), del); }

        @Override Node<K,V> blacken() { return this; }

        @Override Node<K,V> redden() { return new Red<>(key); }

        @Override
        Node<K,V> replace(K key, V val, Node<K,V> left, Node<K,V> right) { return black(key, val, left, right); }

    }

    static class BlackVal<K, V> extends Black<K,V> {
        final V val;

        public BlackVal(K key, V val) {
            super(key);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> redden() { return new RedVal<>(key, val); }

    }

    static class BlackBranch<K, V> extends Black<K,V> {
        final Node<K,V> left;

        final Node<K,V> right;

        public BlackBranch(K key, Node<K,V> left, Node<K,V> right) {
            super(key);
            this.left = left;
            this.right = right;
        }

        @Override
        public Node<K,V> left() { return left; }

        @Override
        public Node<K,V> right() { return right; }

        @Override
        Node<K,V> redden() { return new RedBranch<>(key, left, right); }

    }

    static class BlackBranchVal<K, V> extends BlackBranch<K,V> {
        final V val;

        public BlackBranchVal(K key, V val, Node<K,V> left, Node<K,V> right) {
            super(key, left, right);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> redden() { return new RedBranchVal<>(key, val, left, right); }

    }

    static class Red<K, V> extends Node<K,V> {
        public Red(K key) { super(key); }

        @Override Node<K,V> addLeft(Node<K,V> ins) { return red(key, val(), ins, right()); }

        @Override Node<K,V> addRight(Node<K,V> ins) { return red(key, val(), left(), ins); }

        @Override Node<K,V> removeLeft(Node<K,V> del) { return red(key, val(), del, right()); }

        @Override Node<K,V> removeRight(Node<K,V> del) { return red(key, val(), left(), del); }

        @Override Node<K,V> blacken() { return new Black<>(key); }

        @Override
        Node<K,V> redden() { throw new UnsupportedOperationException("Invariant violation"); }

        @Override
        Node<K,V> replace(K key, V val, Node<K,V> left, Node<K,V> right) { return red(key, val, left, right); }

    }

    static class RedVal<K, V> extends Red<K,V> {
        final V val;

        public RedVal(K key, V val) {
            super(key);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> blacken() { return new BlackVal<>(key, val); }

    }

    static class RedBranch<K, V> extends Red<K,V> {
        final Node<K,V> left;

        final Node<K,V> right;

        public RedBranch(K key, Node<K,V> left, Node<K,V> right) {
            super(key);
            this.left = left;
            this.right = right;
        }

        @Override public Node<K,V> left() { return left; }

        @Override public Node<K,V> right() { return right; }

        @Override Node<K,V> balanceLeft(Node<K,V> parent) {
            if (left instanceof Red)
                return red(key, val(), left.blacken(), black(parent.key, parent.val(), right, parent.right()));
            else if (right instanceof Red)
                return red(right.key, right.val(), black(key, val(), left, right.left()),
                           black(parent.key, parent.val(), right.right(), parent.right()));
            else
                return super.balanceLeft(parent);

        }

        @Override Node<K,V> balanceRight(Node<K,V> parent) {
            if (right instanceof Red)
                return red(key, val(), black(parent.key, parent.val(), parent.left(), left), right.blacken());
            else if (left instanceof Red)
                return red(left.key, left.val(), black(parent.key, parent.val(), parent.left(), left.left()),
                           black(key, val(), left.right(), right));
            else
                return super.balanceRight(parent);
        }

        @Override Node<K,V> blacken() { return new BlackBranch<>(key, left, right); }

    }


    static class RedBranchVal<K, V> extends RedBranch<K,V> {
        final V val;

        public RedBranchVal(K key, V val, Node<K,V> left, Node<K,V> right) {
            super(key, left, right);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> blacken() { return new BlackBranchVal<>(key, val, left, right); }
    }


//    static public class Seq<K, V> extends ASeq<Map.Entry<K,V>> {
//        final ISeq<Node<K,V>> stack;
//        final boolean asc;
//        final int cnt;
//
//        public Seq(ISeq<Node<K,V>> stack, boolean asc) {
//            this.stack = stack;
//            this.asc = asc;
//            this.cnt = -1;
//        }
//
//        public Seq(ISeq<Node<K,V>> stack, boolean asc, int cnt) {
//            this.stack = stack;
//            this.asc = asc;
//            this.cnt = cnt;
//        }
//
//        Seq(ISeq<Node<K,V>> stack, boolean asc, int cnt) {
//            super();
//            this.stack = stack;
//            this.asc = asc;
//            this.cnt = cnt;
//        }
//
//        static <K, V> Seq<K,V> create(Node<K,V> t, boolean asc, int cnt) {
//            return new Seq<>(push(t, null, asc), asc, cnt);
//        }
//
//        static <K, V> ISeq<Node<K,V>> push(Node<K,V> t, ISeq<Node<K,V>> stack, boolean asc) {
//            while (t != null) {
//                stack = RT.cons(t, stack);
//                t = asc ? t.left() : t.right();
//            }
//            return stack;
//        }
//
//        @Override
//        public Node<K,V> first() {
//            return stack.first();
//        }
//
//        @Override
//        public ISeq<Map.Entry<K,V>> next() {
//            Node<K,V> t = stack.first();
//            ISeq<Node<K,V>> nextstack = push(asc ? t.right() : t.left(), stack.next(), asc);
//            if (nextstack != null) {
//                return new Seq<>(nextstack, asc, cnt - 1);
//            }
//            return null;
//        }
//
//        @Override
//        public int count() {
//            if (cnt < 0)
//                return super.count();
//            return cnt;
//        }
//    }

    static public class NodeIterator<K, V> implements UnIterator<UnMap.UnEntry<K,V>> {
        Stack<Node<K,V>> stack = new Stack<>();
        boolean asc;

        NodeIterator(Node<K,V> t, boolean asc) {
            this.asc = asc;
            push(t);
        }

        void push(Node<K,V> t) {
            while (t != null) {
                stack.push(t);
                t = asc ? t.left() : t.right();
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public UnMap.UnEntry<K,V> next() {
            Node<K,V> t = stack.pop();
            push(asc ? t.right() : t.left());
            return t;
        }
    }

    static class KeyIterator<K> implements Iterator<K> {
        NodeIterator<K,?> it;

        KeyIterator(NodeIterator<K,?> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public K next() {
            return it.next().getKey();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class ValIterator<V> implements Iterator<V> {
        NodeIterator<?,V> it;

        ValIterator(NodeIterator<?,V> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public V next() {
            return it.next().getValue();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
