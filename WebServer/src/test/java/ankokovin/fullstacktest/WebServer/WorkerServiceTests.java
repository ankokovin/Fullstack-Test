package ankokovin.fullstacktest.WebServer;
import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import ankokovin.fullstacktest.WebServer.Services.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkerServiceTests {

    @Autowired
    protected WorkerService workerService;

    @MockBean
    protected WorkerRepository workerRepository;


    @Nested
    class Create{

        @Test
        void whenReturns_thenReturn() throws BaseException {
            String name = "Test";
            Integer id = 57;
            Integer org_id = 42;
            Integer head_id = 45;
            Worker expected = new Worker(id, name, org_id, head_id);
            Mockito.when(workerRepository.insert(name, org_id, head_id))
                    .thenReturn(id);
            Mockito.when(workerRepository.getById(id))
                    .thenReturn(expected);
            CreateWorkerInput input = new CreateWorkerInput(name, org_id, head_id);
            Worker actual = workerService.create(input);
            assertEquals(expected, actual);
        }

        @Test
        void whenThrowsWrongHead_thenThrows() throws BaseException {
            String name = "Test";
            Integer org_id = 42;
            Integer head_id = 46;
            Mockito.when(workerRepository.insert(name,org_id,head_id))
                    .thenThrow(new WrongHeadIdException(org_id, Table.WORKER));
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> workerService.create(new CreateWorkerInput(name, org_id, head_id)));
            assertEquals(org_id, e.id);
            assertEquals(Table.WORKER, e.to);
        }
        @Test
        void whenThrowsUnknownException() throws BaseException {
            String name = "Test";
            Integer org_id = 424;
            Integer head_id = 452;
            String message = "TestTest";
            Mockito.when(workerRepository.insert(name,org_id,head_id))
                    .thenThrow(new UnexpectedException(message));
            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> workerService.create(new CreateWorkerInput(name, org_id, head_id)));
            assertEquals(message, e.getMessage());
        }
    }
}
