package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager implements IManager {
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    public ArrayList<Task> getTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            tasksList.add(tasks.get(id)); //достаем из мапы и добавляем в новый лист экземпляр Task
        }
        return tasksList;
    }

    public ArrayList<Subtask> getSubtasksList() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            subtaskList.add(subtasks.get(id));  //аналогично методу выше
        }
        return subtaskList;
    }

    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            epicList.add(epics.get(id));
        }
        return epicList;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
    }

    @Override
    public void deleteEpic() {
        epics.clear();
    }

    @Override
    public Task getTask(Integer id) {
        return tasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(Integer id) {
        return subtasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpic(Integer id) {
        return epics.getOrDefault(id, null);
    }

    @Override
    public int addTask(Task task) {
        //Два одинаковых таска добавить не получится. Даже, если у них разные ID
        if (tasks.containsValue(task)) {
            System.out.println("Таск уже добавлен. Воспользуйтесь командой .update(Task task)\n");
            return -1;
        } else {
            System.out.println("Таск добавлен, его параметры: " + task.toString());
            tasks.put(task.getId(), task);
            return task.getId();
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            System.out.println("Сабтаск уже добавлен. Воспользуйтесь командой .update(Subtask subtask)");
            return -1;
        } else {
            int key = subtask.getLinkID();
            if (epics.containsKey(key)) {
                Epic epic = epics.get(key);
                epic.linkSubtask(subtask);
                subtasks.put(subtask.getId(), subtask);
                epics.put(epic.getId(), epic);
                return subtask.getId();
            } else {
                System.out.println("Ошибка. Сначала создайте Epic с данным id");
                return -1;
            }
        }
    }

    @Override
    public int addEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            System.out.println("Epic уже добавлен. Воспользуйтесь командой .update(Epic epic)");
            return -1;
        } else {
            System.out.println("Epic добавлен, его параметры: " + epic.toString());
            epics.put(epic.getId(), epic);
            return epic.getId();
        }
    }

    //"Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра." - ТЗ от яндекса
    //То есть объект по идее должен существовать, так как мы его апдейтим. Следовательно, .equals() у таска НЕ сверяет
    //айдишники
    @Override
    public void updateTask(Task task) {
        //В таске должен быть айди старого таска, который мы заменяем на новый
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("task не найден в списке HashMap");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId()); //Достаем старый сабтаск
            Epic epic = epics.get(oldSubtask.getLinkID()); //Достаем epic, в котором был этот сабтаск
            epic.epicSubtasksList.remove(oldSubtask); //убираем из листа в Epic этот сабтаск
            epic.linkSubtask(subtask); //связываем текущий сабтаск с эпиком

            subtasks.remove(oldSubtask.getId()); //убираем его из мапы
            subtasks.put(subtask.getId(), subtask); //добавляем новый
        } else {
            System.out.println("subtask не найден в списке HashMap");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        //убираю при обновлении сабтаски старого Epic, добавляю в мапу новый эпик
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            ArrayList<Subtask> oldList = oldEpic.epicSubtasksList;
            for (Subtask subtask : oldList) {
                subtasks.remove(subtask.getId());
            }
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("epic не найден в списке HashMap");
        }
    }

    public void updateAndSaveOldInfoEpic(Epic epic) {
        //Это метод с сохранением сабтасков старого эпика, т.к снова не понял, шо от меня хотят
        if (epics.containsValue(epic)) {
            Epic oldEpic = epics.get(epic.getId());
            ArrayList<Subtask> oldList = oldEpic.epicSubtasksList;
            for (Subtask subtask : oldList) {
                epic.epicSubtasksList.add(subtask);
                epic.checkAndUpdateStatus();
            }
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("epic не найден в списке HashMap");
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Ключа tasks " + id + " не существует");
        }
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask deletedSubtask = subtasks.get(id);
            Epic epic = epics.get(deletedSubtask.getLinkID());
            epic.epicSubtasksList.remove(deletedSubtask);
            epic.checkAndUpdateStatus();
            subtasks.remove(id);
        } else {
            System.out.println("Ключа subtask " + id + " не существует");
        }
    }
    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            ArrayList<Subtask> oldList = epics.get(id).epicSubtasksList;
            for (Subtask subtask : oldList) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        } else {
            System.out.println("Ключа epic " + id + " не существует");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Integer id) {
        return epics.getOrDefault(id, null).epicSubtasksList;
    }
}
