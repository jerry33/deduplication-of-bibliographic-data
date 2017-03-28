package utils;

import com.sun.istack.internal.NotNull;
import models.MarcRecord;

/**
 * Created by jerry on 3/25/17.
 */
public final class Printer {

    public static void printMarcRecord(@NotNull final MarcRecord marcRecord) {
        System.out.println("TYPE OF MATERIAL: " + marcRecord.getTypeOfMaterial());
        System.out.println("PERSONAL NAME: " + marcRecord.getPersonalName());
        System.out.println("PUBLISHER: " + marcRecord.getPublisherName());
        System.out.println("TITLE: " + marcRecord.getTitle());
        System.out.println("NAME OF PART: " + marcRecord.getNameOfPart());
        System.out.println("YEAR OF AUTHOR: " + marcRecord.getYearOfAuthor());
        System.out.println("YEAR OF PUBLICATION: " + marcRecord.getYearOfPublication());
        System.out.println("BLOCKING KEY: " + marcRecord.getBlockingKey());
        System.out.println();
        System.out.println();
    }

    public static void printTimeElapsed(final long start, final long end) {
        final long result = end - start;
        final long resultInMillis = result / 1000000;
        System.out.println("Time elapsed: " + resultInMillis + " ms");
        System.out.println("Nano: " + result + " ms");
    }

}
