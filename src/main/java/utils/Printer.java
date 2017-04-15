package utils;

import com.sun.istack.internal.NotNull;
import models.MarcCompVector;
import models.MarcRecord;

import java.util.*;
import java.util.stream.Collectors;

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

    public static void printOnlyDuplicates(final List<MarcRecord> marcRecords) {
        final List<String> list = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < marcRecords.size(); i++) {
            for (int j = i + 1; j < marcRecords.size(); j++) {
                counter++;
                final MarcRecord record1 = marcRecords.get(i);
                final MarcRecord record2 = marcRecords.get(j);
                if (record1.getC99FieldId().equals(record2.getC99FieldId())) {
                    list.add(record1.getC99FieldId());
                }
            }
        }
//        Iterator<String> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
        System.out.println("number of given MARC records list: " + marcRecords.size());
        System.out.println("number of comparisons: " + counter);
        System.out.println("number of all duplicates: " + list.size());
        System.out.println("number of distinct duplicates: " + list.stream().distinct().collect(Collectors.toList()).size());
    }

    public static void printTimeElapsed(final long start, final long end) {
        final long result = end - start;
        final long resultInMillis = result / 1000000;
        System.out.println("Time elapsed: " + resultInMillis + " ms");
        System.out.println("Nano: " + result + " ms");
    }

    public static void printUniqueList(List<List<MarcRecord>> uniqueList) {
        for (List<MarcRecord> marcRecordList : uniqueList) {
            if (marcRecordList.size() == 0) {
                System.out.println(marcRecordList.get(0).getControlFieldId());
            } else if (marcRecordList.size() > 0) {
                List<MarcRecord> distinctList = marcRecordList.stream().distinct().collect(Collectors.toList());
                marcRecordList.clear();
                marcRecordList.addAll(distinctList);
                for (MarcRecord marcRecord : marcRecordList) {
                    System.out.print(marcRecord.getControlFieldId() + "(" + marcRecord.getLibraryId() + ", " + marcRecord.getBlockingKey() + ") --> ");
                }
            }
            System.out.println();
        }
        System.out.println("uniqueList.size(): " + uniqueList.size());
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
