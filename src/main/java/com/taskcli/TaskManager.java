package com.taskcli;

import java.util.List;
import java.util.ArrayList;

class TaskManager{

    List<Task> tasks;

    public TaskManager(){
        tasks = new ArrayList<Task>();
        //check if JSON persistent exists, create if not
        //fetch each task from it and populate the list tasks
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

    public void addTask(String description){

        int id = generateId();
        Task task = new Task(id, description);
        tasks.add(task);
        // then update the persistent storage path with this tasks list
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