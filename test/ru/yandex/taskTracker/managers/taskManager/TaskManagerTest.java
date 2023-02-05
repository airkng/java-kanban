package ru.yandex.taskTracker.managers.taskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

     public T taskManager;
     public Task book;
     public Task study;
     public Epic homeBuild;
     public Subtask fundament;
     public Subtask workers;
     public Epic movement;
     public Subtask planToMove;

    public abstract T createManager() throws IOException, InterruptedException;

    @BeforeEach
    void prepareToEachTest() throws IOException, InterruptedException {
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
    public void addTask_return2size_afterAddTaskMethod() {
        int idBook = taskManager.addTask(book);
        Assertions.assertNotNull(taskManager.getTasksList());
        Assertions.assertEquals(1, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");

        int studyId = taskManager.addTask(study);
        Assertions.assertEquals(2, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");
    }
    @Test
    public void addTask_returnSpecialId_InputNull() {
        int nullTaskId = taskManager.addTask(null);
        Assertions.assertEquals(-1, nullTaskId, "Ошибка при добавлении addTask со значеннием null");
    }
    @Test
    public void addTask_shouldReturnSpecialId_copyTask(){
       int idBook = taskManager.addTask(book);
       Task bookTaskCopy = new Task("Book", "Buy autoBook", Status.NEW);
       int copyId = taskManager.addTask(bookTaskCopy);
       Assertions.assertEquals(-1, copyId);
    }
    @Test
    public void addSubtask_ReturnSpecialId_EpicDidNotAdd(){
        int fundamentId = taskManager.addSubtask(fundament);
        Assertions.assertEquals(0, taskManager.getTasksList().size(), "Неправильное количество элементов Task в мапе");
        Assertions.assertEquals(-1, fundamentId);
    }
    @Test
    public void addSubtask_returnSpecialId_TryToAddNull(){
        int nullSubTaskId = taskManager.addSubtask(null);
        Assertions.assertEquals(-1, nullSubTaskId, "Ошибка при добавлении addTask со значеннием null");
        Assertions.assertEquals(taskManager.getSubtasksList(), Collections.emptyList());
    }
    @Test
    public void addSubtask_returnRightSizeList_usuallyWork(){
        int homeBuildId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        Assertions.assertEquals(1, taskManager.getSubtasksList().size(), "Неправильное количество элементов Task в мапе");

        int workersId = taskManager.addSubtask(workers);
        Assertions.assertEquals(2, taskManager.getSubtasksList().size(), "Неправильное количество элементов Task в мапе");
    }
    @Test
    public void addSubtask_returnSpecialId_addCopySubtask(){
        int homeBuildId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        int workersId = taskManager.addSubtask(workers);

        Subtask fundamentSubtaskCopy = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuild.getId());
        int copyId = taskManager.addSubtask(fundamentSubtaskCopy);

        Assertions.assertEquals(-1, copyId);
        Assertions.assertEquals(2, taskManager.getSubtasksList().size(), "Неправильное количество элементов Task в мапе");
    }
    @Test
    public void addEpic_returnRightSizeEpicList_afterAddEpicMethod(){
        int homeBuildId = taskManager.addEpic(homeBuild);
        assertNotNull(taskManager.getEpicList());
        assertEquals(1, taskManager.getEpicList().size());

        int movementId = taskManager.addEpic(movement);
        assertNotNull(taskManager.getEpicList());
        assertEquals(2, taskManager.getEpicList().size());

        assertEquals(Status.NEW, taskManager.getEpic(homeBuildId).getStatus());
    }
    @Test
    public void addEpic_returnSpecialId_TryToAddCopy(){
        int homeBuildId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);
        Epic copyEpic = new Epic("home", "building and buying house", homeBuildId);
        int copyId = taskManager.addEpic(copyEpic);

        assertEquals(-1, copyId);
        assertEquals(2, taskManager.getEpicList().size());
    }
    @Test
    public void updateTask_shouldEqualsTrue_afterUpdatingTask(){
        int idBook = taskManager.addTask(study);
        study.setName("Test");
        study.setDescription("testing");
        study.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(study);
        Task test = new Task("Test", "testing", Status.IN_PROGRESS, idBook);
        assertEquals(test, taskManager.getTask(idBook));
    }
    @Test
    public void updateSubtask_shouldEqualsTrue_afterUpdatingSubtask() {
        int homeId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        Subtask replaceSub = new Subtask("Test", "find store and buy fundament", Status.DONE, fundamentId,homeBuild.getId());
        taskManager.updateSubtask(replaceSub);
        Subtask expectedResult = new Subtask("Test", "find store and buy fundament", Status.DONE, fundamentId,homeBuild.getId());
        assertEquals(expectedResult, taskManager.getSubtask(fundamentId));
    }
    @Test
    public void updateSubtask_ReturnDoneEpicStatus_afterUpdateSubtasks(){
        int homeId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        int workersId = taskManager.addSubtask(workers);
        fundament = new Subtask("Test", "find store and buy fundament", Status.DONE, fundamentId,homeBuild.getId());
        workers = new Subtask("Workers", "find a worker for building", Status.DONE, workersId ,homeBuild.getId());
        taskManager.updateSubtask(fundament);
        taskManager.updateSubtask(workers);
        assertEquals(Status.DONE, taskManager.getEpic(homeId).getStatus());
    }
    @Test
    public void updateEpic_returnRightEpicWithNewParameters_afterUpdateEpic() {
        int homeId = taskManager.addEpic(homeBuild);
        int fundamentId = taskManager.addSubtask(fundament);
        int workersId = taskManager.addSubtask(workers);
        Epic test = new Epic("Test","testing", homeId);
        Epic expectedResult = new Epic("Test","testing", Status.IN_PROGRESS,homeId);
        homeBuild.setName("Test");
        homeBuild.setDescription("testing");

        taskManager.updateEpic(test);
        assertEquals(expectedResult, taskManager.getEpic(homeId));
    }
    @Test
    public void deleteTasks_shouldReturnEmptyList_usingThisMethod() {
        taskManager.addTask(book);
        taskManager.addTask(study);
        assertEquals(2, taskManager.getTasksList().size());

        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksList().size());
    }
    @Test
    public void deleteSubtasks_shouldReturnEmptyList_usingThisMethod(){
        //prepareToTest
        taskManager.addEpic(homeBuild);
        taskManager.addEpic(movement);
        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        taskManager.addSubtask(planToMove);
        assertEquals(3, taskManager.getSubtasksList().size());

        taskManager.deleteSubtasks();
        assertEquals(0, taskManager.getSubtasksList().size());
    }
    @Test
    public void deleteEpics_shouldReturnEmptyEpicListAndEmptySubtaskList_UsingThisMethod(){
        //prepareToTest
        taskManager.addEpic(homeBuild);
        taskManager.addEpic(movement);
        assertEquals(2, taskManager.getEpicList().size());

        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        taskManager.addSubtask(planToMove);
        //testing
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicList().size());
        assertEquals(0, taskManager.getSubtasksList().size());
    }
    @Test
    public void deleteTaskById_shouldReturnNull_ByUsingGetTaskMethod(){
        int bookId = taskManager.addTask(book);
        taskManager.deleteTaskById(bookId);
        assertNull(taskManager.getTask(bookId));
    }
    @Test
    public void deleteSubtaskById_shouldReturnNull_ByUsingGetSubtaskMethod(){
        int homebuildId = taskManager.addEpic(homeBuild);
        int testid = taskManager.addSubtask(fundament);
        assertNotNull(taskManager.getSubtask(testid));
        taskManager.deleteSubtaskById(testid);
        assertNull(taskManager.getSubtask(testid));
    }
    @Test
    public void deleteSubtaskById_shouldReturnNEW_STATUS_whenSubtaskWillBeDelete(){
        int homebuildId = taskManager.addEpic(homeBuild);
        fundament.setStatus(Status.IN_PROGRESS);
        int testid = taskManager.addSubtask(fundament);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(homebuildId).getStatus());

        taskManager.deleteSubtaskById(testid);
        assertNull(taskManager.getSubtask(testid));
        assertEquals(Status.NEW, taskManager.getEpic(homebuildId).getStatus());
    }
    @Test
    public void deleteEpicById_shouldChangeSizeEpicListAndSubtaskList_byUsingThisMethod(){
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
    public void getTaskList_shouldReturnEmptyList_NothingDidNotAdd(){
        assertEquals(Collections.emptyList(), taskManager.getTasksList());
        assertEquals(0, taskManager.getTasksList().size());
    }
    @Test
    public void getTasksList_shouldReturnRightSizeAndSequence_UsingAddTaskMethodAndDeleteTaskByIdMethod(){
        int deleteId = taskManager.addTask(book);
        taskManager.addTask(study);
        assertEquals(2, taskManager.getTasksList().size());
        taskManager.deleteTaskById(deleteId);
        assertEquals(List.of(study), taskManager.getTasksList());
    }
    @Test
    public void getSubtaskList_shouldReturnEmptyList_NothingDidNotAdd(){
        assertEquals(Collections.emptyList(), taskManager.getSubtasksList());
        assertEquals(0, taskManager.getSubtasksList().size());
    }
    @Test
    public void getSubtaskList_shouldReturnRightSizeAndSequence_UsingAddTaskMethodAndDeleteTaskByIdMethod(){
        //prepareToTest
        taskManager.addEpic(homeBuild);
        taskManager.addEpic(movement);
        int id1 = taskManager.addSubtask(workers);
        int id2 = taskManager.addSubtask(planToMove);
        //Test
        assertEquals(2, taskManager.getSubtasksList().size());
        //deleteTask
        taskManager.deleteSubtaskById(id1);
        assertEquals(List.of(planToMove), taskManager.getSubtasksList());
        //shouldReturnEmptyList
        taskManager.deleteSubtaskById(id2);
        assertEquals(0, taskManager.getSubtasksList().size());
    }
    @Test
    public void getEpicsList_shouldReturnRightSize_WhenWeAddNewEpic(){
        int homebuildId = taskManager.addEpic(homeBuild);
        assertEquals(1,taskManager.getEpicList().size());
        int movementId = taskManager.addEpic(movement);
        assertEquals(2,taskManager.getEpicList().size());
    }
    @Test
    public void getEpicsList_ReturnRightListAndSize_afterDeletingByIdMethod(){
        int homebuildId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);
        taskManager.deleteEpicById(homebuildId);

        assertEquals(List.of(movement), taskManager.getEpicList());
        assertEquals(1, taskManager.getEpicList().size());
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicList().size());
    }
    @Test
    public void getEpicsList_shouldReturnEmptyList(){
        assertEquals(Collections.emptyList(), taskManager.getEpicList());
        assertEquals(0, taskManager.getEpicList().size());
    }
    @Test
    public void getTask_returnRightTaskAndRightHistory_TaskIsExist(){
        Task testTask = new Task("Book", "Buy autoBook", Status.NEW);
        Task testTask2 = new Task("Study", "learn java lang", Status.IN_PROGRESS);
        int bookId = taskManager.addTask(book);
        int studyId = taskManager.addTask(study);

        assertEquals(testTask, taskManager.getTask(bookId));
        assertEquals(testTask2, taskManager.getTask(studyId));
        assertEquals(2, taskManager.getHistory().size());
        assertEquals(List.of(book, study), taskManager.getHistory());
    }
    @Test
    public void getTask_returnNullAndEmptyHistory_TaskDoesNotExist(){
        int bookId = taskManager.addTask(book);
        int studyId = taskManager.addTask(study);
        assertNull(taskManager.getTask(-1));
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(List.of(), taskManager.getHistory());
    }
    @Test
    public void getSubtask_shouldReturnRightSubtaskAndHistory_afterAddSubtaskMethod(){
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

        assertEquals(3, taskManager.getHistory().size());
        assertEquals(List.of(test1, test2, test3), taskManager.getHistory());
    }
    @Test
    public void getSubtask_shouldReturnNullAndEmptyHistory(){
        assertNull(taskManager.getSubtask(-1));
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(List.of(), taskManager.getHistory());
    }
    @Test
    public void getEpic_returnRightEpic_exist(){
        int homebuildId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);
        Epic test1 = new Epic("home", "building and buying house", homebuildId);
        Epic test2 = new Epic("movement", "movement from Russia to Bali", movementId);

        assertEquals(test1, taskManager.getEpic(homebuildId));
        assertEquals(test2, taskManager.getEpic(movementId));
    }
    @Test
    public void getEpic_returnNull_GettingUnExistEpic(){
        int homebuildId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);

        assertNull(taskManager.getEpic(-1));
    }
    @Test
    public void getEpicSubtasks_ReturnRightSizeAndSubtaskForEachEpic(){
        int homeId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);
        taskManager.addSubtask(fundament);
        taskManager.addSubtask(workers);
        taskManager.addSubtask(planToMove);

        assertEquals(2, taskManager.getEpicSubtasks(homeId).size());
        assertEquals(1, taskManager.getEpicSubtasks(movementId).size());
        assertEquals(planToMove, taskManager.getEpicSubtasks(movementId).get(0));
    }
    @Test
    public void getEpicSubtask_returnEmptyList_NoSubtask(){
        int homeId = taskManager.addEpic(homeBuild);
        int movementId = taskManager.addEpic(movement);

        assertEquals(0, taskManager.getEpicSubtasks(homeId).size());
        assertEquals(0, taskManager.getEpicSubtasks(movementId).size());
    }
    @Test
    public void getHistory_GlobalTestShouldReturnRightSequence(){
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
        //duplicate from begin
        taskManager.getTask(taskid1);
        assertEquals(List.of(task2, epic1, epic2, sub1, sub2, sub3, task1), taskManager.getHistory());
        //duplicate from middle
        taskManager.getSubtask(sub1ID);
        assertEquals(List.of(task2, epic1, epic2, sub2, sub3, task1, sub1), taskManager.getHistory());
        //delete from history
        taskManager.deleteTaskById(taskid2);
        assertEquals(List.of(epic1, epic2, sub2, sub3, task1, sub1), taskManager.getHistory());

        taskManager.deleteEpicById(epic1ID);
        assertEquals(List.of(epic2, task1), taskManager.getHistory());

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        assertEquals(List.of(), taskManager.getHistory());
    }
    @Test
    public void getHistory_shouldReturnEmptyHistoryAndRightTasksLists(){
        taskManager.addTask(study);
        taskManager.addTask(book);
        taskManager.updateTask(book);
        taskManager.addEpic(homeBuild);
        assertEquals(List.of(), taskManager.getHistory());
        assertEquals(2, taskManager.getTasksList().size());
        assertEquals(1, taskManager.getEpicList().size());
    }
    @Test
    public void getPrioritizedTasks_GlobalTest(){
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
