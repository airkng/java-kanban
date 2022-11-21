package ru.yandex.taskTracker.managers.historyManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T> implements HistoryManager<T> {
    private final List<T> historyList = new ArrayList<>();

    @Override
    public void addHistory(T element) {
        if(historyList.size() < 10){
            historyList.add(element);
        }
        else{
            historyList.remove(0);
            historyList.add(element);
        }
    }

    @Override
    public List<T> getHistory(){
        if(historyList.size() > 0){
            return historyList;
        }
        else {
            System.out.println("История пуста.");
            return null;
        }
    }
}
