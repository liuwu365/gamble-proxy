package com.lottery.gamble.proxy.core.entity;

/**
 * Created by Administrator on 2016/11/7 0007.
 */
public class UserAvgLevelStatistics {

    private String statisticsDate;

    private float userLevelAvg;

    private float userFriendsAvg;

    private float threedaysAwayuserlevelAvg;

    private float sevendaysAwayuserlevelAvg;

    public String getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    public float getUserLevelAvg() {
        return userLevelAvg;
    }

    public void setUserLevelAvg(float userLevelAvg) {
        this.userLevelAvg = userLevelAvg;
    }

    public float getUserFriendsAvg() {
        return userFriendsAvg;
    }

    public void setUserFriendsAvg(float userFriendsAvg) {
        this.userFriendsAvg = userFriendsAvg;
    }

    public float getThreedaysAwayuserlevelAvg() {
        return threedaysAwayuserlevelAvg;
    }

    public void setThreedaysAwayuserlevelAvg(float threedaysAwayuserlevelAvg) {
        this.threedaysAwayuserlevelAvg = threedaysAwayuserlevelAvg;
    }

    public float getSevendaysAwayuserlevelAvg() {
        return sevendaysAwayuserlevelAvg;
    }

    public void setSevendaysAwayuserlevelAvg(float sevendaysAwayuserlevelAvg) {
        this.sevendaysAwayuserlevelAvg = sevendaysAwayuserlevelAvg;
    }
}
