package de.cypix.tasks_check_bot.spring;

import de.cypix.tasks_check_bot.manager.SchoolSubject;
import de.cypix.tasks_check_bot.sql.SQLManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
public class AddTaskController {

    @PostMapping("/add")
    public String addTask(@RequestParam int subjectId, @RequestParam String description, @RequestParam String deadLine) {
        SchoolSubject subject = SchoolSubject.getById(subjectId);
        if (subject != null) {
            if (description != null) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(deadLine, DateTimeFormatter.ofPattern("yyyy-MM-dd-kk:mm"));
                    //TODO: add Task actually ...
                    SQLManager.insertNewTask(subject, dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss")), description.replace('_', ' '));
                    return "Added successfully!";
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return "There was a problem, please fix!";
    }

}
