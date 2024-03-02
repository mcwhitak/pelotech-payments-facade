package com.whitaker.payments;

import com.whitaker.payments.generated.model.AuthRequest;
import com.whitaker.payments.generated.model.SuccessModel;
import com.whitaker.payments.generated.api.AbstractDefaultController;
import io.micronaut.http.annotation.Controller;

@Controller
public class AuthController extends AbstractDefaultController {

  @Override
  public SuccessModel postAuth(AuthRequest authRequest) {
    return new SuccessModel("abc123");
  }
}
