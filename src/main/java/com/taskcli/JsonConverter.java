package com.taskcli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal JSON serializer/parser for this application's known task schema.
 * It intentionally supports only the JSON values needed by the task tracker.
 */
class JsonConverter {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    String convertToJson(Task task) {
        StringBuilder json = new StringBuilder();
        json.append("{")
            .append("\"id\":").append(task.getId()).append(',')
            .append("\"description\":\"").append(escapeJson(task.getDescription())).append("\",")
            .append("\"status\":\"").append(task.getStatus().toCliValue()).append("\",")
            .append("\"createdAt\":\"").append(escapeJson(FORMATTER.format(task.getCreatedAt()))).append("\",")
            .append("\"updatedAt\":\"").append(escapeJson(FORMATTER.format(task.getUpdatedAt()))).append("\"")
            .append("}");
        return json.toString();
    }

    String convertToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(convertToJson(tasks.get(i)));
        }
        json.append(']');
        return json.toString();
    }

    List<Task> convertToTasks(String json) {
        Parser parser = new Parser(json);
        List<Task> tasks = new ArrayList<>();
        parser.skipWhitespace();
        parser.expect('[');
        parser.skipWhitespace();

        if (parser.consume(']')) {
            parser.ensureEnd();
            return tasks;
        }

        while (true) {
            tasks.add(parseTask(parser));
            parser.skipWhitespace();
            if (parser.consume(']')) {
                break;
            }
            parser.expect(',');
        }

        parser.ensureEnd();
        return tasks;
    }

    private Task parseTask(Parser parser) {
        parser.skipWhitespace();
        parser.expect('{');

        Integer id = null;
        String description = null;
        String statusValue = null;
        String createdAtValue = null;
        String updatedAtValue = null;

        parser.skipWhitespace();
        if (parser.consume('}')) {
            throw parser.error("Task object is empty.");
        }

        while (true) {
            parser.skipWhitespace();
            String key = parser.readString();
            parser.skipWhitespace();
            parser.expect(':');
            parser.skipWhitespace();

            switch (key) {
                case "id":
                    id = parser.readInteger();
                    break;
                case "description":
                    description = parser.readString();
                    break;
                case "status":
                    statusValue = parser.readString();
                    break;
                case "createdAt":
                    createdAtValue = parser.readString();
                    break;
                case "updatedAt":
                    updatedAtValue = parser.readString();
                    break;
                default:
                    throw parser.error("Unexpected task field: " + key);
            }

            parser.skipWhitespace();
            if (parser.consume('}')) {
                break;
            }
            parser.expect(',');
        }

        if (id == null || description == null || statusValue == null
                || createdAtValue == null || updatedAtValue == null) {
            throw parser.error("Task is missing one or more required fields.");
        }

        Status status = Status.fromCliValue(statusValue);
        LocalDateTime createdAt = parseDateTime(createdAtValue, parser);
        LocalDateTime updatedAt = parseDateTime(updatedAtValue, parser);
        return new Task(id, description, status, createdAt, updatedAt);
    }

    private LocalDateTime parseDateTime(String value, Parser parser) {
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (RuntimeException e) {
            throw parser.error("Invalid timestamp: " + value);
        }
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\b': escaped.append("\\b"); break;
                case '\f': escaped.append("\\f"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }
        return escaped.toString();
    }

    private static final class Parser {
        private final String input;
        private int index;

        Parser(String input) {
            if (input == null) {
                throw new IllegalArgumentException("JSON input cannot be null.");
            }
            this.input = input;
        }

        void skipWhitespace() {
            while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
                index++;
            }
        }

        boolean consume(char expected) {
            skipWhitespace();
            if (index < input.length() && input.charAt(index) == expected) {
                index++;
                return true;
            }
            return false;
        }

        void expect(char expected) {
            skipWhitespace();
            if (index >= input.length() || input.charAt(index) != expected) {
                throw error("Expected '" + expected + "'.");
            }
            index++;
        }

        String readString() {
            skipWhitespace();
            expect('"');
            StringBuilder value = new StringBuilder();

            while (index < input.length()) {
                char c = input.charAt(index++);
                if (c == '"') {
                    return value.toString();
                }
                if (c == '\\') {
                    if (index >= input.length()) {
                        throw error("Incomplete escape sequence.");
                    }
                    char escaped = input.charAt(index++);
                    switch (escaped) {
                        case '"': value.append('"'); break;
                        case '\\': value.append('\\'); break;
                        case '/': value.append('/'); break;
                        case 'b': value.append('\b'); break;
                        case 'f': value.append('\f'); break;
                        case 'n': value.append('\n'); break;
                        case 'r': value.append('\r'); break;
                        case 't': value.append('\t'); break;
                        case 'u':
                            if (index + 4 > input.length()) {
                                throw error("Incomplete Unicode escape.");
                            }
                            String hex = input.substring(index, index + 4);
                            try {
                                value.append((char) Integer.parseInt(hex, 16));
                            } catch (NumberFormatException e) {
                                throw error("Invalid Unicode escape: " + hex);
                            }
                            index += 4;
                            break;
                        default:
                            throw error("Invalid escape sequence: \\" + escaped);
                    }
                } else {
                    if (c < 0x20) {
                        throw error("Unescaped control character in string.");
                    }
                    value.append(c);
                }
            }
            throw error("Unterminated string.");
        }

        int readInteger() {
            skipWhitespace();
            int start = index;
            if (index < input.length() && input.charAt(index) == '-') {
                index++;
            }
            while (index < input.length() && Character.isDigit(input.charAt(index))) {
                index++;
            }
            if (start == index || (index == start + 1 && input.charAt(start) == '-')) {
                throw error("Expected integer.");
            }
            try {
                return Integer.parseInt(input.substring(start, index));
            } catch (NumberFormatException e) {
                throw error("Integer is out of range.");
            }
        }

        void ensureEnd() {
            skipWhitespace();
            if (index != input.length()) {
                throw error("Unexpected trailing content.");
            }
        }

        IllegalArgumentException error(String message) {
            return new IllegalArgumentException(
                    message + " At JSON character position " + index + ".");
        }
    }
}
