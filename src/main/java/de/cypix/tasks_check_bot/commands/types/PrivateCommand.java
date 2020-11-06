package de.cypix.tasks_check_bot.commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public interface PrivateCommand {

    public void performCommand(Member member, MessageChannel messageChannel, Message message, String[] args);

}
