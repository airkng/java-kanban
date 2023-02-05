package ru.yandex.taskTracker.managers.httpServer;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
//Господи всевышний, пожалуйста только без багов
//Прими эту работу, молю
public class HttpTaskSeverTest {
    private static KVServer kvServer;

    static {
        try {
            kvServer = new KVServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpTaskServer httpTaskServer;
    private Gson gson = new Gson();

    private Task task1 = new Task("task1", "testing task", Status.NEW, 1);
    private Task task2 = new Task("task2", "testing", Status.IN_PROGRESS, 2);

    private Epic epic1 = new Epic("epic1", "global Epic test", 3);
    private Epic epic2 = new Epic("epic2", "server is the best", 4);

    private Subtask sub11 = new Subtask("sub11", "java", Status.NEW, 31, epic1.getId());
    private Subtask sub12 = new Subtask("sub12", "pascal", Status.DONE,32 ,epic1.getId());
    private Subtask sub13 = new Subtask("sub13", "c++", Status.NEW, 33,epic1.getId());
    private Subtask sub21 = new Subtask("sub21", "epic2 subtask", Status.IN_PROGRESS,41 ,epic2.getId());


    @BeforeAll
    static void start() throws IOException, InterruptedException {
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

    }

    @AfterAll
    static void end() {
        httpTaskServer.stop();
        kvServer.stop();
    }
    @BeforeEach
    void createTasks() {
        //для удобства, авось изменится id
        task1 = new Task("task1", "testing task", Status.NEW,1 );
        task2 = new Task("task2", "testing", Status.IN_PROGRESS, 2);

        epic1 = new Epic("epic1", "global Epic test", 3);
        epic2 = new Epic("epic2", "server is the best", 4);

        sub11 = new Subtask("sub11", "java", Status.NEW,31 ,epic1.getId());
        sub12 = new Subtask("sub12", "pascal", Status.DONE, 32,epic1.getId());
        sub13 = new Subtask("sub13", "c++", Status.NEW, 33 ,epic1.getId());
        sub21 = new Subtask("sub21", "epic2 subtask", Status.IN_PROGRESS, 41, epic2.getId());
    }

    @Test
    public void addOrUpdateTask_shouldReturnRightCodeResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task test = new Task("test", "testststete", Status.NEW, 100);
        String json = gson.toJson(test);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(test, httpTaskServer.manager.getTask(test.getId()));

        json = gson.toJson(task2);
        body = HttpRequest.BodyPublishers.ofString(json);

        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(task2, httpTaskServer.manager.getTask(task2.getId()));
    }

    @Test
    public void addOrUpdateEpic_shouldReturnRightCodeResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic test = new Epic("testingAPI", "testingServer", 10);
        String json = gson.toJson(test);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        json = gson.toJson(epic2);
        body = HttpRequest.BodyPublishers.ofString(json);

        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(epic2, httpTaskServer.manager.getEpic(epic2.getId()));

    }

    @Test
    public void addOrUpdateSubtask_shouldReturnRightCodeResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI epicUri = URI.create("http://localhost:8080/tasks/epic/");
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(epicUri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        json = gson.toJson(sub11);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(sub11, httpTaskServer.manager.getSubtask(sub11.getId()));

        json = gson.toJson(sub12);
        body = HttpRequest.BodyPublishers.ofString(json);

        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(sub12, httpTaskServer.manager.getSubtask(sub12.getId()));
    }

    @Test
    public void getAllTasksAndTasksByID_shouldReturnCodeResponseAndTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals("task1", task.getName());

    }
    @Test
    public void getAllEpicsAndEpicById_shouldReturnRightCodeAndEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic2);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=4");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals("epic2", epic.getName());
    }
    @Test
    void getAllSubtaskAndSubtasksById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic2);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(sub21);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .header("Content-type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=41");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode());
        assertEquals("sub21", subtask.getName());
    }
    @Test
    public void deleteAllTaskAndTaskById_shouldReturnRightTextResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Task was deleted", response.body());

        url = URI.create("http://localhost:8080/tasks/task/");
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Все задачи успешно удалены", response.body());

    }
    @Test
    public void deleteAllEpicsOrEpicById_shouldResponseRightBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Task was deleted", response.body());

        url = URI.create("http://localhost:8080/tasks/epic/");
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Все задачи успешно удалены", response.body());
    }

    @Test
    void deleteTaskAll_byId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(sub13);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=31");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Task was deleted", response.body());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Все задачи успешно удалены", response.body());
    }
}
