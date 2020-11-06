package de.cypix.tasks_check_bot.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserLogger extends ListenerAdapter
{

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        User author = event.getAuthor();
        Message message = event.getMessage();
            // Print the message of the user
            System.out.println(author.getAsTag() + ": " + message.getContentDisplay());
    }
}