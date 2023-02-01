package ru.yandex.taskTracker.managers.taskManager;

public class InMemoryTaskManagerTest extends TaskMangerTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}

