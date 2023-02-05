package ru.yandex.taskTracker.managers.httpServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.yandex.taskTracker.managers.taskManager.fileManager.FileBackedTaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient client;
    private final Gson gson = new Gson();

    public HttpTaskManager(String url) {
        client = new KVTaskClient(url);
        load();
    }

    @Override
    protected void save() {
        client.put("tasks", gson.toJson(getTasksList()));
        client.put("epics", gson.toJson(getEpicList()));
        client.put("subtasks", gson.toJson(getSubtasksList()));
        client.put("history", gson.toJson(getHistory()));
    }
    public void load() {
        String jsonTasks = client.load("tasks");
        String jsonEpics = client.load("epics");
        String jsonSubtasks = client.load("subtasks");
        String jsonHistory= client.load("history");
        JsonArray jsonArray;
        if (jsonTasks != null) {
            jsonArray = JsonParser.parseString(jsonTasks).getAsJsonArray();
            recoverTasks(jsonArray);
        }

        if (jsonEpics != null) {
            jsonArray = JsonParser.parseString(jsonEpics).getAsJsonArray();
            recoverEpics(jsonArray);
        }
        if (jsonSubtasks != null) {
            jsonArray = JsonParser.parseString(jsonSubtasks).getAsJsonArray();
            recoverSubtasks(jsonArray);
        }

        if (jsonHistory != null) {
            jsonArray = JsonParser.parseString(jsonHistory).getAsJsonArray();
            recoverHistory(jsonArray);
        }

    }

    private void recoverTasks(JsonArray jsonArray) {
        if (jsonArray.isEmpty()) {
            return;
        }
        for (JsonElement jsonElement : jsonArray) {
            Task task = gson.fromJson(jsonElement, Task.class);
            super.addTask(task);
        }
    }

    private void recoverEpics(JsonArray jsonArray) {
        if (jsonArray.isEmpty()) {
            return;
        }
        for (JsonElement jsonElement : jsonArray) {
            Epic epic = gson.fromJson(jsonElement, Epic.class);
            super.addEpic(epic);
        }
    }
    private void recoverSubtasks(JsonArray jsonArray) {
        if (jsonArray.isEmpty()) {
            return;
        }
        for (JsonElement jsonElement : jsonArray) {
            Subtask subtask = gson.fromJson(jsonElement, Subtask.class);
            super.addSubtask(subtask);
        }
    }
    private void recoverHistory(JsonArray jsonArray) {
        if (jsonArray.isEmpty()) {
            return;
        }
        for (JsonElement jsonElement : jsonArray) {
            int id = jsonElement.getAsInt();
            if (tasks.containsKey(id)) {
                history.addHistory(tasks.get(id));
            }
            if (epics.containsKey(id)) {
                history.addHistory(epics.get(id));
            }
            if (subtasks.containsKey(id)) {
                history.addHistory(subtasks.get(id));
            }
        }
    }
}
