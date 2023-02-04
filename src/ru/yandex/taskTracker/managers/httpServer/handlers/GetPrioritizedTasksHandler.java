package ru.yandex.taskTracker.managers.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class GetPrioritizedTasksHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public GetPrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String fullPath = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET": {
                if (fullPath.split("/").length == 2 && exchange.getRequestURI().getQuery() == null) {
                    getPrioritizedTasksResponse(exchange);
                }
                sendClientErrorResponse(exchange, 400, "Некорректный запрос. URl не существует или добавлен" +
                        " неправильный query-заголовок");
                break;
            }
            default:
                sendClientErrorResponse(exchange, 400, "Некорректный метод");
        }
    }

    private void getPrioritizedTasksResponse(HttpExchange exchange) throws IOException {
        if (taskManager.getPrioritizedTasks() == null) {
            sendResponse(exchange, 204, "Nothing to response");
            return;
        }
        String jsonPrioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
        sendResponse(exchange, 200, jsonPrioritizedTasks);
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


