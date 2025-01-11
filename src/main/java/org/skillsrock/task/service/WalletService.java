package org.skillsrock.task.service;

import java.util.UUID;
import org.skillsrock.task.dto.BalanceResponseDTO;

public interface WalletService {

  BalanceResponseDTO getWalletBalance(UUID walletId);

}
