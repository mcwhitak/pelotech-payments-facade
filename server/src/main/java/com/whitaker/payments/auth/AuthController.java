package com.whitaker.payments.auth;

import com.whitaker.payments.generated.api.AuthApi;
import com.whitaker.payments.generated.model.AuthRequest;
import com.whitaker.payments.generated.model.SuccessModel;
import io.micronaut.http.annotation.Controller;

@Controller
public class AuthController implements AuthApi {

    private final AuthInsertCommand insertCommand;

    public AuthController(AuthInsertCommand insertCommand) {
        this.insertCommand = insertCommand;
    }

    public SuccessModel postAuth(AuthRequest authRequest) {
        String id = insertCommand.insertAuth(authRequest.getLvt(), authRequest.getAmount());

        return new SuccessModel(id);
    }
}
