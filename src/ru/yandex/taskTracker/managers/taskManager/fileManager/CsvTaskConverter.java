package ru.yandex.taskTracker.managers.taskManager.fileManager;

import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvTaskConverter {

    public static ArrayList<String> getDataAsStringList(Map<Integer, Task> tasks, Map<Integer, Epic> epics, Map<Integer,
            Subtask> subtasks, HistoryManager history) {
        ArrayList<String> dataList = new ArrayList<>();
        String title = "id,Type,Name,Status,Description,Epic";
        dataList.add(title);
        dataList.addAll(getTasksAsStringList(tasks));
        dataList.addAll(getEpicsAsStringList(epics));
        dataList.addAll(getSubtaskAsStringList(subtasks));
        dataList.add(getHistoryAsStringLine(history));
        return dataList;

        //Не, в данном случае все-таки лучше не буду рефакторить, так как мне кажется это чем-то напоминает антипаттерн
        //божественный метод. + придется убирать, переимновывать методы, параметры менять, меее :( ненавижу рефакторить
        //мне кажется это одно из самых нелюбимых у проггеров
        //я передумал. Сделал ^_^
    }

    private static List<String> getTasksAsStringList(Map<Integer, Task> tasks) {
        List<String> taskDataList = new ArrayList<>();
        for (Task task : tasks.values()) {
            String taskData = toString(task);
            taskDataList.add(taskData);
        }
        return taskDataList;
    }

    private static List<String> getEpicsAsStringList(Map<Integer, Epic> epics) {
        List<String> epicDataList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            String epicData = toString(epic);
            epicDataList.add(epicData);
        }
        return epicDataList;
    }

    private static List<String> getSubtaskAsStringList(Map<Integer,Subtask> subtasks) {
        List<String> subtaskDataList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            String subtaskData = toString(subtask);
            subtaskDataList.add(subtaskData);
        }
        return subtaskDataList;
    }

    private static String getHistoryAsStringLine(HistoryManager manager) {
        List<Task> taskHistory = manager.getHistory();
        StringBuilder historyLine = new StringBuilder();
        historyLine.append("\n");

        for (Task task : taskHistory) {
            historyLine.append(task.getId() + ",");
        }
        return historyLine.toString();
    }

    private static String toString(Subtask subtask){
        return subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + ","
                + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicID();
    }

    private static String toString(Epic epic){
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + ","
                + epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private static String toString(Task task){
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + ",";
    }

    public static Task taskFromString(String list) {
        String[] stringData = list.split(",");
        if (stringData.length > 7) {
            throw new ManagerSaveException("Неверное оформление файла: Указано большое количество запятых");
        }
        int id = Integer.parseInt(stringData[0]);
        String name = stringData[2];
        Status status = getTaskStatus(stringData[3]);
        String description = stringData[4];

        if (TaskType.TASK.toString().equals(stringData[1])) {
            return new Task(name, description, status, id);
        } else if (TaskType.EPIC.toString().equals(stringData[1])) {
            return new Epic(name, description, status, id);
        } else if (TaskType.SUBTASK.toString().equals(stringData[1])) {
            return new Subtask(name, description, status, id, Integer.parseInt(stringData[5]));
        } else {
            throw new ManagerSaveException("неверный тип таска в файле");
        }
    }

    private static Status getTaskStatus(String strStatus) {
        for (Status status : Status.values()) {
            if (strStatus.equals(status.name())) { //ЫЫЫЫЫЫЫЫ
                return status;
            }
        }
        throw new ManagerSaveException("Невозможно сравнить Enum статус");
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> intValuesList = new ArrayList<>();
        String[] values = value.split(",");
        for (String s : values) {
            Integer intValue = Integer.parseInt(s);
            intValuesList.add(intValue);
        }
        return intValuesList;
    }
}
