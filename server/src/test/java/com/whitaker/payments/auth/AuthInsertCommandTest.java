package com.whitaker.payments.auth;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.whitaker.payments.generated.database.tables.Authorizations.AUTHORIZATIONS;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

public class AuthInsertCommandTest {

  private static final int AUTH_ID = 99;

  private AuthInsertCommand command;

  @Test
  public void insertSuccessful() {
    String lvt = "TEST_LVT";
    BigDecimal amount = BigDecimal.TEN;
    MockDataProvider provider = new AuthDataProvider(new Object[]{lvt, amount});
    MockConnection connection = new MockConnection(provider);

    DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

    command = new AuthInsertCommand(dsl);

    String result = command.insertAuth(lvt, amount);
    assertEquals("99", result);
  }

  private class AuthDataProvider implements MockDataProvider {

    private static final String SQL = """
      insert into "public"."authorizations" ("lvt", "amount") values (?, ?) returning "public"."authorizations"."id"
    """.trim();
    private final Object[] matchBindings;

    public AuthDataProvider(Object[] matchBindings) {
      this.matchBindings = matchBindings;
    }

    @Override
    public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
      DSLContext create = DSL.using(SQLDialect.POSTGRES);

      //Validate SQL is what we expect (before bind variables)
      assertEquals(SQL, ctx.sql());

      //Validate Bind Variables
      assertArrayEquals(matchBindings, ctx.bindings());

      MockResult[] mock = new MockResult[1];

      Result<Record1<Integer>> result = create.newResult(AUTHORIZATIONS.ID);
      result.add(create.newRecord(AUTHORIZATIONS.ID).values(AUTH_ID));
      mock[0] = new MockResult(1, result);

      return mock;
    }
  }
}
