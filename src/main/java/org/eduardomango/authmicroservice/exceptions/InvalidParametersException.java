package org.eduardomango.authmicroservice.exceptions;

public class InvalidParametersException extends RuntimeException {
  public InvalidParametersException(String message) {
    super(message);
  }
}
