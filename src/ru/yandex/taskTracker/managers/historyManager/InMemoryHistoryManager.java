package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    @Override
    public void addHistory(Task task) {
        customLinkedList.addElement(task, task.getId());
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTaskList();
    }

    @Override
    public void remove(int id) {
        customLinkedList.removeElement(id);
    }


}

/*
            ⣿⣿⣿⣿⡟⠛⠁⠄⠄⠄⠄⢀⣀⣀⠄⠄⠄⠄⣤⣽⣿⣿⣿⣿⣿⣿⣿⣿
            ⣿⣿⣿⡋⠁⠄⠄⠄⣠⣶⣾⣿⣿⣿⣿⠄⢦⡄⠐⠬⠛⢿⣿⣿⣿⣿⣿⣿
            ⣿⡿⠇⠁⠄⠄⣠⣾⣿⣿⡿⠟⠋⠁⠄⠄⠈⠁⠄⠄⠄⠄⠙⢿⣿⣿⣿⣿
            ⣿⠃⠄⠄⠄⠘⣿⣿⣿⣿⢀⣠⠄⠄⠄⠄⣰⣶⣀⠄⠄⠄⠄⠸⣿⣿⣿⣿
            ⣏⠄⠄⠄⠄⠄⣿⣿⣿⡿⢟⣁⠄⣀⣠⣴⣿⣿⠿⠷⠶⠒⠄⠄⢹⣿⣿⣿
            ⡏⠄⠄⠄⠄⢰⣿⣿⣿⣿⣿⣿⣿⣿⡟⠄⠛⠁⠄⠄⠄⠄⠄⠄⢠⣿⣿⣿
            ⡇⠄⠄⠄⠄⠈⢿⣿⣿⣿⣿⣿⣿⣿⡇⠄⣼⣿⠇⠘⠄⠁⠄⠄⠄⢻⣿⣿
            ⣇⠄⠄⠄⠄⠄⠸⢿⣿⣿⣿⣿⣿⣿⠁⠸⠟⠁⣠⣤⣤⣶⣤⠄⠄⠄⢻⣿
            ⣿⡄⠄⡤⢤⣤⡀⠈⣿⣿⣿⣿⣿⣿⡆⠄⠄⠘⠋⠁⠄⠄⠈⠄⠄⠄⢸⣿
            ⣿⣿⡜⢰⡾⢻⣧⣰⣿⣿⣿⣿⣿⣿⣷⠄⣼⣷⣶⣶⡆⠄⠄⠄⠄⠄⠄⣿
            ⣿⣿⣧⢸⠄⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣿⣿⣿⣿⠄⠄⠄⠄⠄⠄⠄⣿
            ⣿⣿⣿⣿⡿⢿⡟⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⠄⠄⢀⡀⠄⠘⣿
            ⣿⣿⣿⣿⣿⣆⢻⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⠋⠄⠄⠈⠁⠄⠄⣿
            ⣿⣿⣿⣿⣿⣿⡆⢻⣿⣿⣿⣿⣿⣿⡿⠛⠛⠛⠃⠄⠄⠄⠄⠄⠄⠄⢀⣿
            ⣿⣿⣿⣿⣿⣿⣿⣆⣻⣿⣿⣿⣿⣿⣷⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⢸⣿
            */
//В память о Билли Херрингтоне, величайшем человеке которого когда-либо видел мир

