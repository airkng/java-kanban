package taskTracker.managers.taskManager.InMemoryTaskManager;

import ru.yandex.taskTracker.managers.Managers;
import ru.yandex.taskTracker.managers.taskManager.InMemoryTaskManager;
import taskTracker.managers.taskManager.TaskMangerTest;

public class InMemoryTaskManagerTest extends TaskMangerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
