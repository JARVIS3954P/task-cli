package com.taskcli;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class TaskManager {
    private final List<Task> tasks;
    private final TaskStorage taskStorage;
    private final JsonConverter jsonConverter;

    TaskManager() throws IOException {
        taskStorage = new TaskStorage();
        jsonConverter = new JsonConverter();
        tasks = new ArrayList<>();
        loadTasks();
    }

    private void loadTasks() throws IOException {
        String content = taskStorage.readFromFile();
        try {
            tasks.addAll(jsonConverter.convertToTasks(content));
        } catch (IllegalArgumentException e) {
            throw new IOException("tasks.json contains invalid task data: " + e.getMessage(), e);
        }
    }

    private int generateId() {
        int maxId = 0;
        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        if (maxId == Integer.MAX_VALUE) {
            throw new IllegalStateException("No more task IDs are available.");
        }
        return maxId + 1;
    }

    private void persist() throws IOException {
        taskStorage.writeToFile(jsonConverter.convertToJson(tasks));
    }

    void addTask(String description) throws IOException {
        Task task = new Task(generateId(), description);
        tasks.add(task);
        try {
            persist();
        } catch (IOException e) {
            tasks.remove(task);
            throw e;
        }
        System.out.println("Task added successfully (ID: " + task.getId() + ")");
    }

    void updateTask(int id, String description) throws IOException {
        Task task = findTask(id);
        String oldDescription = task.getDescription();
        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        task.setDescription(description);
        task.setUpdatedAt(LocalDateTime.now());
        try {
            persist();
        } catch (IOException e) {
            task.setDescription(oldDescription);
            task.setUpdatedAt(oldUpdatedAt);
            throw e;
        }
        System.out.println("Task updated successfully (ID: " + id + ")");
    }

    void deleteTask(int id) throws IOException {
        Task task = findTask(id);
        int index = tasks.indexOf(task);
        tasks.remove(index);
        try {
            persist();
        } catch (IOException e) {
            tasks.add(index, task);
            throw e;
        }
        System.out.println("Task deleted successfully (ID: " + id + ")");
    }

    void markTask(int id, Status status) throws IOException {
        Task task = findTask(id);
        Status oldStatus = task.getStatus();
        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        try {
            persist();
        } catch (IOException e) {
            task.setStatus(oldStatus);
            task.setUpdatedAt(oldUpdatedAt);
            throw e;
        }
        System.out.println("Task marked " + status.toCliValue() + " successfully (ID: " + id + ")");
    }

    void listAllTasks() {
        printTasks(tasks);
    }

    void listByTaskStatus(Status status) {
        List<Task> filtered = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus() == status) {
                filtered.add(task);
            }
        }
        printTasks(filtered);
    }

    private Task findTask(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        throw new IllegalArgumentException("No task found with ID " + id + ".");
    }

    private void printTasks(List<Task> taskList) {
        if (taskList.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        taskList.sort(Comparator.comparingInt(Task::getId));
        for (Task task : taskList) {
            System.out.println("ID: " + task.getId());
            System.out.println("Description: " + task.getDescription());
            System.out.println("Status: " + task.getStatus().toCliValue());
            System.out.println("Created: " + task.getCreatedAt());
            System.out.println("Updated: " + task.getUpdatedAt());
            System.out.println();
        }
    }
}
