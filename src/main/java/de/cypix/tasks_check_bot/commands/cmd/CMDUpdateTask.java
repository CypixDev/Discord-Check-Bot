package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;

public class CMDUpdateTask implements PrivateCommand {
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

        if (args[2].equalsIgnoreCase("description")) {
            StringBuilder description = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                description.append(args[i] + " ");
            }
            SQLManager.updateDescription(taskId, description.toString());
            messageChannel.sendMessage("Erfolgreich aktualisiert!").queue();
            return;
        }
        if (args[2].equalsIgnoreCase("deadline")) {

            String[] acceptedFormats = {"yyyy-MM-dd-kk:mm","yyyy-MM-dd","dd.MM.yyyy", "dd.MM.yyyy-kk:mm","yyyy-MM-dd-kk:mm:ss"};
            boolean checked = Arrays.stream(acceptedFormats)
                    .anyMatch(pattern -> {
                        try {
                            LocalDateTime.parse(args[3], DateTimeFormatter.ofPattern(pattern));
                            return true;
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                            return false;
                        }
                    });

            if(checked) {
                SQLManager.updateDeadLine(taskId, args[3]);
                messageChannel.sendMessage("Erfolgreich aktualisiert!").queue();
            } else {
                messageChannel.sendMessage("Das Format von dem Datum wurde nicht erkannt! (yyyy-MM-dd-hh:mm)").queue();
            }

            return;
        }
        if (args[2].equalsIgnoreCase("link")) {
            SQLManager.updateLink(taskId, args[3]);
            messageChannel.sendMessage("Erfolgreich aktualisiert!").queue();
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
                messageChannel.sendMessage("Bitte verwende eine Nummer, die als Fach existiert! Um die Liste anzuzeigen, verwende: ```list```").queue();
                return;
            }
            SQLManager.updateSubject(taskId, subjectId);
            messageChannel.sendMessage("Erfolgreich aktualisiert!").queue();
            return;
        }
    }
}
