package ru.yandex.taskTracker.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    private static int epicId = 100_000;
    private final ArrayList<Integer> epicSubtasksList = new ArrayList<>();      //Лист айдишек сабтасков

    public Epic(String name, String description) {
        super(name, description, Status.NEW, epicId);
        epicId++;
        System.out.println("Работу начинает конструктор Epic");
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

    public void removeSubtask(Integer id){
        epicSubtasksList.remove(id);
    }

    public void addSubtask(Integer id){
        epicSubtasksList.add(id);
    }
    //В принципе, тут можно перегрузить метод addSubtask, могу сделать
    public void linkSubtask(Subtask subtask) {
        epicSubtasksList.add(subtask.getId());
    }

    public void checkAndUpdateStatus(HashMap<Integer, Subtask> subtasks) {
        int doneCount = 0;
        int newStatusCount = 0;

        for (Integer id : epicSubtasksList) {
            Subtask subtask = subtasks.get(id);
            if (Status.DONE.equals(subtask.getStatus())){
                doneCount++;
            }
            if (Status.NEW.equals(subtask.getStatus())){
                newStatusCount++;
           }
        }
        // если все сабтаски == done, тогда статус Эпика = done
        // если все сабтаски == new или лист пустой, тогда статус эпика = new.
        // иначе - статус ин_прогресс
        if (doneCount == epicSubtasksList.size()){
            this.status = Status.DONE;
        } else if (newStatusCount == epicSubtasksList.size() || epicSubtasksList.size() == 0){
            this.status = Status.NEW;
        } else {
            this.status = Status.IN_PROGRESS;
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