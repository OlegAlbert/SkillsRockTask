package org.skillsrock.task.service;

import java.util.UUID;
import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;

public interface WalletService {

  WalletResponseDTO getWalletBalance(UUID walletId);

  WalletResponseDTO updateWalletBalance(WalletUpdateRequestDTO walletUpdateRequestDTO);

}
