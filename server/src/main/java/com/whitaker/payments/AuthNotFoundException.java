package com.whitaker.payments;

public class AuthNotFoundException extends RuntimeException {

  private final String invalidId;

  public AuthNotFoundException(String invalidId) {
    super();
    this.invalidId = invalidId;
  }

  public String getInvalidId() {
    return this.invalidId;
  }
}
