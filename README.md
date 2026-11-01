# Task Tracker CLI (Java, No External Dependencies)

```aiignore
https://roadmap.sh/projects/task-tracker
```

A command-line task management application built with Java. It supports creating, updating, deleting, and tracking tasks. Task data is persisted in a JSON file so that it remains available across application runs.

## Requirements

- Java 11 or newer
- No third-party libraries or frameworks

## Compile

From `src/main/java`:

```powershell
javac com\taskcli\*.java
```

## Run

Run commands from the directory where you want `tasks.json` to be stored. The file is created in the JVM's working directory if it does not already exist.

### Add a Task

```powershell
java com.taskcli.Main add "Buy groceries"
```

### Update a Task

```powershell
java com.taskcli.Main update 1 "Buy groceries and cook dinner"
```

### Change Task Status

```powershell
java com.taskcli.Main mark-in-progress 1
java com.taskcli.Main mark-done 1
```

### List Tasks

```powershell
java com.taskcli.Main list
java com.taskcli.Main list todo
java com.taskcli.Main list in-progress
java com.taskcli.Main list done
```

### Delete a Task

```powershell
java com.taskcli.Main delete 1
```

**Note:** Replace `1` with the ID of the task you want to modify or delete.

Descriptions containing spaces must be quoted in the shell.

## Features

- Add tasks with unique numeric IDs.
- Update task descriptions.
- Delete tasks by ID.
- Track task status: `todo`, `in-progress`, or `done`.
- List all tasks or filter them by status.
- Record task creation and last-update timestamps.
- Persist tasks in a JSON file across application runs.
- Validate command-line arguments and handle invalid input.
- Use Java's standard library for JSON conversion and file operations.

## Concepts Revised

This project revisits core Java concepts and demonstrates how they work together in a small, practical application.

### 1. Object-Oriented Programming (OOP)

**What:** A programming approach that organizes code around objects containing data and behavior.

**How it is used:** The project separates responsibilities across classes:

- `Task` represents an individual task and its properties.
- `TaskManager` handles task operations and coordinates persistence.
- `TaskStorage` manages reading and writing task data.
- `JsonConverter` converts tasks between Java objects and JSON.
- `Main` acts as the application's entry point and processes commands.
- `Status` represents the available task states.

This separation keeps each class focused on a specific responsibility.

### 2. Classes, Objects, Fields, and Methods

**What:** Classes define the structure and behavior of objects. Fields store object state, while methods implement behavior.

**How it is used:** Each `Task` object stores an ID, description, status, creation timestamp, and update timestamp. Methods in `TaskManager` perform operations such as adding, updating, deleting, and listing tasks.

### 3. Encapsulation

**What:** Encapsulation restricts direct access to an object's internal state and exposes controlled ways to interact with it.

**How it is used:** The `Task` class keeps its fields private and exposes methods for accessing and modifying task data. This helps maintain control over the task's state.

### 4. Enums

**What:** An enum defines a fixed set of named constants.

**How it is used:** The `Status` enum represents the three supported task states: `TODO`, `IN_PROGRESS`, and `DONE`. It prevents arbitrary status values from being used internally and provides conversion between Java enum values and their CLI/JSON representations.

### 5. Collections and Generics

**What:** Collections store and organize groups of objects. Generics provide compile-time type safety for the elements they contain.

**How it is used:** An `ArrayList<Task>` stores the tasks loaded from the JSON file. The `List<Task>` interface allows the application to work with a collection of tasks without depending everywhere on a particular implementation.

### 6. Command-Line Arguments and Arrays

**What:** Command-line arguments allow users to pass input to a program when launching it. Java provides these arguments through the `String[] args` parameter of `main`.

**How it is used:** `Main` reads the command, task ID, description, and optional status filter from `args`. It validates the input and dispatches the requested operation to `TaskManager`.

For example:

```powershell
java com.taskcli.Main update 1 "Updated description"
```

The arguments identify the `update` operation, task ID `1`, and the new description.

### 7. Conditional Logic and Switch-Based Command Dispatch

**What:** Conditional statements select behavior based on input or application state.

**How it is used:** The command dispatcher determines which operation to execute. Validation logic checks whether the command has the required arguments and whether values such as task IDs and status filters are valid.

### 8. Exception Handling

**What:** Exception handling allows a program to respond to errors without abruptly terminating through an unhandled exception.

**How it is used:** The application handles invalid commands, invalid task IDs, unsupported status values, and file or parsing failures. This allows it to display useful error messages when an operation cannot be completed.

### 9. File Handling and Java NIO

**What:** File handling allows an application to read and write persistent data. Java NIO provides APIs for working with paths and files.

**How it is used:** `TaskStorage` uses Java's standard file APIs to locate `tasks.json`, read its contents, and write updated task data. If the file does not exist, it initializes the file with an empty JSON array.

### 10. Serialization and Deserialization

**What:** Serialization converts an in-memory object into a representation that can be stored or transmitted. Deserialization reconstructs objects from that representation.

**How it is used:** `JsonConverter` converts task objects into JSON before persistence and parses JSON back into task objects when the application starts.

Because the project does not use third-party libraries, the JSON conversion and parsing logic is implemented using Java code.

### 11. String Processing and Escaping

**What:** String processing involves examining, transforming, and validating text. Escaping ensures that special characters are represented correctly in a particular format.

**How it is used:** `JsonConverter` escapes characters such as quotation marks, backslashes, and control characters when generating JSON. Its parser interprets JSON escape sequences when reading task descriptions.

This allows descriptions containing characters such as quotes and backslashes to be stored and retrieved correctly.

### 12. Date and Time API

**What:** The Java Date and Time API provides classes for representing and manipulating dates and times.

**How it is used:** The `Task` class uses `LocalDateTime` to represent task creation and update timestamps. `JsonConverter` converts these timestamps to strings for storage and parses them when loading tasks.

### 13. Interfaces and Separation of Responsibilities

**What:** An interface defines a contract that implementations can satisfy. Programming against interfaces reduces dependence on specific implementations.

**How it is used:** The project uses the `List<Task>` interface rather than exposing `ArrayList<Task>` everywhere. This makes the collection code less dependent on one concrete implementation.

The classes also separate command handling, task management, JSON conversion, and file persistence so that each concern can be understood and maintained independently.

### 14. Sorting and Filtering Collections

**What:** Filtering selects elements that satisfy a condition, while sorting arranges elements according to a defined order.

**How it is used:** The task-listing functionality can display all tasks or only tasks matching a requested status. Tasks are displayed in ID order, making the output easier to read.

### 15. Input Validation and Defensive Programming

**What:** Input validation checks that data satisfies the application's requirements before it is processed. Defensive programming anticipates invalid or unexpected input.

**How it is used:** The application validates command names, argument counts, task IDs, descriptions, and status filters. This prevents invalid input from being treated as a valid task operation.

### 16. Data Persistence and Application Lifecycle

**What:** Persistence allows data to survive after a program terminates. Application lifecycle management determines when data is loaded, modified, and saved.

**How it is used:** `TaskManager` loads existing tasks during initialization. After a successful mutation, the updated collection is saved to `tasks.json`. When the application is launched again, it reloads the previously saved tasks.

This is necessary because each CLI command runs as a separate application process; in-memory data alone would not survive between commands.

### 17. Temporary Files and Safer File Replacement

**What:** Writing directly over an existing data file can leave it incomplete if a write fails. Writing to a temporary file before replacing the original can reduce this risk.

**How it is used:** `TaskStorage` writes updated JSON to a temporary file and then attempts to replace the existing file. It uses an atomic move when supported by the filesystem and falls back to a regular replacement when atomic moves are unsupported.

This improves write safety, although it does not provide full transactional guarantees or coordinate concurrent application instances.

## Application Workflow

The application follows this general flow:

1. `Main` receives and validates the command-line arguments.
2. `TaskManager` loads existing tasks through `TaskStorage`.
3. `TaskManager` executes the requested operation.
4. When a task is modified, `JsonConverter` converts the task collection into JSON.
5. `TaskStorage` saves the updated JSON to `tasks.json`.
6. The application prints the result or an appropriate error message.

## Notes and Limitations

- Tasks are loaded from `tasks.json` at startup and saved after each successful mutation.
- The JSON conversion and parsing logic uses the Java standard library only.
- File writes use a temporary file and replace the previous file where supported.
- The JSON parser is designed for this application's task data format; it is not intended to be a general-purpose JSON library.
- The application is designed for local, single-user CLI usage. Concurrent executions are not coordinated, so running multiple commands simultaneously may cause lost updates.
- The file location depends on the JVM working directory, not necessarily the directory containing the compiled classes.

## Possible Improvements

- Add automated unit tests for task operations, JSON conversion, and error handling.
- Improve JSON validation and test malformed input.
- Add integration tests that execute commands against a temporary data file.
- Add file-locking or coordination if concurrent executions become a requirement.
- Consider a standard JSON library if the no-dependencies constraint is removed.