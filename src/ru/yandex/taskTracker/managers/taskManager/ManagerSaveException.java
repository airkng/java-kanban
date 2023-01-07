package ru.yandex.taskTracker.managers.taskManager;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(){
        super("Неизвестная ошибка при создании файла");
    }
    public ManagerSaveException (String message){
        super(message);
    }

}
