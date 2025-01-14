package org.skillsrock.task.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skillsrock.task.exception.ExceptionMessage.NOT_ENOUGH_MONEY_EXCEPTION_MESSAGE;
import static org.skillsrock.task.exception.ExceptionMessage.WALLET_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.skillsrock.task.util.Constant.MAX_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.Constant.MIN_AMOUNT_OF_TEST_WALLET;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.entity.Transaction;
import org.skillsrock.task.entity.Wallet;
import org.skillsrock.task.exception.NotEnoughMoneyException;
import org.skillsrock.task.exception.WalletNotFoundException;
import org.skillsrock.task.repository.TransactionRepository;
import org.skillsrock.task.repository.WalletRepository;
import org.skillsrock.task.util.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

  @Mock
  WalletRepository walletRepository;

  @Mock
  TransactionRepository transactionRepository;

  @InjectMocks
  WalletServiceImpl walletService;

  private final UUID walletId = UUID.randomUUID();

  @Test
  void getWalletBalance_WalletExists_Success() {
    // Arrange
    Wallet wallet = TestDataFactory.createWallet();
    wallet.setId(walletId);

    when(walletRepository.findById(wallet.getId()))
        .thenReturn(Optional.of(wallet));

    // Act
    WalletResponseDTO response = walletService.getWalletBalance(wallet.getId());

    // Assert
    assertNotNull(response);
    assertEquals(wallet.getId(), response.walletId());
    assertEquals(wallet.getBalance(), response.balance());
    verify(walletRepository, times(1)).findById(wallet.getId());
  }

  @Test
  void getWalletBalance_WalletDoesNotExist_WalletNotFoundExceptionThrown() {
    // Arrange
    when(walletRepository.findById(walletId))
        .thenReturn(Optional.empty());

    // Act
    WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
        () -> walletService.getWalletBalance(walletId));

    // Assert
    assertEquals(WALLET_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    verify(walletRepository, times(1)).findById(walletId);
  }

  @Test
  void updateWalletBalance_DepositOperation_Success() {
    // Arrange
    Wallet wallet = TestDataFactory.createWallet();
    wallet.setId(walletId);
    BigDecimal initAmount = wallet.getBalance();
    WalletUpdateRequestDTO requestDTO = TestDataFactory.createWalletDepositUpdateRequest(
        wallet.getId(), BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));

    when(walletRepository.findByIdForUpdate(requestDTO.walletId()))
        .thenReturn(Optional.of(wallet));

    // Act
    WalletResponseDTO responseDTO = walletService.updateWalletBalance(requestDTO);

    // Assert
    assertNotNull(responseDTO);
    assertEquals(wallet.getId(), responseDTO.walletId());
    assertEquals(initAmount.add(
        BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET)), wallet.getBalance());
    assertEquals(wallet.getBalance(), responseDTO.balance());
    verify(walletRepository, times(1)).findByIdForUpdate(wallet.getId());
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  void updateWalletBalance_WithdrawOperation_Success() {
    // Arrange
    Wallet wallet = TestDataFactory.createWallet();
    wallet.setId(walletId);
    BigDecimal initAmount = wallet.getBalance();
    WalletUpdateRequestDTO requestDTO = TestDataFactory.createWalletWithdrawUpdateRequest(
        wallet.getId(), BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));

    when(walletRepository.findByIdForUpdate(requestDTO.walletId()))
        .thenReturn(Optional.of(wallet));

    // Act
    WalletResponseDTO responseDTO = walletService.updateWalletBalance(requestDTO);

    // Assert
    assertNotNull(responseDTO);
    assertEquals(wallet.getId(), responseDTO.walletId());
    assertEquals(initAmount.subtract(
        BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET)), wallet.getBalance());
    assertEquals(wallet.getBalance(), responseDTO.balance());
    verify(walletRepository, times(1)).findByIdForUpdate(wallet.getId());
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  void updateWalletBalance_WithdrawOperation_NotEnoughMoneyExceptionThrown() {
    // Arrange
    Wallet wallet = TestDataFactory.createWallet();
    wallet.setId(walletId);
    WalletUpdateRequestDTO requestDTO = TestDataFactory.createWalletWithdrawUpdateRequest(
        wallet.getId(), BigDecimal.valueOf(MAX_AMOUNT_OF_TEST_WALLET));

    when(walletRepository.findByIdForUpdate(requestDTO.walletId()))
        .thenReturn(Optional.of(wallet));

    // Act
    NotEnoughMoneyException exception = assertThrows(NotEnoughMoneyException.class,
        () -> walletService.updateWalletBalance(requestDTO));

    // Assert
    assertEquals(NOT_ENOUGH_MONEY_EXCEPTION_MESSAGE, exception.getMessage());
    verify(walletRepository, times(1)).findByIdForUpdate(requestDTO.walletId());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void updateWalletBalance_WalletNotFound_WalletNotFoundExceptionThrown() {
    // Arrange
    WalletUpdateRequestDTO requestDTO = TestDataFactory.createWalletDepositUpdateRequest(
        UUID.randomUUID(), BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));

    when(walletRepository.findByIdForUpdate(requestDTO.walletId()))
        .thenReturn(Optional.empty());

    // Act
    WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
        () -> walletService.updateWalletBalance(requestDTO));

    // Assert
    assertEquals(WALLET_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    verify(walletRepository, times(1)).findByIdForUpdate(requestDTO.walletId());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }
}
