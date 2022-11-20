package ru.yandex.taskTracker.tasks;

import java.util.Objects;

public class Task {

    private static int count;

    protected String name;
    protected String description;
    protected Status status;
    protected Integer id;

    public Task(String name, String description, Status status, int id) {
        if (name.length() < 80) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = id;
        } else {
            System.out.println("Неверные значения при создании объекта");
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

    public int getId() {
        return id;
    }

    protected static int generateID(String name, String description, Status status) {
        int hash = Objects.hash(name, description, status); //Корректно ли сюда засовывать Enum?
        hash += count;
        return hash;
    }

    @Override
    public String toString() {
        return "Task = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return  Objects.equals(this.name, task.getName()) &&    //проверка имен
                Objects.equals(this.description, task.getDescription()) && // проверка описания
                (this.status == task.getStatus()); //проверка статуса.
        // Хз как лучше проверить, через equals, == или Objects.equals
        //Думаю, все-таки через Objects.equals() было бы получше, из-за читабельности
    }

    @Override
    public int hashCode() {
        int hash = (description.hashCode() * 29 + name.hashCode()) * 31;
        hash = (status.hashCode() + hash + count);
        return hash;
    }
    //Старый коммит, оставил себе на заметку
    //Я как будто бы вижу в этом много потенциальных ошибок. А вытащить объект и пересоздать новый
    //да, сложно неудобно, но как будто бы потенциально меньше багов возникает. А тут допустим, дергаем у эпика метод,
    //меняем его айдишку. И все собственно. Сабтаски указывают на несуществующий старый айди эпика и о новом ничего не знают
    // Надо подумать над этим, как 4 спринт пройду, отпишусь с мыслями по этому поводу в слаке
}
