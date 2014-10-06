package org.qik.empire.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;

import static org.qik.empire.utils.Utils.toStr;

/**
 * Created by qik on 05.10.2014.
 */
public class Lambda<T> implements Iterable<T> {

    private final Iterable<T> input;
    private Predicate<T> filterFunction;
    private Function<T, T> mapFunction;

    public Lambda(Iterable<T> input) {
        this.input = input;
    }

    public Lambda<T> filter(Predicate<T> filter) {
        Lambda result = new Lambda(this);
        result.filterFunction = filter;

        return result;
    }

    public <R> Lambda<R> map(Function<T, R> function) {
        Lambda result = new Lambda<>(this);
        result.mapFunction = function;
        return result;
    }

    public <C extends Collection<T>> C copyTo(C result) {
        for(T element: this) {
            result.add(element);
        }

        return result;
    }

    public List<T> toList(){
        return copyTo(new ArrayList<>());
    }

    public <K, V, M extends Map<K, V>>
                    M copyToMap(M map, Function<T, K> keyFunction,
                                       Function<T, V> valueFunction) {
        for(T element: this) {
            map.put(keyFunction.apply(element),
                    valueFunction.apply(element));
        }

        return map;
    }

    public <K, M extends Map<K, T>> M copyToIndex(M indexMap, Function<T, K> keyFunction){
        return copyToMap(indexMap, keyFunction,
                                   x -> x);
    }

    public Lambda<T> copy(){
        ArrayList<T> target = new ArrayList<>();

        for(T element: this) {
            target.add(element);
        }

        return new Lambda<T>(target);
    }

    @Override
    public Iterator<T> iterator() {
        return new LambdaIterator(input.iterator());
    }

    public static <T> Lambda<T> asLambda(T... array) {
        return new Lambda<>(asList(array));
    }

    public T[] toArray(Class<T> objectClass) {
        return toList().toArray((T[]) Array.newInstance(objectClass, 0));
    }

    private class LambdaIterator implements Iterator<T> {
        private Iterator<T> iterator;
        private T nextValue;
        private Boolean hasNext = null;

        private LambdaIterator(Iterator<T> iterator) {
            this.iterator = iterator;

        }

        @Override
        public boolean hasNext() {
            if(hasNext != null) return hasNext;
            hasNext = true;

            while (iterator.hasNext()) {
                nextValue = iterator.next();
                if(filterFunction == null || filterFunction.test(nextValue)) return hasNext;
            }

            hasNext = false;
            return hasNext;
        }

        @Override
        public T next() {
            if(!hasNext()) throw new NoSuchElementException();

            hasNext = null;
            return mapFunction != null ? mapFunction.apply(nextValue) :
                                                           nextValue;
        }

        @Override
        public void remove() {
            if(hasNext != null) throw new IllegalStateException("hasNext method has been called already");
            iterator.remove();
        }
    }
}
