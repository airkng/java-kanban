
package ru.yandex.taskTracker.managers.utils;

import ru.yandex.taskTracker.managers.taskManager.InMemoryTaskManager;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

//Величайшая ошибка во Вселенной. Попытались мы как-то значит по тз идти...
//класс позора и унижения. Он останется в памяти навсегда
@Deprecated
public class MemeClass {

    private final Comparator<Task> timeComparator = (o1, o2) -> {
        if (o1.getStartTime() == null) {
            return 1;
        } else if(o2.getStartTime() == null){
            return -1;
        } else {
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else  {
                return 0;
            }
        }
    };

    private final TreeSet<Task> timePrioritizedTasks = new TreeSet<>(timeComparator);
    TreeMap<LocalDateTime, Boolean> freeTimeDurationsMap = resetFreeTimeDurationsMap();

    private TreeMap<LocalDateTime, Boolean> resetFreeTimeDurationsMap(){
        LocalDateTime startDate = LocalDateTime.of(2023, 1,1, 0,0);
        LocalDateTime endDate = startDate.plusDays(365);
        TreeMap<LocalDateTime, Boolean> timeTable = new TreeMap<>();
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)){
            timeTable.put(startDate, true);
            startDate = startDate.plusMinutes(15);
        }
        return timeTable;
    }



    public Boolean isTaskTimeCrossing(Task task){
        if (task.getStartTime() == null){
            return false;
        }
        if (task.getStartTime() != null
    && (task.getStartTime().isAfter(freeTimeDurationsMap.firstKey()) || task.getStartTime().isEqual(freeTimeDurationsMap.firstKey()))
    && (task.getEndTime().isBefore(freeTimeDurationsMap.lastKey()) || task.getEndTime().isEqual(freeTimeDurationsMap.lastKey())))
        {
            long startMinutes = task.getStartTime().getMinute();
            LocalDateTime lowerBorder = floorLowBorder(startMinutes, task.getStartTime());

            long endMinutes = task.getEndTime().getMinute();
            LocalDateTime highBorder = ceilHighBorder(endMinutes, task.getEndTime());

            SortedMap<LocalDateTime, Boolean> duration = freeTimeDurationsMap.subMap(lowerBorder, highBorder);
            for(Boolean timeValue : duration.values()){
                if (!timeValue) {
                    return true;
                }
            }
            updateFreeTimeDurationsMapStatus(duration);
            return false;
        } else {
            System.out.println("Временной интервал, заданный таском " + task + " за пределами 2023 года. Ошибка");
            return true;
        }
    }

    private LocalDateTime floorLowBorder(long taskMinutes, LocalDateTime highBorder){
        long roundTo;
        if (taskMinutes == 0){
            roundTo = 0;
        } else if (taskMinutes < 15 && taskMinutes > 0){
            roundTo = taskMinutes;
        } else if (taskMinutes < 30){
            roundTo = taskMinutes - 15 ;
        } else if (taskMinutes < 45){
            roundTo = taskMinutes - 30;
        } else {
            roundTo = taskMinutes - 45;
        }
        return highBorder.minusMinutes(roundTo);
    }

    private LocalDateTime ceilHighBorder(long taskMinutes, LocalDateTime lowBorder){
        long roundTo;
         if (taskMinutes <= 15 && taskMinutes > 0){
            roundTo = 15 - taskMinutes;
        } else if (taskMinutes <= 30){
            roundTo = 30 - taskMinutes;
        } else if (taskMinutes <= 45){
            roundTo = 45 - taskMinutes;
        } else {
            roundTo = 60 - taskMinutes;
        }
        return lowBorder.plusMinutes(roundTo);
    }

    public void addToPrioritizedList(Task task){
        if (task.getStartTime() != null) {
            long startMinutes = task.getStartTime().getMinute();
            LocalDateTime lowerBorder = floorLowBorder(startMinutes, task.getStartTime());

            long endMinutes = task.getEndTime().getMinute();
            LocalDateTime highBorder = ceilHighBorder(endMinutes, task.getEndTime());

            SortedMap<LocalDateTime, Boolean> duration = freeTimeDurationsMap.subMap(lowerBorder, highBorder);
            updateFreeTimeDurationsMapStatus(duration);

        }
        timePrioritizedTasks.add(task);
    }

    private void updateFreeTimeDurationsMapStatus(SortedMap<LocalDateTime, Boolean> duration){
        for(LocalDateTime time: duration.keySet()){
            freeTimeDurationsMap.replace(time, false);
        }
    }
    private void updatePrioritizedList(Map<Integer, Task> tasks, Map<Integer, Subtask> subtasks,Map<Integer, Epic> epics){
        timePrioritizedTasks.clear();

        for(Task task: tasks.values()){
            addToPrioritizedList(task);
        }
        for (Subtask subtask: subtasks.values()){
            addToPrioritizedList(subtask);
        }
        for (Epic epic: epics.values()){
            addToPrioritizedList(epic);
        }
    }



    public TreeSet<Task> getPrioritizedTasks(){
        return timePrioritizedTasks;
    }

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        System.out.println("Создание тасков");
        Task task1 = new Task("Book", "Buy autoBook", Status.NEW, 16, LocalDateTime.of(2023,1,1,2,0));
        Task task2 = new Task("Study", "learn java lang", Status.IN_PROGRESS, 10, LocalDateTime.of(2023,1,1,0, 0));
        int taskid1 = taskManager.addTask(task1);
        int taskid2 = taskManager.addTask(task2);


        System.out.println("Создание эпиков:");
        Epic epic1 = new Epic("home", "building and buying house");
        int epic1ID = taskManager.addEpic(epic1);
        Epic epic2 = new Epic("movement", "movement from Russia to Bali");

        int epic2ID = taskManager.addEpic(epic2);

        System.out.println("Создание subtasks:");
        Subtask sub1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, 100000, 10, LocalDateTime.of(2023, 1,1,0,16));
        int sub1ID = taskManager.addSubtask(sub1);

        Subtask sub2 = new Subtask("Create plan", "smthingt", Status.NEW, 100000);
        int sub2ID = taskManager.addSubtask(sub2);

        Subtask sub3 = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, 100000);
        int sub3ID = taskManager.addSubtask(sub3);

        System.out.println(taskManager.getPrioritizedTasks());
        taskManager.deleteTasks();
        System.out.println(taskManager.getTasksList());
    }
}

