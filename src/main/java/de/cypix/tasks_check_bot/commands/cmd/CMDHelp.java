package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDHelp implements PrivateCommand {

    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 1){
            messageChannel.sendMessage("Hier kommt die lang ersehnte Hilfe: \n" +
                    "Um einen neue Aufgabe hinzu zu fügen: ```addTask <Num-Subject>(show with 'list') [Day/Date(Format: 2020-10-30)] [Description]```\n" + //length = >= 4
                    "Um an einer Aufgabe etwas zu verändern verwende: ```updateTask <task-id(not subject id)> <description/DeadLine/Link/subject> [new thing]```\n" + //length >= 4
                    "Um eine Aufgabe zu entfernen Benutze: ```delTask <task-id>```\n" + //length -> 2
                    "Um alle Aufgaben eines faches zu entfernen: ```delAllTasks <subject-id>```\n" + //length -> 2
                    "Dein Ping bekommst du mit ```ping```\n" +
                    "Damit du weißt welches Fach welche nummer hat, benutzte ```list```\n" +
                    "Um einen File zu einem bestehenden Task hinzuzufügen: ```ziehe den File per drag and drop auf den Bot und füge als kommentar folgendese hinzu: addfile <taskId>```\n" +
                    "Um änderungen wirksam zu machen benutzte ```update```").queue();
        }else messageChannel.sendMessage("Bitte benutzte nur 'help'").queue();
    }
}
