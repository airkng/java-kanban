package ru.yandex.taskTracker.tasks;

import ru.yandex.taskTracker.managers.utils.StartTimeComparator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Epic extends Task {

    private static int epicId = 100_000;
    private final ArrayList<Subtask> epicSubtasksList = new ArrayList<>();      //Лист сабтасков эпика
    private final Comparator<Task> timeSort = new StartTimeComparator();


    //конструктор для fileManager
    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, epicId);
        epicId++;
    }
    //конструктор для FileManager
    public Epic(String name, String description, Status status, int id, long duration, LocalDateTime startTime){
        super(name, description, status, id, duration, startTime);
    }
    public Epic(String name, String description, int oldEpicID) {
        super(name, description, Status.NEW, oldEpicID);
    }

    public ArrayList<Subtask> getEpicSubtasksList() {
        return epicSubtasksList;
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("О-оу, господин. Такое делать запрещено");
    }

    @Override
    public void setId(Integer id) {
        System.out.println("Изменение id эпика приведет к потере взаимосвязи сабтасков и эпиков.");
        this.id = id;
    }

    @Deprecated
    public void setDuration(long duration) {
        throw new IllegalStateException("Нельзя изменять поле duration in Epic.class");
    }

    @Deprecated
    public void setStartTime(LocalDateTime startTime) {
        throw new IllegalStateException("Нельзя изменять поле startTime in Epic.class");
    }

    @Override
    public LocalDateTime getEndTime() {
        if (!epicSubtasksList.isEmpty()) {
            epicSubtasksList.sort(timeSort);
            Subtask latestSubtask = epicSubtasksList.get((epicSubtasksList.size() - 1));
            return latestSubtask.getEndTime();
        } else {
            System.out.println("В классе Epic отсутствуют time-Subtasks");
            return null;
        }
    }

    @Override
    public LocalDateTime getStartTime() {
        if (!epicSubtasksList.isEmpty()) {
            epicSubtasksList.sort(timeSort);
            return epicSubtasksList.get(0).getStartTime();
        } else {
            return null;
        }
    }

    @Override
    public long getDuration() {
        if (epicSubtasksList.isEmpty()) {
            return 0;
        }
        duration = null;
        for (Subtask subtask : epicSubtasksList) {
            if (subtask.getStartTime() == null || subtask.getDuration() == 0){
                continue;
            }
            if (duration == null) {
                duration = Duration.ofMinutes(subtask.getDuration());
                continue;
            }
            duration = duration.plus(Duration.ofMinutes(subtask.getDuration()));
        }
        if (duration == null){
            return 0;
        }
        return duration.toMinutes();
    }

    public void removeSubtask(Subtask subtask) {
        epicSubtasksList.remove(subtask);
    }

    public void addSubtask(Subtask subtask) {
        epicSubtasksList.add(subtask);
    }

    public void linkSubtask(Subtask subtask) {
        epicSubtasksList.add(subtask);
    }

    public void checkAndUpdateStatus() {
        if (epicSubtasksList.isEmpty()) {
            this.status = Status.NEW;
        } else {
            int doneCount = 0;
            int newStatusCount = 0;

            for (Subtask subtask : epicSubtasksList) {
                //Если у сабтаска есть time-переменные, то добавляем в лист сабтасков, откуда будем вытаскивать
                //startTime, endTime и duration для Эпика

                if (Status.DONE.equals(subtask.getStatus())) {
                    doneCount++;
                }
                if (Status.NEW.equals(subtask.getStatus())) {
                    newStatusCount++;
                }
            }
            // если все сабтаски == done, тогда статус Эпика = done
            // если все сабтаски == new или лист пустой, тогда статус эпика = new.
            // иначе - статус ин_прогресс
            if (doneCount == epicSubtasksList.size()) {
                this.status = Status.DONE;
            } else if (newStatusCount == epicSubtasksList.size() || epicSubtasksList.size() == 0) {
                this.status = Status.NEW;
            } else {
                this.status = Status.IN_PROGRESS;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return (Objects.equals(this.name, epic.getName()) && Objects.equals(this.description, epic.getDescription())
                && Objects.equals(this.status, epic.getStatus()) && Objects.equals(this.id, epic.getId()));
    }

    @Override
    public String toString() {
        return "Epic = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'' +
                " startTime = '" + this.getStartTime() + '\'' +
                " duration = '" + this.getDuration() + '\'' + "\n";
    }
}