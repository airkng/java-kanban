package ru.yandex.taskTracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    private static int epicId = 100_000;
    private final ArrayList<Integer> epicSubtasksList = new ArrayList<>();      //Лист айдишек сабтасков

    private final Comparator<Subtask> endTimeSort = (o1, o2) -> {
        LocalDateTime time1 = o1.getStartTime().plusMinutes(o1.getDuration());
        LocalDateTime time2 = o2.getStartTime().plusMinutes(o2.getDuration());

        if (time1.isAfter(time2)) {
            return 1;
        } else if (time1.isBefore(time2)) {
            return -1;
        } else {
            return 0;
        }
    };
    private final Comparator<Subtask> startTimeSort = (o1, o2) -> {
        if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else {
            return 0;
        }
    };
    private final ArrayList<Subtask> timeSubtasksList = new ArrayList<>();
    //конструктор для fileManager
    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, epicId);
        epicId++;
    }

    public Epic(String name, String description, int oldEpicID) {
        super(name, description, Status.NEW, oldEpicID);
    }

    public ArrayList<Integer> getEpicSubtasksList() {
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
        if (!timeSubtasksList.isEmpty()) {
            timeSubtasksList.sort(endTimeSort);
            Subtask latestSubtask = timeSubtasksList.get((timeSubtasksList.size() - 1));
            return latestSubtask.getEndTime();
        } else {
            System.out.println("В классе Epic отсутствуют time-Subtasks");
            return null;
        }
    }

    @Override
    public LocalDateTime getStartTime() {
        if (!timeSubtasksList.isEmpty()) {
            timeSubtasksList.sort(startTimeSort);
            return timeSubtasksList.get(0).getStartTime();
        } else {
            return null;
        }
    }

    @Override
    public long getDuration() {
        if (!timeSubtasksList.isEmpty()) {
            return Duration.between(getStartTime(), getEndTime()).toMinutes(); //Насколько помню, сделали плюс день
            //так как день не учитывается
        } else {
            return 0;
        }
    }

    public void removeSubtask(Integer id) {
        epicSubtasksList.remove(id);
    }

    public void addSubtask(Integer id) {
        epicSubtasksList.add(id);
    }

    public void linkSubtask(Subtask subtask) {
        epicSubtasksList.add(subtask.getId());
    }

    public void checkAndUpdateStatus(HashMap<Integer, Subtask> subtasks) {
        if (epicSubtasksList.isEmpty()) {
            this.status = Status.NEW;
        } else {
            int doneCount = 0;
            int newStatusCount = 0;

            for (Integer id : epicSubtasksList) {
                Subtask subtask = subtasks.get(id);
                //Если у сабтаска есть time-переменные, то добавляем в лист сабтасков, откуда будем вытаскивать
                //startTime, endTime и duration для Эпика
                if (subtask.getStartTime() != null) {
                    timeSubtasksList.add(subtask);
                    startTime = getStartTime();
                    duration = getDuration();
                }
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
                " id = '" + this.id + '\'';
    }
}