package de.cypix.tasks_check_bot.manager;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.List;

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
                MessageAction messageAction = channel.sendMessage(schoolTask.getTaskId()+
                        ". *"+schoolTask.getSchoolSubject().getSubjectName()+"* " +
                        ""+(schoolTask.getSchoolSubject().getEmoji() != null ? schoolTask.getSchoolSubject().getEmoji() : "")+
                        " | Deadline: ** "+schoolTask.getDeliveryDay()+"**"+
                        " ```"+schoolTask.getTaskDescription()+"```");

                messageAction.queue(e -> {
                    channel.addReactionById(e.getId(), "U+2705").queue();
                    channel.addReactionById(e.getId(), "U+274C").queue();
                    channel.addReactionById(e.getId(), "U+2753").queue();
                });
            }
        }
    }
}
