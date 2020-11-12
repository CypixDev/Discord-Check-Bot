package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDList implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        StringBuilder list = new StringBuilder("Liste aller Fächer mit Nummern:\n");
        for (SchoolSubject value : SchoolSubject.values()) {
            list.append(value.getId() + ". " + value.getSubjectName() + " " + value.getEmoji() + "\n");
        }
        messageChannel.sendMessage(list.toString()).queue();
        return;
    }
}
