package ru.yandex.taskTracker.main;

import ru.yandex.taskTracker.managers.Managers;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

public class Main {


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        //Создание тасков
        Task autoTask = new Task("Auto", "Buy auto", Status.NEW);
        Task autoTaskOneMore = new Task("Auto", "Buy auto", Status.DONE);
        Task autoTaskCopy = new Task("Auto", "Buy auto", Status.DONE);
        Task business = new Task("Business", "investing in Binance", Status.IN_PROGRESS);

        //добавление в мапу
        int id1 = taskManager.addTask(autoTask);              //добавится
        int id2 = taskManager.addTask(autoTaskOneMore);       //тоже добавится
        int idOfCopy = taskManager.addTask(autoTaskCopy);     //не добавится
        int idBusiness = taskManager.addTask(business);       //добавится

        System.out.println("Вывод листа тасков. Переменная tasks");
        List<Task> tasks = taskManager.getTasksList();
        System.out.println(tasks);

        autoTask.setId(1234);   //изменится как объект, так и мапа в taskManager
        System.out.println();
        System.out.println(taskManager.getTasksList()); //убедились в вышесказанном
        System.out.println();
        System.out.println("Вывод листа тасков. Переменная tasks");
        System.out.println(tasks); // тут тоже изменилось
        System.out.println();

        System.out.println("Создание эпиков:");
        Epic homeBuild = new Epic("build a home", "building and buying house");
        int homeBuildID = taskManager.addEpic(homeBuild);
        Epic movement = new Epic("movement", "movement from Russia to Bali");
        int movementID = taskManager.addEpic(movement);

        taskManager.addEpic(homeBuild); //не добавится, так как выше уже создали Эпик
        taskManager.addEpic(movement); //аналогично

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





        //test 5
        Task task1 = new Task("Book", "Buy autoBook", Status.NEW);
        Task task2 = new Task("Study", "learn java lang", Status.IN_PROGRESS);
        int taskid1 = taskManager.addTask(task1);
        int taskid2 = taskManager.addTask(task2);

       Epic epic1 = new Epic("home", "building and buying house");
       int epic1ID = taskManager.addEpic(epic1);

        Epic epic2 = new Epic("movement", "movement from Russia to Bali");
        int epic2ID = taskManager.addEpic(epic2);

        Subtask sub1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, epic1ID);
        int sub1ID = taskManager.addSubtask(sub1);

        Subtask sub2 = new Subtask("Create plan", "smthingt", Status.NEW, epic1ID);
        int sub2ID = taskManager.addSubtask(sub2);

        Subtask sub3 = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, epic1ID);
        int sub3ID = taskManager.addSubtask(sub3);

        taskManager.getTask(taskid1);
        taskManager.getTask(taskid2);
        taskManager.getEpic(epic1ID);
        taskManager.getEpic(epic2ID);
        taskManager.getSubtask(sub1ID);
        taskManager.getSubtask(sub2ID);
        taskManager.getSubtask(sub3ID);
        taskManager.getTask(taskid1);

        System.out.println("История: \n" + taskManager.getHistory());
        taskManager.deleteTaskById(taskid1);
        taskManager.deleteEpicById(epic1ID);
        System.out.println("История: \n" + taskManager.getHistory());

        //epic1.ge

    }
}
