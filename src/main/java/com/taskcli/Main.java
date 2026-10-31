package com.taskcli;

class Main{

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        if(args.length==0){
            System.out.println("Please provide an action to perform...");
            return;
        }

        String action = args[0];
        System.out.print("\n");
        switch (action){
            case "add":
                if(args.length!=2){
                    System.out.println("Expected command: add {task-description}");
                    return;
                }
                try{
                    taskManager.addTask(args[1]);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                break;

            case "update":
                if(args.length!=3){
                    System.out.println("Expected Command: update {task-id} {task-description}");
                    return;
                }
                try{
                    taskManager.updateTask(Integer.parseInt(args[1]), args[2]);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                break;

            case "delete":
                if(args.length!=2){
                    System.out.println("Expected Command: delete {task-id}");
                    return;
                }
                try{
                    taskManager.deleteTask(Integer.parseInt(args[1]));
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                break;

            case "list":
                if(args.length==1){
                    taskManager.listAllTask();
                } else if (args.length == 2) {
                    try{
                        taskManager.listByTaskStatus(args[1]);
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("Expected Command: list #optional->{task-status}");
                    return;
                }
                break;
            default:
                System.out.println("Invalid action");
        }
    }
}