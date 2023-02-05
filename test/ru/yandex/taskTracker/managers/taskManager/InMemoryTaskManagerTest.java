package ru.yandex.taskTracker.managers.taskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}

