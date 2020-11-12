package de.cypix.tasks_check_bot.commands.types;

import net.dv8tion.jda.api.entities.*;

public interface PrivateCommand {

    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args);

}
