package com.thetiptop.service;

import com.thetiptop.domain.Code;
import com.thetiptop.domain.User;
import com.thetiptop.repository.CodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class CodeService {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_USED = "USED";

    private final CodeRepository codeRepository;

    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Transactional(readOnly = true)
    public Code getUsableCode(String codeValue) {
        Code code = codeRepository.findByCode(codeValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Code introuvable"));

        if (code.getExpirationDate() != null && code.getExpirationDate().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expiré");
        }

        if (!STATUS_NEW.equalsIgnoreCase(code.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code déjà utilisé");
        }

        return code;
    }

    @Transactional
    public Code markUsed(Code code, User user) {
        OffsetDateTime now = OffsetDateTime.now();
        code.setStatus(STATUS_USED);
        code.setUseDate(now);
        code.setValidatedAt(now);
        code.setValidatedBy(user);
        code.setUpdatedAt(now);
        return codeRepository.save(code);
    }
}

