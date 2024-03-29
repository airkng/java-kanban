package ru.yandex.taskTracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private static int count;

    protected String name;
    protected String description;
    protected Status status;
    protected Integer id;

    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status, int id, long duration, LocalDateTime startTime) {
        if (name.length() < 80) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = id;
            this.duration = Duration.ofMinutes(duration);
            this.startTime = startTime;

        } else {
            System.out.println("Неверные значения при создании объекта");
            throw new IllegalArgumentException("Неверные значения при создании объекта Task");
        }
    }

    public Task(String name, String description, Status status, long duration, LocalDateTime startTime) {
        if (name.length() < 80 ) {
            count++;
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = generateID(name, description, status);
            this.duration = Duration.ofMinutes(duration);
            this.startTime = startTime;
        } else {
            System.out.println("Неверные значения при создании объекта");
            throw new IllegalArgumentException("Неверные значения при создании объекта Task");
        }

    }

    public Task(String name, String description, Status status, int id) {
        if (name.length() < 80) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = id;
        } else {
            System.out.println("Неверные значения при создании объекта");
            throw new IllegalArgumentException("Неверные значения при создании объекта Task");
        }
    }

    public Task(String name, String description, Status status) {
        if (name.length() < 80) {
            count++;
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = generateID(name, description, status);
        } else {
            System.out.println("Неверные значения при создании объекта");
            throw new IllegalArgumentException("Неверные значения при создании объекта Task");
        }

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public long getDuration() {
        if (duration == null){
            return 0;
        }
        return duration.toMinutes();
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (this.duration.toMinutes() != 0 && this.startTime != null) {
            return startTime.plus(duration);
        } else {
           return null;
        }
    }

    protected static int generateID(String name, String description, Status status) {
        int hash = Objects.hash(name, description, status);
        hash += count;
        return hash;
    }

    @Override
    public String toString() {
        return "Task = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'' +
                " startTime = '" + this.getStartTime() + '\'' +
                " duration = '" + this.getDuration() + '\'' + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(this.name, task.getName()) &&               //проверка имен
                Objects.equals(this.description, task.getDescription()) && // проверка описания
                (this.status == task.getStatus());                         //проверка статуса.
    }

    @Override
    public int hashCode() {
        int hash = (description.hashCode() * 29 + name.hashCode()) * 31;
        hash = (status.hashCode() + hash + count);
        return hash;
    }
}
