package ru.yandex.taskTracker.managers.taskManager.fileManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;
import ru.yandex.taskTracker.managers.taskManager.TaskMangerTest;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskMangerTest<FileBackedTaskManager> {
    Path path = Path.of("src/ru/yandex/taskTracker/managers/loadData/test2.csv");
    @Override
    public FileBackedTaskManager createManager() {
        return FileBackedTaskManager.loadFromFile(path);
    }
    @AfterEach
    void deleteFile() throws IOException {
        Files.delete(path);
    }
    @Test
    public void getHistory_shouldReturnEmptyHistoryAndTasksListsFromEmptyFile_LoadFromEmptyFile(){
        FileBackedTaskManager test = FileBackedTaskManager.loadFromFile(Path.of("src/ru/yandex/taskTracker/managers/loadData/emptyData.csv"));
        assertEquals(List.of(), test.getHistory());
        assertEquals(List.of(), test.getTasksList());
        assertEquals(List.of(), test.getSubtasksList());
        assertEquals(List.of(), test.getEpicList());
    }
    @Test
    public void getHistory_shouldReturnRightSequenceHistory_loadTasksDataFromPath(){
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

        FileBackedTaskManager test = FileBackedTaskManager.loadFromFile(path);
        assertEquals(List.of(task2, epic1, epic2, sub1, sub2, sub3,task1), test.getHistory());
    }
    @Test
    public void saveAndLoadFromFile_shouldBeEqualsManagers_SaveToPathAndLoadFromItTasksDataInfo(){
        int id1 = taskManager.addTask(book);
        int id2 = taskManager.addTask(study);
        int id3 =taskManager.addEpic(homeBuild);
        int id4 = taskManager.addEpic(movement);
        int id5 = taskManager.addSubtask(workers);
        int id6 = taskManager.addSubtask(fundament);
        int id7 = taskManager.addSubtask(planToMove);

        taskManager.getTask(id1);
        taskManager.getTask(id2);
        taskManager.getEpic(id3);
        taskManager.getEpic(id4);
        taskManager.getSubtask(id5);
        taskManager.getSubtask(id6);
        taskManager.getSubtask(id7);

        FileBackedTaskManager loadFromFile = FileBackedTaskManager.loadFromFile(path);
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());
        assertEquals(taskManager.getTasksList(), loadFromFile.getTasksList());
        assertEquals(taskManager.getEpicList(), loadFromFile.getEpicList());
        assertEquals(taskManager.getSubtasksList(), loadFromFile.getSubtasksList());
    }
}
