package com.taskcli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class TaskStorage {
    private final Path path;

    TaskStorage() throws IOException {
        this(Path.of(System.getProperty("user.dir"), "tasks.json"));
    }

    TaskStorage(Path path) throws IOException {
        this.path = path;
        if (Files.notExists(path)) {
            Files.writeString(path, "[]", StandardCharsets.UTF_8);
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Task storage path is not a regular file: " + path);
        }
    }

    String readFromFile() throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    void writeToFile(String content) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        Path temp = Files.createTempFile(parent, "tasks-", ".json.tmp");
        try {
            Files.writeString(temp, content, StandardCharsets.UTF_8);
            try {
                Files.move(temp, path,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
