package taskTracker.managers.tasks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.managers.Managers;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private  TaskManager taskManager;
    private  Epic homeBuild;
    private int homeBuildID;
    private  Subtask fundament;
    private  Subtask workers;
    private  Subtask buyWorkers;

    @BeforeEach
    public void prepareToTest(){
        taskManager = Managers.getDefault();                                     //Создали ТаскМенеджер
        homeBuild = new Epic("build a home", "building and buying house");
        homeBuildID = taskManager.addEpic(homeBuild);                            //добавили Эпик в таскМенеджер
        fundament = new Subtask("buy fundament",
                "find store and buy fundament",                         //Создали сабтаски, чтобы по сто раз их не создавать
                Status.NEW,
                homeBuildID);

        workers = new Subtask("Workers",
                "find a worker for building",
                Status.NEW,
                homeBuildID);

        buyWorkers = new Subtask("buy to workers",
                "pay money to workers for done work",
                Status.NEW,
                homeBuildID);
    }

    private void addSubtasksToManager(){
        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        taskManager.addSubtask(buyWorkers);
    }

    private void setStatusToSubtasks(Status fundamentStatus, Status workersStatus, Status buyWorkersStatus){
        fundament.setStatus(fundamentStatus);
        workers.setStatus(workersStatus);
        buyWorkers.setStatus(buyWorkersStatus);
    }

    @Test
    public void checkEpicStatusWithEmptySubtaskList(){
        Epic testEpic = taskManager.getEpic(homeBuildID);
        assertEquals(testEpic.getStatus(), Status.NEW);
    }

    @Test
    public void checkEpicStatusWithAllNEWSubtasksStatus(){
        setStatusToSubtasks(Status.NEW, Status.NEW, Status.NEW);
        addSubtasksToManager();

        Epic testEpic = taskManager.getEpic(homeBuildID);
        assertEquals(testEpic.getStatus(), Status.NEW);
    }

    @Test
    public void checkEpicStatusWithAllDONESubtasksStatus(){
       setStatusToSubtasks(Status.DONE, Status.DONE, Status.DONE);
       addSubtasksToManager();

       Epic testEpic = taskManager.getEpic(homeBuildID);
       assertEquals(testEpic.getStatus(), Status.DONE);
    }

    @Test
    public void checkEpicStatusWithNEWandDONESubtasksStatus(){
        setStatusToSubtasks(Status.NEW, Status.DONE, Status.NEW);
        addSubtasksToManager();

        Epic testEpic = taskManager.getEpic(homeBuildID);
        assertEquals(testEpic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void checkEpicStatusWithIN_PROGRESS_SubtaskStatus(){
        setStatusToSubtasks(Status.IN_PROGRESS, Status.IN_PROGRESS, Status.IN_PROGRESS);
        addSubtasksToManager();

        Epic testEpic = taskManager.getEpic(homeBuildID);
        assertEquals(testEpic.getStatus(), Status.IN_PROGRESS);
    }
}