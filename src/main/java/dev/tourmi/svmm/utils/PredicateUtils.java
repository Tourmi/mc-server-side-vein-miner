package dev.tourmi.svmm.utils;

public final class PredicateUtils {
    private PredicateUtils() {}

    public static <T> boolean always(T dummy) {
        return true;
    }

    public static <T> boolean never(T dummy) {
        return false;
    }
}
