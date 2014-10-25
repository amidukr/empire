package org.qik.empire.utils;

import java.util.stream.Stream;

import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;

/**
 * Created by Dmytro_Brazhnyk on 23.06.2014.
 */
public final class Utils {


    public static boolean isEmpty(String empty) {
        return empty == null || empty.trim().isEmpty();
    }

    public static String defaultValue(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    public static String toStr(Object value) {
        return value == null ? null : value.toString();
    }


    public static String firstWord(String sentence, String splitRegex){
        String[] st = sentence.split(splitRegex);

        return st[0];
    }

    public static <T> Stream<T> asStream(T ... array){
        return stream(spliterator(array, 0), false);
    }

}
