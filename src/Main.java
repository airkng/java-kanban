import manager.Manager;
import tasks.Task;

public class Main {
    //Счетчик проведенных дней: 1.5

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task autoTask = new Task("Auto", "Clear auto", "NEW");
        Task autoTaskD = new Task("Auto", "Clear auto", "DONE");
        Task autoTas = new Task("Auto", "Clear auto", "NEW");
        Task business = new Task("Business", "investing in Binance", "NEW");

      int id1 = manager.addTask(autoTask);
      int id2 = manager.addTask(autoTaskD); // add
        autoTask.setId(1234);
    }
}
