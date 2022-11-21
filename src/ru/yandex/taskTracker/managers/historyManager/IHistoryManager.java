package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

public interface IHistoryManager<T> {
    void addHistory(T element);
    List<T> getHistory();
}
