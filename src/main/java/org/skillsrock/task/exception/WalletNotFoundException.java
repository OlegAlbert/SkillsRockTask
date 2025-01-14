package org.skillsrock.task.exception;

import static org.skillsrock.task.exception.ExceptionMessage.WALLET_NOT_FOUND_EXCEPTION_MESSAGE;

public class WalletNotFoundException extends RuntimeException {

  public WalletNotFoundException() {
    super(WALLET_NOT_FOUND_EXCEPTION_MESSAGE);
  }
}
