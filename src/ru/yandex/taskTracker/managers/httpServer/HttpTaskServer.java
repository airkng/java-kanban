package ru.yandex.taskTracker.managers.httpServer;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.taskTracker.managers.Managers;
import ru.yandex.taskTracker.managers.httpServer.handlers.*;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    /**
     * Молитва на принятие работы:
     * Отче наш, Иже еси на небесех!
     * Да святится имя Твое,
     * да приидет Царствие Твое,
     * да будет воля Твоя,
     * яко на небеси и на земли.
     * Хлеб наш насущный даждь нам днесь;
     * и остави нам долги наша,
     * якоже и мы оставляем должником нашим;
     * и не введи нас во искушение,
     * но избави нас от лукаваго.
     * Ибо Твое есть Царство и сила и слава во веки.
     * Аминь.
     */
    private final int PORT = 8080;
    private final HttpServer server;
    public final TaskManager manager;

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault("http://localhost:8078");
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks/task/", new TaskHandler(manager));
        server.createContext("/tasks/subtask/", new SubtaskHandler(manager));
        server.createContext("/tasks/epic/", new EpicHandler(manager));
        server.createContext("/tasks/history/", new TaskHistoryHandler(manager));
        server.createContext("/tasks/subtask/epic/", new GetEpicSubtaskListHandler(manager));
        server.createContext("/tasks/", new GetPrioritizedTasksHandler(manager));
        server.start();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

}
