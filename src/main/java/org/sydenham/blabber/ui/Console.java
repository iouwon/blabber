package org.sydenham.blabber.ui;

import org.sydenham.blabber.exception.ApplicationException;
import org.sydenham.blabber.service.UserCommandOrchestrator;

import java.io.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Console {
    public static final Pattern COMMAND_PATTERN = Pattern.compile("(?i)(\\w+)(?:\\s+(\\S*)(?:\\s+(\\w+))?)?");
    private final BufferedReader in;
    private final PrintStream out;
    private final UserCommandOrchestrator userCommandOrchestrator;

    public Console(BufferedReader in, PrintStream out, UserCommandOrchestrator userCommandOrchestrator) {
        this.in = in;
        this.out = out;
        this.userCommandOrchestrator = userCommandOrchestrator;
    }

    public void run() {
        try {
            runCommandLoop(of(userCommandOrchestrator));
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private void runCommandLoop(Optional<UserCommandOrchestrator> maybeUserCommandOrchestrator) throws IOException {
        while (maybeUserCommandOrchestrator.isPresent()) {
            String userInput = in.readLine();
            Matcher comandMatcher = COMMAND_PATTERN.matcher(userInput);

            if (comandMatcher.matches()) {
                CommandType commandType = CommandType.mapFrom(comandMatcher.group(2));
                maybeUserCommandOrchestrator = handleCommand(maybeUserCommandOrchestrator.get(), commandType, comandMatcher);
            } else {
                maybeUserCommandOrchestrator = empty();
            }
        }
    }

    private Optional<UserCommandOrchestrator> handleCommand(UserCommandOrchestrator userCommandOrchestrator, CommandType commandType, Matcher matcher) {
        switch (commandType) {
            case POST:
                return of(doPostMessage(userCommandOrchestrator, matcher.group(1), matcher.group(3)));
            default:
                return empty();
        }
    }

    private UserCommandOrchestrator doPostMessage(UserCommandOrchestrator userCommandOrchestrator, String username, String msg) {
        return userCommandOrchestrator.postMessage(username, msg);
    }

    private enum CommandType {
        POST, QUIT;

        public static final String POST_TEXT = "->";

        public static CommandType mapFrom(String commandText) {
            switch (commandText) {
                case POST_TEXT:
                    return POST;
                default:
                    return QUIT;
            }
        }
    }
}
