package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDPing implements PrivateCommand {
    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        long time = System.currentTimeMillis();
        messageChannel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response -> {
                    response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                });
    }
}
