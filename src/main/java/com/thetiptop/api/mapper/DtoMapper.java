package com.thetiptop.api.mapper;

import com.thetiptop.api.dto.CodeDto;
import com.thetiptop.api.dto.NewsletterDto;
import com.thetiptop.api.dto.ParticipantSummaryDto;
import com.thetiptop.api.dto.ParticipationDto;
import com.thetiptop.api.dto.PrizeDto;
import com.thetiptop.api.dto.UserDto;
import com.thetiptop.domain.Code;
import com.thetiptop.domain.Newsletter;
import com.thetiptop.domain.Participation;
import com.thetiptop.domain.Prize;
import com.thetiptop.domain.User;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public PrizeDto toPrizeDto(Prize prize) {
        if (prize == null) {
            return null;
        }
        PrizeDto dto = new PrizeDto();
        dto.setId(prize.getId());
        dto.setName(prize.getName());
        dto.setDescription(prize.getDescription());
        dto.setImage(prize.getImage());
        return dto;
    }

    public CodeDto toCodeDto(Code code) {
        if (code == null) {
            return null;
        }
        CodeDto dto = new CodeDto();
        dto.setId(code.getId());
        dto.setCode(code.getCode());
        dto.setStatus(code.getStatus());
        dto.setExpirationDate(code.getExpirationDate());
        dto.setPrize(toPrizeDto(code.getPrize()));
        return dto;
    }

    public ParticipationDto toParticipationDto(Participation participation) {
        if (participation == null) {
            return null;
        }
        ParticipationDto dto = new ParticipationDto();
        dto.setId(participation.getId());
        dto.setCode(toCodeDto(participation.getCode()));
        dto.setPrize(toPrizeDto(participation.getCode() != null ? participation.getCode().getPrize() : null));
        dto.setCreatedAt(participation.getCreatedAt());
        return dto;
    }

    public ParticipantSummaryDto toParticipantSummaryDto(Participation participation) {
        if (participation == null) {
            return null;
        }
        ParticipantSummaryDto dto = new ParticipantSummaryDto();
        dto.setId(participation.getId());
        dto.setUser(toUserDto(participation.getUser()));
        dto.setCode(toCodeDto(participation.getCode()));
        dto.setPrize(toPrizeDto(participation.getCode() != null ? participation.getCode().getPrize() : null));
        dto.setCreatedAt(participation.getCreatedAt());
        return dto;
    }

    public NewsletterDto toNewsletterDto(Newsletter newsletter) {
        if (newsletter == null) {
            return null;
        }
        NewsletterDto dto = new NewsletterDto();
        dto.setId(newsletter.getId());
        dto.setEmail(newsletter.getEmail());
        dto.setCreatedAt(newsletter.getCreatedAt());
        return dto;
    }
}

