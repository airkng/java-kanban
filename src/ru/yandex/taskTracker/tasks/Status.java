package ru.yandex.taskTracker.tasks;

public enum Status {
    //ПРОЕКТ СЫРОЙ, Можешь в принципе глянуть, я вроде только недопонял про Manager, мне надо дженерик создать что ли
    NEW,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
