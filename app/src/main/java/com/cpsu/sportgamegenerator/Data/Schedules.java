package com.cpsu.sportgamegenerator.Data;

public class Schedules {
    public static final String TABLE_SCHEDULES = "schedules";
    public static final String TABLE_GAMESCHED = "game_schedule";

    public static final String COL_SCH_ID = "sch_id";
    public static final String COL_SCH_DATE = "sch_date";
    public static final String COL_SCH_SPORT = "sch_sport";
    public static final String COL_SCH_TYPE = "sch_type";
    public static final String COL_SCH_T1 = "sch_t1";
    public static final String COL_SCH_T2 = "sch_t2";
    public static final String COL_SCH_RND = "sch_rnd";
    public static final String COL_SCH_GAME = "sch_game";

    public static final String COL_GAME_SCH_ID = "g_sch_id";
    public static final String COL_GAME_NO =  "g_game";
    public static final String COL_GAME_DATE = "g_date";

    private String schedId;
    private String date;

    private String sport;
    private String type;

    public String getSchedId() {
        return schedId;
    }

    public void setSchedId(String schedId) {
        this.schedId = schedId;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
