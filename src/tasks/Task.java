package tasks;

import java.util.Objects;

public class Task {
    protected static final String newStatus = "NEW";
    protected static final String inProgressStatus = "IN_PROGRESS";
    protected static final String doneStatus = "DONE";
    protected String name;
    protected String description;
    protected String status;
    protected int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task(String name, String description, String status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }
  @Override
    public String toString(){
        return "Task = {\n name = '" + this.name + '\'' +
        "\n description = ' " + this.description + '\'' +
        "\n status = ' " + this.status + '\'' +
        "\n id = ' " + this.id + '\'';
  }
  @Override
    public boolean equals(Object obj){
        if (obj == null || this == null){
            return false;
        }
        if(!(obj instanceof Task)){
            return false;
        }
        Task task = (Task) obj;
        return (Objects.equals(this.name, task.getName()) && Objects.equals(this.description, task.getDescription())
        && Objects.equals(this.status, task.getStatus()) && Objects.equals(this.id, task.getId()));
  }
  @Override
    public int hashCode(){
        int hash = 29;
        hash = hash * this.id;
        hash += description.hashCode() + 31 * name.hashCode();
        hash = (status.hashCode() + hash);
        return hash;
  }
}
