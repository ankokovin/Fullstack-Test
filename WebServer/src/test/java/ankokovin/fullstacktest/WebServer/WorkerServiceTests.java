package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.*;
import ankokovin.fullstacktest.WebServer.Models.Input.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Input.UpdateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Response.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.Response.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Models.Response.WorkerTreeListElement;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import ankokovin.fullstacktest.WebServer.Services.WorkerService;
import org.assertj.core.util.Lists;
import org.jooq.Record8;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkerServiceTests {

    @Autowired
    protected WorkerService workerService;

    @MockBean
    protected WorkerRepository workerRepository;


    @Nested
    class Create {

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
            Mockito.when(workerRepository.insert(name, org_id, head_id))
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
            Mockito.when(workerRepository.insert(name, org_id, head_id))
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
            Mockito.when(workerRepository.insert(name, org_id, head_id))
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
        void whenNotFound_thenThrows() throws BaseException {
            UpdateWorkerInput model = new UpdateWorkerInput(1,"test",1,null);
            Mockito.when(workerRepository.update(model.id, model.name, model.org_id, model.head_id))
                    .thenThrow(new NoSuchRecordException(model.id));
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> workerService.update(model));
            assertEquals(model.id, e.id);
        }

        @Test
        void whenUnexpectedNotFound_Throws() throws BaseException {
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
        void whenDelete_Returns() throws BaseException {
            Worker expected = new Worker(1353, "test", 1, null);
            Mockito.when(workerRepository.getById(expected.getId()))
                    .thenReturn(expected);
            Mockito.when(workerRepository.delete(expected.getId()))
                    .thenReturn(expected.getId());
            Worker actual = workerService.delete(expected.getId());
            assertEquals(expected, actual);
        }

        @Test
        void whenNoSuchRecord_Throws() throws BaseException {
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
                Worker expected = new Worker(1, "test", 1, null);
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
        @Nested
        class GetAll {
            @Test
            void whenAsked_Returns() {
                int page = 2;
                int pageSize = 10;
                int org_id = 1;
                int head_id = 4;
                String org_name = "test";
                String head_name = "head_name";
                String name_format = "Name:";
                List<WorkerListElement> expected
                        = Lists.newArrayList(IntStream.rangeClosed(pageSize*(page-1), pageSize*page)
                        .mapToObj((i) -> new WorkerListElement(
                                        i,
                                        String.format(name_format+"%d",i),
                                        head_id,
                                        head_name,
                                        org_id,
                                        org_name)
                        ).collect(Collectors.toList()));
                List<Record8<Integer, String, Integer, String, Integer, String, Integer, Integer>> mockResult
                        = Lists.newArrayList(IntStream.rangeClosed(pageSize*(page-1), pageSize*page)
                        .mapToObj((i) -> {
                            Record8<Integer, String, Integer, String, Integer, String, Integer, Integer> res
                                    = Mockito.mock(Record8.class);
                            Mockito.when(res.component1()).thenReturn(i);
                            Mockito.when(res.component2()).thenReturn(String.format(name_format+"%d",i));
                            Mockito.when(res.component3()).thenReturn(head_id);
                            Mockito.when(res.component4()).thenReturn(head_name);
                            Mockito.when(res.component5()).thenReturn(org_id);
                            Mockito.when(res.component6()).thenReturn(org_name);
                            return res;
                        }).collect(Collectors.toList()));
                Mockito.when(workerRepository.getAll(page, pageSize,org_name,name_format))
                        .thenReturn(mockResult);
                List<WorkerListElement> actual = workerService.get(page, pageSize,name_format,org_name);
                assertEquals(expected.size(), actual.size());
                assertIterableEquals(expected, actual);
            }
        }
        @Nested
        class GetTree {
            @Test
            void whenCalled_returns() throws NoSuchRecordException {
                int id = 42;
                int depth = 1;
                TreeNode<WorkerTreeListElement> expected = new TreeNode<>(
                        new WorkerTreeListElement(42, "Hi",1,"test"));
                Mockito.when(workerRepository.getTree(depth, id)).thenReturn(expected);
                TreeNode<WorkerTreeListElement> actual = workerService.getTree(depth, id);
                assertEquals(expected, actual);
            }
            @Test
            void whenThrows_thenThrows() throws NoSuchRecordException {
                int id = 42;
                int depth = 1;
                NoSuchRecordException expected = new NoSuchRecordException(id);
                Mockito.when(workerRepository.getTree(depth, id)).thenThrow(expected);
                NoSuchRecordException actual = assertThrows(NoSuchRecordException.class
                        ,() -> workerService.getTree(depth, id));
                assertEquals(expected, actual);
            }
        }
    }
}
