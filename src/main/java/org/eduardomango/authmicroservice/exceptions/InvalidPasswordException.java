package org.eduardomango.authmicroservice.exceptions;

public class InvalidPasswordException extends RuntimeException {
  public InvalidPasswordException(String message) {
    super(message);
  }
}
