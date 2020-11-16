package de.cypix.tasks_check_bot.manager;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TasksManager {

    public static void updateAllTasks(){
        TextChannel channel = TasksCheckBot.getJda().getTextChannelsByName(TasksCheckBot.getConfigManager().getChannelName(), true).get(0);
        List<Integer> tasksInChannel = new ArrayList<>();
        for (Message message : channel.getIterableHistory().complete()) {
            try{
                int taskId = Integer.parseInt(message.getContentRaw().split(" ")[0].replace(".", ""));
                String newMessage = getTaskOverview(taskId);
                if(newMessage != null) {
                    try{
                        message.editMessage(newMessage).queue();
                        tasksInChannel.add(taskId);
                    }catch (ErrorResponseException ignored) {

                    }
                }
            }catch (NumberFormatException e){
                //none
            }
        }
            for (Integer allTask : SQLManager.getAllTasks()) {
                if(!tasksInChannel.contains(allTask)){
                    TasksManager.addNewTaskToList(allTask);
                }
            }

    }

    public static void deleteTask(int taskId){
        TextChannel channel = TasksCheckBot.getJda().getTextChannelsByName(TasksCheckBot.getConfigManager().getChannelName(), true).get(0);
        for (Message message : channel.getIterableHistory().complete()) {
            if(Integer.parseInt(message.getContentRaw().split(" ")[0].replace(".", "")) == taskId){
                try{
                    message.delete().queue(e -> TasksCheckBot.logger.info("Task "+taskId+" is now not longer shown!"));
                }catch (ErrorResponseException e){
                    //none
                }
            }

        }
    }

    public static void addNewTaskToList(int taskId){
        TextChannel channel = TasksCheckBot.getJda().getTextChannelsByName(TasksCheckBot.getConfigManager().getChannelName(), true).get(0);

        boolean ok = true;
        try{
            for (Message message : channel.getIterableHistory().complete()) {
                if (Integer.parseInt(message.getContentRaw().split(" ")[0].replace(".", "")) == taskId) {
                    ok = false;
                }
            }
        }catch (NumberFormatException e){
            //do none
        }

        if(ok){
            MessageAction messageAction = channel.sendMessage(Objects.requireNonNull(getTaskOverview(taskId)));

            messageAction.queue(e -> {
                channel.addReactionById(e.getId(), "U+2705").queue();
                channel.addReactionById(e.getId(), "U+274C").queue();
                channel.addReactionById(e.getId(), "U+2753").queue();
                System.out.println("Added new message to overview!");
            });
        }else System.out.println("Doubled message !");
        TasksCheckBot.logger.info("Added new task to list in "+TasksCheckBot.getConfigManager().getChannelName()+"["+taskId+"]");
    }

    public static String getTaskOverview(int taskId){
        SchoolTask schoolTask = SQLManager.getTaskInfo(taskId);
        if(schoolTask == null){
            deleteTask(taskId);
            return null;
        }
        LocalDateTime deadLine = schoolTask.getDeadLine();
        LocalDateTime timeNow = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");

        long daysBetween = ChronoUnit.DAYS.between(timeNow, deadLine);
        long hoursBetween = ChronoUnit.HOURS.between(timeNow, deadLine);
        long minutesBetween = ChronoUnit.MINUTES.between(timeNow, deadLine);

        StringBuilder deadLineBuilder = new StringBuilder("| Deadline: **")
                .append(deadLine.format(formatter)).append(" ");
        if(hoursBetween < 0){
            deadLineBuilder.append("Abgelaufen seit ");
            int hours = (int) (hoursBetween*(-1));
            if(hours <= 24){
                if(hours == 1){
                    deadLineBuilder.append("einer Stunde ");
                }else{
                    deadLineBuilder.append(hours);
                    deadLineBuilder.append(" Stunden");
                }
            }else {
                if(daysBetween == -1){
                    deadLineBuilder.append("einem Tag ");
                }else{
                    deadLineBuilder.append(daysBetween*(-1));
                    deadLineBuilder.append(" Tagen");
                }
                int tmp = (int) (hours-(((daysBetween*-1)*24)));
                deadLineBuilder.append(" und ");
                if(tmp == 1){
                    deadLineBuilder.append("einer Stunde");
                }else{
                    deadLineBuilder.append(hours-(((daysBetween*-1)*24)));
                    deadLineBuilder.append(" Stunden");

                }
            }
        }else{
            deadLineBuilder.append("Fällig in: ");
            int hours = (int) (hoursBetween);

            if(hoursBetween >= 24){
                //gib in tagen an
                if(daysBetween == 1){
                    deadLineBuilder.append("einem Tag");
                }else{
                    deadLineBuilder.append(daysBetween);
                    deadLineBuilder.append(" ");
                    deadLineBuilder.append("Tagen");
                }
                deadLineBuilder.append(" und ");
                deadLineBuilder.append(hours-((daysBetween*24)));
                deadLineBuilder.append(" Stunden");
            }else{
                //gib in stunden an
                if(hoursBetween == 1){
                    deadLineBuilder.append(" einer Stunde ");
                }else{
                    deadLineBuilder.append(hoursBetween);
                    deadLineBuilder.append(" ");
                    deadLineBuilder.append("Stunden");
                }
                deadLineBuilder.append(" und ");
                if(minutesBetween == 1){
                    deadLineBuilder.append("einer Minute");
                }else{
                    deadLineBuilder.append(minutesBetween-(hoursBetween*60));
                    deadLineBuilder.append(" Minuten");
                }
            }
        }


        deadLineBuilder.append("**");

        return schoolTask.getTaskId()+
                ". *"+schoolTask.getSchoolSubject().getSubjectName()+"* " +
                ""+(schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "")+
                " "+deadLineBuilder.toString()+
                " ```"+schoolTask.getTaskDescription()+"```";

    }

    public List<File> getFilesForTaskId(int taskId){
        List<File> list = new ArrayList<>();
        for (File file : FileManager.getFilesFromTask(taskId)) {
            list.add(file);
        }
        return list;
    }

    public static void updateTaskOverview(){
        //Guild guild = TasksCheckBot.getJda().getGuildsByName("Server von Cypix", false).get(0);
        TextChannel channel = TasksCheckBot.getJda().getTextChannelsByName(TasksCheckBot.getConfigManager().getChannelName(), true).get(0);
        //remove old message
        channel.getIterableHistory().forEach(e -> {
            e.delete().queue();
        });

        channel.sendMessage("Hier Aktuelle Infos über alle Schulaufgaben: ").queue();

        for (SchoolSubject value : SchoolSubject.values()) {
            List<SchoolTask> list = SQLManager.getAllTasks(value);
            for (SchoolTask schoolTask : list) {
                LocalDateTime deadLine = schoolTask.getDeadLine();
                LocalDateTime timeNow = LocalDateTime.now();


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");

                long daysBetween = ChronoUnit.DAYS.between(timeNow, deadLine);
                long hoursBetween = ChronoUnit.HOURS.between(timeNow, deadLine);
                long minutesBetween = ChronoUnit.MINUTES.between(timeNow, deadLine);

                StringBuilder deadLineBuilder = new StringBuilder("| Deadline: **")
                        .append(deadLine.format(formatter)).append(" ");
                if(hoursBetween < 0){
                    deadLineBuilder.append("Abgelaufen seit ");
                    int hours = (int) (hoursBetween*(-1));
                    if(hours <= 24){
                        if(hours == 1){
                            deadLineBuilder.append("einer Stunde ");
                        }else{
                            deadLineBuilder.append(hours);
                            deadLineBuilder.append(" Stunden");
                        }
                    }else {
                        if(daysBetween == -1){
                            deadLineBuilder.append("einem Tag ");
                        }else{
                            deadLineBuilder.append(daysBetween*(-1));
                            deadLineBuilder.append(" Tagen");
                        }
                        int tmp = (int) (hours-(((daysBetween*-1)*24)));
                        deadLineBuilder.append(" und ");
                        if(tmp == 1){
                            deadLineBuilder.append("einer Stunde");
                        }else{
                            deadLineBuilder.append(hours-(((daysBetween*-1)*24)));
                            deadLineBuilder.append(" Stunden");

                        }
                    }
                }else{
                    deadLineBuilder.append("Fällig in: ");
                    int hours = (int) (hoursBetween);

                    if(hoursBetween >= 24){
                        //gib in tagen an
                        if(daysBetween == 1){
                            deadLineBuilder.append("einem Tag");
                        }else{
                            deadLineBuilder.append(daysBetween);
                            deadLineBuilder.append(" ");
                            deadLineBuilder.append("Tagen");
                        }
                        deadLineBuilder.append(" und ");
                        deadLineBuilder.append(hours-((daysBetween*24)));
                        deadLineBuilder.append(" Stunden");
                    }else{
                        //gib in stunden an
                        if(hoursBetween == 1){
                            deadLineBuilder.append(" einer Stunde ");
                        }else{
                            deadLineBuilder.append(hoursBetween);
                            deadLineBuilder.append(" ");
                            deadLineBuilder.append("Stunden");
                        }
                        deadLineBuilder.append(" und ");
                        if(minutesBetween == 1){
                            deadLineBuilder.append("einer Minute");
                        }else{
                            deadLineBuilder.append(minutesBetween-(hoursBetween*60));
                            deadLineBuilder.append(" Minuten");
                        }
                    }
                }


                deadLineBuilder.append("**");

                MessageAction messageAction = channel.sendMessage(schoolTask.getTaskId()+
                        ". *"+schoolTask.getSchoolSubject().getSubjectName()+"* " +
                        ""+(schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "")+
                        " "+deadLineBuilder.toString()+
                        " ```"+schoolTask.getTaskDescription()+"```");



                messageAction.queue(e -> {
                    channel.addReactionById(e.getId(), "U+2705").queue();
                    channel.addReactionById(e.getId(), "U+274C").queue();
                    channel.addReactionById(e.getId(), "U+2753").queue();
                });/*
                for (File file : FileManager.getFilesFromTask(schoolTask.getTaskId())) {
                    channel.sendMessage(" ").addFile(file).queue();
                }*/
            }
        }
    }
    public static void sendTodo(MessageChannel channel, long discordId){
        long start = System.currentTimeMillis();
        TasksCheckBot.logger.info("Sending todo ["+discordId+"]....");

        int userId = SQLManager.getUserId(discordId);
        //Guild guild = TasksCheckBot.getJda().getGuildsByName("Server von Cypix", false).get(0);

        channel.sendMessage("Hier die sachen die **du** noch zu tun hast: ").queue();

        for (SchoolSubject subject : SchoolSubject.values()) {
            List<SchoolTask> list = SQLManager.getAllTasks(subject);
            for (SchoolTask schoolTask : list) {
                if(!SQLManager.isIgnoringSubjectByDiscordId(discordId, subject)){
                    if(!SQLManager.isTaskFinished(userId, schoolTask.getTaskId())){
                        LocalDateTime deadLine = schoolTask.getDeadLine();
                        LocalDateTime timeNow = LocalDateTime.now();


                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");

                        long daysBetween = ChronoUnit.DAYS.between(timeNow, deadLine);
                        long hoursBetween = ChronoUnit.HOURS.between(timeNow, deadLine);
                        long minutesBetween = ChronoUnit.MINUTES.between(timeNow, deadLine);

                        StringBuilder deadLineBuilder = new StringBuilder("| Deadline: **")
                                .append(deadLine.format(formatter)).append(" ");
                        if(hoursBetween < 0){
                            deadLineBuilder.append("Abgelaufen seit ");
                            int hours = (int) (hoursBetween*(-1));
                            if(hours <= 24){
                                if(hours == 1){
                                    deadLineBuilder.append("einer Stunde ");
                                }else{
                                    deadLineBuilder.append(hours);
                                    deadLineBuilder.append(" Stunden");
                                }
                            }else {
                                if(daysBetween == -1){
                                    deadLineBuilder.append("einem Tag ");
                                }else{
                                    deadLineBuilder.append(daysBetween*(-1));
                                    deadLineBuilder.append(" Tagen");
                                }
                                int tmp = (int) (hours-(((daysBetween*-1)*24)));
                                deadLineBuilder.append(" und ");
                                if(tmp == 1){
                                    deadLineBuilder.append("einer Stunde");
                                }else{
                                    deadLineBuilder.append(hours-(((daysBetween*-1)*24)));
                                    deadLineBuilder.append(" Stunden");
                                }
                            }
                        }else{
                            deadLineBuilder.append("Fällig in: ");
                            int hours = (int) (hoursBetween);

                            if(hoursBetween >= 24){
                                //gib in tagen an
                                if(daysBetween == 1){
                                    deadLineBuilder.append("einem Tag");
                                }else{
                                    deadLineBuilder.append(daysBetween);
                                    deadLineBuilder.append(" ");
                                    deadLineBuilder.append("Tagen");
                                }
                                deadLineBuilder.append(" und ");
                                deadLineBuilder.append(hours-((daysBetween*24)));
                                deadLineBuilder.append(" Stunden");
                            }else{
                                //gib in stunden an
                                if(hoursBetween == 1){
                                    deadLineBuilder.append(" einer Stunde ");
                                }else{
                                    deadLineBuilder.append(hoursBetween);
                                    deadLineBuilder.append(" ");
                                    deadLineBuilder.append("Stunden");
                                }
                                deadLineBuilder.append(" und ");
                                if(minutesBetween == 1){
                                    deadLineBuilder.append("einer Minute");
                                }else{
                                    deadLineBuilder.append(minutesBetween-(hoursBetween*60)); //fixed
                                    deadLineBuilder.append(" Minuten");
                                }
                            }
                        }


                        deadLineBuilder.append("**");

                        MessageAction messageAction = channel.sendMessage(schoolTask.getTaskId()+
                                ". *"+schoolTask.getSchoolSubject().getSubjectName()+"* " +
                                ""+(schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "")+
                                " "+deadLineBuilder.toString()+
                                " ```"+schoolTask.getTaskDescription()+"```");


                        messageAction.queue();
                        for (File file : FileManager.getFilesFromTask(schoolTask.getTaskId())) {
                            channel.sendMessage(" ").addFile(file).queue();
                        }
                    }
                }
            }
        }
        TasksCheckBot.logger.info("Sending todo done, takes "+(System.currentTimeMillis()-start)+"millis ["+discordId+"]....");
    }
}
