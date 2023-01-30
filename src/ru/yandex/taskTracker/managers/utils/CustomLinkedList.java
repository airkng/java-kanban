package ru.yandex.taskTracker.managers.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomLinkedList<T> {

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
        }
        //удалили пояснение, что в случае если ключа не существует, в консоль это не выводится
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

    //ДЖАВАСКРИПТИЗЕРЫ АХАХАХХАХА, Александр, у вас потрясные шутки!
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