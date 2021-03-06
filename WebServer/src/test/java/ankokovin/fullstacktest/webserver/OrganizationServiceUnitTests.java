package ankokovin.fullstacktest.webserver;


import ankokovin.fullstacktest.webserver.exceptions.*;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Organization;
import ankokovin.fullstacktest.webserver.models.Table;
import ankokovin.fullstacktest.webserver.models.response.OrgListElement;
import ankokovin.fullstacktest.webserver.models.response.Page;
import ankokovin.fullstacktest.webserver.models.response.TreeNode;
import ankokovin.fullstacktest.webserver.models.webinput.CreateOrganizationInput;
import ankokovin.fullstacktest.webserver.models.webinput.UpdateOrganizationInput;
import ankokovin.fullstacktest.webserver.repos.OrganizationRepository;
import ankokovin.fullstacktest.webserver.services.OrganizationService;
import org.jooq.Record4;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class OrganizationServiceUnitTests {

    @SuppressWarnings({"unused", "InnerClassMayBeStatic"})
    abstract class OrganizationServiceTestClassTemplate {
        @Autowired
        protected OrganizationService organizationService;

        @MockBean
        protected OrganizationRepository organizationRepository;
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class Create extends OrganizationServiceTestClassTemplate {
        @BeforeEach
        public void setUp() throws BaseException {
            String name1 = "ООО Тест";
            Mockito.when(organizationRepository.insert(name1, null))
                    .thenReturn(1)
                    .thenThrow(new SameNameException(name1));
            Mockito.when(organizationRepository.getById(1))
                    .thenReturn(new Organization(1, name1, null));

            String name2 = "ООО Тест-2";
            Mockito.when(organizationRepository.insert(name2, 1))
                    .thenReturn(2)
                    .thenThrow(new SameNameException(name2));
            Mockito.when(organizationRepository.getById(2))
                    .thenReturn(new Organization(2, name2, 1));

            String name3 = "ООО Тест-3";
            Mockito.when(organizationRepository.insert(name3, 1))
                    .thenReturn(3)
                    .thenThrow(new SameNameException(name3));
            Mockito.when(organizationRepository.getById(3))
                    .thenReturn(new Organization(3, name3, 1));

            Mockito.when(organizationRepository.insert(name3, 3))
                    .thenThrow(new WrongHeadIdException(3, Table.ORGANIZATION));
            String name4 = "ООО Тест-4";
            Mockito.when(organizationRepository.insert(name4, 4)).thenReturn(4);
            Mockito.when(organizationRepository.getById(4)).thenThrow(new NoSuchRecordException(4));
        }

        @Test
        public void whenCreateCorrectNoHead_thenOrganizationCreates() throws BaseException {
            String name = "ООО Тест";

            Organization actual = organizationService.create(new CreateOrganizationInput(name, null));
            assertEquals(new Organization(1, name, null), actual);
        }

        @Test
        public void whenCreateCorrectHead_thenOrganizationCreates() throws BaseException {
            String name = "ООО Тест-2";

            Organization actual = organizationService.create(new CreateOrganizationInput(name, 1));
            assertEquals(new Organization(2, name, 1), actual);
        }

        @Test
        public void whenCreateSameName_thenThrowsError() throws BaseException {
            String name = "ООО Тест-3";

            Organization actual_first = organizationService.create(new CreateOrganizationInput(name, 1));
            assertEquals(new Organization(3, name, 1), actual_first);

            SameNameException exception = assertThrows(SameNameException.class,
                    () -> organizationService.create(new CreateOrganizationInput(name, 1)));
            assertEquals(name, exception.name);
        }

        @Test
        public void whenCreateWrongHead_thenThrows() {
            int expected = 3;
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationService.create(new CreateOrganizationInput("ООО Тест-3", expected)));
            assertEquals(expected, e.id);
        }

        @Test
        public void whenCreateNoSuchRecord_thenThrowsUnexpected() {
            int expected = 4;
            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationService.create(new CreateOrganizationInput("ООО Тест-4", expected)));
            assertTrue(e.getCause() instanceof NoSuchRecordException);
            assertEquals(expected, ((NoSuchRecordException) e.getCause()).id);
        }
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class Update extends OrganizationServiceTestClassTemplate {

        @Test
        public void whenUpdateCorrect_thenReturns() throws BaseException {
            Organization expected = new Organization(1, "ООО Тест", null);
            Mockito.when(organizationRepository.update(
                    expected.getId(),
                    expected.getOrgName(),
                    expected.getHeadOrgId()))
                    .thenReturn(1);
            Mockito.when(organizationRepository.getById(1))
                    .thenReturn(expected);
            Organization actual = organizationService
                    .update(new UpdateOrganizationInput(
                                    expected.getId(),
                                    expected.getOrgName(),
                                    expected.getHeadOrgId()
                            )
                    );
            assertEquals(expected, actual);
        }

        @Test
        public void whenUpdateThrowsSameName_thenThrows() throws BaseException {
            String expected = "ООО Тест";
            UpdateOrganizationInput model = new UpdateOrganizationInput(1, expected, null);
            Mockito.when(organizationRepository
                    .update(model.id, model.name, model.org_id))
                    .thenThrow(new SameNameException(expected));
            SameNameException e = assertThrows(SameNameException.class,
                    () -> organizationService.update(model));
            assertEquals(expected, e.name);
        }

        @Test
        public void whenUpdateThrowsWrongHeadId_thenThrows() throws BaseException {
            Integer expected = 1;
            UpdateOrganizationInput model = new UpdateOrganizationInput(1, "ООО Тест", expected);
            Mockito.when(organizationRepository
                    .update(model.id, model.name, model.org_id))
                    .thenThrow(new WrongHeadIdException(expected, Table.ORGANIZATION));
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationService.update(model));
            assertEquals(expected, e.id);
        }

        @Test
        public void whenUpdateNotFound_thenThrows() throws BaseException {
            Integer expected = 1;
            UpdateOrganizationInput model = new UpdateOrganizationInput(1, "ООО Тест", expected);
            Mockito.when(organizationRepository
                    .update(model.id, model.name, model.org_id))
                    .thenThrow(new NoSuchRecordException(expected));
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> organizationService.update(model));
            assertEquals(expected, e.id);
        }

        @Test
        public void whenUpdateUnexpectedChange_thenThrows() throws BaseException {
            Integer expected = 1;
            UpdateOrganizationInput model = new UpdateOrganizationInput(expected, "ООО Тест", null);
            Mockito.when(organizationRepository
                    .update(model.id, model.name, model.org_id))
                    .thenReturn(model.id);
            Mockito.when(organizationRepository.getById(expected))
                    .thenThrow(new NoSuchRecordException(expected));
            UnexpectedException ex = assertThrows(UnexpectedException.class,
                    () -> organizationService.update(model));
            assertTrue(ex.getCause() instanceof NoSuchRecordException);
            assertEquals(expected, ((NoSuchRecordException) ex.getCause()).id);
        }
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class Delete extends OrganizationServiceTestClassTemplate {

        @Test
        public void whenDeleteSucceeds_thenReturns() throws BaseException {
            int id = 1;
            Organization expected = new Organization(id, "ООО Тест", null);
            Mockito.when(organizationRepository.delete(id))
                    .thenReturn(id);
            Mockito.when(organizationRepository.getById(id))
                    .thenReturn(expected);

            Organization actual = organizationService.delete(id);
            assertEquals(expected, actual);
        }

        @Test
        public void whenDeleteThrowsNoSuchRecord_thenThrowsNoSuchRecord() throws BaseException {
            int expected = 1;
            Mockito.when(organizationRepository.delete(expected))
                    .thenThrow(new NoSuchRecordException(expected));

            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> organizationService.delete(expected));
            assertEquals(expected, e.id);
        }

        @Test
        public void whenDeleteThrowsDeleteHasChildExceptionOrganization_thenThrowsDeleteHasChildExceptionOrganization()
                throws BaseException {
            DeleteHasChildException expected = new DeleteHasChildException(42, Table.ORGANIZATION);
            Mockito.when(organizationRepository.delete(expected.id))
                    .thenThrow(expected);

            DeleteHasChildException e = assertThrows(DeleteHasChildException.class,
                    () -> organizationService.delete(expected.id));
            assertEquals(expected.id, e.id);
            assertEquals(expected.table, e.table);
        }

        @Test
        public void whenDeleteThrowsDeleteHasChildExceptionWorker_thenThrowsDeleteHasChildExceptionWorker()
                throws BaseException {
            DeleteHasChildException expected = new DeleteHasChildException(42, Table.WORKER);
            Mockito.when(organizationRepository.delete(expected.id))
                    .thenThrow(expected);

            DeleteHasChildException e = assertThrows(DeleteHasChildException.class,
                    () -> organizationService.delete(expected.id));
            assertEquals(expected.id, e.id);
            assertEquals(expected.table, e.table);
        }

        @Test
        public void whenDeleteThrowsUnexpectedException_thenThrowsUnexpectedException()
                throws BaseException {
            DeleteHasChildException expected = new DeleteHasChildException(42, Table.UNKNOWN);
            Mockito.when(organizationRepository.delete(expected.id))
                    .thenThrow(new UnexpectedException(expected));

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationService.delete(expected.id));
            assertTrue(e.getCause() instanceof DeleteHasChildException);
            DeleteHasChildException actual = (DeleteHasChildException) e.getCause();
            assertEquals(expected.id, actual.id);
            assertEquals(expected.table, actual.table);
        }

        @Test
        public void whenDeleteReturnedWrongId() throws BaseException {
            DeleteHasChildException expected = new DeleteHasChildException(42, Table.UNKNOWN);
            Mockito.when(organizationRepository.delete(expected.id))
                    .thenReturn(expected.id + 1);

            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> organizationService.delete(expected.id));
            assertEquals("Delete returned wrong id", e.getMessage());
        }
    }

    @Nested
    class Get {

        @Nested
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
        class GetById extends OrganizationServiceTestClassTemplate {
            @Test
            void whenReturns_thenReturns() throws NoSuchRecordException {
                int id = 42;
                Organization expected = new Organization(id, "ООО Тест", null);
                Mockito.when(organizationRepository.getById(id))
                        .thenReturn(expected);

                Organization actual = organizationService.getById(id);
                assertEquals(expected, actual);
            }

            @Test
            void whenThrows_thenThrows() throws NoSuchRecordException {
                int id = 42;
                NoSuchRecordException expected = new NoSuchRecordException(42);
                Mockito.when(organizationRepository.getById(id))
                        .thenThrow(expected);

                NoSuchRecordException actual = assertThrows(NoSuchRecordException.class,
                        () -> organizationService.getById(id));
                assertEquals(expected, actual);
            }
        }

        @Nested
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
        class GetAll extends OrganizationServiceTestClassTemplate {
            @Test
            void whenAsked_thenReturns() {
                int pageNum = 1;
                int pageSize = 1;
                OrgListElement el = new OrgListElement(1, "Test", 42);
                List<Record4<Integer, String, Integer, Integer>> repRes = new LinkedList<>();
                @SuppressWarnings("unchecked")
                Record4<Integer, String, Integer, Integer> mockResult = Mockito.mock(Record4.class);
                Mockito.when(mockResult.component1()).thenReturn(el.id);
                Mockito.when(mockResult.component2()).thenReturn(el.name);
                Mockito.when(mockResult.component3()).thenReturn(el.count);
                Mockito.when(organizationRepository.getCount(null)).thenReturn(1);
                repRes.add(mockResult);
                Mockito.when(organizationRepository.getAllWithCount(pageNum, pageSize, null))
                        .thenReturn(repRes);
                Page<List<OrgListElement>> actual = organizationService.getAllWithCount(pageNum, pageSize, null);
                assertEquals(1, actual.total);
                assertEquals(pageNum, actual.page);
                assertEquals(pageSize, actual.pageSize);
                assertEquals(1, actual.list.size());
                assertEquals(el, actual.list.get(0));
            }
        }

        @Nested
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
        class GetTree extends OrganizationServiceTestClassTemplate {
            @Test
            void whenAsked_thenReturns() throws NoSuchRecordException {

                int root = 1;
                int depth = 1;
                TreeNode<Organization> expected = new TreeNode<>(new Organization(root, "test", null));
                Mockito.when(organizationService.getTree(root, depth))
                        .thenReturn(expected);
                TreeNode<Organization> actual = organizationService.getTree(root, depth);
                assertEquals(expected, actual);
            }

            @Test
            void whenThrows_thenThrows() throws NoSuchRecordException {
                int root = 42;
                int depth = 1;
                NoSuchRecordException expected = new NoSuchRecordException(root);
                Mockito.when(organizationService.getTree(root, depth))
                        .thenThrow(expected);
                assertEquals(expected, assertThrows(NoSuchRecordException.class,
                        () -> organizationService.getTree(root, depth)));
            }
        }
    }
}
