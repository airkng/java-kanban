package ru.yandex.taskTracker.main;

import ru.yandex.taskTracker.managers.taskManager.TaskManagerI;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

public class Main {
    //ПРОЕКТ СЫРОЙ, МОЖЕШЬ ДАЖЕ НЕ СМОТРЕТЬ ПОКА

    public static void main(String[] args) {
        TaskManagerI taskManager = new TaskManagerI();
        Task autoTask = new Task("Auto", "Buy auto", Status.NEW);
        Task autoTaskOneMore = new Task("Auto", "Buy auto", Status.DONE);
        Task autoTaskCopy = new Task("Auto", "Buy auto", Status.DONE);

        Task business = new Task("Business", "investing in Binance", Status.IN_PROGRESS);

        int id1 = taskManager.addTask(autoTask);              //добавится
        int id2 = taskManager.addTask(autoTaskOneMore);       //тоже добавится
        int idOfCopy = taskManager.addTask(autoTaskCopy);     //не добавится
        int idBusiness = taskManager.addTask(business);       //добавится
        List<Task> tasks = taskManager.getTasksList();
        System.out.println(tasks);
        autoTask.setId(1234);   //изменится в мапе тоже
        System.out.println();
        System.out.println(taskManager.getTasksList()); //убедились в вышесказанном
        System.out.println();
        System.out.println();

        Epic homeBuild = new Epic("build a home", "building and buying house");
        int homeBuildID = taskManager.addEpic(homeBuild);
        Epic movement = new Epic("movement", "movement from Russia to Bali");
        int movementID = taskManager.addEpic(movement);

        Subtask fundament = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuildID);
        int fundamentID = taskManager.addSubtask(fundament); //добавится

        Subtask fundament1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuildID);
        taskManager.addSubtask(fundament1); //не добавится

        Subtask workers = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, homeBuildID);
        //после того, как добавим, статус у эпика поменяется на In_progress
        int workersID = taskManager.addSubtask(workers);

        Subtask movementSubt = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, movementID);
        int movementSubID = taskManager.addSubtask(movementSubt); //добавится к эпику Переезд
        movementSubt.setName("ticket");
        movementSubt.setDescription("airline ticket to Bali");

        taskManager.addEpic(homeBuild); //не добавится, так как выше уже создали Эпик
        taskManager.addEpic(movement); //аналогично
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtasksList());

        movementSubt.setStatus(Status.DONE); //в мапе поменяется

        taskManager.updateSubtask(movementSubt); //обновился статус эпика Переезд (done)
        System.out.println(taskManager.getSubtasksList());
        System.out.println(taskManager.getEpicList());

        System.out.println("Удаляем Эпик");
        taskManager.deleteEpicById(homeBuildID); //Вместе с эпиком удалились его сабтаски
        System.out.println(taskManager.getSubtasksList());
        System.out.println(taskManager.getEpicList());

        taskManager.deleteTaskById(id1); // удалили таск
        System.out.println(taskManager.getTasksList());

        System.out.println(taskManager.getTask(id2));
        System.out.println(taskManager.getTask(idBusiness));
        System.out.println(taskManager.getEpic(movementID));
        System.out.println(taskManager.getSubtask(movementSubID));

        System.out.println(taskManager.getTask(id2));
        System.out.println(taskManager.getTask(idBusiness));
        System.out.println(taskManager.getEpic(movementID));
        System.out.println(taskManager.getSubtask(movementSubID));

        System.out.println(taskManager.getTask(id2));
        System.out.println(taskManager.getTask(idBusiness));
        System.out.println(taskManager.getEpic(movementID));
        System.out.println(taskManager.getSubtask(movementSubID));

        System.out.println("История: \n");
        System.out.println(taskManager.getHistory());

    }
}
