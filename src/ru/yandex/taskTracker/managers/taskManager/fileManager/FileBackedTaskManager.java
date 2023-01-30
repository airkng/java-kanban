package ru.yandex.taskTracker.managers.taskManager.fileManager;

import ru.yandex.taskTracker.managers.taskManager.InMemoryTaskManager;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
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
import java.util.List;

import ru.yandex.taskTracker.managers.utils.CsvTaskConverter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    //Реальная история. Коротко о разработчиках MicroSoft:
    //когда разработчики Microsoft убрали из Windows игру Pinball, потому что не смогли портировать
    // ее на 64-х разрядную архитектуру. Причем у них даже были ее исходники. Просто они не могли понять,
    // как работает написанный код.

    public static void main(String[] args) {

        TaskManager taskManager = new FileBackedTaskManager("src/ru/yandex/taskTracker/managers/loadData/test2.csv");

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
        //taskManager.getTask(taskid1);

        System.out.println("История: \n" + taskManager.getHistory());
        //taskManager.deleteTaskById(taskid1);

        TaskManager taskLoaderManager = FileBackedTaskManager.loadFromFile(Path.of("src/ru/yandex/taskTracker/managers/loadData/test2.csv"));
        System.out.println("Сабтаски загруженные из файла");
        System.out.println(taskLoaderManager.getSubtasksList());
        System.out.println("Эпики загруженные из файла");
        System.out.println(taskLoaderManager.getEpicList());
        System.out.println("Таски из файла");
        System.out.println(taskLoaderManager.getTasksList());
        System.out.println("История из файла:");
        System.out.println(taskManager.getHistory());
    }

    private FileBackedTaskManager(String file) {
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

    @Override
    public void deleteEpics() {
        super.deleteEpics();
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

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

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
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void saveExistFile() throws IOException {
        List<String> dataList = CsvTaskConverter.getDataAsStringList(tasks, epics, subtasks, history);
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

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(path.toString());
        try {
            if (Files.exists(path)) {
                if (Files.readAllLines(path).isEmpty()) {
                    return fileManager;
                }
                List<String> dataList = Files.readAllLines(path);
                int i = 1;
                while (!dataList.get(i).isEmpty()) {
                    String textTask = dataList.get(i);
                    Task task = CsvTaskConverter.taskFromString(textTask);
                    String typeOfTask = textTask.split(",")[1];

                    if (TaskType.EPIC.toString().equals(typeOfTask)) {
                        fileManager.epics.put(task.getId(), (Epic) task);
                    } else if (TaskType.SUBTASK.toString().equals(typeOfTask)) {
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                    } else if (TaskType.TASK.toString().equals(typeOfTask)) {
                        fileManager.tasks.put(task.getId(), task);
                    } else {
                        throw new ManagerSaveException("неверный тип таска в файле");
                    }
                    i++;
                }
                if (i == dataList.size() - 1) {
                    System.out.println("Истории нет");
                } else {
                    List<Integer> historyTasksIdList = CsvTaskConverter.historyFromString(dataList.get(dataList.size() - 1));
                    for (Integer id : historyTasksIdList) {
                        if (fileManager.getTask(id) != null) fileManager.history.addHistory(fileManager.getTask(id));
                        if (fileManager.getEpic(id) != null) fileManager.history.addHistory(fileManager.getEpic(id));
                        if (fileManager.getSubtask(id) != null)
                            fileManager.history.addHistory(fileManager.getSubtask(id));
                    }
                }
            } else {
                fileManager.createUnExistFile();
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return fileManager;
    }

    private void createUnExistFile() throws IOException {
        Path parentDirectory = path.getParent();
        String fileName = path.getFileName().toString();
        Files.createDirectories(parentDirectory);
        if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
            Files.createFile(path);
        } else {
            throw new ManagerSaveException("Указан неверный формат файла");
        }
    }
}

