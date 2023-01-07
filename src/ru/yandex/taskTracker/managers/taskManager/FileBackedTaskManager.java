package ru.yandex.taskTracker.managers.taskManager;

import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path path;
    //Реальная история. Коротко о разработчиках MicroSoft:
    //когда разработчики Microsoft убрали из Windows игру Pinball, потому что не смогли портировать
    // ее на 64-х разрядную архитектуру. Причем у них даже были ее исходники. Просто они не могли понять,
    // как работает написанный код.

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager("testDir\\test.csv");

        System.out.println("Создание тасков");
        Task task1 = new Task("Book", "Buy autoBook", Status.NEW);
        Task task2 = new Task("Study", "learn java lang", Status.IN_PROGRESS);
        int taskid1 = taskManager.addTask(task1);
        int taskid2 = taskManager.addTask(task2);


        System.out.println("Создание эпиков:");
        Epic epic1 = new Epic("home", "building and buying house");
        int epic1ID = taskManager.addEpic(epic1);
        Epic epic2 = new Epic("movement", "movement from Russia to Bali");
        int epic2ID = taskManager.addEpic(epic2);

        System.out.println("Создание subtasks:");
        Subtask sub1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, 100000);
        int sub1ID = taskManager.addSubtask(sub1);

        Subtask sub2 = new Subtask("Create plan", "smthingt", Status.NEW, 100000);
        int sub2ID = taskManager.addSubtask(sub2);

        Subtask sub3 = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, 100000);
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
        //taskManager.deleteTaskById(taskid1);

        FileBackedTaskManager taskLoaderManager = FileBackedTaskManager.loadFromFile(Path.of("testDir\\test.csv"));
        System.out.println("Сабтаски загруженные из файла");
        System.out.println(taskManager.getSubtaskAsList());
        System.out.println("Эпики загруженные из файла");
        System.out.println(taskManager.getEpicsAsStringList());
        System.out.println("Таски из файла");
        System.out.println(taskManager.getTasksAsStringList());
        System.out.println("История из файла:");
        System.out.println(taskManager.getHistory());
    }

    public FileBackedTaskManager(String file) {
        path = Paths.get(file);
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }
    //С Новым Годом, Рождеством! Надеюсь алкоголь уже выветрился и Александр Валюк с новыми силами ворвался в новый год
    @Override
    public void deleteEpic() {
        super.deleteEpic();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask sub = super.getSubtask(id);
        save();
        return sub;
    }
    // Как дела?
    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }
    //Жизнь боль, это когда ты отлаживаешь код, у тебя выходит ошибка компиляции при создании файла, ты
    //отдебаживаешь его полтора часа, пишешь тотже самый код в другом файле другого проекта и он сука работает
    //А в этом проекте нет, в итоге колдуешь 20-30 минут, вспоминаешь остаточные знания из Хогвардста и отправляешь
    // Александру Дамблдору на проверку
    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    private void save() {
        try {
            if (Files.exists(path)) {
                saveExistFile();
            } else {
                saveNonExistFile();
            }
            saveHistory();
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void saveExistFile() throws IOException {
        List<String> dataList = new ArrayList<>();
        String title = "id,Type,Name,Status,Description,Epic";
        dataList.add(title);
        dataList.addAll(getTasksAsStringList());
        dataList.addAll(getEpicsAsStringList());
        dataList.addAll(getSubtaskAsList());
        try (Writer fileWriter = new FileWriter(path.toFile())) {
            for (String data : dataList) {
                fileWriter.write(data + "\n");
            }
        }
    }

    private void saveNonExistFile() throws IOException {
        Path parentDirectory = path.getParent();
        String fileName = path.getFileName().toString();
        Files.createDirectories(parentDirectory);
        if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
            Files.createFile(path);
            saveExistFile();
        } else {
            throw new ManagerSaveException("Указан неверный формат файла");
        }
    }

    private void saveHistory() throws IOException {
        try (FileWriter historyWritter = new FileWriter(path.toFile(), true)) {
            historyWritter.write("\n");
            List<String> historyData = historyToString(history);
            for (String historyDatum : historyData) {
                historyWritter.write(historyDatum);
            }
        }
    }

    private static List<String> historyToString(HistoryManager manager) {
        List<Task> taskHistory = manager.getHistory();
        List<String> stringHistory = new ArrayList<>();
        for (Task task : taskHistory) {
            stringHistory.add(task.getId() + ",");
        }
        return stringHistory;
    }

    private List<String> getTasksAsStringList() {
        List<String> taskDataList = new ArrayList<>();
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            String taskData = toString(entry.getValue());
            taskDataList.add(taskData);
        }
        return taskDataList;
    }

    private List<String> getEpicsAsStringList() {
        List<String> epicDataList = new ArrayList<>();
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            String epicData = toString(entry.getValue());
            epicDataList.add(epicData);
        }
        return epicDataList;
    }

    private List<String> getSubtaskAsList() {
        List<String> subtaskDataList = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            String subtaskData = toString(entry.getValue());
            subtaskDataList.add(subtaskData);
        }
        return subtaskDataList;
    }

    private String toString(Object obj) {
        if (obj instanceof Subtask) {
            Subtask subtask = (Subtask) obj;
            return subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + ","
                    + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicID();
        } else if (obj instanceof Epic) {
            Epic epic = (Epic) obj;
            return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + ","
                    + epic.getStatus() + "," + epic.getDescription() + ",";
        } else if (obj instanceof Task) {
            Task task = (Task) obj;
            return task.getId() + "," + TaskType.TASK + "," + task.getName() + ","
                    + task.getStatus() + "," + task.getDescription() + ",";
        } else {
            throw new IllegalArgumentException("На вход подан неверный класс");
        }
    }

    private static FileBackedTaskManager loadFromFile(Path path) {

        FileBackedTaskManager fileManager = new FileBackedTaskManager(path.toString());
        try {
            List<String> dataList = Files.readAllLines(path);
            int i = 1;
            while (!dataList.get(i).isEmpty()) {
                String textTask = dataList.get(i);
                Task task = fileManager.taskFromString(textTask);
                if (task instanceof Epic) {
                    fileManager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    fileManager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    fileManager.tasks.put(task.getId(), task);
                }
                i++;
            }
            if (i == dataList.size() - 1) {
                System.out.println("Истории нет");
            } else {
                List<Integer> historyTasksIdList = historyFromString(dataList.get(dataList.size() - 1));
                for (Integer id : historyTasksIdList) {
                    if (fileManager.getTask(id) != null) fileManager.history.addHistory(fileManager.getTask(id));
                    if (fileManager.getEpic(id) != null) fileManager.history.addHistory(fileManager.getEpic(id));
                    if (fileManager.getSubtask(id) != null) fileManager.history.addHistory(fileManager.getSubtask(id));
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return fileManager;
    }

    private Task taskFromString(String list) {
        String[] stringData = list.split(",");
        if (stringData.length > 7) {
            throw new ManagerSaveException("Неверное оформление файла: Указано большое количество запятых");
        }
        int id = Integer.parseInt(stringData[0]);
        String name = stringData[2];
        Status status = getTaskStatus(stringData[3]);
        String description = stringData[4];

        if (stringData[1].equals(TaskType.TASK.toString())) {
            return new Task(name, description, status, id);
        } else if (stringData[1].equals(TaskType.EPIC.toString())) {
            return new Epic(name, description, status, id);
        } else if (stringData[1].equals(TaskType.SUBTASK.toString())) {
            return new Subtask(name, description, status, id, Integer.parseInt(stringData[5]));
        } else {
            throw new ManagerSaveException("неверный тип таска в файле");
        }
    }

    private Status getTaskStatus(String strStatus) {
        for (Status status : Status.values()) {
            if (strStatus.equals(status.toString())) return status;
        }
        throw new ManagerSaveException("Невозможно сравнить Enum статус");
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> intValuesList = new ArrayList<>();
        String[] values = value.split(",");
        for (String s : values) {
            Integer intValue = Integer.parseInt(s);
            intValuesList.add(intValue);
        }
        return intValuesList;
    }

}
