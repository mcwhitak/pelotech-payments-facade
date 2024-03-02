package com.whitaker.payments;

import static com.whitaker.payments.generated.database.tables.Authorizations.AUTHORIZATIONS;
import static com.whitaker.payments.generated.database.tables.Settlements.SETTLEMENTS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.whitaker.payments.generated.database.tables.records.AuthorizationsRecord;
import com.whitaker.payments.generated.database.tables.records.SettlementsRecord;
import com.whitaker.payments.generated.model.AuthRequest;
import com.whitaker.payments.generated.model.SettleRequest;
import com.whitaker.payments.generated.model.SuccessModel;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;

@MicronautTest
public class IntegrationTest {

  @Inject
  EmbeddedServer server;

  @Inject
  DSLContext dsl;

  @Inject
  @Client("/")
  HttpClient client;

  @Test
  public void insertAuthSuccess() {
    HttpRequest<AuthRequest> request = HttpRequest.POST("/auth", new AuthRequest("lvt", new BigDecimal("100.0")));

    SuccessModel response = client.toBlocking()
        .retrieve(request, Argument.of(SuccessModel.class));

    //Throws NumberFormatException if not a valid int
    int authId = Integer.parseInt(response.getId());

    //Ensure auth exists
    AuthorizationsRecord authRecord = dsl.selectFrom(AUTHORIZATIONS)
      .where(AUTHORIZATIONS.ID.eq(authId))
      .fetchOne();

    assertAll(
        () -> assertEquals(authId, authRecord.getId()),
        () -> assertEquals("lvt", authRecord.getLvt()),
        () -> assertEquals(new BigDecimal("100.0"), authRecord.getAmount()),
        () -> assertTrue(LocalDateTime.now().isAfter(authRecord.getCreateTimestamp()))
    );
  }

  @Test
  public void insertAuthAndSettleSuccess() {
    HttpRequest<AuthRequest> authRequest = HttpRequest.POST("/auth", new AuthRequest("lvt", new BigDecimal("999.99")));

    SuccessModel authResponse = client.toBlocking()
        .retrieve(authRequest, Argument.of(SuccessModel.class));


    HttpRequest<SettleRequest> settleRequest = HttpRequest.POST("/settle", new SettleRequest(authResponse.getId()));

    SuccessModel settleResponse = client.toBlocking()
        .retrieve(settleRequest, Argument.of(SuccessModel.class));

    //Throws NumberFormatException if not a valid int
    int authId = Integer.parseInt(authResponse.getId());
    int settleId = Integer.parseInt(settleResponse.getId());


    //Ensure auth exists
    AuthorizationsRecord authRecord = dsl.selectFrom(AUTHORIZATIONS)
      .where(AUTHORIZATIONS.ID.eq(authId))
      .fetchOne();

    assertAll(
        () -> assertEquals(authId, authRecord.getId()),
        () -> assertEquals("lvt", authRecord.getLvt()),
        () -> assertEquals(new BigDecimal("999.99"), authRecord.getAmount()),
        () -> assertTrue(LocalDateTime.now().isAfter(authRecord.getCreateTimestamp()))
    );

    //Ensure settle exists
    SettlementsRecord settleRecord = dsl.selectFrom(SETTLEMENTS)
        .where(SETTLEMENTS.ID.eq(settleId))
        .fetchOne();

    assertAll(
        () -> assertEquals(settleId, settleRecord.getId()),
        () -> assertEquals(authId, settleRecord.getAuthId()),
        () -> assertTrue(LocalDateTime.now().isAfter(settleRecord.getCreateTimestamp()))
    );
  }

  @Test
  public void settleFailureInvalidAuthId() {
    HttpRequest<SettleRequest> request = HttpRequest.POST("/settle", new SettleRequest("INVALID"));


    HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
      HttpResponse<?> response = client.toBlocking()
        .exchange(request);
    });

    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  public void settleFailureAuthIdNotFound() {
    HttpRequest<SettleRequest> request = HttpRequest.POST("/settle", new SettleRequest("99900099"));

    HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
      HttpResponse<?> response = client.toBlocking()
        .exchange(request);
    });

    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }
}
