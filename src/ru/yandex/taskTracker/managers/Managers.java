package ru.yandex.taskTracker.managers;

import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.managers.historyManager.IHistoryManager;
import ru.yandex.taskTracker.managers.taskManager.ITaskManager;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;

public class Managers {

    static public ITaskManager getDefault() {
        return new TaskManager();
    }

    static public IHistoryManager getHistoryManager() {
        return new HistoryManager<>();
    }
}
