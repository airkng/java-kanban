package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addHistory(Task task);
    List<Task> getHistory();
    void remove(int i);
}
