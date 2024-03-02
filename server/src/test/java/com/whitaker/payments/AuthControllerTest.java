package com.whitaker.payments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.whitaker.payments.generated.model.AuthRequest;
import com.whitaker.payments.generated.model.SuccessModel;

public class AuthControllerTest {

  private static final AuthController CONTROLLER = new AuthController();

  @Test
  public void successfulAuth() {
    SuccessModel result = CONTROLLER.postAuth(new AuthRequest("LVT", 100.0f));
    assertEquals(new SuccessModel("abc123"), result);
  }

}
