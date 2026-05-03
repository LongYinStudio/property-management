package com.property.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void registerRequestShouldRejectShortPasswordAndInvalidPhone() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("owner001");
        request.setPassword("1234");
        request.setPhone("123");

        Set<String> messages = validate(request);

        assertTrue(messages.contains("密码长度必须在5-20位之间"));
        assertTrue(messages.contains("手机号格式不正确"));
    }

    @Test
    void parkingRentalRequestShouldRejectInvalidDateRange() {
        ParkingRentalRequest request = new ParkingRentalRequest();
        request.setSpaceId(1L);
        request.setStartDate(LocalDate.of(2026, 5, 10));
        request.setEndDate(LocalDate.of(2026, 5, 1));
        request.setAmount(new BigDecimal("100.00"));

        Set<String> messages = validate(request);

        assertTrue(messages.contains("结束日期不能早于开始日期"));
    }

    @Test
    void voteSubmitRequestShouldRequireOptionOrContent() {
        VoteSubmitRequest request = new VoteSubmitRequest();

        Set<String> messages = validate(request);

        assertTrue(messages.contains("投票选项和意见内容不能同时为空"));
    }

    @Test
    void ownerContractRequestShouldRejectInvalidDateRange() {
        OwnerContractRequest request = new OwnerContractRequest();
        request.setUserId(1L);
        request.setContractNo("HT-2026-001");
        request.setContractName("入住协议");
        request.setContractType(1);
        request.setSignDate(LocalDate.of(2026, 5, 1));
        request.setStartDate(LocalDate.of(2026, 6, 1));
        request.setEndDate(LocalDate.of(2026, 5, 1));
        request.setAmount(new BigDecimal("1000.00"));
        request.setStatus(2);

        Set<String> messages = validate(request);

        assertTrue(messages.contains("结束日期不能早于开始日期"));
    }

    private Set<String> validate(Object target) {
        return VALIDATOR.validate(target).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
