package org.qik.empire.utils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

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

    @Override
    public Iterator<T> iterator() {
        return new LambdaIterator(input.iterator());
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






    public Lambda<T> copy(){
        return new Lambda<T>(toList());
    }

    public List<T> toList(){
        return copyTo(new ArrayList<>());
    }

    public List<? super T> toList(Class<? super T> clazz){
        return copyTo(new ArrayList<T>());
    }

    public <C extends Collection<? super T>> C copyTo(C result) {
        for(T element: this) {
            result.add(element);
        }

        return result;
    }




    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> objectClass) {
        return toList().toArray((T[]) Array.newInstance(objectClass, 0));
    }

    public <K, V> Map<K, V> toMap(Function<T, K> keyFunction,
                                  Function<T, V> valueFunction) {
        return copyToMap(new HashMap<>(), keyFunction, valueFunction);
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


    public static <T> Lambda<T> asLambda____(T... array) { return new Lambda<>(asList(array)); }
    public static <T> Lambda<T> asLambda    (T... array) { return new Lambda<>(asList(array)); }

    public static <T> Lambda<T> asLambda____(Iterable<T> iterable) { return new Lambda<>(iterable); }
    public static <T> Lambda<T> asLambda    (Iterable<T> iterable) { return new Lambda<>(iterable); }





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
