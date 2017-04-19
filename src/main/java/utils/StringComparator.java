package utils;

import info.debatty.java.stringsimilarity.JaroWinkler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jerry on 12/10/16.
 */
public class StringComparator {

    public static double comparePersonalName(final String s1, final String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            return new JaroWinkler().similarity(s1, s2);
        }
        return 0.5f;
    }

    public static double comparePublisher(final String s1, final String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            return new JaroWinkler().similarity(s1, s2);
        }
        return 0.5f;
    }

    public static double compareTitle(final String s1, final String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            return new JaroWinkler().similarity(s1, s2);
        }
        return 0.5f;
    }

    public static double compareNameOfPart(final String s1, final String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            return new JaroWinkler().similarity(s1, s2);
        }
        return 0.5f;
    }

    public static double compareInternationalStandardNumbers(final String s1, final String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            return s1.equals(s2) ? 1.0f : 0.0f;
        }
        return 0.5f;
    }

//    public static double compareInternationalStandardNumbers(final List<String> numbers1, final List<String> numbers2) {
//        if (numbers1 == null || numbers2 == null) {
//            return 0.5f;
//        }
//        final List<Double> listOfNumbersComparisonValues = new ArrayList<>();
//        final JaroWinkler jaroWinkler = new JaroWinkler();
//        for (String s1 : numbers1) {
//            for (String s2 : numbers2) {
//                listOfNumbersComparisonValues.add(jaroWinkler.similarity(s1, s2));
//            }
//        }
//        if (listOfNumbersComparisonValues.size() > 0) {
//            final double maxValue = Collections.max(listOfNumbersComparisonValues);
//            return maxValue > 0.95f ? maxValue : 0.0f; // setting threshold for ISBN and ISSN
//        }
//        return 0.5f;
//    }

    public static double compareYears(String s1, String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            try {
                s1 = s1.replace(".", "");
                s2 = s2.replace(".", "");
                final int year1 = Integer.parseInt(s1);
                final int year2 = Integer.parseInt(s2);
                final int diff = Math.abs(year1 - year2);
                if (diff == 0) return 0.0f;
                if (diff == 1) return 0.25f;
                if (diff == 2) return 0.5f;
                if (diff == 3) return 0.75f;
                if (diff >= 4) return 1.0f;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0.5f;
    }

    public static double compare915(String s1, String s2) {
        if (StringUtils.isValid(s1) && StringUtils.isValid(s2)) {
            final double similarity = new JaroWinkler().similarity(s1, s2);
            return similarity >= 0.9f ? similarity : 0.0f;
        }
        return 0.5f;
    }

}
