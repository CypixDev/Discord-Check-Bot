package de.cypix.tasks_check_bot.commands;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CMDHelp implements PrivateCommand {

    @Override
    public void performCommand(Member member, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 1){
            messageChannel.sendMessage("Hier kommt die lang ersehnte Hilfe: \n" +
                    "Um einen neue Aufgabe hinzu zu fügen: ```addTask <Num-Subject>(show with 'list') [Day/Date] [Description]```\n" + //length = >= 4
                    "Um an einer Aufgabe etwas zu verändern verwende: ```updateTask <task-id(not subject id)> <description/DeadLine/Link/subject> [new thing]```\n" + //length >= 4
                    "Um eine Aufgabe zu entfernen Benutze: ```delTask <task-id>```\n" + //length -> 2
                    "Um alle Aufgaben eines faches zu entfernen: ```delAllTasks <subject-id>```\n" + //length -> 2
                    "\n" +
                    "").queue();
        }else messageChannel.sendMessage("Bitte benutzte nur 'help'");
    }
}
