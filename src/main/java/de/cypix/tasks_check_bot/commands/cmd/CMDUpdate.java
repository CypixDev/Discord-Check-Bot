package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.TasksManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDUpdate implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("all")){
                TasksManager.updateTaskOverview();
                messageChannel.sendMessage("Es wird alles neu aufgelistet....").queue();
            }
        }else{
            TasksManager.updateAllTasks();
            messageChannel.sendMessage("Wird aktuallisiert....").queue();
        }

    }
}
