package de.cypix.tasks_check_bot.manager;

import de.cypix.tasks_check_bot.sql.SQLManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<File> getFilesFromTask(int taskId){
        List<File> files = new ArrayList<>();
        SchoolSubject schoolSubject = SQLManager.getSchoolSubjectByTaskId(taskId);
        File parent = new File("files/"+schoolSubject+"/"+taskId);
        if(parent.exists()){
            for (File file : parent.listFiles()) {
                files.add(file);
            }
        }
        return files;
    }

}
