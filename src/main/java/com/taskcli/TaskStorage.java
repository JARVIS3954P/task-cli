package com.taskcli;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.io.IOException;

class TaskStorage {
    Path path = Path.of(System.getProperty("user.dir"),"tasks.json");

    TaskStorage() throws IOException {
        initialize();
    }

    public void initialize() throws IOException {
        if(Files.notExists(path)){
            Files.writeString(path, "[]", StandardOpenOption.CREATE_NEW);
        }
        System.out.println("Initialized tasks persistence");
    }

    public void writeToFile(String content) throws IOException {
        Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING);
    }
}