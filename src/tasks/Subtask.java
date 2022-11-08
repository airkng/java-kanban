package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicID; // переменная, которая привязывает SubTask к Epic

    //Вот зачем тебе сеттеры, от эпика?) Сломать логику хочешь?)) Создай ты новый объект, ничего страшного)))
    //Окей, я даю тебе сеттер -_-
    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public Subtask(String name, String description, String status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    //Конструктор для метода .update()
    public Subtask(String name, String description, String status, int id, int epicID) {
        super(name, description, status, id);
        this.epicID = epicID;
    }


    //Отличие от equals из таска дополнительной провреркой поля linkID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask) obj;
        return (Objects.equals(this.name, subtask.getName()) &&
                Objects.equals(this.description, subtask.getDescription()) &&
                Objects.equals(this.status, subtask.getStatus()) &&
                Objects.equals(epicID, subtask.getEpicID()));
    }
    @Override
    public int hashCode() {
        int hash = 29;
        hash = (description.hashCode() * 29 + name.hashCode()) * 31;
        hash = (status.hashCode() + hash - epicID);
        return hash;
    }
    @Override
    public String toString() {
        return "Subtask = {\n name = '" + this.name + '\'' +
                "\n description = ' " + this.description + '\'' +
                "\n status = '" + this.status + '\'' +
                "\n id = '" + this.id + '\'' +
                "\n linkId = '" + this.epicID + '\'' + "\n";
    }
}
