package models;

import com.opencsv.bean.CsvBindByName;

/**
 * Created by jerry on 12/5/16.
 */
public class IdCompVector {

    private String id;
    private boolean isDuplicate;

    public IdCompVector() {}

    public IdCompVector(final String id, final boolean isDuplicate) {
        this.id = id;
        this.isDuplicate = isDuplicate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }
}
