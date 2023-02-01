package ru.yandex.taskTracker.managers.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.tasks.Epic;
import ru.yandex.taskTracker.tasks.Status;
import ru.yandex.taskTracker.tasks.Subtask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private  Epic homeBuild;
    private int homeBuildID;
    private  Subtask s1;
    private  Subtask s2;
    private  Subtask s3;

    @BeforeEach
    public void prepareToTest(){

        homeBuild = new Epic("build a home", "building and buying house");
        homeBuildID = homeBuild.getId();
        s1 = new Subtask("buy fundament",
                "find store and buy fundament",                         //Создали сабтаски, чтобы по сто раз их не создавать
                Status.NEW,
                homeBuildID);

        s2 = new Subtask("Workers",
                "find a worker for building",
                Status.NEW,
                homeBuildID);

        s3 = new Subtask("buy to workers",
                "pay money to workers for done work",
                Status.NEW,
                homeBuildID);
    }

    private void addSubtasksToEpic(){
        homeBuild.addSubtask(s1);
        homeBuild.addSubtask(s2);
        homeBuild.addSubtask(s3);
    }

    private void setStatusToSubtasks(Status fundamentStatus, Status workersStatus, Status buyWorkersStatus){
        s1.setStatus(fundamentStatus);
        s2.setStatus(workersStatus);
        s3.setStatus(buyWorkersStatus);
    }

    @Test
    public void checkEpicStatusWithEmptySubtaskList(){
        homeBuild.checkAndUpdateStatus();
        assertEquals(homeBuild.getStatus(), Status.NEW);
    }

    @Test
    public void checkEpicStatusWithAllNEWSubtasksStatus(){
        setStatusToSubtasks(Status.NEW, Status.NEW, Status.NEW);
        addSubtasksToEpic();
        homeBuild.checkAndUpdateStatus();
        assertEquals(homeBuild.getStatus(), Status.NEW);
    }

    @Test
    public void checkEpicStatusWithAllDONESubtasksStatus(){
       setStatusToSubtasks(Status.DONE, Status.DONE, Status.DONE);
       addSubtasksToEpic();
        homeBuild.checkAndUpdateStatus();
       assertEquals(homeBuild.getStatus(), Status.DONE);
    }

    @Test
    public void checkEpicStatusWithNEWandDONESubtasksStatus(){
        setStatusToSubtasks(Status.NEW, Status.DONE, Status.NEW);
        addSubtasksToEpic();
        homeBuild.checkAndUpdateStatus();
        assertEquals(homeBuild.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void checkEpicStatusWithIN_PROGRESS_SubtaskStatus(){
        setStatusToSubtasks(Status.IN_PROGRESS, Status.IN_PROGRESS, Status.IN_PROGRESS);
        addSubtasksToEpic();
        homeBuild.checkAndUpdateStatus();
        assertEquals(homeBuild.getStatus(), Status.IN_PROGRESS);
    }
    @Test
    public void getStartTime_returnRightValue_afterAddSubtasks(){
        addSubtasksToEpic();
        s1.setStartTime(LocalDateTime.of(2023,1,1,0,0));
        s1.setDuration(15);

        s2.setStartTime(LocalDateTime.of(2023,1,1,1,35));
        s2.setDuration(25);

        assertEquals(LocalDateTime.of(2023,1,1,0,0), homeBuild.getStartTime());
    }
    @Test
    public void getDuration_return40minutesValue_afterAddSubtask(){
        addSubtasksToEpic();
        s3.setStartTime(LocalDateTime.of(2023,1,1,5,25));
        s3.setDuration(10);

        s2.setStartTime(LocalDateTime.of(2023,1,1,0,0));
        s2.setDuration(13);

        s1.setStartTime(LocalDateTime.of(2024, 2, 14, 12, 15 ));
        s1.setDuration(17);

        assertEquals(40, homeBuild.getDuration());
        assertEquals(LocalDateTime.of(2023,1,1,0,0), homeBuild.getStartTime());
        assertEquals(LocalDateTime.of(2024, 2, 14, 12, 32 ), homeBuild.getEndTime());
    }
    @Test
    public void getEndTimeAndGetStartTime(){
        addSubtasksToEpic();
        s3.setStartTime(LocalDateTime.of(2023,1,1,5,25));
        s3.setDuration(10);

        s2.setStartTime(LocalDateTime.of(2023,1,1,0,0));
        s2.setDuration(13);

        s1.setStartTime(LocalDateTime.of(2024, 2, 14, 12, 15 ));
        s1.setDuration(17);

        assertEquals(LocalDateTime.of(2023,1,1,0,0), homeBuild.getStartTime());
        assertEquals(LocalDateTime.of(2024, 2, 14, 12, 32 ), homeBuild.getEndTime());
    }
}