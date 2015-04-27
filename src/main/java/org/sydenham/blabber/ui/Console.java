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
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?i)(\\w+)(?:\\s+(\\S+)(?:\\s+(.+))?)?");
    private static final String TIMELINE_POST_OUTPUT_TEMPLATE = "%s (%s ago)";
    private static final String WALL_POST_OUTPUT_TEMPLATE = "%s - %s";

    private final BufferedReader in;
    private final PrintStream out;
    private final UserCommandOrchestrator userCommandOrchestrator;
    private final Clock clock;

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
            Matcher commandMatcher = COMMAND_PATTERN.matcher(in.readLine());

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
                return of(postMessage(userCommandOrchestrator, matcher.group(1), matcher.group(3)));
            case READ_TIMELINE:
                return of(readTimelineMatcher(userCommandOrchestrator, matcher.group(1)));
            case FOLLOW:
                return of(follow(userCommandOrchestrator, matcher.group(1), matcher.group(3)));
            case READ_WALL:
                return of(readWall(userCommandOrchestrator, matcher.group(1)));
            default:
                return empty();
        }
    }

    private UserCommandOrchestrator postMessage(UserCommandOrchestrator userCommandOrchestrator, String username, String msg) {
        return userCommandOrchestrator.postMessage(username, msg);
    }

    private UserCommandOrchestrator readTimelineMatcher(UserCommandOrchestrator userCommandOrchestrator, String username) {
        userCommandOrchestrator.forEachTimelinePostOf(username, post -> out.println(printTimelinePost(post)));
        return userCommandOrchestrator;
    }

    private UserCommandOrchestrator follow(UserCommandOrchestrator userCommandOrchestrator, String followerName, String followedName) {
        return userCommandOrchestrator.userAFollowsUserB(followerName, followedName);
    }

    private UserCommandOrchestrator readWall(UserCommandOrchestrator userCommandOrchestrator, String username) {
        userCommandOrchestrator.forEachWallPostOf(username, post -> out.println(printWallPost(post)));
        return userCommandOrchestrator;
    }

    private String printTimelinePost(Post post) {
        return format(TIMELINE_POST_OUTPUT_TEMPLATE, post.message, timeSinceText(post.timestamp));
    }

    private String printWallPost(Post post) {
        String timelinePost = printTimelinePost(post);
        return format(WALL_POST_OUTPUT_TEMPLATE, post.user.name, timelinePost);
    }

    private String timeSinceText(LocalDateTime timestamp) {
        String durationFormat = Duration.between(LocalDateTime.from(clock.instant().atZone(systemDefault())), timestamp).toString();
        durationFormat = durationFormat.replace("PT-", "");
        durationFormat = durationFormat.replace("-", " ");
        durationFormat = durationFormat.replaceFirst("(\\d+)S", "$1 seconds");
        durationFormat = durationFormat.replaceFirst("\\b1 seconds", "1 second");
        durationFormat = durationFormat.replaceFirst("(\\d+)M", "$1 minutes");
        durationFormat = durationFormat.replaceFirst("\\b1 minutes", "1 minute");
        durationFormat = durationFormat.replaceFirst("(\\d+)H", "$1 hours");
        durationFormat = durationFormat.replaceFirst("\\b1 hours", "1 hour");

        return durationFormat;
    }

    private enum CommandType {
        POST, READ_TIMELINE, FOLLOW, READ_WALL, QUIT;

        private static final String POST_TEXT = "->";
        private static final String FOLLOW_TEXT = "follows";
        private static final String READ_WALL_TEXT = "wall";

        private static CommandType mapFrom(String commandText) {
            if (commandText != null) {
                switch (commandText.toLowerCase()) {
                    case POST_TEXT:
                        return POST;
                    case FOLLOW_TEXT:
                        return FOLLOW;
                    case READ_WALL_TEXT:
                        return READ_WALL;
                    default:
                        return QUIT;
                }
            } else {
                return READ_TIMELINE;
            }
        }
    }
}