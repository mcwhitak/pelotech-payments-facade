package com.whitaker.payments.auth;

import static com.whitaker.payments.generated.database.tables.Authorizations.AUTHORIZATIONS;

import jakarta.inject.Singleton;
import java.math.BigDecimal;
import org.jooq.DSLContext;
import org.jooq.Record1;

@Singleton
public class AuthInsertCommand {

  private final DSLContext dsl;

  public AuthInsertCommand(DSLContext dsl) {
    this.dsl = dsl;
  }

  public String insertAuth(String lvt, BigDecimal amount) {
    Integer id = dsl.insertInto(AUTHORIZATIONS)
        .set(AUTHORIZATIONS.LVT, lvt)
        .set(AUTHORIZATIONS.AMOUNT, amount)
        .returningResult(AUTHORIZATIONS.ID)
        .fetchOne(Record1::value1);

    return String.valueOf(id);
  }
}
