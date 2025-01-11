package org.skillsrock.task.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletDTO(
    UUID walletId,
    String operationType,
    BigDecimal amount
) {

}
