package ru.yandex.taskTracker.main;

import ru.yandex.taskTracker.managers.httpServer.HttpTaskServer;
import ru.yandex.taskTracker.managers.httpServer.KVServer;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {


    public static void main(String[] args) throws IOException {
       KVServer server = new KVServer();
       server.start();
       //TaskManager taskManager = Managers.getDefault("http://localhost:8078");
        HttpTaskServer server1 = new HttpTaskServer();
        server1.start();

        Task t1 = new Task("test", "testign", Status.IN_PROGRESS, 0, LocalDateTime.of(2023, 1,1,1,1));
        //TaskManager taskManager = Managers.getDefault();
        //Создание тасков
        Task autoTask = new Task("Auto", "Buy auto", Status.NEW);
        Task autoTaskOneMore = new Task("Auto", "Buy auto", Status.DONE);
        Task autoTaskCopy = new Task("Auto", "Buy auto", Status.DONE);
        Task business = new Task("Business", "investing in Binance", Status.IN_PROGRESS);

        //добавление в мапу
        int id1 = server1.manager.addTask(autoTask);              //добавится
        int id2 = server1.manager.addTask(autoTaskOneMore);       //тоже добавится
        int idOfCopy = server1.manager.addTask(autoTaskCopy);     //не добавится
        int idBusiness = server1.manager.addTask(business);       //добавится

        System.out.println("Вывод листа тасков. Переменная tasks");
        List<Task> tasks = server1.manager.getTasksList();
        System.out.println(tasks);

        autoTask.setId(1234);   //изменится как объект, так и мапа в taskManager
        System.out.println();
        System.out.println(server1.manager.getTasksList()); //убедились в вышесказанном
        System.out.println();
        System.out.println("Вывод листа тасков. Переменная tasks");
        System.out.println(tasks); // тут тоже изменилось
        System.out.println();

        System.out.println("Создание эпиков:");
        Epic homeBuild = new Epic("build a home", "building and buying house");
        int homeBuildID = server1.manager.addEpic(homeBuild);
        Epic movement = new Epic("movement", "movement from Russia to Bali");
        int movementID = server1.manager.addEpic(movement);

        server1.manager.addEpic(homeBuild); //не добавится, так как выше уже создали Эпик
        server1.manager.addEpic(movement); //аналогично

        Subtask fundament = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuildID);
        int fundamentID = server1.manager.addSubtask(fundament); //добавится

        Subtask fundament1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuildID);
        server1.manager.addSubtask(fundament1); //не добавится

        Subtask workers = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, homeBuildID);
        //после того, как добавим, статус у эпика поменяется на In_progress
        int workersID = server1.manager.addSubtask(workers);

        Subtask movementSubt = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, movementID);
        int movementSubID = server1.manager.addSubtask(movementSubt); //добавится к эпику Переезд
        movementSubt.setName("ticket");
        movementSubt.setDescription("airline ticket to Bali");


        System.out.println(server1.manager.getEpicList());
        System.out.println(server1.manager.getSubtasksList());

        movementSubt.setStatus(Status.DONE); //в мапе поменяется

        server1.manager.updateSubtask(movementSubt); //обновился статус эпика Переезд (done)
        System.out.println(server1.manager.getSubtasksList());
        System.out.println(server1.manager.getEpicList());

        System.out.println("Удаляем Эпик");
        server1.manager.deleteEpicById(homeBuildID); //Вместе с эпиком удалились его сабтаски
        System.out.println(server1.manager.getSubtasksList());
        System.out.println(server1.manager.getEpicList());

        server1.manager.deleteTaskById(id1); // удалили таск
        System.out.println(server1.manager.getTasksList());





        //test 5
        Task task1 = new Task("Book", "Buy autoBook", Status.NEW);
        Task task2 = new Task("Study", "learn java lang", Status.IN_PROGRESS);
        int taskid1 = server1.manager.addTask(task1);
        int taskid2 = server1.manager.addTask(task2);

       Epic epic1 = new Epic("home", "building and buying house");
       int epic1ID = server1.manager.addEpic(epic1);

        Epic epic2 = new Epic("movement", "movement from Russia to Bali");
        int epic2ID = server1.manager.addEpic(epic2);

        Subtask sub1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, epic1ID);
        int sub1ID = server1.manager.addSubtask(sub1);

        Subtask sub2 = new Subtask("Create plan", "smthingt", Status.NEW, epic1ID);
        int sub2ID = server1.manager.addSubtask(sub2);

        Subtask sub3 = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, epic1ID);
        int sub3ID = server1.manager.addSubtask(sub3);

        /*taskManager.getTask(taskid1);
        taskManager.getTask(taskid2);
        taskManager.getEpic(epic1ID);
        taskManager.getEpic(epic2ID);
        taskManager.getSubtask(sub1ID);
        taskManager.getSubtask(sub2ID);
        taskManager.getSubtask(sub3ID);
        taskManager.getTask(taskid1);*/

        /*System.out.println("История: \n" + taskManager.getHistory());
        taskManager.deleteTaskById(taskid1);
        taskManager.deleteEpicById(epic1ID);
        System.out.println("История: \n" + taskManager.getHistory());

        //epic1.ge*/

    }
}
