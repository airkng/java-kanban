package taskTracker.managers.taskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.managers.taskManager.TaskManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TaskMangerTest<T extends TaskManager> {

     public T taskManager;
     public Task book;
     public Task study;
     public Epic homeBuild;
     public Subtask fundament;
     public Subtask workers;
     public Epic movement;
     public Subtask planToMove;

    public abstract T createManager();

    @BeforeEach
    void prepareToEachTest(){
        taskManager = createManager();
        book = new Task("Book", "Buy autoBook", Status.NEW);
        study = new Task("Study", "learn java lang", Status.IN_PROGRESS);
        homeBuild = new Epic("home", "building and buying house");
        movement = new Epic("movement", "movement from Russia to Bali");
        fundament = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuild.getId());
        workers = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, homeBuild.getId());
        planToMove = new Subtask("Create plan", "smthingt", Status.NEW, movement.getId());
    }

    @Test
    public void addTaskTest() {
        Assertions.assertEquals(taskManager.getTasksList(), Collections.emptyList());

       int idBook = taskManager.addTask(book);
       Assertions.assertNotNull(taskManager.getTasksList());
       Assertions.assertEquals(1, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");

       int nullTaskId = taskManager.addTask(null);
       Assertions.assertEquals(-1, nullTaskId, "Ошибка при добавлении addTask со значеннием null");

       int studyId = taskManager.addTask(study);
       Assertions.assertEquals(2, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");

       Task bookTaskCopy = new Task("Book", "Buy autoBook", Status.NEW);
       int copyId = taskManager.addTask(bookTaskCopy);
       Assertions.assertEquals(-1, copyId);
       Assertions.assertEquals(2, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");
    }
    @Test
    public void addSubtaskTest(){
        Assertions.assertEquals(taskManager.getSubtasksList(), Collections.emptyList());

        //не добавится, так как не добавлен эпик
        int fundamentId = taskManager.addSubtask(fundament);
        Assertions.assertEquals(0, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");
        Assertions.assertEquals(-1, fundamentId);

        //не добавится так как значение null
        int nullSubTaskId = taskManager.addSubtask(null);
        Assertions.assertEquals(-1, nullSubTaskId, "Ошибка при добавлении addTask со значеннием null");
        Assertions.assertEquals(taskManager.getSubtasksList(), Collections.emptyList());

        //Добавится, тк добавили сначала эпик
        int homeBuildId = taskManager.addEpic(homeBuild);
        fundamentId = taskManager.addSubtask(fundament);
        Assertions.assertEquals(1, taskManager.getSubtasksList().size(), "Неправильное количество элементов Task в мапе");

        int workersId = taskManager.addSubtask(workers);
        Assertions.assertEquals(2, taskManager.getSubtasksList().size(), "Неправильное количество элементов Task в мапе");

        Subtask bookSubtaskCopy = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuild.getId());
        int copyId = taskManager.addSubtask(bookSubtaskCopy);
        Assertions.assertEquals(-1, copyId);
        Assertions.assertEquals(2, taskManager.getSubtasksList().size(), "Неправильное количество элементов Task в мапе");
    }

    @Test
    public void addEpicTest(){
        assertEquals(Collections.emptyList(), taskManager.getEpicList());

        int homeBuildId = taskManager.addEpic(homeBuild);
        assertNotNull(taskManager.getEpicList());
        assertEquals(1, taskManager.getEpicList().size());

        int movementId = taskManager.addEpic(movement);
        assertNotNull(taskManager.getEpicList());
        assertEquals(2, taskManager.getEpicList().size());
        //не добавится
        Epic copyEpic = new Epic("home", "building and buying house", homeBuildId);
        int copyId = taskManager.addEpic(copyEpic);
        assertEquals(-1, copyId);
        assertEquals(2, taskManager.getEpicList().size());
        assertEquals(Status.NEW, taskManager.getEpic(homeBuildId).getStatus());
        //проверка статуса new(ниже)
        fundament.setStatus(Status.NEW);
        workers.setStatus(Status.NEW);
        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        assertEquals(Status.NEW, taskManager.getEpic(homeBuildId).getStatus());
        //Проверка статуса IN_Progress
        fundament.setStatus(Status.NEW);
        workers.setStatus(Status.DONE);
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(homeBuildId).getStatus());
        //Проверка статуса IN_Progress
        fundament.setStatus(Status.IN_PROGRESS);
        workers.setStatus(Status.NEW);
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(homeBuildId).getStatus());
        //Проверка статуса DONE
        fundament.setStatus(Status.DONE);
        workers.setStatus(Status.DONE);
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.DONE, taskManager.getEpic(homeBuildId).getStatus());
    }

    @Test
    public void updateTaskTest(){
        int idBook = taskManager.addTask(study);
        study.setName("Test");
        study.setDescription("testing");
        study.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(study);
        Task test = new Task("Test", "testing", Status.IN_PROGRESS, idBook);
        assertEquals(test, taskManager.getTask(idBook));
    }

    @Test
    public void updateSubtaskTest(){
        int homeId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        int workersId = taskManager.addSubtask(workers);
        Subtask test = new Subtask("Test", "find store and buy fundament", Status.DONE, fundamentId,homeBuild.getId());
        workers = new Subtask("Workers", "find a worker for building", Status.DONE, workersId ,homeBuild.getId());
        taskManager.updateSubtask(test);
        taskManager.updateSubtask(workers);
        Subtask expectedResult = new Subtask("Test", "find store and buy fundament", Status.DONE, fundamentId,homeBuild.getId());
        assertEquals(Status.DONE, taskManager.getEpic(homeId).getStatus());
        assertEquals(expectedResult, taskManager.getSubtask(fundamentId));
        assertEquals(workers, taskManager.getSubtask(workersId));

    }
    @Test
    public void updateEpicTest(){
        int homeId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        int workersId = taskManager.addSubtask(workers);
        Epic test = new Epic("Test","testing", homeId);
        Epic expectedResult = new Epic("Test","testing", Status.IN_PROGRESS,homeId);
        homeBuild.setName("Test");
        homeBuild.setDescription("testing");
        taskManager.updateEpic(test);
        assertEquals(expectedResult, taskManager.getEpic(homeId));

        fundament.setStatus(Status.NEW);
        workers.setStatus(Status.DONE);
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(homeId).getStatus());
        //Проверка статуса IN_Progress
        fundament.setStatus(Status.IN_PROGRESS);
        workers.setStatus(Status.NEW);
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(homeId).getStatus());
        //Проверка статуса DONE
        fundament.setStatus(Status.DONE);
        workers.setStatus(Status.DONE);
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.DONE, taskManager.getEpic(homeId).getStatus());
    }
    @Test
    public void deleteAllTasksTest() {
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());

        int idBook = taskManager.addTask(book);
        int idStudy = taskManager.addTask(study);
        assertEquals(2, taskManager.getTasksList().size());

        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void deleteAllSubtasksTest(){
        taskManager.deleteSubtasks();
        assertEquals(0, taskManager.getSubtasksList().size());

        int homebuildId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);
        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        taskManager.addSubtask(planToMove);
        assertEquals(3, taskManager.getSubtasksList().size());

        taskManager.deleteSubtasks();
        assertEquals(0, taskManager.getSubtasksList().size());
    }
   @Test
    public void deleteAllEpicsTest(){
       taskManager.deleteEpics();
       assertEquals(0, taskManager.getEpicList().size());

       int homebuildId = taskManager.addEpic(homeBuild);
       int movementId = taskManager.addEpic(movement);
       assertEquals(2, taskManager.getEpicList().size());

       taskManager.deleteEpics();
       assertEquals(0, taskManager.getEpicList().size());
       assertEquals(0, taskManager.getSubtasksList().size());
   }
   @Test
   public void deleteTaskByIdTest(){
        int bookId = taskManager.addTask(book);
        taskManager.deleteTaskById(bookId);
        assertNull(taskManager.getTask(bookId));
   }
   @Test
   public void deleteSubtaskByIdTest(){
        int homebuildId = taskManager.addEpic(homeBuild);
        fundament.setStatus(Status.IN_PROGRESS);
        int testid = taskManager.addSubtask(fundament);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(homebuildId).getStatus());
        taskManager.deleteSubtaskById(testid);
        assertNull(taskManager.getSubtask(testid));
       assertEquals(Status.NEW, taskManager.getEpic(homebuildId).getStatus());
    }
    @Test
    public void deleteEpicByIdTest(){
        int homeId = taskManager.addEpic(homeBuild);
        taskManager.addEpic(movement);
        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        taskManager.addSubtask(planToMove);
        assertEquals(2, taskManager.getEpicList().size());
        assertEquals(3, taskManager.getSubtasksList().size());
        taskManager.deleteEpicById(homeId);
        assertEquals(1, taskManager.getEpicList().size());
        assertEquals(1, taskManager.getSubtasksList().size());
        assertEquals(planToMove, taskManager.getSubtasksList().get(0));
        assertEquals(movement, taskManager.getEpicList().get(0));
    }
   @Test
    public void getTasksListTest(){
        assertEquals(Collections.emptyList(), taskManager.getTasksList());
        int id = taskManager.addTask(book);
        taskManager.addTask(study);
        assertEquals(2, taskManager.getTasksList().size());

        taskManager.deleteTaskById(id);
        assertEquals(List.of(study), taskManager.getTasksList());

   }
   @Test
    public void getSubtaskListTest(){
        assertEquals(Collections.emptyList(), taskManager.getSubtasksList());
       int homebuildId = taskManager.addEpic(homeBuild);
       int movementId = taskManager.addEpic(movement);
       taskManager.addSubtask(fundament);
       int id1 = taskManager.addSubtask(workers);
       int id2 = taskManager.addSubtask(planToMove);
       assertEquals(3, taskManager.getSubtasksList().size());

       taskManager.deleteSubtaskById(id1);
       taskManager.deleteSubtaskById(id2);

       assertEquals(List.of(fundament), taskManager.getSubtasksList());
       taskManager.deleteSubtasks();
       assertEquals(0, taskManager.getSubtasksList().size());
   }
   @Test
   public void getEpicsListTest(){
        assertEquals(Collections.emptyList(), taskManager.getEpicList());
       int homebuildId = taskManager.addEpic(homeBuild);
       int movementId = taskManager.addEpic(movement);

       assertEquals(2,taskManager.getEpicList().size());
       taskManager.deleteEpicById(homebuildId);
       assertEquals(List.of(movement), taskManager.getEpicList());

       homebuildId = taskManager.addEpic(homeBuild);
       taskManager.deleteEpics();
       assertEquals(0, taskManager.getEpicList().size());
   }
   @Test
    public void getTaskTest(){
       Task testTask = new Task("Book", "Buy autoBook", Status.NEW);
       Task testTask2 = new Task("Study", "learn java lang", Status.IN_PROGRESS);
       int bookId = taskManager.addTask(book);
       int studyId = taskManager.addTask(study);

       assertEquals(testTask, taskManager.getTask(bookId));
       assertEquals(testTask2, taskManager.getTask(studyId));
       assertNull(taskManager.getTask(-1));
   }
   @Test
    public void getSubtaskTest(){
       int homebuildId = taskManager.addEpic(homeBuild);
       int movementId = taskManager.addEpic(movement);
       int id1 = taskManager.addSubtask(fundament);
       int id2 = taskManager.addSubtask(workers);
       int id3 = taskManager.addSubtask(planToMove);
       Subtask test1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuild.getId());
       Subtask test2 = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, homeBuild.getId());
       Subtask test3 = new Subtask("Create plan", "smthingt", Status.NEW, movement.getId());

       assertEquals(test1, taskManager.getSubtask(id1));
       assertEquals(test2, taskManager.getSubtask(id2));
       assertEquals(test3, taskManager.getSubtask(id3));
       assertNull(taskManager.getSubtask(-1));
   }
   @Test
    public void getEpicTest(){
       int homebuildId = taskManager.addEpic(homeBuild);
       int movementId = taskManager.addEpic(movement);
       Epic test1 = new Epic("home", "building and buying house", homebuildId);
       Epic test2 = new Epic("movement", "movement from Russia to Bali", movementId);

       assertEquals(test1, taskManager.getEpic(homebuildId));
       assertEquals(test2, taskManager.getEpic(movementId));
       assertNull(taskManager.getEpic(-1));
   }
   @Test
    public void getEpicSubtasks(){
       int homeId = taskManager.addEpic(homeBuild);
       int movementId = taskManager.addEpic(movement);
       taskManager.addSubtask(fundament);
       taskManager.addSubtask(workers);
       taskManager.addSubtask(planToMove);
       assertEquals(2, taskManager.getEpicList().size());
       assertEquals(3, taskManager.getSubtasksList().size());

       assertEquals(2, taskManager.getEpicSubtasks(homeId).size());
       assertEquals(1, taskManager.getEpicSubtasks(movementId).size());
       assertEquals(planToMove, taskManager.getEpicSubtasks(movementId).get(0));
   }
   @Test
   public void getHistoryTest(){
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
       assertEquals(List.of(task1, task2, epic1, epic2, sub1, sub2, sub3), taskManager.getHistory());
       taskManager.getTask(taskid1);
       assertEquals(List.of(task2, epic1, epic2, sub1, sub2, sub3, task1), taskManager.getHistory());
       taskManager.getSubtask(sub1ID);
       assertEquals(List.of(task2, epic1, epic2, sub2, sub3, task1, sub1), taskManager.getHistory());

       taskManager.deleteTaskById(taskid2);
       assertEquals(List.of(epic1, epic2, sub2, sub3, task1, sub1), taskManager.getHistory());

       taskManager.deleteEpicById(epic1ID);
       assertEquals(List.of(epic2, task1), taskManager.getHistory());

       taskManager.deleteTasks();
       taskManager.deleteEpics();
       assertEquals(List.of(), taskManager.getHistory());
   }
   @Test
    public void getPrioritizedTasksTest(){
       Task task1 = new Task("Book", "Buy autoBook", Status.NEW, 16, LocalDateTime.of(2023,1,1,2,0));
       Task task2 = new Task("Study", "learn java lang", Status.IN_PROGRESS, 10, LocalDateTime.of(2023,1,1,0, 0));
       int taskid1 = taskManager.addTask(task1);
       int taskid2 = taskManager.addTask(task2);

       Epic epic1 = new Epic("home", "building and buying house");
       int epic1ID = taskManager.addEpic(epic1);
       Epic epic2 = new Epic("movement", "movement from Russia to Bali");
       taskManager.addEpic(epic2);


       Subtask sub1 = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, epic1ID, 10, LocalDateTime.of(2023, 1,1,0,16));
       int sub1ID = taskManager.addSubtask(sub1);

       Subtask sub2 = new Subtask("Create plan", "smthingt", Status.NEW, epic1ID, 15, LocalDateTime.of(2023, 1,1,0,30));
       int sub2ID = taskManager.addSubtask(sub2);

       Subtask sub3 = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, epic1ID, 30, LocalDateTime.of(2023, 1,1,0,45));
       int sub3ID = taskManager.addSubtask(sub3);
       ArrayList<Task> expectedRes1 = new ArrayList<>();
       expectedRes1.add(task2);
       expectedRes1.add(sub1);
       expectedRes1.add(sub2);
       expectedRes1.add(sub3);
       expectedRes1.add(task1);
       expectedRes1.add(epic2);
       assertEquals(expectedRes1, taskManager.getPrioritizedTasks());
       assertEquals(epic1.getStartTime(), sub1.getStartTime());
       assertEquals(epic1.getEndTime(), sub3.getEndTime());
       //пересечение
       Task cross1 = new Task("test", "testing", Status.IN_PROGRESS, 15, LocalDateTime.of(2023, 1,1,0,20));
       int idTest = taskManager.addTask(cross1);
       assertEquals(-1, idTest);
       assertEquals(expectedRes1, taskManager.getPrioritizedTasks());
   }
}
