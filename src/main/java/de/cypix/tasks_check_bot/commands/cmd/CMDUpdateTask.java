package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDUpdateTask implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        int taskId = -1;
        try {
            taskId = Integer.parseInt(args[1]);
            if (!SQLManager.taskExists(taskId)) {
                messageChannel.sendMessage("Dieser Task Existiert nicht!").queue();
                return;
            }
        } catch (NumberFormatException e) {
                messageChannel.sendMessage("Bitte gebe eine Zahl ein!").queue();
            return;
        }

        if (args[2].equalsIgnoreCase("description")) {
            StringBuilder description = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                description.append(args[i] + " ");
            }
            SQLManager.updateDescription(taskId, description.toString());
            messageChannel.sendMessage("Erfolgreich aktuallisiert!").queue();
            return;
        }
        if (args[2].equalsIgnoreCase("deadline")) {
            StringBuilder deadLine = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                deadLine.append(args[i] + " ");
            }
            SQLManager.updateDeadLine(taskId, deadLine.toString());
            messageChannel.sendMessage("Erfolgreich aktuallisiert!").queue();
            return;
        }
        if (args[2].equalsIgnoreCase("link")) {
            SQLManager.updateLink(taskId, args[3]);
            messageChannel.sendMessage("Erfolgreich aktuallisiert!").queue();
            return;
        }
        if (args[2].equalsIgnoreCase("subject")) {
            int subjectId;
            try {
                subjectId = Integer.parseInt(args[1]);
                //check if its > than 0 and < than max schoolSubject id
                if (subjectId < 0 || subjectId > SchoolSubject.getHighestSubjectId())
                    new NumberFormatException("Subject is not existing");
            } catch (NumberFormatException e) {
                messageChannel.sendMessage("Bitte verwende eine Nummer die als Fach existiert! Um die liste anzeigen zu lassen verwende: ```list```").queue();
                return;
            }
            SQLManager.updateSubject(taskId, subjectId);
            messageChannel.sendMessage("Erfolgreich aktuallisiert!").queue();
            return;
        }
    }
}
