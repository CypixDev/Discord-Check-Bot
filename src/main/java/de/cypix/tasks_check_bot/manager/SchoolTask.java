package de.cypix.tasks_check_bot.manager;

import java.sql.Date;
import java.time.LocalDateTime;

public class SchoolTask {

    private int taskId;
    private SchoolSubject schoolSubject;
    private String taskDescription;
    //TODO: private List<User> finishedBy;
    private String taskLink;
    private LocalDateTime deadLine;

    public SchoolTask(int taskId, SchoolSubject schoolSubject, String taskDescription, String taskLink, LocalDateTime deadLine) {
        this.taskId = taskId;
        this.schoolSubject = schoolSubject;
        this.taskDescription = taskDescription;
        this.taskLink = taskLink;
        this.deadLine = deadLine;
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
}
