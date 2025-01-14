package org.skillsrock.task.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.entity.Wallet;
import org.skillsrock.task.repository.WalletRepository;
import org.skillsrock.task.util.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class WalletServiceConcurrencyTest {

  @Autowired
  private WalletRepository walletRepository;

  @Autowired
  private WalletService walletService;

  private static final int NUM_REQUESTS = 1000;
  private static final BigDecimal UPDATE_AMOUNT = BigDecimal.valueOf(10.00);

  @Test
  void testConcurrencyOnSingleWallet() {
    // Arrange
    Wallet wallet = new Wallet();
    wallet.setBalance(BigDecimal.valueOf(1000));
    walletRepository.save(wallet);

    WalletUpdateRequestDTO requestDTO = TestDataFactory.createWalletDepositUpdateRequest(
        wallet.getId(), UPDATE_AMOUNT);

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    List<CompletableFuture<Void>> futures = new ArrayList<>();

    // Act
    for (int i = 0; i < NUM_REQUESTS; i++) {
      futures.add(CompletableFuture.runAsync(() -> {
        walletService.updateWalletBalance(requestDTO);
      }, executorService));
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    Wallet updatedWallet = walletRepository.findById(wallet.getId()).orElseThrow();

    BigDecimal actualBalance = updatedWallet.getBalance();
    BigDecimal expectedBalance = BigDecimal.valueOf((NUM_REQUESTS + (NUM_REQUESTS * 10)));

    // Assert
    assertEquals(0, actualBalance.compareTo(expectedBalance));

    executorService.shutdown();
  }
}