package tasks;

import java.util.Objects;

public class Task {

    private static int count;

    protected static final String newStatus = "NEW";
    protected static final String inProgressStatus = "IN_PROGRESS";
    protected static final String doneStatus = "DONE";

    //Enum не проходил, на каникулах разберу. Пока боюсь в дедлайн жесткого спринта не уложиться
    //Итак эту неделю про*бал читая документацию по Git. Сейчас хочу нормально шарить за гит.
    //А я че то подумал, зачем сеттеры нужны тут, ахаха. Из-за них багов столько потенциально может появиться как будто бы
    protected String name;
    protected String description;
    protected String status;
    protected Integer id;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //Я как будто бы вижу в этом много потенциальных ошибок. А вытащить объект и пересоздать новый
    //да, сложно неудобно, но как будто бы потенциально меньше багов возникает. А тут допустим, дергаем у эпика метод,
    //меняем его айдишку. И все собственно. Сабтаски указывают на несуществующий старый айди эпика и о новом ничего не знают
    // Надо подумать над этим, как 4 спринт пройду, отпишусь с мыслями по этому поводу в слаке

    public void setStatus(String status) {
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

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public Task(String name, String description, String status, int id) {
        if ((status.equals(newStatus) || status.equals(inProgressStatus) || status.equals(doneStatus))
                && name.length() < 80) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = id;
        } else {
            System.out.println("Неверные значения при создании объекта");
        }
    }

    public Task(String name, String description, String status) {
        if ((status.equals(newStatus) || status.equals(inProgressStatus) || status.equals(doneStatus))
                && name.length() < 80) {
            count++;
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = generateID(name, description, status);
        } else {
            System.out.println("Неверные значения при создании объекта");
        }

    }

    protected static int generateID(String name, String description, String status) {
        int hash = Objects.hash(name, description, status);
        hash += count;
        return hash;
    }

    @Override
    //Лооол, я точно помню, что их убирал, чеее за херня
    public String toString() {
        return "Task = {name = '" + this.name + '\'' +
                " description = ' " + this.description + '\'' +
                " status = '" + this.status + '\'' +
                " id = '" + this.id + '\'' + "\n"; // вот тут так и не понял, надо убрать этот перенос или нет
    }
    //хорошо, буду знать в следующий раз
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return (Objects.equals(this.name, task.getName()) &&
                Objects.equals(this.description, task.getDescription()) &&
                Objects.equals(this.status, task.getStatus()));
    }

    @Override
    public int hashCode() {
        int hash = (description.hashCode() * 29 + name.hashCode()) * 31;
        hash = (status.hashCode() + hash + count);
        return hash;
    }
}
