package org.sydenham.blabber.ui;

import org.sydenham.blabber.domain.Post;
import org.sydenham.blabber.exception.ApplicationException;
import org.sydenham.blabber.service.UserCommandOrchestrator;

import java.io.*;
import java.time.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.time.ZoneId.systemDefault;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Console {
    public static final Pattern COMMAND_PATTERN = Pattern.compile("(?i)(\\w+)(?:\\s+(\\S+)(?:\\s+(\\w+))?)?");
    public static final String READ_MESSAGE_OUTPUT_TEMPLATE = "%s (%s ago)";

    private final BufferedReader in;
    private final PrintStream out;
    private final UserCommandOrchestrator userCommandOrchestrator;
    private Clock clock;

    public Console(BufferedReader in, PrintStream out, UserCommandOrchestrator userCommandOrchestrator, Clock clock) {
        this.in = in;
        this.out = out;
        this.userCommandOrchestrator = userCommandOrchestrator;
        this.clock = clock;
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
            Matcher commandMatcher = COMMAND_PATTERN.matcher(userInput);

            if (commandMatcher.matches()) {
                CommandType commandType = CommandType.mapFrom(commandMatcher.group(2));
                maybeUserCommandOrchestrator = handleCommand(maybeUserCommandOrchestrator.get(), commandType, commandMatcher);
            } else {
                maybeUserCommandOrchestrator = empty();
            }
        }
    }

    private Optional<UserCommandOrchestrator> handleCommand(UserCommandOrchestrator userCommandOrchestrator, CommandType commandType, Matcher matcher) {
        switch (commandType) {
            case POST:
                return of(doPostMessage(userCommandOrchestrator, matcher.group(1), matcher.group(3)));
            case READ_TIMELINE:
                return of(doReadTimelineMatcher(matcher.group(1)));
            default:
                return empty();
        }
    }

    private UserCommandOrchestrator doPostMessage(UserCommandOrchestrator userCommandOrchestrator, String username, String msg) {
        return userCommandOrchestrator.postMessage(username, msg);
    }

    private UserCommandOrchestrator doReadTimelineMatcher(String username) {
        userCommandOrchestrator.forEachTimelinePostOf(username, post -> out.println(print(post, READ_MESSAGE_OUTPUT_TEMPLATE)));
        return userCommandOrchestrator;
    }

    private String print(Post post, String printTemplate) {
        return format(printTemplate, post.message, timeSinceText(post.timestamp));
    }

    private String timeSinceText(LocalDateTime timestamp) {
        String durationFormat = Duration.between(LocalDateTime.from(clock.instant().atZone(systemDefault())), timestamp).toString();
        durationFormat = durationFormat.replace("PT-", "");
        durationFormat = durationFormat.replaceFirst("(\\d+)S", "$1 seconds");
        durationFormat = durationFormat.replaceFirst("\\b1 seconds", "1 second");
        durationFormat = durationFormat.replaceFirst("(\\d+)M", "$1 minutes");
        durationFormat = durationFormat.replaceFirst("\\b1 minutes", "1 minute");

        return durationFormat;
    }

    private enum CommandType {
        POST, READ_TIMELINE, QUIT;

        public static final String POST_TEXT = "->";

        public static CommandType mapFrom(String commandText) {
            if (commandText != null) {
                switch (commandText) {
                    case POST_TEXT:
                        return POST;
                    default:
                        return QUIT;
                }
            } else {
                return READ_TIMELINE;
            }
        }
    }
}
