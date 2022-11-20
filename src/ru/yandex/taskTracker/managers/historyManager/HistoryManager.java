package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager implements IHistoryManager {
    private final List<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task) {
        if(historyList.size() < 10){
            historyList.add(task);
        }
        else{
            historyList.remove(0);
            historyList.add(task);
        }
    }
    @Override
    public List<Task> getHistory(){
        if(historyList.size() > 0){
            return historyList;
        }
        else {
            System.out.println("История пуста.");
            return null;
        }
    }
}
