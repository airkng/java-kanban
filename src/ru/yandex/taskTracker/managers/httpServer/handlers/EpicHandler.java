package ru.yandex.taskTracker.managers.httpServer.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String fullPath = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET": {
                String[] requestPathAndParameters = fullPath.split("/");
                if (requestPathAndParameters.length == 3) {
                    if (exchange.getRequestURI().getQuery() == null) {
                        getEpicsResponse(exchange); //метод
                        return;
                    }
                    if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                        getEpicResponse(exchange, exchange.getRequestURI().getQuery()); //метод
                        return;
                    }
                    sendClientErrorResponse(exchange, 400, "Неверные параметры query-заголовка (id) либо URL");
                    break;
                }
                sendClientErrorResponse(exchange, 404, "Путь не найден");
                break;
            }
            case "POST": {
                addOrUpdateSubtask(exchange);
                break;
            }
            case "DELETE": {
                String[] requestPathAndParameters = fullPath.split("/");
                if (requestPathAndParameters.length == 3) {
                    if (exchange.getRequestURI().getQuery() == null) {
                        deleteEpics(exchange);
                        return;
                    }
                    if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                        deleteEpicById(exchange, exchange.getRequestURI().getQuery());
                        return;
                    }
                    sendClientErrorResponse(exchange, 400, "Неверные параметр (id) query-заголовка");
                    break;
                }
                sendClientErrorResponse(exchange, 404, "Запрос по данному URL не найден");
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

    public void getEpicsResponse(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getEpicList());
        sendResponse(exchange, 200, jsonString);
    }

    public void getEpicResponse(HttpExchange exchange, String requestParameters) throws IOException {
        String epicID = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(epicID);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Передан некорректный id в параметре");
            return;
        }
        Epic epic = taskManager.getEpic(id.get());
        if (epic == null) {
            sendClientErrorResponse(exchange, 404, "Epic c данным id не найден");
        } else {
            String jsonSubtask = gson.toJson(epic);
            sendResponse(exchange, 200, jsonSubtask);
        }
    }

    public void deleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        sendResponse(exchange, 200, "Все задачи успешно удалены");
    }

    public void deleteEpicById(HttpExchange exchange, String requestParameters) throws IOException {
        String epicID = requestParameters.split("=")[1];
        Optional<Integer> id = getTaskId(epicID);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Передан некорректный id в параметре");
            return;
        }
        taskManager.deleteEpicById(id.get());
        sendResponse(exchange, 200, "Task was deleted");
    }

    public void addOrUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            /*if (exchange.getRequestBody().available() != 0) {*/
                InputStream body = exchange.getRequestBody();
                String jsonBody = new String(body.readAllBytes(), Charset.defaultCharset());
                Epic epic = gson.fromJson(jsonBody, Epic.class); //Что-то не так с эпиком, не понимаю ничего
                if (epic == null) {
                    sendClientErrorResponse(exchange, 400, "Передан null объект");
                    return;
                }
                if (epic.getId() != null) {
                    if (epic.getId() == -1) {
                        sendClientErrorResponse(exchange, 423, "Некорректный id");
                        return;
                    }
                    if (epic.getStatus() == null) {
                        epic.setStatus(Status.NEW); //добавлена проверка status по сравнению с другими handlers
                    }
                    sendResponse(exchange, 201, "Ваши изменения были учтены");
                    int epicId = taskManager.addEpic(epic);
                    if (epicId == -1) {
                        taskManager.updateEpic(epic);
                    }
                    return;
                } else {
                    sendClientErrorResponse(exchange, 400, "В json-объекте отсутствует id или Status");
                }
            /*} else {*/
                sendClientErrorResponse(exchange, 400, "Отсутствует json-объект");
            //}
        } catch (JsonSyntaxException e) {
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
