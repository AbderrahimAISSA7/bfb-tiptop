package com.thetiptop.service;

import com.thetiptop.api.dto.AdminCodeDto;
import com.thetiptop.api.dto.AdminUserDto;
import com.thetiptop.api.dto.CreateCodeRequest;
import com.thetiptop.api.dto.PrizeDto;
import com.thetiptop.api.dto.UpdateCodeStatusRequest;
import com.thetiptop.api.dto.UpdateUserRoleRequest;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.domain.Code;
import com.thetiptop.domain.Prize;
import com.thetiptop.domain.User;
import com.thetiptop.repository.CodeRepository;
import com.thetiptop.repository.PrizeRepository;
import com.thetiptop.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AdminManagementService {

    private final UserRepository userRepository;
    private final CodeRepository codeRepository;
    private final PrizeRepository prizeRepository;
    private final DtoMapper mapper;

    public AdminManagementService(UserRepository userRepository,
                                  CodeRepository codeRepository,
                                  PrizeRepository prizeRepository,
                                  DtoMapper mapper) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.prizeRepository = prizeRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<AdminUserDto> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toAdminUserDto)
                .toList();
    }

    @Transactional
    public AdminUserDto updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        user.setRole(request.getRole().toUpperCase());
        return mapper.toAdminUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<AdminCodeDto> listCodes() {
        return codeRepository.findAll()
                .stream()
                .map(mapper::toAdminCodeDto)
                .toList();
    }

    @Transactional
    public AdminCodeDto createCode(CreateCodeRequest request) {
        Prize prize = prizeRepository.findById(request.getPrizeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot introuvable"));
        Code code = new Code();
        code.setCode(request.getCode());
        code.setPrize(prize);
        code.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : CodeService.STATUS_NEW);
        code.setExpirationDate(request.getExpirationDate());
        OffsetDateTime now = OffsetDateTime.now();
        code.setIssueDate(now);
        code.setCreatedAt(now);
        code.setUpdatedAt(now);
        return mapper.toAdminCodeDto(codeRepository.save(code));
    }

    @Transactional
    public AdminCodeDto updateCodeStatus(Long codeId, UpdateCodeStatusRequest request) {
        Code code = codeRepository.findById(codeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Code introuvable"));
        code.setStatus(request.getStatus());
        code.setUpdatedAt(OffsetDateTime.now());
        return mapper.toAdminCodeDto(codeRepository.save(code));
    }

    @Transactional(readOnly = true)
    public List<PrizeDto> listPrizes() {
        return prizeRepository.findAll()
                .stream()
                .map(mapper::toPrizeDto)
                .toList();
    }
}
