package de.cypix.tasks_check_bot.events;

import de.cypix.tasks_check_bot.main.TasksCheckBot;
import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (SQLManager.isConnected()) {
            SQLManager.insertUser(event.getAuthor());
        }
        if (event.isFromGuild()) {
            if(event.getChannel().getName().equals("zusammenfassung-beta")){
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
        } else {
            if (event.getAuthor().isBot()) return;
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    StringBuilder list = new StringBuilder("Liste aller Fächer mit Nummern:\n");
                    for (SchoolSubject value : SchoolSubject.values()) {
                        list.append(value.getId() + ". " + value.getSubjectName() + " " + value.getEmoji() + "\n");
                    }
                    event.getChannel().sendMessage(list.toString()).queue();
                    return;
                }
            }
            if (args.length >= 4) {
                if (args[0].equalsIgnoreCase("addtask")) {
                    int subjectId;
                    try {
                        subjectId = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Bitte verwende eine Nummer als fach! Um die liste anzeigen zu lassen tippe: list").queue();
                        return;
                    }

                    String date = args[2];
                    StringBuilder description = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        description.append(args[i] + " ");
                    }
                    SQLManager.insertNewTask(SchoolSubject.getById(subjectId), date, description.toString());
                    System.out.println("Inserted -> " + SchoolSubject.getById(subjectId) + " " + date + " " + description.toString());
                    event.getChannel().sendMessage("Wahrscheinlich erfolgreich hinzugefügt!").queue();
                    return;
                }
            }

            event.getChannel().sendMessage("Please use the following format: update <Subject-Number> <duty till(just ex: monday)> <short description of Task> ").queue();
        }
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("ping")) {
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("Pong!") /* => RestAction<Message> */
                    .queue(response /* => Message */ -> {
                        response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                    });
        }
    }

}
