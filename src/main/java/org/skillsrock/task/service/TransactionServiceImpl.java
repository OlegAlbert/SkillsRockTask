package org.skillsrock.task.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.entity.OperationType;
import org.skillsrock.task.entity.Transaction;
import org.skillsrock.task.entity.Wallet;
import org.skillsrock.task.repository.TransactionRepository;
import org.skillsrock.task.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final WalletRepository walletRepository;
  private final TransactionRepository transactionRepository;

  @Override
  @Transactional
  public WalletResponseDTO updateWalletBalance(WalletUpdateRequestDTO walletUpdateRequestDTO) {
    Wallet wallet = walletRepository.findById(walletUpdateRequestDTO.walletId())
        .orElseThrow(EntityNotFoundException::new);

    checkWalletBalance(wallet, walletUpdateRequestDTO);
    Transaction transaction = createNewTransaction(wallet, walletUpdateRequestDTO);
    changeWalletBalance(wallet, transaction);

    return new WalletResponseDTO(wallet.getId(), wallet.getBalance());
  }

  private void changeWalletBalance(Wallet wallet, Transaction transaction) {
    switch (transaction.getOperationType()) {
      case WITHDRAW -> wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
      case DEPOSIT -> wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
    }
  }

  private void checkWalletBalance(Wallet wallet, WalletUpdateRequestDTO walletUpdateRequestDTO) {
    if (walletUpdateRequestDTO.operationType() == OperationType.WITHDRAW &&
        wallet.getBalance().compareTo(walletUpdateRequestDTO.amount()) < 0) {
      throw new RuntimeException();
    }
  }

  private Transaction createNewTransaction(Wallet wallet, WalletUpdateRequestDTO walletUpdateRequestDTO) {
    Transaction transaction = new Transaction();
    transaction.setWallet(wallet);
    transaction.setAmount(walletUpdateRequestDTO.amount());
    transaction.setOperationType(walletUpdateRequestDTO.operationType());
    transaction.setDateOfOperation(LocalDateTime.now());
    transactionRepository.save(transaction);
    return transaction;
  }
}
