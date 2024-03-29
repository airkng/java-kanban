package ru.yandex.taskTracker.managers.httpServer.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public SubtaskHandler(TaskManager taskManager) {
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
                    getSubtasksResponse(exchange); //метод
                    return;
                }
                if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                    getSubtaskResponse(exchange, exchange.getRequestURI().getQuery()); //метод
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Неверные параметры query-заголовка (id) либо URL");
                break;
            }
            case "POST": {
                if (exchange.getRequestURI().getQuery() == null) {
                    addSubtaskResponse(exchange);                                         //метод
                    return;
                }
                if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                    updateSubtaskResponse(exchange, exchange.getRequestURI().getQuery());     //метод
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Bad request");
            }
            case "DELETE": {
                String[] requestPathAndParameters = fullPath.split("/");
                if (exchange.getRequestURI().getQuery() == null) {
                    deleteSubtasks(exchange);
                    return;
                }
                if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                    deleteSubtaskById(exchange, exchange.getRequestURI().getQuery());
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Неверные параметр (id) query-заголовка");
                break;
            }
            default: {
                String response = "Неверный метод http-запроса";
                sendClientErrorResponse(exchange, 404, response);
            }
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

    public void getSubtasksResponse(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getSubtasksList());
        sendResponse(exchange, 200, jsonString);
    }

    public void getSubtaskResponse(HttpExchange exchange, String requestParameters) throws IOException {
        String subtaskID = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(subtaskID);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Передан некорректный id в параметре");
            return;
        }
        Subtask subtask = taskManager.getSubtask(id.get());
        if (subtask == null) {
            sendClientErrorResponse(exchange, 404, "Id не найден");
        } else {
            String jsonSubtask = gson.toJson(subtask);
            sendResponse(exchange, 200, jsonSubtask);
        }
    }

    public void deleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasks();
        sendResponse(exchange, 200, "Все задачи успешно удалены");
    }

    public void deleteSubtaskById(HttpExchange exchange, String requestParameters) throws IOException {
        String subtaskID = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(subtaskID);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 404, "Передан некорректный id в параметре");
            return;
        }
        taskManager.deleteSubtaskById(id.get());
        sendResponse(exchange, 200, "Task was deleted");
    }

    public void addSubtaskResponse(HttpExchange exchange) throws IOException {
        try {
            InputStream body = exchange.getRequestBody();
            String jsonBody = new String(body.readAllBytes(), Charset.defaultCharset());
            Subtask subtask = gson.fromJson(jsonBody, Subtask.class);
            if (subtask == null) {
                sendClientErrorResponse(exchange, 400, "Передан null объект");
                return;
            }
            if (subtask.getId() != null && subtask.getStatus() != null && subtask.getEpicID() != null) {
                if (subtask.getId() == -1 || subtask.getEpicID() == -1) {
                    sendClientErrorResponse(exchange, 423, "Некорректный id");
                    return;
                }
                int subtaskId = taskManager.addSubtask(subtask);
                if (subtaskId == -1) {
                    sendClientErrorResponse(exchange, 423, "Воспользуйтесь методом update");
                    return;
                }
                sendResponse(exchange, 201, "Ваши изменения были учтены");
            } else {
                sendClientErrorResponse(exchange, 400, "В json-объекте отсутствует id или Status");
            }
        } catch (JsonSyntaxException | IOException e) {
            sendClientErrorResponse(exchange, 400, "Отправлен некорректный json-формат данных");
        }
    }

    public void updateSubtaskResponse(HttpExchange exchange, String requestParameters) throws IOException {
        String subtaskId = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(subtaskId);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Передан некорректный id в параметре");
            return;
        }
        try {
            InputStream body = exchange.getRequestBody();
            String jsonBody = new String(body.readAllBytes(), Charset.defaultCharset());
            Subtask subtask = gson.fromJson(jsonBody, Subtask.class);
            if (subtask == null) {
                sendClientErrorResponse(exchange, 400, "Передан null объект");
                return;
            }
            if (subtask.getId() != null && subtask.getStatus() != null && subtask.getEpicID() != null) {
                if (subtask.getId() == -1 || subtask.getEpicID() == -1) {
                    sendClientErrorResponse(exchange, 423, "Некорректный id");
                    return;
                }
                if (!subtask.getId().equals(id.get())) {
                    sendClientErrorResponse(exchange,400, "Id в теле не совпадает с id в query");
                    return;
                }
                sendResponse(exchange, 201, "Ваши изменения были учтены");
                taskManager.updateSubtask(subtask);
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