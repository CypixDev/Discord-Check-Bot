package de.cypix.tasks_check_bot.events;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(!event.getUser().isBot()){
            super.onGuildMessageReactionAdd(event);
            TextChannel channel = TasksCheckBot.getJda().getTextChannelsByName(TasksCheckBot.getConfigManager().getChannelName(), true).get(0);
            if(channel.getId().equals(event.getChannel().getId())){
                if(event.getReaction().getReactionEmote().getEmoji().equals("✅")) {
                    for (Message message : event.getChannel().getIterableHistory().complete()) {
                        if (message.getIdLong() == event.getMessageIdLong()) {
                            if(SQLManager.markAsFinish(SQLManager.getUserId(event.getUserIdLong()),
                                    Integer.parseInt(message.getContentRaw().split(" ")[0].replace(".", "")))){
                                event.getUser().openPrivateChannel().complete().sendMessage("Diese Aufgabe wurde nun als erledigt markiert!").queue();
                            }else{
                                event.getUser().openPrivateChannel().complete().sendMessage("Diese Aufgabe wurde bereits als erledigt markiert!!").queue();
                            }
                        }
                    }

                }else if(event.getReaction().getReactionEmote().getEmoji().equals("❌")){
                    for (Message message : event.getChannel().getIterableHistory().complete()) {
                        if (message.getIdLong() == event.getMessageIdLong()) {
                            SQLManager.markAsNotFinish(SQLManager.getUserId(event.getUserIdLong()),
                                    Integer.parseInt(message.getContentRaw().split(" ")[0].replace(".", "")));
                            event.getUser().openPrivateChannel().complete().sendMessage("Diese Aufgabe wurde nun als nicht erledigt markiert!").queue();
                        }
                    }

                }else if(event.getReaction().getReactionEmote().getEmoji().equals("❓")){
                    for (Message message : event.getChannel().getIterableHistory().complete()) {
                        if (message.getIdLong() == event.getMessageIdLong()) {

                           StringBuilder stringBuilder = new StringBuilder("Diese Jungs haben die Aufgabe schon gemeistert: ");
                            for (String name : SQLManager.getFinishedTasksDiscordNames(Integer.parseInt(message.getContentRaw().split(" ")[0].replace(".", "")))) {
                                stringBuilder.append(name);
                                stringBuilder.append("; ");
                            }
                            event.getUser().openPrivateChannel().complete().sendMessage(stringBuilder.toString()).queue();
                        }
                    }

                }
                //really remove that ones? YES
                /*else{
                    for (Message message : event.getChannel().getHistoryFromBeginning(99).complete().getRetrievedHistory()) {
                        if(message.getReactions() != null){
                            for (MessageReaction reaction :message.getReactions()) {
                                if(reaction.getReactionEmote().isEmoji()){
                                    if(!reaction.getReactionEmote().getEmoji().equals("✅") && !reaction.getReactionEmote().getEmoji().equals("❌") && !reaction.getReactionEmote().getEmoji().equals("❓")){
                                        reaction.removeReaction().queue();
                                    }
                                }
                            }
                        }
                    }
                }*/
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        super.onGuildMessageReactionRemove(event);
    }
}
