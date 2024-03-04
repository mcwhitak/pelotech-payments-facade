package com.whitaker.payments.settle;

import static com.whitaker.payments.generated.database.tables.Settlements.SETTLEMENTS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

public class SettleInsertCommandTest {

    private static final int SETTLEMENT_ID = 938;

    private SettleInsertCommand command;

    @Test
    public void insertSuccessful() {
        Integer authId = 99;

        MockDataProvider provider = new SettleDataProvider(new Object[] {authId}, true);
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        command = new SettleInsertCommand(dsl);

        Optional<String> result = command.insertSettle(String.valueOf(authId));

        assertEquals("938", result.get());
    }

    @Test
    public void insertFailNan() {
        MockDataProvider provider = new SettleDataProvider(new Object[] {31}, true);
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        command = new SettleInsertCommand(dsl);

        Optional<String> result = command.insertSettle("JUNK");
        assertTrue(result.isEmpty());
    }

    @Test
    public void insertFailIntegrityConstraintViolation() {
        Integer authId = 99;

        MockDataProvider provider = new SettleDataProvider(new Object[] {authId}, false);
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        command = new SettleInsertCommand(dsl);

        Optional<String> result = command.insertSettle(String.valueOf(authId));

        assertTrue(result.isEmpty());
    }

    private class SettleDataProvider implements MockDataProvider {
        private static final String SQL =
                """
      insert into "public"."settlements" ("auth_id") values (?) returning "public"."settlements"."id"
    """
                        .trim();
        private final Object[] matchBindings;
        private final boolean fkMatches;

        public SettleDataProvider(Object[] matchBindings, boolean fkMatches) {
            this.matchBindings = matchBindings;
            this.fkMatches = fkMatches;
        }

        @Override
        public MockResult[] execute(MockExecuteContext ctx) throws SQLException {

            if (!fkMatches) {
                throw new IntegrityConstraintViolationException("FK Constraint violated");
            }
            DSLContext create = DSL.using(SQLDialect.POSTGRES);

            // Validate SQL is what we expect (before bind variables)
            assertEquals(SQL, ctx.sql());

            // Validate Bind variables
            assertArrayEquals(matchBindings, ctx.bindings());

            MockResult[] mock = new MockResult[1];

            Result<Record1<Integer>> result = create.newResult(SETTLEMENTS.ID);
            result.add(create.newRecord(SETTLEMENTS.ID).values(SETTLEMENT_ID));
            mock[0] = new MockResult(1, result);

            return mock;
        }
    }
}
