package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private static int epicId = 100_000;

    public ArrayList<Subtask> epicSubtasksList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, newStatus, epicId);
        epicId++;
        System.out.println("Работу начинает конструктор Epic");
    }

    public Epic(String name, String description, int oldEpicID) {
        super(name, description, "NEW", oldEpicID);
    }

    public void linkSubtask(Subtask subtask) {
        epicSubtasksList.add(subtask);
        this.checkAndUpdateStatus();
    }

    public void checkAndUpdateStatus() {

        for (Subtask subtask : epicSubtasksList) {
            if (subtask.getStatus().equals(inProgressStatus) || subtask.getStatus().equals(doneStatus)) {
                this.status = inProgressStatus;
            }
        }
        String status = doneStatus;
        for (Subtask subtask : epicSubtasksList) {
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
        if (obj == null || this == null) {
            return false;
        }
        if (!(obj instanceof Epic)) {
            return false;
        }
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