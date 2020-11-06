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
/*                if (args[0].equalsIgnoreCase("help")) {
                    event.getChannel().sendMessage("Help: \n" +
                            "Um einen neue Aufgabe hinzu zu fügen: ```addTask <Num-Subject>(show with 'list') [Day/Date] [Description]```\n" + //length = >= 4
                            "Um an einer Aufgabe etwas zu verändern verwende: ```updateTask <task-id(not subject id)> <description/DeadLine/Link/subject> [new thing]```\n" + //length >= 4
                            "Um eine Aufgabe zu entfernen Benutze: ```delTask <task-id>```\n" + //length -> 2
                            "Um alle Aufgaben eines faches zu entfernen: ```delAllTasks <subject-id>```\n" + //length -> 2
                            "\n" +
                            "").queue();
                    return;
                }*/
/*                if (args[0].equalsIgnoreCase("ping")) {
                    MessageChannel channel = event.getChannel();
                    long time = System.currentTimeMillis();
                    channel.sendMessage("Pong!") *//* => RestAction<Message> *//*
                            .queue(response *//* => Message *//* -> {
                                response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                            });
                }*/
            }
            if (args.length == 2) {
                //delete task -> delTask <task-id>
                if (args[0].equalsIgnoreCase("deltask")) {
                    int taskId = -1;
                    try {
                        taskId = Integer.parseInt(args[1]);
                        if (!SQLManager.taskExists(taskId)) {
                            event.getChannel().sendMessage("Dieser Task Existiert nicht!").queue();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Bitte gebe eine Zahl ein!").queue();
                        return;
                    }
                    SQLManager.delTask(taskId);
                    event.getChannel().sendMessage("Erfolgreich gelöscht!").queue();
                    return;
                }
                //dell tasks from subject -> delAllTasks <subject-id>
                if (args[0].equalsIgnoreCase("delAllTasks")) {
                    int subjectId;
                    try {
                        subjectId = Integer.parseInt(args[1]);
                        //check if its > than 0 and < than max schoolSubject id
                        if (subjectId < 0 || subjectId > SchoolSubject.getHighestSubjectId())
                            new NumberFormatException("Subject is not existing");
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Bitte verwende eine Nummer die als Fach existiert! Um die liste anzeigen zu lassen verwende: ```list```").queue();
                        return;
                    }
                    int count = SQLManager.getAllTasks(SchoolSubject.getById(subjectId)).size();
                    SQLManager.delTasksFromSubject(SchoolSubject.getById(subjectId));
                    event.getChannel().sendMessage("Erfolgreich " + count + " Tasks gelöscht!").queue();
                    return;
                }
                if(args[0].equalsIgnoreCase("archive")){
                    int taskId = -1;
                    try {
                        taskId = Integer.parseInt(args[1]);
                        if (!SQLManager.taskExists(taskId)) {
                            event.getChannel().sendMessage("Dieser Task Existiert nicht!").queue();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Bitte gebe eine Zahl ein!").queue();
                        return;
                    }
                    SQLManager.delTask(taskId);
                    event.getChannel().sendMessage("Erfolgreich gelöscht!").queue();
                    return;
                }
            }

            //addtask
            if (args.length >= 4) {
                //Add Task -> addTask <Num-Subject>(show with 'list') [Day/Date] [Description]
                if (args[0].equalsIgnoreCase("addtask")) {
                    int subjectId;
                    try {
                        subjectId = Integer.parseInt(args[1]);
                        //check if its > than 0 and < than max schoolSubject id
                        if (subjectId < 0 || subjectId > SchoolSubject.getHighestSubjectId())
                            new NumberFormatException("Subject is not existing");
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Bitte verwende eine Nummer die als Fach existiert! Um die liste anzeigen zu lassen verwende: ```list```").queue();
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
                //update task -> updateTask <task-id(not subject id)> <description/DeadLine/Link/subject> [new thing]
                if (args[0].equalsIgnoreCase("updateTask")) {
                    int taskId = -1;
                    try {
                        taskId = Integer.parseInt(args[1]);
                        if (!SQLManager.taskExists(taskId)) {
                            event.getChannel().sendMessage("Dieser Task Existiert nicht!").queue();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Bitte gebe eine Zahl ein!").queue();
                        return;
                    }

                    if (args[2].equalsIgnoreCase("description")) {
                        StringBuilder description = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            description.append(args[i] + " ");
                        }
                        SQLManager.updateDescription(taskId, description.toString());
                        event.getChannel().sendMessage("Erfolgreich aktuallisiert!").queue();
                        return;
                    }
                    if (args[2].equalsIgnoreCase("deadline")) {
                        StringBuilder deadLine = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            deadLine.append(args[i] + " ");
                        }
                        SQLManager.updateDeadLine(taskId, deadLine.toString());
                        event.getChannel().sendMessage("Erfolgreich aktuallisiert!").queue();
                        return;
                    }
                    if (args[2].equalsIgnoreCase("link")) {
                        SQLManager.updateLink(taskId, args[3]);
                        event.getChannel().sendMessage("Erfolgreich aktuallisiert!").queue();
                        return;
                    }
                    if (args[2].equalsIgnoreCase("subject")) {
                        int subjectId;
                        try {
                            subjectId = Integer.parseInt(args[1]);
                            //check if its > than 0 and < than max schoolSubject id
                            if (subjectId < 0 || subjectId > SchoolSubject.getHighestSubjectId())
                                new NumberFormatException("Subject is not existing");
                        } catch (NumberFormatException e) {
                            event.getChannel().sendMessage("Bitte verwende eine Nummer die als Fach existiert! Um die liste anzeigen zu lassen verwende: ```list```").queue();
                            return;
                        }
                        SQLManager.updateSubject(taskId, subjectId);
                        event.getChannel().sendMessage("Erfolgreich aktuallisiert!").queue();
                        return;
                    }
                }
            }

            event.getChannel().sendMessage("Benutzte 'help' zum anzeigen all deiner möglichkeiten").queue();
            return;
        }
    }

}
