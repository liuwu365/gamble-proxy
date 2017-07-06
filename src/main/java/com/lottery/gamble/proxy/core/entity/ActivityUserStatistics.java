package com.lottery.gamble.proxy.core.entity;

/**
 * Created by Administrator on 2016/11/7 0007.
 */
public class ActivityUserStatistics {

    private String statisticsDate;

    private Integer activityUserCount;

    private Integer newUserCount;

    private Integer allLoginCount;

    private Integer threedaysAwayuserCount;

    private Integer sevendaysAwayuserCount;

    private Integer allRegisterCount;

    public String getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    public Integer getActivityUserCount() {
        return activityUserCount;
    }

    public void setActivityUserCount(Integer activityUserCount) {
        this.activityUserCount = activityUserCount;
    }

    public Integer getNewUserCount() {
        return newUserCount;
    }

    public void setNewUserCount(Integer newUserCount) {
        this.newUserCount = newUserCount;
    }

    public Integer getAllLoginCount() {
        return allLoginCount;
    }

    public void setAllLoginCount(Integer allLoginCount) {
        this.allLoginCount = allLoginCount;
    }

    public Integer getThreedaysAwayuserCount() {
        return threedaysAwayuserCount;
    }

    public void setThreedaysAwayuserCount(Integer threedaysAwayuserCount) {
        this.threedaysAwayuserCount = threedaysAwayuserCount;
    }

    public Integer getSevendaysAwayuserCount() {
        return sevendaysAwayuserCount;
    }

    public void setSevendaysAwayuserCount(Integer sevendaysAwayuserCount) {
        this.sevendaysAwayuserCount = sevendaysAwayuserCount;
    }

    public Integer getAllRegisterCount() {
        return allRegisterCount;
    }

    public void setAllRegisterCount(Integer allRegisterCount) {
        this.allRegisterCount = allRegisterCount;
    }
}
