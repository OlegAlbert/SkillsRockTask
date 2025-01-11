package org.skillsrock.task.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import org.skillsrock.task.entity.OperationType;

public record WalletUpdateRequestDTO(
    @NotNull
    UUID walletId,

    @NotNull
    OperationType operationType,

    @NotNull
    @Positive
    BigDecimal amount
) {

}
