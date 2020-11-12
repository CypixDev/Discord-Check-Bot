package de.cypix.tasks_check_bot.manager;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class TasksManager {

    private List<String> subjects;

    public TasksManager(){

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
                Date deadLine = schoolTask.getDeadLine();

                MessageAction messageAction = channel.sendMessage(schoolTask.getTaskId()+
                        ". *"+schoolTask.getSchoolSubject().getSubjectName()+"* " +
                        ""+(schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "")+
                        " | Deadline: ** "+deadLine.toLocalDate().toString()+"**"+
                        " ```"+schoolTask.getTaskDescription()+"```");

                messageAction.queue(e -> {
                    channel.addReactionById(e.getId(), "U+2705").queue();
                    channel.addReactionById(e.getId(), "U+274C").queue();
                    channel.addReactionById(e.getId(), "U+2753").queue();
                });
                for (File file : FileManager.getFilesFromTask(schoolTask.getTaskId())) {
                    channel.sendMessage(" ").addFile(file).queue();
                }
            }
        }
    }

    public static void sendTodo(MessageChannel channel, long discordId){
        int userId = SQLManager.getUserId(discordId);
        //Guild guild = TasksCheckBot.getJda().getGuildsByName("Server von Cypix", false).get(0);

        channel.sendMessage("Hier die sachen die **du** noch zu tun hast: ").queue();

        for (SchoolSubject value : SchoolSubject.values()) {
            List<SchoolTask> list = SQLManager.getAllTasks(value);
            for (SchoolTask schoolTask : list) {
                if(!SQLManager.isTaskFinished(userId, schoolTask.getTaskId())){
                    Date deadLine = schoolTask.getDeadLine();

                    MessageAction messageAction = channel.sendMessage(schoolTask.getTaskId()+
                            ". *"+schoolTask.getSchoolSubject().getSubjectName()+"* " +
                            ""+(schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "")+
                            " | Deadline: ** "+deadLine.toLocalDate().toString()+"**"+
                            " ```"+schoolTask.getTaskDescription()+"```");


                    messageAction.queue();
                    for (File file : FileManager.getFilesFromTask(schoolTask.getTaskId())) {
                        channel.sendMessage(" ").addFile(file).queue();
                    }
                }
            }
        }

    }
}
