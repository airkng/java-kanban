package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

public interface IHistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
