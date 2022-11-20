package ru.yandex.taskTracker.tasks;

public enum Status {
    //ПРОЕКТ СЫРОЙ, МОЖЕШЬ ДАЖЕ НЕ СМОТРЕТЬ ПОКА
    NEW,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
