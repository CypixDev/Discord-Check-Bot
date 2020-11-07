package de.cypix.tasks_check_bot.events;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //save everyone in database
        if (SQLManager.isConnected()) {
            SQLManager.insertUser(event.getAuthor());
        }

        if (event.isFromGuild()) {
            if (event.getChannel().getName().equals("zusammenfassung-beta")) {
                //delete message
                if (!event.getAuthor().isBot()) {
                    event.getChannel().deleteMessageById(event.getChannel().getLatestMessageId()).queue();
                } else return;

                if (!event.getAuthor().hasPrivateChannel()) event.getAuthor().openPrivateChannel().queue();

                //asking private channel id is absolutely useless!
                for (PrivateChannel privateChannel : TasksCheckBot.getJda().getPrivateChannels()) {
                    if (privateChannel.getUser().getId().equals(event.getAuthor().getId())) {
                        //just saving privateChannelId
                        if (SQLManager.isConnected()) {
                            SQLManager.insertPrivateChannelId(privateChannel.getUser().getIdLong(), privateChannel.getIdLong());
                        }
                        privateChannel.sendMessage("Bitte schreibe nicht in diesen Channel!!!").queue();
                    }
                }
                return;
            }
        }


        //writing in channel
        if (event.getChannel().getName().equals("zusammenfassung-beta")) {

            //disallow writing in info-channel and delete message
            if (!event.getAuthor().isBot()) {
                try{

                }catch(ErrorResponseException e){
                    //TODO: nix?
                }
            } else return;
            return;
        }

        String message = event.getMessage().getContentRaw();
        String[] args = message.split(" ");
        if(!event.getAuthor().isBot()){
            if(event.getChannel().getType().equals(ChannelType.PRIVATE)){
                //call command
                if(!TasksCheckBot.getCommandManager().perform(args[0], event.getMember(), event.getChannel(), event.getMessage(), args)){
                    event.getChannel().sendMessage("Dieser Command ist noch nicht im CommandManager registriert....").queue();
                }
            }
        }
    }
}
