package ru.yandex.taskTracker.managers.httpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.managers.taskManager.TaskManagerTest;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.taskTracker.tasks.Status.NEW;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer kvServer;
    final HttpTaskManager manager = createManager();

    public HttpTaskManagerTest() throws IOException, InterruptedException {
    }

    static {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Если запускать все тесты по отдельности, то они отлично работают, не понимаю почему
    //примерно понимаю почему но не могу никак это исправить
    @Override
    public HttpTaskManager createManager() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078");
    }

    @Test
    void addNewTasksAndHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task1 description", NEW);
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        int epicId = epic1.getId();
        Subtask sub1_1 = new Subtask("SubTask1 - 1", "SubTask1-1 description", NEW, epicId);

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addSubtask(sub1_1);

        assertEquals(1, manager.getTasksList().size());
        assertEquals(1, manager.getEpicList().size());
        assertEquals(1, manager.getSubtasksList().size());

        assertEquals("Task1", manager.getTask(task1.getId()).getName());
        assertEquals("Epic1", manager.getEpic(epicId).getName());
        assertEquals("SubTask1 - 1", manager.getSubtask(sub1_1.getId()).getName());
        assertEquals("Task1", manager.getHistory().get(0).getName());
    }

    @Test
    void deleteAllTasksAndGetNullTasks() {
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();

        assertEquals(0, manager.getTasksList().size(), "Таски не удалились");
        assertEquals(0, manager.getEpicList().size(), "Эпики не удалились");
        assertEquals(0, manager.getSubtasksList().size(), "Сабы не удалились");
    }
}
