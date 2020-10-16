package com.bsuir.iyazis.languagerecognizer.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TextUtils {

    private static final String CLEAR_TEXT_REGEX = "[,.!?:()—#\\-\\[\\]]";
    private static final String DISTINCT_WORDS_REGEX = "[\\s\n]";

    public static String clearTextFromAuxiliarySymbols(String text) {
        return text.replace(CLEAR_TEXT_REGEX, "");
    }

    public static Stream<String> createStreamOfDistinctWords(String text) {
        return Arrays.stream(text.split(DISTINCT_WORDS_REGEX));
    }
}
