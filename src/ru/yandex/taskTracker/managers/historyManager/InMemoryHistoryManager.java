package ru.yandex.taskTracker.managers.historyManager;

import ru.yandex.taskTracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    //ОЧЕНЬ РАД ЗА ТЕБЯ!!! Красавчик! Горы и море - это классно, смотри не заболей, раз море холодное
    //Заранее с внж не буду позравлять, шоб не сглазить как говорится
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

// Вообще изначально сделал вложенный класс, потом думаю, а нафиг надо, вынес его в отдельный
// не ставил модификаторы доступа, т.к. подумал, что КастомЛист используется только в этом пакете и на кой черт ему модификатор
// public, пусть лучше побудет package-private.
// Если надо..могу перенести в отдельный файл, просто отталкивался от места, где он используется. А используется он только
// тут, поэтому чтобы разрабу не лазить от одного файла к другому. В общем, как скажешь
class CustomLinkedList<T> {

    private final HashMap<Integer, Node<T>> historyMap = new HashMap<>();
    private Node<T> head = null;
    private Node<T> tail = null;
    int size = 0;

    public ArrayList<T> getTaskList() {

        ArrayList<T> historyList = new ArrayList<>();
        Node<T> currentNode = head;
        while (currentNode != null) {
            historyList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return historyList;
    }
    //Я ТАК ОРАЛ С ТОГО, ЧТО ГЛАВНАЯ ПРОБЛЕМА СИДИТ ПЕРЕД МОНИТОРОМ
    //ПРОСТО АХАХАХ

    public void addElement(T element, Integer id) {
        if (historyMap.containsKey(id)) {
            removeElement(id);
        }
        link(element, id);
    }

    public void removeElement(int id) {
        if (historyMap.containsKey(id)) {
            unlink(historyMap.get(id));
            historyMap.remove(id);
            size--;
        } else {
            System.out.println("В истории не обнаружено ключа " + id);
            return;
        }
    }

    private void link(T element, Integer id) {
        Node<T> node = new Node<>(element);
        if (head == null && tail == null) {
            head = node;
            tail = node;
            historyMap.put(id, node);
        } else {
            linkLast(element, id);
        }
        size++;

    }

    private void linkLast(T element, Integer id) {
        Node<T> newTail = new Node<>(element);
        Node<T> oldTail = tail;
        oldTail.next = newTail;
        newTail.prev = oldTail;
        tail = newTail;
        historyMap.put(id, newTail);
    }

    private void unlink(Node<T> nodeToRemove) {
        final Node<T> prev = nodeToRemove.prev;
        final Node<T> next = nodeToRemove.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            nodeToRemove.prev = null;
            // А-нет)) тут бы с тобой поспорили разрабы LinkedList, т.к я свято спер оттуда этот метод
            // + все же я считаю, что это правильно, т.к GC явно понимает, что нужно удалить объект
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            nodeToRemove.next = null;
        }

        nodeToRemove.data = null;
    }

    // .net - это педики, которые сперли все с java, потом их прижали и они сделали свое говно
    private static class Node<E> {
        private E data;
        private Node<E> prev;
        private Node<E> next;

        public Node(E data) {
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

