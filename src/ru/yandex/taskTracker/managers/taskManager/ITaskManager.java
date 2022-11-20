package ru.yandex.taskTracker.managers.taskManager;

import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.util.ArrayList;

public interface ITaskManager {
    ArrayList<Task> getTasksList();

    ArrayList<Subtask> getSubtasksList();

    ArrayList<Epic> getEpicList();

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpic();

    Task getTask(Integer id);

    Subtask getSubtask(Integer id);

    Epic getEpic(Integer id);

    int addTask(Task task);

    int addSubtask(Subtask subtask);

    int addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic (Epic epic);

    void deleteTaskById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteEpicById(Integer id);

    ArrayList<Subtask> getEpicSubtasks(Integer id);

}
