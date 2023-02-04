package ru.yandex.taskTracker.managers.historyManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.managers.Managers;
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
    public void getHistory_returnEmptyList_EmptyHistory() {
        assertEquals(List.of(), historyManager.getHistory());
    }

    @Test
    public void getHistory_returnRightSequence_UnEmptyHistory() {
        addAllTasksToHistory();
        List<Task> expectedList = List.of(book, study, homeBuild, movement, fundament, workers, planToMove);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void addHistory_shouldReturnBookInEndOfList_duplicationInBeginOfList() {
        List<Task> expectedList = List.of(study, homeBuild, movement, fundament, workers, planToMove, book);
        addAllTasksToHistory();
        historyManager.addHistory(book);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void addHistory_shouldReturnMovementInEndOfList_duplicationInMiddleOfList() {
        List<Task> expectedList = List.of(book, study, homeBuild, fundament, workers, planToMove, movement);
        addAllTasksToHistory();
        historyManager.addHistory(movement);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void addHistory_shouldReturnPlanToMoveInEndOfList_duplicationInEndOfList() {
        List<Task> expectedList = List.of(book, study, homeBuild, movement, fundament, workers, planToMove);
        addAllTasksToHistory();
        historyManager.addHistory(planToMove);
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void remove_shouldReturnWithOutBookInEndOfList_FromBeginHistory() {
        List<Task> expectedList = List.of(study, homeBuild, movement, fundament, workers, planToMove);
        addAllTasksToHistory();
        historyManager.remove(book.getId());
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void remove_shouldReturnWithOutMovementInEndOfList_FromMiddleHistory() {
        List<Task> expectedList = List.of(book, study, homeBuild, fundament, workers, planToMove);
        addAllTasksToHistory();
        historyManager.remove(movement.getId());
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void remove_shouldReturnWithOutPlanToMoveInEndOfList_FromEndHistory() {
        List<Task> expectedList = List.of(book, study, homeBuild, movement, fundament, workers);
        addAllTasksToHistory();
        historyManager.remove(planToMove.getId());
        List<Task> result = historyManager.getHistory();
        Assertions.assertEquals(expectedList, result);
    }
}