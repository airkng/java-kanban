import manager.Manager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Main {
    //Счетчик проведенных дней: 1.5
    // Время почти два часа ночи. У меня нервяк. Проект доделан, но я не знаю, какую шутку придумать
    // думаю пойти гуглить анекдоты про котят. Или какой-нибудь гаччи анекдот...
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task autoTask = new Task("Auto", "Clear auto", "NEW");
        Task autoTaskD = new Task("Auto", "Clear auto", "DONE");
        Task autoTas = new Task("Auto", "Clear auto", "NEW");
        Task business = new Task("Business", "investing in Binance", "NEW");
        //Интересно, мой код тесты прошел? Предыдущий, не считая апдейта Epic
        int auto = manager.addTask(autoTask);
        int autoDOne = manager.addTask(autoTaskD);
        manager.addTask(autoTas);
        int bus = manager.addTask(business);

        ArrayList<Task> uuu = manager.getTasksList();
        System.out.println(uuu);

        Task task = manager.getTask(autoDOne);
        System.out.println(task);

        manager.updateTask(autoTask);

        Task taskBus = new Task("amth new", "New business", "IN_PROGRESS", bus);
        manager.updateTask(taskBus);
        uuu = manager.getTasksList();
        System.out.println(uuu);

        manager.deleteTaskById(bus);
        uuu = manager.getTasksList();
        System.out.println(uuu);

        manager.deleteTasks();
        uuu = manager.getTasksList();
        System.out.println(uuu);

        Epic epic1 = new Epic("Построить дом", "Кирпичный дом");
        int epicID = manager.addEpic(epic1); //добавится

        Epic epic3 = new Epic("Построить дом", "Кирпичный дом");
        int epic3ID = manager.addEpic(epic3); // должен добавиться

        Epic epic2 = new Epic("Построить дом", "Кирпичный дом", epicID);
        int epic2ID = manager.addEpic(epic2); //не добавится

        Subtask subtask11 = new Subtask("Основное", "Заложить фундамент", "NEW", epicID);
        int subtask11ID = manager.addSubtask(subtask11); //добавится

        Subtask subtask12 = new Subtask("Основное", "Заложить фундамент", "IN_PROGRESS", epicID);
        int subtask11OneMore = manager.addSubtask(subtask12); //тоже добавится

        Subtask subtask13 = new Subtask(subtask11.getName(), subtask11.getDescription(), subtask11.getStatus(), subtask11ID, epicID);
        manager.addSubtask(subtask13); // не добавится

        Subtask subtask11Updated = new Subtask("Вторичное", "Заложить фундамент", "DONE", subtask11ID, epicID);
        Subtask subtask12Updated = new Subtask("Другое", "Покупка бревен", "NEW",subtask11OneMore, epicID);
        manager.updateSubtask(subtask11Updated);
        manager.updateSubtask(subtask12Updated);

        System.out.println(manager.getEpicSubtasks(epicID).toString());
        System.out.println(epic1.getStatus() + " " + manager.getEpicList());
        System.out.println(manager.getSubtasksList());

        Subtask subtask13Updated = new Subtask("Покупки", "Покупка бревен", "DONE",subtask11OneMore, epicID);
        manager.updateSubtask(subtask13Updated);

        Subtask subtask31 = new Subtask("Hello", "buy auto", "IN_PROGRESS", epic3ID);
        manager.addSubtask(subtask31);

        System.out.println(manager.getEpicSubtasks(epicID).toString());
        System.out.println(epic1.getStatus() + " " + manager.getEpicList());
        System.out.println(manager.getSubtasksList());
    }
}
