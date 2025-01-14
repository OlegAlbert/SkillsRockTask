package org.skillsrock.task.dto;

import static org.skillsrock.task.exception.ExceptionMessage.VALIDATION_EXCEPTION_MESSAGE;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import org.skillsrock.task.entity.OperationType;

public record WalletUpdateRequestDTO(

    @NotNull(message = VALIDATION_EXCEPTION_MESSAGE)
    UUID walletId,

    @NotNull(message = VALIDATION_EXCEPTION_MESSAGE)
    OperationType operationType,

    @NotNull(message = VALIDATION_EXCEPTION_MESSAGE)
    @Positive
    BigDecimal amount
) {

}
