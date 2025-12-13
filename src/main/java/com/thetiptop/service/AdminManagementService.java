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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.StringJoiner;

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
    public List<AdminCodeDto> listCodesSlice(String query, int offset, int limit) {
        String search = (query == null) ? "" : query.trim();
        int safeLimit = Math.max(1, Math.min(limit, 500));
        int safeOffset = Math.max(0, offset);
        int pageIndex = safeOffset / safeLimit;
        Pageable pageable = PageRequest.of(pageIndex, safeLimit);
        Page<Code> page = StringUtils.hasText(search)
                ? codeRepository.findByCodeContainingIgnoreCase(search, pageable)
                : codeRepository.findAll(pageable);
        return page.stream().map(mapper::toAdminCodeDto).toList();
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

    @Transactional(readOnly = true)
    public void writeCodesCsv(OutputStream outputStream, String query, String status) throws IOException {
        final String search = (query == null) ? "" : query.trim();
        final boolean hasStatus = StringUtils.hasText(status);
        final int batchSize = 5000;

        outputStream.write("id,code,status,prize,expirationDate,createdAt\n".getBytes(StandardCharsets.UTF_8));

        int pageIndex = 0;
        Page<Code> page;
        do {
            Pageable pageable = PageRequest.of(pageIndex, batchSize);
            if (StringUtils.hasText(search) && hasStatus) {
                page = codeRepository.findByCodeContainingIgnoreCaseAndStatus(search, status, pageable);
            } else if (StringUtils.hasText(search)) {
                page = codeRepository.findByCodeContainingIgnoreCase(search, pageable);
            } else if (hasStatus) {
                page = codeRepository.findByStatus(status, pageable);
            } else {
                page = codeRepository.findAll(pageable);
            }

            StringJoiner joiner = new StringJoiner("\n");
            page.forEach(code -> joiner.add(String.format("%d,%s,%s,%s,%s,%s",
                    code.getId(),
                    escapeCsv(code.getCode()),
                    escapeCsv(code.getStatus()),
                    escapeCsv(code.getPrize() != null ? code.getPrize().getName() : ""),
                    code.getExpirationDate() != null ? code.getExpirationDate() : "",
                    code.getCreatedAt() != null ? code.getCreatedAt() : "")));

            outputStream.write(joiner.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            pageIndex++;
        } while (page.hasNext());
    }

    @Transactional(readOnly = true)
    public void writeCodesJson(OutputStream outputStream, String query, String status) throws IOException {
        final String search = (query == null) ? "" : query.trim();
        final boolean hasStatus = StringUtils.hasText(status);
        final int batchSize = 5000;
        outputStream.write("[".getBytes(StandardCharsets.UTF_8));
        boolean first = true;
        int pageIndex = 0;
        Page<Code> page;
        do {
            Pageable pageable = PageRequest.of(pageIndex, batchSize);
            if (StringUtils.hasText(search) && hasStatus) {
                page = codeRepository.findByCodeContainingIgnoreCaseAndStatus(search, status, pageable);
            } else if (StringUtils.hasText(search)) {
                page = codeRepository.findByCodeContainingIgnoreCase(search, pageable);
            } else if (hasStatus) {
                page = codeRepository.findByStatus(status, pageable);
            } else {
                page = codeRepository.findAll(pageable);
            }

            for (Code code : page) {
                if (!first) {
                    outputStream.write(",".getBytes(StandardCharsets.UTF_8));
                }
                first = false;
                String json = String.format(
                        "{\"id\":%d,\"code\":\"%s\",\"status\":\"%s\",\"prize\":\"%s\",\"expirationDate\":\"%s\",\"createdAt\":\"%s\"}",
                        code.getId(),
                        jsonEscape(code.getCode()),
                        jsonEscape(code.getStatus()),
                        jsonEscape(code.getPrize() != null ? code.getPrize().getName() : ""),
                        code.getExpirationDate() != null ? code.getExpirationDate() : "",
                        code.getCreatedAt() != null ? code.getCreatedAt() : ""
                );
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            pageIndex++;
        } while (page.hasNext());
        outputStream.write("]".getBytes(StandardCharsets.UTF_8));
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v + "\"";
        }
        return v;
    }

    private String jsonEscape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
