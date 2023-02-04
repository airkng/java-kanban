package ru.yandex.taskTracker.managers.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TaskHistoryHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public TaskHistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String fullPath = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET": {
                if (fullPath.split("/").length == 3 && exchange.getRequestURI().getQuery() == null) {
                    if (taskManager.getHistory().isEmpty()) {
                        sendResponse(exchange, 204, "History is empty");
                        return;
                    }
                    String jsonHistory = gson.toJson(taskManager.getHistory());
                    sendResponse(exchange, 201, jsonHistory);
                    return;
                }
                sendClientErrorResponse(exchange, 400, "Некорректный запрос. URl не существует или добавлен query-заголовок");
                break;
            }
            default:
                sendClientErrorResponse(exchange, 400, "Некорректный метод");
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
}
