package org.skillsrock.task.service;

import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;

public interface TransactionService {

  WalletResponseDTO updateWalletBalance(WalletUpdateRequestDTO walletUpdateRequestDTO);

}
