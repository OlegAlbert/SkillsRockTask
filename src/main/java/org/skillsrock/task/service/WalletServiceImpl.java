package org.skillsrock.task.service;

import static org.skillsrock.task.exception.ExceptionMessage.WALLET_NOT_FOUND_EXCEPTION;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.entity.OperationType;
import org.skillsrock.task.entity.Transaction;
import org.skillsrock.task.entity.Wallet;
import org.skillsrock.task.exception.NotEnoughMoneyException;
import org.skillsrock.task.repository.TransactionRepository;
import org.skillsrock.task.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final TransactionRepository transactionRepository;

  @Override
  @Transactional(readOnly = true)
  public WalletResponseDTO getWalletBalance(UUID walletId) {
    Wallet wallet = walletRepository.findById(walletId)
        .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_EXCEPTION));

    return new WalletResponseDTO(walletId, wallet.getBalance());
  }

  @Override
  @Transactional
  public WalletResponseDTO updateWalletBalance(WalletUpdateRequestDTO walletUpdateRequestDTO) {
    Wallet wallet = walletRepository.findByIdForUpdate(walletUpdateRequestDTO.walletId())
        .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_EXCEPTION));

    checkWalletBalance(wallet, walletUpdateRequestDTO);
    Transaction transaction = createNewTransaction(wallet, walletUpdateRequestDTO);
    changeWalletBalance(wallet, transaction);

    transactionRepository.save(transaction);

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
      throw new NotEnoughMoneyException();
    }
  }

  private Transaction createNewTransaction(Wallet wallet, WalletUpdateRequestDTO walletUpdateRequestDTO) {
    Transaction transaction = new Transaction();
    transaction.setWallet(wallet);
    transaction.setAmount(walletUpdateRequestDTO.amount());
    transaction.setOperationType(walletUpdateRequestDTO.operationType());
    transaction.setDateOfOperation(LocalDateTime.now());
    return transaction;
  }
}
