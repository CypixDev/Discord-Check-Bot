package de.cypix.tasks_check_bot.manager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SchoolTask {

    private int taskId;
    private SchoolSubject schoolSubject;
    private String taskDescription;
    //TODO: private List<User> finishedBy;
    private String taskLink;
    private final LocalDateTime deadLine;
    private final int ownerUserId; //public if equals to -1

    private final long secondsBetween;
    private final long minutesBetween;
    private final long hoursBetween;
    private final long daysBetween;

    public SchoolTask(int taskId, SchoolSubject schoolSubject, String taskDescription, String taskLink, LocalDateTime deadLine, int ownerUserId) {
        this.taskId = taskId;
        this.schoolSubject = schoolSubject;
        this.taskDescription = taskDescription;
        this.taskLink = taskLink;
        this.deadLine = deadLine;
        this.ownerUserId = ownerUserId;

        //Take care! it's only loaded when calling the constructor!
        //This is cause the reminder tasks..
        LocalDateTime timeNow = LocalDateTime.now();

        daysBetween = ChronoUnit.DAYS.between(timeNow, deadLine);
        hoursBetween = ChronoUnit.HOURS.between(timeNow, deadLine);
        minutesBetween = ChronoUnit.MINUTES.between(timeNow, deadLine);
        secondsBetween = ChronoUnit.SECONDS.between(timeNow, deadLine);

    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public SchoolSubject getSchoolSubject() {
        return schoolSubject;
    }

    public void setSchoolSubject(SchoolSubject schoolSubject) {
        this.schoolSubject = schoolSubject;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskLink() {
        return taskLink;
    }

    public void setTaskLink(String taskLink) {
        this.taskLink = taskLink;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public long getSecondsBetween() {
        return secondsBetween;
    }

    public long getMinutesBetween() {
        return minutesBetween;
    }

    public long getHoursBetween() {
        return hoursBetween;
    }

    public long getDaysBetween() {
        return daysBetween;
    }

    public boolean isPrivateTask() {
        return this.ownerUserId != -1;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }
}
