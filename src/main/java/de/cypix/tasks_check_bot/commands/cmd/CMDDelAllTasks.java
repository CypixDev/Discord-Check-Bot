package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDDelAllTasks implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
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
        int count = SQLManager.getAllTasks(SchoolSubject.getById(subjectId)).size();
        SQLManager.delTasksFromSubject(SchoolSubject.getById(subjectId));
        messageChannel.sendMessage("Erfolgreich " + count + " Tasks gelöscht!").queue();
        return;
    }
}
