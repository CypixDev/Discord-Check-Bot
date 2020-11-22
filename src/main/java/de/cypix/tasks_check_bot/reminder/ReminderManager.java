package de.cypix.tasks_check_bot.reminder;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.manager.SchoolTask;
import de.cypix.tasks_check_bot.sql.SQLManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderManager {

    private ReminderScheduler reminderScheduler;
    private HashMap<Integer, SchoolTask> schoolTasksOld;

    public ReminderManager() {
        schoolTasksOld = new HashMap<>();
        initScheduler();
    }

    private void initScheduler() {
        reminderScheduler = new ReminderScheduler(new Runnable() {
            @Override
            public void run() {
                List<SchoolTask> schoolTasks = SQLManager.getAllTasksAsTask();
                HashMap<Integer, SchoolTask> schoolTasksOldClone = getSchoolTasksOld();
                schoolTasksOld.clear();
                for (SchoolTask schoolTask : schoolTasks) {

                    LocalDateTime deadLine = schoolTask.getDeadLine();
                    LocalDateTime timeNow = LocalDateTime.now();


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");

                    long secondsBetween = ChronoUnit.SECONDS.between(timeNow, deadLine);
                    long minutesBetween = ChronoUnit.MINUTES.between(timeNow, deadLine);
                    long hoursBetween = ChronoUnit.HOURS.between(timeNow, deadLine);
                    long daysBetween = ChronoUnit.DAYS.between(timeNow, deadLine);


                    SchoolTask oldSchoolTask = schoolTasksOldClone.get(schoolTask.getTaskId());
                    checkSecondStep(secondsBetween, schoolTask);
                    try {
                        if (minutesBetween < oldSchoolTask.getMinutesBetween()) {
                            checkMinuteStep(minutesBetween, schoolTask);
                        }
                        if (hoursBetween < oldSchoolTask.getHoursBetween()) {
                            checkHourStep(hoursBetween, schoolTask);
                        }
                        if (daysBetween < oldSchoolTask.getDaysBetween()) {
                            checkDayStep(daysBetween, schoolTask);
                        }
                    } catch (NullPointerException e) {

                    }

                    schoolTasksOld.put(schoolTask.getTaskId(), schoolTask);

                }
            }
        }, 1); //every second
    }

    public HashMap<Integer, SchoolTask> getSchoolTasksOld() {
        return (HashMap<Integer, SchoolTask>) schoolTasksOld.clone();
    }

    private void checkSecondStep(long timeBefore, SchoolTask schoolTask) {
        //TODO: Optimise... maybe just select all with exact the same second/hour/min/day !
        try {
            List<ReminderTask> reminderTasks = SQLManager.getAllReminderTasksPerSecond();
            for (ReminderTask reminderTask : reminderTasks) {
                if (reminderTask.getTimeUnit().equals(TimeUnit.SECOND)) {
                    if (reminderTask.getTimeBefore() == timeBefore) {
                        TasksCheckBot.getJda().openPrivateChannelById(reminderTask.getDiscordId()).queue(e -> {
                            e.sendMessage("Du musst noch " + schoolTask.getSchoolSubject().getSubjectName() +
                                    (schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "") +
                                    " fertig machen!").queue();
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkMinuteStep(long timeBefore, SchoolTask schoolTask) {
        try {
            List<ReminderTask> reminderTasks = SQLManager.getAllReminderTasksPerMinute();
            for (ReminderTask reminderTask : reminderTasks) {
                if (reminderTask.getTimeUnit().equals(TimeUnit.MINUTE)) {
                    if (reminderTask.getTimeBefore() == timeBefore) {
                        TasksCheckBot.getJda().openPrivateChannelById(reminderTask.getDiscordId()).queue(e -> {
                            e.sendMessage("Du musst noch " + schoolTask.getSchoolSubject().getSubjectName() +
                                    (schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "") +
                                    " fertig machen!").queue();
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkHourStep(long timeBefore, SchoolTask schoolTask) {
        try {
            List<ReminderTask> reminderTasks = SQLManager.getAllReminderTasksPerHour();
            for (ReminderTask reminderTask : reminderTasks) {
                if (reminderTask.getTimeUnit().equals(TimeUnit.HOUR)) {
                    if (reminderTask.getTimeBefore() == timeBefore) {
                        TasksCheckBot.getJda().openPrivateChannelById(reminderTask.getDiscordId()).queue(e -> {
                            e.sendMessage("Du musst noch " + schoolTask.getSchoolSubject().getSubjectName() +
                                    (schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "") +
                                    " fertig machen!").queue();
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkDayStep(long timeBefore, SchoolTask schoolTask) {
        try {
            List<ReminderTask> reminderTasks = SQLManager.getAllReminderTasksPerDay();
            for (ReminderTask reminderTask : reminderTasks) {
                if (reminderTask.getTimeUnit().equals(TimeUnit.DAY)) {
                    if (reminderTask.getTimeBefore() == timeBefore) {
                        TasksCheckBot.getJda().openPrivateChannelById(reminderTask.getDiscordId()).queue(e -> {
                            e.sendMessage("Du musst noch " + schoolTask.getSchoolSubject().getSubjectName() +
                                    (schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "") +
                                    " fertig machen!").queue();
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
