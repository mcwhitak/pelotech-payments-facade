package com.whitaker.payments.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.whitaker.payments.generated.model.AuthRequest;
import com.whitaker.payments.generated.model.SuccessModel;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthInsertCommand insertCommand;

    private AuthController controller;

    @BeforeEach
    public void setup() {
        this.controller = new AuthController(insertCommand);
    }

    @Test
    public void successfulAuth() {
        String expected = "abc123";
        when(insertCommand.insertAuth("LVT", new BigDecimal("100.0"))).thenReturn(expected);

        SuccessModel result = controller.postAuth(new AuthRequest("LVT", new BigDecimal("100.0")));
        assertEquals(expected, result.getId());
    }
}
