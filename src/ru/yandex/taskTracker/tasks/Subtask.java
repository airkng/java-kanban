package ru.yandex.taskTracker.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicID; // переменная, которая привязывает SubTask к Epic

    public Subtask(String name, String description, Status status, int epicID, long duration, LocalDateTime startTime ) {
        super(name, description, status, duration ,startTime);
        this.epicID = epicID;
    }

    //Конструктор для метода .update()
    public Subtask(String name, String description, Status status, int id, int epicID, long duration, LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, int epicID ) {
        super(name, description, status);
        this.epicID = epicID;
    }

    //Конструктор для метода .update()
    public Subtask(String name, String description, Status status, int id, int epicID) {
        super(name, description, status, id);
        this.epicID = epicID;
    }
    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        System.out.println("Внимание!!! Attention!!! Этот метод создан только по просьбе Александра Валюка. НИ ПРИ КАКИХ ОБСТОЯТЕЛЬСТВАХ \n" +
                "НЕ ТРОГАТЬ!!! DON'T TOUCH!!!!");
        this.epicID = epicID;
    }

    @Override
    //Отличие от equals из таска дополнительной провреркой поля epicID
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask) obj;
        return (Objects.equals(this.name, subtask.getName()) &&                 //имя
                Objects.equals(this.description, subtask.getDescription()) &&   //описание
                (this.status == subtask.getStatus()) &&                         //статус
                Objects.equals(epicID, subtask.getEpicID()));                   //epicID
    }

    @Override
    public int hashCode() {
        int hash = (description.hashCode() * 29 + name.hashCode()) * 31;
        hash = (status.hashCode() + hash - epicID);
        return hash;
    }

    @Override
    public String toString() {
        return "Subtask = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'' +
                " epicId = '" + this.epicID + '\'';
    }
}
