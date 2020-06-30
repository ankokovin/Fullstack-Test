package ankokovin.fullstacktest.WebServer;


import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.Mockito;
import org.mockito.internal.matchers.Same;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.annotation.Target;


public class OrganizationServiceTests {

    abstract  class OrganizationServiceTestClassTemplate {
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
                    .thenThrow(new WrongHeadIdException(3));

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
            String name = "ООО Тест-3";

            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationService.create(new CreateOrganizationInput(name, 3)));
        }
    }

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class Update extends OrganizationServiceTestClassTemplate {
        @BeforeEach
        public void setUp() throws BaseException {
        }

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
        public void whenUpdateThrowsWrongHeadId() throws BaseException {
            Integer expected = 1;
            UpdateOrganizationInput model = new UpdateOrganizationInput(1, "ООО Тест", expected);
            Mockito.when(organizationRepository
                    .update(model.id, model.name, model.org_id))
                    .thenThrow(new WrongHeadIdException(expected));
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationService.update(model));
            assertEquals(expected, e.id);
        }
    }

}
