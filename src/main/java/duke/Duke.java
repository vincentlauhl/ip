package duke;

import commands.ExitCommand;
import commands.CommandResult;
import commands.Command;
import commands.PrintOptions;

import constants.Message;
import parser.Parser;
import storage.Storage;
import task.Task;
import tasklist.TaskList;
import ui.Ui;

import java.io.IOException;

/**
 * The class that contains the main code for the execution of the Duke Program.
 */
public class Duke {

    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /**
     * Creates a Duke object to run the application. It also tries to find and load
     * the text file duke.txt.
     */
    public Duke() {
        ui = new Ui();
        storage = new Storage();
        tasks = new TaskList();
        ui.printStartingMessage();
        ui.printMessage(Message.GETTING_TASK);
        try {
            storage.loadTextFile(tasks);
            ui.printMessage(Message.DONE);
        } catch (DukeException error) {
            ui.printMessage(Message.FILE_NOT_CREATED);
            ui.printMessage(Message.DONE);
        } catch (IOException error) {
            ui.printMessage(Message.FILE_READING_ERROR_MESSAGE);
        } catch (Exception error) {
            ui.printMessage(Message.FILE_ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Repeats the process of getting an input from the user, creating a Parser to parse
     * the input, creating a Command object based on the parsed input, then executing the
     * command and showing the command output to the user.
     * Stops when an ExitCommand is created.
     */
    //@@author vincentlauhl-reused
    //Reused from https://github.com/se-edu/addressbook-level2/blob/master/src/seedu/addressbook/Main.java
    //with minor modifications
    public void run(){
        Command command = null;
        while (!(command instanceof ExitCommand)){
            String input = ui.getUserCommand();
            command = new Parser().parseInput(input);
            CommandResult result = executeCommand(command);
            ui.showResult(result);
        }
        exitProcess();
    }
    //@@author

    private void exitProcess() {
        try {
            storage.saveTasksToFile(tasks);
        } catch (IOException error) {
            ui.printMessage(Message.FILE_WRITING_ERROR_MESSAGE);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        new Duke().run();
    }

    /**
     * Passes the tasks to the command and tries to execute the command. Returns
     * appropriate error messages if execution fails.
     *
     * @param command Command that needs to be executed.
     * @return Message and actions from the execution of the command.
     */
    private CommandResult executeCommand(Command command) {
        String errorMessage;
        try {
            command.passTaskList(tasks);
            return command.execute();
        } catch (IndexOutOfBoundsException error) {
            errorMessage = Message.giveSensibleRange(Task.getTotalTasks());
        } catch (NumberFormatException error) {
            errorMessage = Message.PROMPT_NUMBER;
        } catch (DefaultException error) {
            errorMessage = Message.DEFAULT_ERROR_MESSAGE;
        } catch (DukeException error) {
            errorMessage = Message.TYPE_SUITABLE_COMMAND_MESSAGE;
        }
        return new CommandResult(errorMessage, PrintOptions.DEFAULT);
    }
}
