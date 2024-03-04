package com.whitaker.payments.settle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.whitaker.payments.generated.model.SettleRequest;
import com.whitaker.payments.generated.model.SuccessModel;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SettleControllerTest {

    @Mock
    private SettleInsertCommand insertCommand;

    private SettleController controller;

    @BeforeEach
    public void setup() {
        this.controller = new SettleController(insertCommand);
    }

    @Test
    public void successfulSettle() {
        String expected = "abc123";

        when(insertCommand.insertSettle("auth_id")).thenReturn(Optional.of(expected));

        SuccessModel result = controller.settleAuth(new SettleRequest("auth_id"));
        assertEquals(expected, result.getId());
    }
}
