package com.uav.autodebit.vo;

public class ScoreBucketVO extends BaseVO {

    private Integer scoreBucketId;
    private String bucketName;
    private Integer minRange;
    private Integer maxRange;
    private StatusVO statusId;


    public ScoreBucketVO() {
    }

    public Integer getScoreBucketId() {
        return scoreBucketId;
    }

    public void setScoreBucketId(Integer scoreBucketId) {
        this.scoreBucketId = scoreBucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Integer getMinRange() {
        return minRange;
    }

    public void setMinRange(Integer minRange) {
        this.minRange = minRange;
    }

    public Integer getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(Integer maxRange) {
        this.maxRange = maxRange;
    }

    public StatusVO getStatusId() {
        return statusId;
    }

    public void setStatusId(StatusVO statusId) {
        this.statusId = statusId;
    }
}
