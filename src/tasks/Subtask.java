package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int linkID; // переменная, которая привязывает SubTask к Epic


    public int getLinkID() {
        return linkID;
    }


    public Subtask(String name, String description, String status, int epicID) {
        super(name, description, status);
        linkID = epicID;
    }

    //Конструктор для метода .update()
    public Subtask(String name, String description, String status, int id, int epicID) {
        super(name, description, status, id);
        linkID = epicID;
    }


    //Отличие от equals из таска дополнительной провреркой поля linkID
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this == null) {
            return false;
        }
        if (!(obj instanceof Subtask)) {
            return false;
        }
        Subtask subtask = (Subtask) obj;
        return (Objects.equals(this.name, subtask.getName()) && Objects.equals(this.description, subtask.getDescription())
                && Objects.equals(this.status, subtask.getStatus()) && Objects.equals(linkID, subtask.getLinkID()));
    }

    @Override
    public String toString() {
        return "Subtask = {\n name = '" + this.name + '\'' +
                "\n description = ' " + this.description + '\'' +
                "\n status = '" + this.status + '\'' +
                "\n id = '" + this.id + '\'' +
                "\n linkId = '" + this.linkID + '\'' + "\n";
    }

    @Override
    public int hashCode() {
        int hash = 29;
        hash = (description.hashCode() * 29 + name.hashCode()) * 31;
        hash = (status.hashCode() + hash - linkID);
        return hash;
    }
}
