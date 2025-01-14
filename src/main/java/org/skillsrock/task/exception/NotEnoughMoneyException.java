package org.skillsrock.task.exception;

import static org.skillsrock.task.exception.ExceptionMessage.NOT_ENOUGH_MONEY_EXCEPTION_MESSAGE;

public class NotEnoughMoneyException extends RuntimeException {

  public NotEnoughMoneyException() {
    super(NOT_ENOUGH_MONEY_EXCEPTION_MESSAGE);
  }
}
