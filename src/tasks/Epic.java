package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Epic extends Task {
    //Александр, жду с нетерпением приглашения к вам на работу по окончании практикума ^_^
    //Особые недостатки: душнила
    private static int epicId = 100_000;

    private ArrayList<Integer> epicSubtasksList = new ArrayList<>();

    public ArrayList<Integer> getEpicSubtasksList() {
        return epicSubtasksList;
    }

    //А ты реально в проектах пользуешься этим слаком? По-моему в телеграме было бы проще работать
    public Epic(String name, String description) {
        super(name, description, newStatus, epicId);
        epicId++;
        System.out.println("Работу начинает конструктор Epic");
    }
    @Override
    public void setStatus(String status) {
        System.out.println("О-оу, господин. Такое делать запрещено");
    }
    @Override
    public void setId(Integer id) {
        //хотел консольку подключить, чтобы оставлять за пользователем выбор, но вспоминл, шо низя консоль использовать))
        System.out.println("Изменение id эпика приведет к потере взаимосвязи сабтасков и эпиков.");
        this.id = id;
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

    //ЫЫЫЫЫЫЫЫЫЫЫЫЫЫЫЫЫЫЫЫ
    public void checkAndUpdateStatus(HashMap<Integer, Subtask> subtasks) {
        int doneCount = 0;
        int newStatusCount = 0;

        for (Integer id : epicSubtasksList) {
            Subtask subtask = subtasks.get(id);
           if (subtask.getStatus().equals(doneStatus)){
                doneCount++;
           }
           if (subtask.getStatus().equals(newStatus)){
                newStatusCount++;
           }
        }
        if (doneCount == epicSubtasksList.size()){ //значит, если все сабтаски == done, тогда статус Эпика = done
            this.status = doneStatus;
        } else if (newStatusCount == epicSubtasksList.size() || epicSubtasksList.size() == 0){
            //если все сабтаски == new или лист пустой, тогда статус эпика = new.
            this.status = newStatus;
        } else { //иначе - статус ин_прогресс
            this.status = inProgressStatus;
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
    public String toString() {
        return "Epic = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'';
    }
}