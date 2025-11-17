package com.thetiptop.api.dto;

import java.util.List;

public class StatsResponse {

    private long totalCodes;
    private long usedCodes;
    private List<PrizeDistributionDto> prizeDistribution;

    public long getTotalCodes() {
        return totalCodes;
    }

    public void setTotalCodes(long totalCodes) {
        this.totalCodes = totalCodes;
    }

    public long getUsedCodes() {
        return usedCodes;
    }

    public void setUsedCodes(long usedCodes) {
        this.usedCodes = usedCodes;
    }

    public List<PrizeDistributionDto> getPrizeDistribution() {
        return prizeDistribution;
    }

    public void setPrizeDistribution(List<PrizeDistributionDto> prizeDistribution) {
        this.prizeDistribution = prizeDistribution;
    }
}

