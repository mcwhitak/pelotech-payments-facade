package com.whitaker.payments.settle;

import com.whitaker.payments.AuthNotFoundException;
import com.whitaker.payments.generated.api.SettleApi;
import com.whitaker.payments.generated.model.SettleRequest;
import com.whitaker.payments.generated.model.SuccessModel;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;

@Controller
public class SettleController implements SettleApi {

  private final SettleInsertCommand insertCommand;

  public SettleController(SettleInsertCommand insertCommand) {
    this.insertCommand = insertCommand;
  }

  public SuccessModel settleAuth(SettleRequest request) {
    return insertCommand.insertSettle(request.getAuthId())
        .map(SuccessModel::new)
        .orElseThrow(() -> new AuthNotFoundException(request.getAuthId()));
  }

  @Error
  public HttpResponse<String> invalidAuthId(HttpRequest request, AuthNotFoundException e) {
    return HttpResponse.<String>status(HttpStatus.BAD_REQUEST, "Invalid Auth Id")
        .body("Invalid Auth Id: " + e.getInvalidId());
  }
}
