package org.skillsrock.task.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.skillsrock.task.util.TestDataFactory.createWallet;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.skillsrock.task.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class WalletRepositoryTest {

  @Autowired
  private WalletRepository walletRepository;

  @Test
  void saveWalletTest_Success() {
    // Arrange
    Wallet wallet = createWallet();

    // Act
    Wallet savedWallet = walletRepository.save(wallet);

    // Assert
    assertNotNull(savedWallet);
    assertNotNull(savedWallet.getId());
    assertEquals(wallet.getId(), savedWallet.getId());
    assertEquals(wallet.getBalance(), savedWallet.getBalance());
  }

  @Test
  void findWalletByIdTest_Success() {
    // Arrange
    Wallet wallet = createWallet();
    walletRepository.save(wallet);

    // Act
    Optional<Wallet> foundWallet = walletRepository.findById(wallet.getId());

    // Assert
    assertTrue(foundWallet.isPresent());
    assertEquals(wallet.getId(), foundWallet.get().getId());
    assertEquals(wallet.getBalance(), foundWallet.get().getBalance());
  }

  @Test
  void findWalletByIdTest_NotFound() {
    // Act
    Optional<Wallet> foundWallet = walletRepository.findById(UUID.randomUUID());

    // Assert
    assertTrue(foundWallet.isEmpty());
  }

  @Test
  void findWalletByIdForUpdateTest_Success() {
    // Arrange
    Wallet wallet = createWallet();
    walletRepository.save(wallet);

    // Act
    Optional<Wallet> lockedWallet = walletRepository.findByIdForUpdate(wallet.getId());

    // Assert
    assertTrue(lockedWallet.isPresent());
    assertEquals(wallet.getId(), lockedWallet.get().getId());
    assertEquals(wallet.getBalance(), lockedWallet.get().getBalance());
  }

  @Test
  void findWalletByIdForUpdateTest_NotFound() {
    // Act
    Optional<Wallet> lockedWallet = walletRepository.findByIdForUpdate(UUID.randomUUID());

    // Assert
    assertTrue(lockedWallet.isEmpty());
  }

}
