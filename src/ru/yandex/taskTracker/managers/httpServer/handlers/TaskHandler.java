package ru.yandex.taskTracker.managers.httpServer.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String fullPath = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET": {
                String[] requestPathAndParameters = fullPath.split("/");
                if (exchange.getRequestURI().getQuery() == null) {
                    getTasksResponse(exchange);
                    return;
                }
                if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                    getTaskResponse(exchange, exchange.getRequestURI().getQuery());
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Неверные параметры query-заголовка (id) либо URL");
                break;
            }
            case "POST": {
                if (exchange.getRequestURI().getQuery() == null) {
                    addTaskResponse(exchange);                                         //метод
                    return;
                }
                if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                    updateTaskResponse(exchange, exchange.getRequestURI().getQuery());     //метод
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Bad request");
            }
            case "DELETE": {
                String[] requestPathAndParameters = fullPath.split("/");
                if (exchange.getRequestURI().getQuery() == null) {
                    deleteTasks(exchange);
                    return;
                }
                if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                    deleteTaskById(exchange, exchange.getRequestURI().getQuery());
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Неверные параметр (id) query-заголовка");
                break;
            }
            default:
                String response = "Неверный метод http-запроса";
                sendClientErrorResponse(exchange, 404, response);
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    private void sendClientErrorResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close(); //хз нужно ли, так как мы обернули в try-with-resources
    }

    public void getTasksResponse(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getTasksList());
        sendResponse(exchange, 200, jsonString);
    }

    public void getTaskResponse(HttpExchange exchange, String requestParameters) throws IOException {
        String taskID = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(taskID);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Передан некорректный id в параметре");
            return;
        }
        Task task = taskManager.getTask(id.get());
        if (task == null) {
            sendClientErrorResponse(exchange, 404, "Id таска не найден");
        } else {
            String jsonTask = gson.toJson(task);
            sendResponse(exchange, 200, jsonTask);
        }
    }

    public void deleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        sendResponse(exchange, 200, "Все задачи успешно удалены");
    }

    public void deleteTaskById(HttpExchange exchange, String requestParameters) throws IOException {
        String taskID = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(taskID);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 404, "Передан некорректный id в параметре");
            return;
        }
        taskManager.deleteTaskById(id.get());
        sendResponse(exchange, 200, "Task was deleted");
    }

    public void addTaskResponse(HttpExchange exchange) throws IOException {
        try {
            InputStream body = exchange.getRequestBody();
            String jsonBody = new String(body.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(jsonBody, Task.class);
            if (task == null) {
                sendClientErrorResponse(exchange, 400, "Передан null объект");
                return;
            }
            if (task.getId() != null && task.getStatus() != null) {
                if (task.getId() == -1) {
                    sendClientErrorResponse(exchange, 423, "Некорректный ip");
                    return;
                }
                int taskId = taskManager.addTask(task);
                if (taskId == -1) {
                    sendClientErrorResponse(exchange, 423, "Воспользуйтесь методом update");
                    return;
                }
                sendResponse(exchange, 201, "Ваши изменения были учтены");
                return;
            } else {
                sendClientErrorResponse(exchange, 400, "В json-объекте отсутствует id или Status");
            }
        } catch (JsonSyntaxException | IOException e) {
            sendClientErrorResponse(exchange, 400, "Отправлен некорректный json-формат данных");
        }
    }

    public void updateTaskResponse(HttpExchange exchange, String requestParameters) throws IOException {
        String taskId = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(taskId);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Передан некорректный id в параметре");
            return;
        }
        try {
            InputStream body = exchange.getRequestBody();
            String jsonBody = new String(body.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(jsonBody, Task.class);
            if (task == null) {
                sendClientErrorResponse(exchange, 400, "Передан null объект");
                return;
            }
            if (task.getId() != null) {
                if (!task.getId().equals(id.get())) {
                    sendClientErrorResponse(exchange,400, "Id в теле не совпадает с id в query");
                    return;
                }
                if (task.getId() == -1) {
                    sendClientErrorResponse(exchange, 423, "Некорректный id");
                    return;
                }
                if (task.getStatus() == null) {
                    task.setStatus(Status.NEW); //добавлена проверка status по сравнению с другими handlers
                }
                sendResponse(exchange, 201, "Ваши изменения были учтены");
                taskManager.updateTask(task);
            } else {
                sendClientErrorResponse(exchange, 400, "В json-объекте отсутствует id или Status");
            }
        } catch (JsonSyntaxException | IOException e) {
            sendClientErrorResponse(exchange, 400, "Отправлен некорректный json-формат данных");
        }
    }


    private Optional<Integer> getTaskId(String stringID) {
        try {
            return Optional.of(Integer.parseInt(stringID));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
