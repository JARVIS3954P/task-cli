package com.taskcli;

enum Status {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String cliValue;

    Status(String cliValue) {
        this.cliValue = cliValue;
    }

    String toCliValue() {
        return cliValue;
    }

    static Status fromCliValue(String value) {
        for (Status status : values()) {
            if (status.cliValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(
                "Invalid status '" + value + "'. Use todo, in-progress, or done.");
    }
}
