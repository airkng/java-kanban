package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    private static int epicId = 100_000;

    private ArrayList<Integer> epicSubtasksList = new ArrayList<>();

    public ArrayList<Integer> getEpicSubtasksList() {
        return epicSubtasksList;
    }


    public Epic(String name, String description) {
        super(name, description, newStatus, epicId);
        epicId++;
        System.out.println("Работу начинает конструктор Epic");
    }

    public Epic(String name, String description, int oldEpicID) {
        super(name, description, "NEW", oldEpicID);
    }

    public void removeSubtask(Integer id){
        epicSubtasksList.remove(id);
    }

    public void addSubtask(Integer id){
        epicSubtasksList.add(id);
    }
    public void linkSubtask(Subtask subtask) {
        epicSubtasksList.add(subtask.getId());
    }
    //Эххх, а я старался

    public void checkAndUpdateStatus(HashMap<Integer, Subtask> subtasks) {
        for (Integer id : epicSubtasksList) {
            Subtask subtask = subtasks.get(id);
            //если в листе сабтасков есть хоть один элемент IN_PROGRESS или DONE, тогда меняем статус у эпика на
            // IN_PROGRESS
            if (subtask.getStatus().equals(inProgressStatus) || subtask.getStatus().equals(doneStatus)) {
                this.status = inProgressStatus;
            }
        }
        //Если хоть один элемент не Done, то статус Эпика остается таким же как был и прерываем цикл
        String status = doneStatus;
        for (Integer id : epicSubtasksList) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStatus().equals(doneStatus)) {
            } else {
                status = this.status;
                break;
            }
        }
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return (Objects.equals(this.name, epic.getName()) && Objects.equals(this.description, epic.getDescription())
                && Objects.equals(this.status, epic.getStatus()) && Objects.equals(this.id, epic.getId()));
    }

    @Override
    public String toString() {
        return "Epic = {\n name = '" + this.name + '\'' +
                "\n description = ' " + this.description + '\'' +
                "\n status = '" + this.status + '\'' +
                "\n id = '" + this.id + '\'';
    }
}