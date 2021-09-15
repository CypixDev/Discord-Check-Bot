package de.cypix.tasks_check_bot.spring.task;

import de.cypix.tasks_check_bot.manager.SchoolTask;
import de.cypix.tasks_check_bot.sql.SQLManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

    @GetMapping(value = "/list")
    public Collection<SchoolTask> list(){
        return SQLManager.getAllTasksAsTask();
    }

}
