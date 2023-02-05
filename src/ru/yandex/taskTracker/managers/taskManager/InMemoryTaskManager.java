package ru.yandex.taskTracker.managers.taskManager;

import ru.yandex.taskTracker.managers.Managers;
import ru.yandex.taskTracker.managers.historyManager.HistoryManager;
import ru.yandex.taskTracker.managers.utils.StartTimeComparator;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Subtask;
import ru.yandex.taskTracker.tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected final HistoryManager history = Managers.getHistoryManager();
    private final Comparator<Task> timeComparator = new StartTimeComparator();
    protected final TreeSet<Task> timePrioritizedTasks = new TreeSet<>(timeComparator);

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteTasks() {
        Set<Integer> set = tasks.keySet();
        for (int id : set) {
            history.remove(id);
        }
        tasks.clear();
        updatePrioritizedList(tasks, subtasks, epics);
    }

    @Override
    public void deleteSubtasks() {
        for (int id : subtasks.keySet()) {
            history.remove(id);
        }
        subtasks.clear();
        updatePrioritizedList(tasks, subtasks, epics);
    }

    @Override
    public void deleteEpics() {
        for (int id : epics.keySet()) {
            history.remove(id);
        }
        epics.clear();
        deleteSubtasks();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.getOrDefault(id, null);
        if (task != null) {
            history.addHistory(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Task subtask = subtasks.getOrDefault(id, null);
        if (subtask != null) {
            history.addHistory(subtask);
        }
        return subtasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpic(Integer id) {
        Task epic = epics.getOrDefault(id, null);
        if (epic != null) {
            history.addHistory(epic);
        }
        return epics.getOrDefault(id, null);
    }

    @Override
    public int addTask(Task task) {
        //Два одинаковых таска добавить не получится. Даже, если у них разные ID
        if (tasks.containsValue(task)) {
            System.out.println("Таск уже добавлен. Воспользуйтесь командой .update(Task task)\n");
            return -1;
        }
        if (task == null){
            System.out.println("в метод addTask передан null");
            return -1;
        }
        if (isTaskTimeCrossing(task)) {
            System.out.println("Найдено пересечение у Task" + task);
            return -1;
        }
        addToPrioritizedList(task);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) {
            System.out.println("Сабтаск уже добавлен. Воспользуйтесь командой .update(Subtask subtask)");
            return -1;
        }
        if (subtask == null) {
            System.out.println("В метод addSubtask передан null");
            return -1;
        }
        if (isTaskTimeCrossing(subtask)) {
            System.out.println("Найдено пересечение у Subtask" + subtask);
            return -1;
        }
        int key = subtask.getEpicID();
        if (epics.containsKey(key)) {
            Epic epic = epics.get(key);
            epic.linkSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
            epic.checkAndUpdateStatus();
            epics.put(epic.getId(), epic);
            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet
            return subtask.getId();
        } else {
            System.out.println("Ошибка. Сначала создайте Epic с данным id");
            return -1;
        }
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("В метод addEpic передан null");
            return -1;
        }
        if (epics.containsValue(epic)) {
            System.out.println("Epic уже добавлен. Воспользуйтесь командой .update(Epic epic)");
            return -1;
        }
        System.out.println("Epic добавлен, его параметры: " + epic.toString());
        epics.put(epic.getId(), epic);
        addToPrioritizedList(epic); //добавляем в сет
        return epic.getId();

    }

    @Override
    public void updateTask(Task task) {
        //В таске должен быть айди старого таска, который мы заменяем на новый
        if (task == null){
            return;
        }
        if (isTaskTimeCrossing(task)) {
            System.out.println("Ошибка апдейта у Таска " + task);
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet
        } else {
            System.out.println("task не найден в списке HashMap");
        }
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null){
            return;
        }
        if (isTaskTimeCrossing(subtask)) {
            System.out.println("Ошибка! Пересечение при апдейта Subtask " + subtask);
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());     //Достаем старый сабтаск
            Epic epic = epics.get(oldSubtask.getEpicID());          //Достаем epic, в котором был этот сабтаск
            epic.removeSubtask(oldSubtask);                         //убираем из листа в Epic этот сабтаск
            epic.linkSubtask(subtask);                              //связываем текущий сабтаск с эпиком
            subtasks.put(subtask.getId(), subtask);                 //добавляем новый
            epic.checkAndUpdateStatus();                            //проверяем и меняем статус Epic если надо

            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet

        } else {
            System.out.println("subtask не найден в списке HashMap");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null){
            return;
        }
        if (isTaskTimeCrossing(epic)) {
            System.out.println("Найдено пересечение в Эпике " + epic);
            return;
        }
        if (epics.containsValue(epic)) {
            Epic oldEpic = epics.get(epic.getId());
            ArrayList<Subtask> oldList = oldEpic.getEpicSubtasksList();
            for (Subtask subtask : oldList) {
                epic.addSubtask(subtask);
            }
            epic.checkAndUpdateStatus();
            epics.put(epic.getId(), epic);
            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet
        } else {
            System.out.println("epic не найден в списке HashMap");
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            history.remove(id);
            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet
        } else {
            System.out.println("Ключа task " + id + " не существует");
        }
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask deletedSubtask = subtasks.get(id);
            Epic epic = epics.get(deletedSubtask.getEpicID());
            epic.removeSubtask(deletedSubtask);
            epic.checkAndUpdateStatus(); // проверяем статус Эпика
            subtasks.remove(id);
            history.remove(id);
            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet
        } else {
            System.out.println("Ключа subtask " + id + " не существует");
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtasksID = epics.get(id).getEpicSubtasksList();
            for (Subtask subtask : subtasksID) {
                subtasks.remove(subtask.getId());
                history.remove(subtask.getId());
            }
            epics.remove(id);
            history.remove(id);
            updatePrioritizedList(tasks, subtasks, epics);         //updateTreeSet
        } else {
            System.out.println("Ключа epic " + id + " не существует");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Integer id) {
        if (epics.containsKey(id)) {
           return epics.get(id).getEpicSubtasksList();
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        //был пьяный
        return new ArrayList<>(timePrioritizedTasks);
    }
    //Да блин, там в задании сказано, что как доп задание, можно создать какой-то
    // сет, с временными промежутками в 15 минут, чтобы проверял свободен ли или нет за О(1)
    // вот я так с этим и морочился, в жизни бы такого не придумал, ахах
    private void updatePrioritizedList(Map<Integer, Task> tasks, Map<Integer, Subtask> subtasks,Map<Integer, Epic> epics){
        timePrioritizedTasks.clear();

        for(Task task: tasks.values()){
            addToPrioritizedList(task);
        }
        for (Subtask subtask: subtasks.values()){
            addToPrioritizedList(subtask);
        }
        for (Epic epic: epics.values()){
            addToPrioritizedList(epic);
        }
    }
    private void addToPrioritizedList(Task task){
        timePrioritizedTasks.add(task);
    }

    private boolean isTaskTimeCrossing(Task task){
        if (task.getStartTime() == null ){
            return false;
        }
        for (Task element : timePrioritizedTasks) {
            if (element.getStartTime() == null || element.getEndTime() == null){
                continue;
            }
            if(
            (task.getStartTime().isAfter(element.getStartTime()) && task.getStartTime().isBefore(element.getEndTime()))
            || (task.getEndTime().isAfter(element.getStartTime()) && task.getEndTime().isBefore(element.getEndTime()))
            ) {
                return true;
            }
        }
        return false;
    }
}
