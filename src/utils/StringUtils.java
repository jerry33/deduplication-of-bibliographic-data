package utils;

import com.sun.istack.internal.Nullable;

import java.text.Normalizer;
import java.util.Arrays;

/**
 * Created by jerry on 12/5/16.
 */
public class StringUtils {

    private static String stripAccents(String s) {
        if (isValid(s)) {
            s = Normalizer.normalize(s, Normalizer.Form.NFD);
            s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            return s;
        }
        return "";
    }

    private static String removePunctuation(String s) {
        if (isValid(s)) {
            s = s.replaceAll("\\p{P}", "").toLowerCase();
            return s;
        }
        return "";
    }

    public static String standardizeString(@Nullable String s) {
        if (isValid(s)) {
            s = removePunctuation(s);
            s = stripAccents(s);
            return s.trim().replaceAll(" +", " ");
        }
        return "";
    }

    public static String removeNonNumericCharacters(String s) {
        if (StringUtils.isValid(s)) {
            s = s.replaceAll("[^\\d.]", "");
            return s.trim().replaceAll(" +", " ");
        }
        return "";
    }

    public static boolean isValid(String s) {
        return s != null && s.length() > 0;
    }

    public static boolean isSubsetOfAnother(char[] c1, char[] c2) {
        if (c1.length == c2.length) {
            for (int i = 0; i < c1.length; i++) {
                boolean isFound = false;
                for (int j = 0; j < c2.length; j++) {
                    if (c1[i] == c2[j]) {
                        isFound = true;
                    }
                }
                if (!isFound) return false;
            }
            return true;
        }
        return false;
    }

    public static String sortCharactersInString(String s1) {
        s1 = s1.toLowerCase();
        char[] chars = s1.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public static String getFirstCharactersFromEachWord(String s1) {
        String[] arrOfWords = s1.split(" ");
        final StringBuilder sb = new StringBuilder();
        for (String s : arrOfWords) {
            sb.append(s.charAt(0));
        }
        return sb.toString();
    }

}
