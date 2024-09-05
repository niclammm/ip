package repsmax;

import java.util.List;

/**
 * Parses user inputs and executes corresponding commands.
 * The <code>Parser</code> class is responsible for interpreting user input, managing tasks,
 * and interacting with the user interface and storage.
 */
public class Parser {

    // Constants for command keywords
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_FIND = "find";

    /**
     * Parses the user's input and executes the corresponding command.
     *
     * @param userInput The input string entered by the user.
     * @param tasks     The list of tasks to be managed.
     * @param ui        The user interface to interact with the user.
     * @param storage   The storage handler for saving and loading tasks.
     */
    public String parse(String userInput, TaskList tasks, Ui ui, Storage storage) {
        String[] splitInput = userInput.split(" ", 2);
        String command = splitInput[0];

        switch (command) {
            case COMMAND_LIST:
                return handleListCommand(tasks, ui);

            case COMMAND_MARK:
                return handleMarkCommand(splitInput, tasks, ui, storage);

            case COMMAND_UNMARK:
                return handleUnmarkCommand(splitInput, tasks, ui, storage);

            case COMMAND_TODO:
                return handleTodoCommand(splitInput, tasks, ui, storage);

            case COMMAND_DEADLINE:
                return handleDeadlineCommand(splitInput, tasks, ui, storage);

            case COMMAND_EVENT:
                return handleEventCommand(splitInput, tasks, ui, storage);

            case COMMAND_DELETE:
                return handleDeleteCommand(splitInput, tasks, ui, storage);

            case COMMAND_BYE:
                return handleByeCommand(tasks, ui, storage);

            case COMMAND_FIND:
                return handleFindCommand(splitInput, tasks, ui);

            default:
                return ("Unknown command: " + userInput);
        }
    }

    /**
     * Handles the "list" command, displaying all tasks.
     *
     * @param tasks The list of tasks to display.
     * @param ui    The user interface to interact with the user.
     */
    private String handleListCommand(TaskList tasks, Ui ui) {
        StringBuilder response = new StringBuilder();
        response.append("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            response.append((i + 1)).append(".").append(tasks.get(i)).append("\n");
        }
        return response.toString();
    }

    /**
     * Handles the "mark" command, marking a task as done.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be managed.
     * @param ui         The user interface to interact with the user.
     * @param storage    The storage handler for saving the updated tasks.
     */
    private String handleMarkCommand(String[] splitInput, TaskList tasks, Ui ui, Storage storage) {
        try {
            int markIndex = Integer.parseInt(splitInput[1]) - 1;
            if (markIndex >= 0 && markIndex < tasks.size()) {
                tasks.get(markIndex).setDone();
                storage.save(tasks);
                return ("Nice! I've marked this task as done:\n" + tasks.get(markIndex));
            } else {
                return ("OOPS!!! The task number is out of range.");
            }
        } catch (NumberFormatException e) {
            return ("OOPS!!! The task number must be an integer.");
        }
    }

    /**
     * Handles the "unmark" command, marking a task as not done.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be managed.
     * @param ui         The user interface to interact with the user.
     * @param storage    The storage handler for saving the updated tasks.
     */
    private String handleUnmarkCommand(String[] splitInput, TaskList tasks, Ui ui, Storage storage) {
        try {
            int unmarkIndex = Integer.parseInt(splitInput[1]) - 1;
            if (unmarkIndex >= 0 && unmarkIndex < tasks.size()) {
                tasks.get(unmarkIndex).setUndone();
                storage.save(tasks);
                return ("OK, I've marked this task as not done yet:\n" + tasks.get(unmarkIndex));
            } else {
                return ("OOPS!!! The task number is out of range.");
            }
        } catch (NumberFormatException e) {
            return ("OOPS!!! The task number must be an integer.");
        }
    }

    /**
     * Handles the "todo" command, adding a new ToDo task.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be managed.
     * @param ui         The user interface to interact with the user.
     * @param storage    The storage handler for saving the updated tasks.
     */
    private String handleTodoCommand(String[] splitInput, TaskList tasks, Ui ui, Storage storage) {
        if (splitInput.length > 1) {
            String description = splitInput[1];
            Todo newTodo = new Todo(description);
            tasks.add(newTodo);
            storage.save(tasks);
            System.out.println("Task list after adding: " + tasks);
            return "Got it. I've added this task:\n" + tasks.get(tasks.size() - 1) +
                    "\nNow you have " + tasks.size() + " tasks in the list.";
        } else {
            return ("OOPS!!! The description of a todo cannot be empty.");
        }
    }

    /**
     * Handles the "deadline" command, adding a new Deadline task.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be managed.
     * @param ui         The user interface to interact with the user.
     * @param storage    The storage handler for saving the updated tasks.
     */
    private String handleDeadlineCommand(String[] splitInput, TaskList tasks, Ui ui, Storage storage) {
        try {
            String[] parts = splitInput[1].split("/by ", 2);
            String description = parts[0];
            String by = parts[1];
            tasks.add(new Deadline(description, by));
            storage.save(tasks);
            return "Got it. I've added this task:\n" + tasks.get(tasks.size() - 1) +
            ("\nNow you have " + tasks.size() + " tasks in the list.");
        } catch (ArrayIndexOutOfBoundsException e) {
            return ("OOPS!!! The deadline command must include '/by <date/time>'.");
        }
    }

    /**
     * Handles the "event" command, adding a new Event task.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be managed.
     * @param ui         The user interface to interact with the user.
     * @param storage    The storage handler for saving the updated tasks.
     */
    private String handleEventCommand(String[] splitInput, TaskList tasks, Ui ui, Storage storage) {
        try {
            String[] parts = splitInput[1].split("/from ", 2);
            String[] fromTo = parts[1].split("/to ", 2);
            String description = parts[0];
            String from = fromTo[0];
            String to = fromTo[1];
            tasks.add(new Event(description, from, to));
            storage.save(tasks);
            return ("Got it. I've added this task:\n" + tasks.get(tasks.size() - 1)) +
            ("\nNow you have " + tasks.size() + " tasks in the list.");
        } catch (ArrayIndexOutOfBoundsException e) {
            return ("OOPS!!! The event command must include '/from <start date/time>' and '/to <end date/time>'.");
        }
    }

    /**
     * Handles the "delete" command, removing a task from the list.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be managed.
     * @param ui         The user interface to interact with the user.
     * @param storage    The storage handler for saving the updated tasks.
     */
    private String handleDeleteCommand(String[] splitInput, TaskList tasks, Ui ui, Storage storage) {
        try {
            int deleteIndex = Integer.parseInt(splitInput[1]) - 1;
            if (deleteIndex >= 0 && deleteIndex < tasks.size()) {  // Check index bounds
                Task removedTask = tasks.remove(deleteIndex);  // Remove the task
                storage.save(tasks);  // Save after deletion
                return ("Noted. I've removed this task:\n" + removedTask) +
                        "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                return ("OOPS!!! The task number is out of range.");
            }
        } catch (NumberFormatException e) {
            return ("OOPS!!! The task number must be an integer.");
        }
    }

    /**
     * Handles the "bye" command, saving the current tasks and exiting the program.
     *
     * @param tasks   The list of tasks to be saved.
     * @param ui      The user interface to interact with the user.
     * @param storage The storage handler for saving the tasks.
     */
    private String handleByeCommand(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks);
        return ("Bye. Hope to see you again soon!");
    }

    /**
     * Handles the "find" command, searching for tasks with the given keyword.
     *
     * @param splitInput The user's input split into command and arguments.
     * @param tasks      The list of tasks to be searched.
     * @param ui          The user interface to interact with the user.
     */
    private String handleFindCommand(String[] splitInput, TaskList tasks, Ui ui) {
        if (splitInput.length > 1) {
            String keyword = splitInput[1];
            List<Task> results = tasks.find(keyword);
            ui.showSearchResults(results);
        } else {
            return ("OOPS!!! The find command must include a keyword.");
        }
        String keyword = splitInput[1];
        List<Task> foundTasks = tasks.find(keyword);
        if (foundTasks.isEmpty()) {
            return "There are no matching tasks in your list.";
        }
        StringBuilder response = new StringBuilder();
        response.append("Here are the matching tasks in your list:\n");
        for (int i = 0; i < foundTasks.size(); i++) {
            response.append((i + 1)).append(".").append(foundTasks.get(i)).append("\n");
        }
        return response.toString();
    }
}

