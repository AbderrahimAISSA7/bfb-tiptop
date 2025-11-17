package com.thetiptop.api.dto;

public class PrizeDistributionDto {

    private String prizeName;
    private long count;

    public PrizeDistributionDto() {
    }

    public PrizeDistributionDto(String prizeName, long count) {
        this.prizeName = prizeName;
        this.count = count;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

