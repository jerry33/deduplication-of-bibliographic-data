package utils;

import com.sun.istack.internal.NotNull;
import models.MarcCompVector;
import models.MarcRecord;

import java.util.List;

/**
 * Created by jerry on 3/25/17.
 */
public final class Printer {

    public static void printMarcRecord(final MarcRecord marcRecord) {
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

    public static void printDuplicates(final List<MarcCompVector> marcCompVectors, final List<MarcRecord> marcRecords) {
        for (MarcCompVector marcCompVector : marcCompVectors) {
            if (marcCompVector.isDuplicate()) {
                System.out.println("DUPLICATES:");
                System.out.println();
                // TODO: change marcRecords to HashMap for quicker access to ids
                for (MarcRecord marcRecord : marcRecords) {
                    if (marcRecord.getControlFieldId().equals(marcCompVector.getCompControlField1())) {
                        System.out.println(marcRecord.toString());
                        System.out.println();
                    }
                }
                System.out.println("AND");
                System.out.println();
                for (MarcRecord marcRecord : marcRecords) {
                    if (marcRecord.getControlFieldId().equals(marcCompVector.getCompControlField2())) {
                        System.out.println(marcRecord.toString());
                        System.out.println();
                    }
                }
                System.out.println("------------------------------------------");
                System.out.println();
            }
        }
    }

    public static void printTimeElapsed(final long start, final long end) {
        final long result = end - start;
        final long resultInMillis = result / 1000000;
        System.out.println("Time elapsed: " + resultInMillis + " ms");
        System.out.println("Nano: " + result + " ms");
    }

    public static void printBlockingKey(final MarcRecord marcRecord) {
        if (marcRecord.getControlFieldId().equals("sc000011")) {
            System.out.println("Blocking key of " + marcRecord.getControlFieldId() + ": " + marcRecord.getBlockingKey());
            System.out.println("Title: " + marcRecord.getTitle());
        }
//        if (marcRecord.getBlockingKey().equals("")) {
//            System.out.println("Blocking key of " + marcRecord.getControlFieldId() + ":  EMPTY ");
//        } else {
//            System.out.println("Blocking key of " + marcRecord.getControlFieldId() + ": " + marcRecord.getBlockingKey());
//        }
    }

    public static void printBlockingKeys(final List<MarcRecord> marcRecords) {
        System.out.println("MARC records list size: " + marcRecords.size());
        for (MarcRecord marcRecord : marcRecords) {
            printBlockingKey(marcRecord);
        }
//        for (int i = 0; i < marcRecords.size(); i++) {
//            if (i <= 5000) {
//                printBlockingKey(marcRecords.get(i));
//            }
//        }
    }

}
