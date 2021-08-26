package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDArchive implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        int taskId = -1;
        try {
            taskId = Integer.parseInt(args[1]);
            if (!SQLManager.taskExists(taskId)) {
                messageChannel.sendMessage("Dieser Task existiert nicht!").queue();
                return;
            }
        } catch (NumberFormatException e) {
            messageChannel.sendMessage("Bitte gib eine Zahl ein!").queue();
            return;
        }
        SQLManager.delTask(taskId);
        messageChannel.sendMessage("Erfolgreich gelöscht!").queue();
        return;
    }
}
