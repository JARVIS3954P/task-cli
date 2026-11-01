package com.taskcli;

class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            TaskManager taskManager = new TaskManager();
            String action = args[0];

            switch (action) {
                case "add":
                    requireArgs(args, 2, "add <description>");
                    taskManager.addTask(args[1]);
                    break;

                case "update":
                    requireArgs(args, 3, "update <id> <description>");
                    taskManager.updateTask(parseId(args[1]), args[2]);
                    break;

                case "delete":
                    requireArgs(args, 2, "delete <id>");
                    taskManager.deleteTask(parseId(args[1]));
                    break;

                case "mark-in-progress":
                    requireArgs(args, 2, "mark-in-progress <id>");
                    taskManager.markTask(parseId(args[1]), Status.IN_PROGRESS);
                    break;

                case "mark-done":
                    requireArgs(args, 2, "mark-done <id>");
                    taskManager.markTask(parseId(args[1]), Status.DONE);
                    break;

                case "list":
                    if (args.length == 1) {
                        taskManager.listAllTasks();
                    } else if (args.length == 2) {
                        taskManager.listByTaskStatus(Status.fromCliValue(args[1]));
                    } else {
                        throw new IllegalArgumentException("Usage: list [todo|in-progress|done]");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown command: " + action);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static int parseId(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id <= 0) {
                throw new IllegalArgumentException("Task ID must be a positive integer.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Task ID must be an integer: " + value);
        }
    }

    private static void requireArgs(String[] args, int expectedLength, String usage) {
        if (args.length != expectedLength) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
        if ((args[0].equals("add") && args[1].trim().isEmpty())
                || (args[0].equals("update") && args[2].trim().isEmpty())) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
    }

    private static void printUsage() {
        System.err.println("Commands:");
        System.err.println("  add <description>");
        System.err.println("  update <id> <description>");
        System.err.println("  delete <id>");
        System.err.println("  mark-in-progress <id>");
        System.err.println("  mark-done <id>");
        System.err.println("  list [todo|in-progress|done]");
    }
}
