package org.skillsrock.task.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.skillsrock.task.util.Constant.MIN_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.TestDataFactory.createWallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.skillsrock.task.entity.OperationType;
import org.skillsrock.task.entity.Transaction;
import org.skillsrock.task.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private WalletRepository walletRepository;

  @Test
  void saveTransactionTest_Success() {
    // Arrange
    Wallet wallet = createWallet();
    walletRepository.save(wallet);

    Transaction transaction = new Transaction();
    transaction.setWallet(wallet);
    transaction.setAmount(BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));
    transaction.setOperationType(OperationType.WITHDRAW);
    transaction.setDateOfOperation(LocalDateTime.now());

    // Act
    Transaction savedTransaction = transactionRepository.save(transaction);

    // Assert
    assertNotNull(savedTransaction);
    assertNotNull(savedTransaction.getId());
    assertEquals(BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET), savedTransaction.getAmount());
    assertEquals(OperationType.WITHDRAW, savedTransaction.getOperationType());
  }

}
