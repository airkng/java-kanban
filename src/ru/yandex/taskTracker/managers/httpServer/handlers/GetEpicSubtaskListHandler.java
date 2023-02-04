package ru.yandex.taskTracker.managers.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class GetEpicSubtaskListHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public GetEpicSubtaskListHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String fullPath = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET": {
                if (fullPath.split("/").length == 4 && exchange.getRequestURI().getQuery() != null) {
                    if (exchange.getRequestURI().getQuery().startsWith("id=")) {
                        getEpicSubtasksResponse(exchange);
                        return;
                    }
                }
                sendClientErrorResponse(exchange, 400, "Некорректный запрос. URl не существует или добавлен" +
                        " неправильный query-заголовок");
                break;
            }
            default:
                sendClientErrorResponse(exchange, 400, "Некорректный метод");
        }
    }

    private void getEpicSubtasksResponse(HttpExchange exchange) throws IOException {
        String epicId = exchange.getRequestURI().getQuery().split("=")[1];
        Optional<Integer> id = getTaskId(epicId);
        if (id.isEmpty()) {
            sendClientErrorResponse(exchange, 400, "Некорректный id in query-header");
            return;
        }
        if (taskManager.getEpicSubtasks(id.get()) == null) {
            sendResponse(exchange, 204, "Subtasks are missing or incorrect Id");
            return;
        }
        String jsonHistory = gson.toJson(taskManager.getEpicSubtasks(id.get()));
        sendResponse(exchange, 200, jsonHistory);
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

    private Optional<Integer> getTaskId(String stringID) {
        try {
            return Optional.of(Integer.parseInt(stringID));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

