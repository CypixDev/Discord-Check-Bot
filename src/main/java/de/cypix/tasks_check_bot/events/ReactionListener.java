package de.cypix.tasks_check_bot.events;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        super.onGuildMessageReactionAdd(event);
        TextChannel channel = TasksCheckBot.getJda().getTextChannelsByName(TasksCheckBot.getConfigManager().getChannelName(), true).get(0);
        if(channel.getId().equals(event.getChannel().getId())){
            if(event.getReaction().getReactionEmote().getEmoji().equals("✅")){
                TasksCheckBot.getJda().getPrivateChannelCache().iterator();
                for (Iterator<PrivateChannel> it = TasksCheckBot.getJda().getPrivateChannelCache().iterator(); it.hasNext(); ) {
                    PrivateChannel privateChannel = it.next();
                    if (privateChannel.getUser().getId().equals(event.getUser().getId())) {
                        //just saving privateChannelId
/*                        if (SQLManager.isConnected()) {
                            SQLManager.insertPrivateChannelId(privateChannel.getUser().getIdLong(), privateChannel.getIdLong());
                        }*/
                        privateChannel.sendMessage("Die abgehakte Aufgabe wird für dich jetzt als fertig angezeigt.(COOMING SOON)").queue();
                    }
                }
            }else{
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
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        super.onGuildMessageReactionRemove(event);
    }
}
