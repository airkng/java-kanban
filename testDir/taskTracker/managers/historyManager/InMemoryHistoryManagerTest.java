package taskTracker.managers.historyManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.managers.Managers;
import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private static Task book;
    private static Task study;
    private static Epic homeBuild;
    private static Subtask fundament;
    private static Subtask workers;
    private static Epic movement;
    private static Subtask planToMove;

    @BeforeAll
    public static void prepareToTest() {
        book = new Task("Book", "Buy autoBook", Status.NEW);
        study = new Task("Study", "learn java lang", Status.IN_PROGRESS);
        homeBuild = new Epic("home", "building and buying house");
        movement = new Epic("movement", "movement from Russia to Bali");
        fundament = new Subtask("buy fundament", "find store and buy fundament", Status.NEW, homeBuild.getId());
        workers = new Subtask("Workers", "find a worker for building", Status.IN_PROGRESS, homeBuild.getId());
        planToMove = new Subtask("Create plan", "smthingt", Status.NEW, movement.getId());
    }

    @BeforeEach
    public void prepareToEachTest() {
        historyManager = Managers.getHistoryManager();
    }

    private void addAllTasksToHistory() {
        historyManager.addHistory(book);
        historyManager.addHistory(study);
        historyManager.addHistory(homeBuild);
        historyManager.addHistory(movement);
        historyManager.addHistory(fundament);
        historyManager.addHistory(workers);
        historyManager.addHistory(planToMove);
    }

    @Test
    public void getWithEmptyHistoryManager() {
        assertEquals(List.of(), historyManager.getHistory());
    }

    @Test
    public void getWithUnEmptyHistoryManager() {
        addAllTasksToHistory();
        List<Task> expectedList = List.of(book, study, homeBuild, movement, fundament, workers, planToMove);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void duplicationInBeginOfList() {
        List<Task> expectedList = List.of(study, homeBuild, movement, fundament, workers, planToMove, book);
        addAllTasksToHistory();
        historyManager.addHistory(book);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void duplicationInMiddleOfList() {
        List<Task> expectedList = List.of(book, study, homeBuild, fundament, workers, planToMove, movement);
        addAllTasksToHistory();
        historyManager.addHistory(movement);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void duplicationInEndOfList() {
        List<Task> expectedList = List.of(book, study, homeBuild, movement, fundament, workers, planToMove);
        addAllTasksToHistory();
        historyManager.addHistory(planToMove);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void removeFromBeginHistory() {
        List<Task> expectedList = List.of(study, homeBuild, movement, fundament, workers, planToMove);
        addAllTasksToHistory();
        historyManager.remove(book.getId());
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void removeFromMiddleHistory() {
        List<Task> expectedList = List.of(book, study, homeBuild, fundament, workers, planToMove);
        addAllTasksToHistory();
        historyManager.remove(movement.getId());
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void removeFromEndHistory() {
        List<Task> expectedList = List.of(book, study, homeBuild, movement, fundament, workers);
        addAllTasksToHistory();
        historyManager.remove(planToMove.getId());
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }
}