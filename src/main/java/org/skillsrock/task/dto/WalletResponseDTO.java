package org.skillsrock.task.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponseDTO(
    UUID id,
    BigDecimal balance
) {

}
