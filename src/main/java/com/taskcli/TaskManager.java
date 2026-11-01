package com.taskcli;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

class TaskManager{

    List<Task> tasks;
    TaskStorage taskStorage;

    public TaskManager() throws IOException{
        tasks = new ArrayList<Task>();
        taskStorage = new TaskStorage();
    }

    public int generateId(){

        if(tasks.isEmpty()){ return 1;}

        int id = Integer.MIN_VALUE;
        for(Task t:tasks){
            int tId = t.getId();
            if(tId>id){
                id = tId;
            }
        }
        id = id + 1;
        return id;
    }

    public void addTask(String description) throws IOException{
        int id = generateId();
        Task task = new Task(id, description);
        tasks.add(task);
        taskStorage.writeToFile(tasks.toString());
        System.out.println("Task "+ id +" added successfully");
    }

    public String updateTask(int id, String description){
        return "Task"+ id +"updated successfully";
    }

    public String deleteTask(int id){
        return "Task"+ id +"deleted successfully";
    }

    public String listAllTask(){
        return "All tasks";
    }

    public String listByTaskStatus(String st){
        //first convert st into Status type
        return "All tasks by status";
    }

}