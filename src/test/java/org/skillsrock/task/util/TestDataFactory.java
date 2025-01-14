package org.skillsrock.task.util;

import static org.skillsrock.task.util.Constant.MAX_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.Constant.MIN_AMOUNT_OF_TEST_WALLET;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.entity.OperationType;
import org.skillsrock.task.entity.Wallet;

public class TestDataFactory {

  private static final Random random = new Random();

  public static Wallet createWallet() {
    Wallet wallet = new Wallet();
    wallet.setBalance(BigDecimal.valueOf(random.nextDouble(
        MIN_AMOUNT_OF_TEST_WALLET + 1.00, MAX_AMOUNT_OF_TEST_WALLET)));
    return wallet;
  }

  public static WalletUpdateRequestDTO createWalletDepositUpdateRequest(
      UUID walletId, BigDecimal amount) {
    return new WalletUpdateRequestDTO(
        walletId,
        OperationType.DEPOSIT,
        amount
    );
  }

  public static WalletUpdateRequestDTO createWalletWithdrawUpdateRequest(
      UUID walletId,
      BigDecimal amount) {
    return new WalletUpdateRequestDTO(
        walletId,
        OperationType.WITHDRAW,
        amount
    );
  }
}
