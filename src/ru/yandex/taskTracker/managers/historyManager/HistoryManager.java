package ru.yandex.taskTracker.managers.historyManager;

import java.util.List;

public interface HistoryManager<T> {
    void addHistory(T element);
    List<T> getHistory();
}
