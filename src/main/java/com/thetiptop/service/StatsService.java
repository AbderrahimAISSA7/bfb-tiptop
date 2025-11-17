package com.thetiptop.service;

import com.thetiptop.api.dto.PrizeDistributionDto;
import com.thetiptop.api.dto.StatsResponse;
import com.thetiptop.repository.CodeRepository;
import com.thetiptop.repository.ParticipationRepository;
import com.thetiptop.repository.projection.PrizeCountProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatsService {

    private final CodeRepository codeRepository;
    private final ParticipationRepository participationRepository;

    public StatsService(CodeRepository codeRepository, ParticipationRepository participationRepository) {
        this.codeRepository = codeRepository;
        this.participationRepository = participationRepository;
    }

    @Transactional(readOnly = true)
    public StatsResponse buildStats() {
        StatsResponse response = new StatsResponse();
        response.setTotalCodes(codeRepository.count());
        response.setUsedCodes(codeRepository.countByStatus(CodeService.STATUS_USED));

        List<PrizeDistributionDto> distribution = participationRepository.countByPrize().stream()
                .map(projection -> new PrizeDistributionDto(
                        projection.getPrizeName(),
                        projection.getPrizeCount()))
                .toList();

        response.setPrizeDistribution(distribution);
        return response;
    }
}

