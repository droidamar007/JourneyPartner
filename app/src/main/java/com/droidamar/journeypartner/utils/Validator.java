package com.droidamar.journeypartner.utils;

import java.util.List;

/**
 * Created by Amar
 */

public final class Validator {

    public static boolean isListNullOrEmpty(final List list) {
        return null == list || list.size() <= 0;
    }

    public static boolean isStringNullOrEmpty(final String string) {
        return null == string || string.isEmpty();
    }
}
