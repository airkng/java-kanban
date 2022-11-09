package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    //Александр, жду с нетерпением приглашения к вам на работу по окончании практикума ^_^
    //Особые недостатки: душнила
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
        int doneCount = 0;
        int progressCount = 0;
        for (Integer id : epicSubtasksList) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStatus().equals(inProgressStatus)) {
                progressCount++;
            }
            if (subtask.getStatus().equals(doneStatus)){
                doneCount++;
            }
        }
        if (doneCount == epicSubtasksList.size()){
            this.status = doneStatus;
        }
        else if (progressCount >= 1){
            this.status = inProgressStatus;
        }
        else {
            this.status = newStatus;
        }
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
    //Упс, забыл поправить
    public String toString() {
        return "Epic = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'';
    }
}