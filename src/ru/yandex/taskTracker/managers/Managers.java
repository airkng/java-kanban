package ru.yandex.taskTracker.managers;

import ru.yandex.taskTracker.managers.historyManager.InMemoryHistoryManager;
import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.managers.taskManager.InMemoryTaskManager;

public class Managers {

    static public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
