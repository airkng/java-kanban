package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // Сидишь такой, попиваешь сок, купаешься на пляже, катаешься на байке...
    // А потом просыпаешься и понимаешь, что ты не Александр Валюк
    // Как дела? Что нового? Фотки в инсте топ!
    private final CustomLinkedList customLinkedList = new CustomLinkedList();

    @Override
    public void addHistory(Task task) {
        customLinkedList.addElement(task);
        //Код с прошлого ТЗ
        //if (historyList.size() == 10) {
        //    historyList.remove(0);
        //}
        //historyList.add(task);
    }

    @Override
    public List<Task> getHistory(){
        return customLinkedList.getTaskList();
    }

    @Override
    public void remove(int id) {
        customLinkedList.removeElement(id);
    }


}
 class CustomLinkedList {
    private final HashMap<Integer, CustomLinkedList.Node> historyMap = new HashMap<>();
    private Node head = null;
    private Node tail = null;
    int size = 0;

    public ArrayList<Task> getTaskList(){
        ArrayList<Task> historyList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null){
            historyList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return historyList;
    }

     void addElement(Task task){
         if (historyMap.containsKey(task.getId())){
             removeElement(task.getId());
         }
         link(task);
     }

     void removeElement(int id){
        if(historyMap.containsKey(id)) {
            unlink(historyMap.get(id));
            historyMap.remove(id);
            size--;
        } else {
            System.out.println("В истории не обнаружено ключа " + id);
            return;
        }
     }

    private void link(Task task){
        Node node = new Node(task);
        if (head == null && tail == null){
            head = node;
            tail = node;
            historyMap.put(task.getId(), node);
        } else {
            linkLast(task);
        }
        size++;

    }

    private void linkLast(Task task){
         Node newTail = new Node(task);
         Node oldTail = tail;
         oldTail.next = newTail;
         newTail.prev = oldTail;
         tail = newTail;
         historyMap.put(task.getId(), newTail);
    }

    private void unlink(Node nodeToRemove){
        final Node prev = nodeToRemove.prev;
        final Node next = nodeToRemove.next;

        if (prev == null){
            head = next;
        } else {
            prev.next = next;
            nodeToRemove.prev = null;
        }

        if (next == null){
            tail = prev;
        } else {
            next.prev = prev;
            nodeToRemove.next = null;
        }

        nodeToRemove.data = null;
    }

    private class Node {
        private Task data;
        private Node prev;
        private Node next;

        public Node(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
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

