package com.thetiptop.repository;

import com.thetiptop.domain.Code;
import com.thetiptop.domain.Prize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CodeRepositoryTest {

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    private Prize prize;

    @BeforeEach
    void setUp() {
        prize = new Prize();
        prize.setName("Test Prize");
        prize.setCreatedAt(OffsetDateTime.now());
        prize.setUpdatedAt(prize.getCreatedAt());
        prize = prizeRepository.save(prize);
    }

    @Test
    void findByCode_returnsEntity() {
        Code saved = codeRepository.save(buildCode("CODE123", "NEW"));

        Code found = codeRepository.findByCode("CODE123").orElseThrow();
        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @Test
    void findByStatus_returnsMatchingCodes() {
        Code code1 = codeRepository.save(buildCode("CODE1", "NEW"));
        Code code2 = codeRepository.save(buildCode("CODE2", "USED"));
        Code code3 = codeRepository.save(buildCode("CODE3", "USED"));

        List<Code> usedCodes = codeRepository.findByStatus("USED");

        assertThat(usedCodes)
                .hasSize(2)
                .extracting(Code::getCode)
                .containsExactlyInAnyOrder("CODE2", "CODE3");
        assertThat(codeRepository.countByStatus("USED")).isEqualTo(2);
    }

    private Code buildCode(String codeValue, String status) {
        Code code = new Code();
        code.setCode(codeValue);
        code.setStatus(status);
        code.setPrize(prize);
        code.setCreatedAt(OffsetDateTime.now());
        code.setUpdatedAt(code.getCreatedAt());
        return code;
    }
}
