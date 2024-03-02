package com.whitaker.payments.settle;

import static com.whitaker.payments.generated.database.tables.Settlements.SETTLEMENTS;

import jakarta.inject.Singleton;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.exception.IntegrityConstraintViolationException;

@Singleton
public class SettleInsertCommand {

  private final DSLContext dsl;

  public SettleInsertCommand(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Optional<String> insertSettle(String authId) {
    int authIdInt;

    //Try-catch for ensuring authId is valid integer
    try {
      authIdInt = Integer.parseInt(authId);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }

    //Try-catch for FK constraint ensuring auth_id is present in AUTHORIZATIONS table
    try {
      Integer id = dsl.insertInto(SETTLEMENTS)
          .set(SETTLEMENTS.AUTH_ID, Integer.valueOf(authIdInt))
          .returningResult(SETTLEMENTS.ID)
          .fetchOne(Record1::value1);

      return Optional.of(String.valueOf(id));
    } catch (IntegrityConstraintViolationException e) {
      return Optional.empty();
    }
  }

}
