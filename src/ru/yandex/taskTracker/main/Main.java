package ru.yandex.taskTracker.main;

import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Task;

public class Main {
    //ПРОЕКТ СЫРОЙ, МОЖЕШЬ ДАЖЕ НЕ СМОТРЕТЬ ПОКА

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task autoTask = new Task("Auto", "Clear auto", Status.NEW);
        Task autoTaskD = new Task("Auto", "Clear auto", Status.DONE);
        Task autoTas = new Task("Auto", "Clear auto", Status.DONE);
        Task business = new Task("Business", "investing in Binance", Status.NEW);

      int id1 = taskManager.addTask(autoTask);
      int id2 = taskManager.addTask(autoTaskD); // add
        autoTask.setId(1234);
    }
}
