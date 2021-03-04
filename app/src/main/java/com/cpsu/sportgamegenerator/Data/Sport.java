package com.cpsu.sportgamegenerator.Data;

public class Sport {
    public static final String TABLE_SPORTS = "sports";
    public static final String COL_SPORT_ID = "sport_id";
    public static final String COL_SPORT_NAME = "sport_name";

    private String sportID;
    private String sportName;

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }


    public String getSportID() {
        return sportID;
    }

    public void setSportID(String sportID) {
        this.sportID = sportID;
    }

    public int getIdxByItem(String[] array, String att) {
        int idx = 0;
        for (int i=0; i <array.length; i++) {
            if(array[i].contains(att)) {
                idx = i;
                break;
            }
        }
        return idx;
    }
}
