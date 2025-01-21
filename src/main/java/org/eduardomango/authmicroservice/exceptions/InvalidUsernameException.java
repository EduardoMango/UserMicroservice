package org.eduardomango.authmicroservice.exceptions;

public class InvalidUsernameException extends RuntimeException {
  public InvalidUsernameException(String message) {
    super(message);
  }
}
