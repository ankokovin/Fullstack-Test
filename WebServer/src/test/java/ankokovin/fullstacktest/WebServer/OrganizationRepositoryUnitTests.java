package ankokovin.fullstacktest.WebServer;


import ankokovin.fullstacktest.WebServer.Exceptions.DeleteHasChildException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;


public class OrganizationRepositoryUnitTests {
    @Nested
    public class Insert {
        @Test
        public void whenCreateThrowsSQLException_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new SQLException("BOOM");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.insert("1", null));
            assertTrue(e.getCause() instanceof DataAccessException);
        }

        @Test
        public void whenCreateThrowsUnknownDataIntegrityException_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new org.springframework.dao.DataIntegrityViolationException("lol");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.insert("1", null));
            assertTrue(e.getCause() instanceof org.springframework.dao.DataIntegrityViolationException);
        }
    }

    @Nested
    public class Update {
        @Test
        public void whenUpdateThrowsSQLException_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new SQLException("BOOM");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.update(1,"1", null));
            assertTrue(e.getCause() instanceof DataAccessException);
        }

        @Test
        public void whenUpdateThrowsUnknownDataIntegrityException_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new org.springframework.dao.DataIntegrityViolationException("lol");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.update(1,"1", null));
            assertTrue(e.getCause() instanceof org.springframework.dao.DataIntegrityViolationException);
        }

        @Test
        public void whenUpdateThrowsUnknownUncategorised_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new org.springframework.jdbc.UncategorizedSQLException(null,null,new SQLException());
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.update(1,"1", null));
            assertTrue(e.getCause() instanceof org.springframework.jdbc.UncategorizedSQLException);
        }
    }

    @Nested
    public class Delete {
        @Test
        public void whenDeleteThrowsSQLException_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new SQLException("BOOM");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.delete(1));
            assertTrue(e.getCause() instanceof DataAccessException);
        }

        @Test
        public void whenDeleteThrowsUnknownDataIntegrityException_thenThrowsUnexpectedException() {
            MockDataProvider mockDataProvider = ctx -> {
                throw new org.springframework.dao.DataIntegrityViolationException("lol");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.delete(1));
            assertTrue(e.getCause() instanceof org.springframework.dao.DataIntegrityViolationException);
        }
        @Test
        public void whenDeleteThrowsDataIntegrityException_withRefOnUnknownTable_thenThrowsUnexpectedException() {
            int id = 42;
            int expectedId = 32;
            MockDataProvider mockDataProvider = ctx -> {
                throw new org.springframework.dao.DataIntegrityViolationException(
                        "Delete violates foreign key constrain: record is still referenced in table UNKNOWN with key " +
                                "(id)=("+expectedId+")");
            };
            DSLContext dslContext = DSL.using(new MockConnection(mockDataProvider), SQLDialect.POSTGRES);
            OrganizationRepository organizationRepository = new OrganizationRepository(dslContext);
            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationRepository.delete(id));
            assertTrue(e.getCause() instanceof DeleteHasChildException);
            assertEquals(Table.UNKNOWN, ((DeleteHasChildException) e.getCause()).table);
            assertEquals(expectedId, ((DeleteHasChildException) e.getCause()).id);
        }
    }
}
