package de.cypix.tasks_check_bot.events;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;


public class ReadyListener implements EventListener {
    @Override
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent)
            TasksCheckBot.logger.info("API is ready!");
    }
}