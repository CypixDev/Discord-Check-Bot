package de.cypix.tasks_check_bot.commands.cmd;

import de.cypix.tasks_check_bot.commands.types.PrivateCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CMDHelp implements PrivateCommand {
        /*
        commandManager.registerCommand("help", new CMDHelp());
        commandManager.registerCommand("ping", new CMDPing());
        commandManager.registerCommand("deltask", new CMDDelTask());
        commandManager.registerCommand("addtask", new CMDAddTask());
        commandManager.registerCommand("delalltasks", new CMDDelAllTasks());
        commandManager.registerCommand("updatetask", new CMDUpdateTask());
        commandManager.registerCommand("archive", new CMDArchive());
        commandManager.registerCommand("list", new CMDList());
        commandManager.registerCommand("addfile", new CMDAddFile());
        commandManager.registerCommand("update", new CMDUpdate());
        commandManager.registerCommand("todo", new CMDTodo());
        commandManager.registerCommand("ignore", new CMDIgnore());
        */

    @Override
    public void performCommand(User user, MessageChannel messageChannel, Message message, String[] args) {
        if(args.length == 1){
            messageChannel.sendMessage("Hier kommt die lang ersehnte Hilfe: \n" +
                    "Um einen neue Aufgabe hinzuzufügen: ```addTask <Num-Subject>(show with 'list') [Day/Date(Format: 2020-10-30-23:00)] [Description]```\n" + //length = >= 4
                    "Um an einer Aufgabe etwas zu verändern, verwende: ```updateTask <task-id(not subject id)> <description/DeadLine/Link/subject> [new thing]```\n" + //length >= 4
                    "Um eine Aufgabe zu entfernen, benutze: ```delTask <task-id>```\n" + //length -> 2
                    "Um alle Aufgaben eines Faches zu entfernen: ```delAllTasks <subject-id>```\n" + //length -> 2
                    "Dein Ping bekommst du mit ```ping```\n" +
                    "Damit du weißt welches Fach welche Nummer hat, benutzte ```list```\n" +
                    "Um einen File zu einem bestehenden Task hinzuzufügen: ```ziehe den File per drag and drop auf den Bot und füge als kommentar folgendese hinzu: addfile <taskId>```\n" +
                    "Um Änderungen wirksam zu machen, benutzte ```update```" +
                    "Um Tasks von einem bestimmten Fach nicht mehr anzeigen zu lassen, verwende ```ignore <subject id>```" +
                    "Um deine Errinerungen zu verwalten, verwende: ```reminder add <time_before> <m(-in)/h(-our)/s(-ec)>```" +
                    " ```reminder remove [time_before]/all <m(-in)/h(-our)/s(-ec)>``` ```reminder list```" +
                    "Um einen Task in Private Channel als fertig zu markieren, benutzte ```finish <task-id>```").queue();
        }else messageChannel.sendMessage("Bitte benutzte nur 'help'").queue();
    }
}
