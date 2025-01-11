package org.skillsrock.task.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.skillsrock.task.dto.BalanceResponseDTO;
import org.skillsrock.task.entity.Wallet;
import org.skillsrock.task.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;

  @Override
  @Transactional(readOnly = true)
  public BalanceResponseDTO getWalletBalance(UUID walletId) {
    Wallet wallet = walletRepository.findById(walletId)
        .orElseThrow(EntityNotFoundException::new);

    return new BalanceResponseDTO(wallet.getBalance());
  }
}
