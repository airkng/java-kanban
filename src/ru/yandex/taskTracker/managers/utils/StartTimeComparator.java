package ru.yandex.taskTracker.managers.utils;

import ru.yandex.taskTracker.tasks.Task;

import java.util.Comparator;

public class StartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartTime() == null) {
            return 1;
        } else if(o2.getStartTime() == null){
            return -1;
        } else {
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
