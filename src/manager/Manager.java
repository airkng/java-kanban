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

    @Override
    public ArrayList<Task> getTasksList() {
        //Спасибо за совет, очень лаконично красиво мило
        return new ArrayList<>(tasks.values());
    }
    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
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
            int key = subtask.getEpicID();
            if (epics.containsKey(key)) {
                Epic epic = epics.get(key);
                epic.linkSubtask(subtask);
                epic.checkAndUpdateStatus(subtasks);

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
    //Воообще, задумка была следующая. При добавлении, удалении, либо при апдейте сабтаска либо при апдейте эпика, мы сразу
    //вычисляем у него статус с помощью одного из метода
    // и checkAndUpdateStatus
    //Ну просто это же логично, как по-другому то)) ТОгда это не приложение получится, а фигня какая-то забагованная
    //Тз для меня очень непонятные, как будто бы говорят сделать что-то мега масштабное,а на деле... пару простых методов написать. меее :(

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId()); //Достаем старый сабтаск
            Epic epic = epics.get(oldSubtask.getEpicID()); //Достаем epic, в котором был этот сабтаск
            epic.removeSubtask(oldSubtask.getId()); //убираем из листа в Epic этот сабтаск
            epic.linkSubtask(subtask); //связываем текущий сабтаск с эпиком
            epic.checkAndUpdateStatus(subtasks); //проверяем и меняем статус Epic если надо
            //убрал эту строчку, согласен с тобой. Забыл ее убрать до проверки
            subtasks.put(subtask.getId(), subtask); //добавляем новый
        } else {
            System.out.println("subtask не найден в списке HashMap");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            Epic oldEpic = epics.get(epic.getId());
            ArrayList<Integer> oldList = oldEpic.getEpicSubtasksList();
            for (Integer id : oldList) {
                epic.addSubtask(id);
            }
            epic.checkAndUpdateStatus(subtasks);
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
            Epic epic = epics.get(deletedSubtask.getEpicID());
            epic.removeSubtask(deletedSubtask.getId());
            epic.checkAndUpdateStatus(subtasks); // проверяем статус Эпика
            subtasks.remove(id);
        } else {
            System.out.println("Ключа subtask " + id + " не существует");
        }
    }
    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksID = epics.get(id).getEpicSubtasksList();
            for (Integer key : subtasksID) {
                subtasks.remove(key);
            }
            epics.remove(id);
        } else {
            System.out.println("Ключа epic " + id + " не существует");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Integer id) {

        //Вообще, я прекрасно понимаю, зачем нужно сеттеры ставить)) просто специально для багов оставил
        // чтобы как раньше в Гта или Симс 3 коды вводить и вуаля! мы с бабками, оружием, телочками крутые дяди
        if(epics.containsKey(id)) {
            ArrayList<Integer> subtasksID = epics.get(id).getEpicSubtasksList();
            ArrayList<Subtask> subtasksList = new ArrayList<>();
            for (Integer key : subtasksID) {
                subtasksList.add(subtasks.get(key));
            }
            return subtasksList;
        }
        else {
            return null;
        }
    }
}
