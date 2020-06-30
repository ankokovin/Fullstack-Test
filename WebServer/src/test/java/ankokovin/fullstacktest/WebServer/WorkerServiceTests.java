package ankokovin.fullstacktest.WebServer;
import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.UpdateWorkerInput;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import ankokovin.fullstacktest.WebServer.Services.WorkerService;
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
        @Test
        void whenNotFound_throwsUnknown() throws BaseException {
            String name = "Test";
            Integer org_id = 42442;
            Integer head_id = 452235;
            Integer id = 5356;
            Mockito.when(workerRepository.insert(name,org_id,head_id))
                    .thenReturn(id);
            Mockito.when(workerRepository.getById(id))
                    .thenThrow(new NoSuchRecordException(id));
            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> workerService.create(new CreateWorkerInput(name, org_id, head_id)));
            assertTrue(e.getCause() instanceof NoSuchRecordException);
        }
    }

    @Nested
    class Update {
        @Test
        void whenReturns_Return() throws BaseException {
            String name = "Test";
            Integer id = 1;
            Integer head_id = 2;
            Integer org_id = 3;
            Worker expected = new Worker(id, name, head_id, org_id);
            Mockito.when(workerRepository.update(
                    id,
                    name,
                    org_id,
                    head_id))
                    .thenReturn(id);
            Mockito.when(workerRepository.getById(id))
                    .thenReturn(expected);

            Worker actual = workerService.update(new UpdateWorkerInput(
                    id,
                    name,
                    org_id,
                    head_id
            ));
            assertEquals(expected, actual);
        }

        @Test
        void whenThrowsWrongId_Throws() throws BaseException {
            String name = "Test";
            Integer id = 2;
            Integer head_id = 3;
            Integer org_id = 4;
            Mockito.when(workerRepository.update(
                    id,
                    name,
                    org_id,
                    head_id))
                    .thenThrow(new WrongHeadIdException(head_id, Table.WORKER));
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> workerService.update(new UpdateWorkerInput(
                            id,
                            name,
                            org_id,
                            head_id)));
            assertEquals(head_id, e.id);
            assertEquals(Table.WORKER, e.to);
        }

        @Test
        void whenNotFound_Throws() throws BaseException {
            String name = "Test";
            Integer id = 3;
            Integer head_id = 4;
            Integer org_id = 5;
            Mockito.when(workerRepository.update(
                    id,
                    name,
                    org_id,
                    head_id))
                    .thenReturn(id);
            Mockito.when(workerRepository.getById(id))
                    .thenThrow(new NoSuchRecordException(id));
            UnexpectedException e = assertThrows(UnexpectedException.class,
                    () -> workerService.update(new UpdateWorkerInput(
                            id,
                            name,
                            org_id,
                            head_id)));
            assertTrue(e.getCause() instanceof NoSuchRecordException);
            assertEquals(id, ((NoSuchRecordException) e.getCause()).id);
        }
    }
    @Nested
    class Delete {
        @Test
        void whenDelete_Returns() throws NoSuchRecordException {
            Worker expected = new Worker(1353,"test",1,null);
            Mockito.when(workerRepository.getById(expected.getId()))
                    .thenReturn(expected);
            Mockito.when(workerRepository.delete(expected.getId()))
                    .thenReturn(expected.getId());
            Worker actual = workerService.delete(expected.getId());
            assertEquals(expected, actual);
        }
        @Test
        void whenNoSuchRecord_Throws() throws NoSuchRecordException {
            Integer id = 25265;
            Mockito.when(workerRepository.getById(id))
                    .thenThrow(new NoSuchRecordException(id));
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> workerService.delete(id));
            assertEquals(id, e.id);
        }
    }
    @Nested
    class Get {
        @Nested
        class GetById {
            @Test
            void whenReturns_Returns() throws NoSuchRecordException {
                Worker expected = new Worker(1,"test", 1, null);
                Mockito.when(workerRepository.getById(expected.getId()))
                        .thenReturn(expected);
                Worker actual = workerService.getById(expected.getId());
                assertEquals(expected, actual);
            }
            @Test
            void whenThrows_Throws() throws NoSuchRecordException {
                Integer id = 15165;
                Mockito.when(workerRepository.getById(id))
                        .thenThrow(new NoSuchRecordException(id));
                NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                        () -> workerService.getById(id));
                assertEquals(id, e.id);
            }
        }
    }
}
