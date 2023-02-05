package ru.yandex.taskTracker.managers;

import ru.yandex.taskTracker.managers.historyManager.InMemoryHistoryManager;
import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.managers.taskManager.HttpTaskManager;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.managers.taskManager.InMemoryTaskManager;
import ru.yandex.taskTracker.managers.taskManager.fileManager.FileBackedTaskManager;

import java.nio.file.Path;

public class Managers {

    static public TaskManager getDefault(String URL) {
        return new HttpTaskManager(URL);
    }

    static public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }

    static FileBackedTaskManager getFileBackedManager(String path){
        return FileBackedTaskManager.loadFromFile(Path.of(path));
    }

    static InMemoryTaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }


}
