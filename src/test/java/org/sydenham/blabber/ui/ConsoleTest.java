package org.sydenham.blabber.ui;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.stubbing.Stubber;
import org.sydenham.blabber.domain.Post;
import org.sydenham.blabber.domain.User;
import org.sydenham.blabber.exception.ApplicationException;
import org.sydenham.blabber.service.UserCommandOrchestrator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.time.*;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConsoleTest {

    private static final String USERNAME = "username";
    private static final String FOLLOWED_USERNAME = "followed";
    private static final String MSG = "!msg! with! space! and! punc!";
    private static final String POST_MESSAGE_COMMAND_TEMPLATE = "%s -> %s";
    private static final String READ_TIMELINE_OUTPUT_TEMPLATE = "%s (%s ago)";
    private static final String FOLLOW_USER_COMMAND_TEMPLATE = "%s follows %s";
    private static final String READ_WALL_COMMAND_TEMPLATE = "%s wall";
    private static final String QUIT_COMMAND = "";
    private static final Class<Consumer<Post>> CONSUMER_CLASS = null;
    private static final LocalDateTime TWO_HOURS_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    private static final LocalDateTime ONE_HOUR_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 1, 0, 0);
    private static final LocalDateTime TWO_MINUTES_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 1, 58, 0);
    private static final LocalDateTime ONE_MINUTE_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 1, 59, 0);
    private static final LocalDateTime TWO_SECONDS_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 1, 59, 58);
    private static final LocalDateTime ONE_SECOND_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 1, 59, 59);
    private static final LocalDateTime ONE_HOUR_TWO_MINUTES_ONE_SECOND_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 57, 59);

    private Console console;
    @Mock
    private UserCommandOrchestrator userCommandOrchestratorMock;
    @Mock
    private BufferedReader userInputMock;
    @Mock
    private PrintStream userOutputMock;
    @Mock
    private Clock clockMock;

    @BeforeMethod
    public void setUp() {
        initMocks(this);

        console = new Console(userInputMock, userOutputMock, userCommandOrchestratorMock, clockMock);
    }

    @Test(expectedExceptions = ApplicationException.class)
    public void whenAnIOExceptionOccursItKillsTheApplication() throws IOException {
        when(userInputMock.readLine()).thenThrow(new IOException());

        console.run();
    }

    @Test
    public void whenAUserPostsAnUnknownMessageItInterpretsThatAsQuit() throws IOException {
        String unknownCommand = "mary unknownPart";
        when(userInputMock.readLine()).thenReturn(unknownCommand);

        console.run();

        verifyZeroInteractions(userCommandOrchestratorMock, userOutputMock);
    }

    @Test
    public void whenAUserPostsAMessageTheRequestIsForwardedOnToTheServiceLayer() throws IOException {
        String postMessageCommand = format(POST_MESSAGE_COMMAND_TEMPLATE, USERNAME, MSG);
        when(userInputMock.readLine()).thenReturn(postMessageCommand, postMessageCommand).thenReturn(QUIT_COMMAND);
        when(userCommandOrchestratorMock.postMessage(USERNAME, MSG)).thenReturn(userCommandOrchestratorMock);

        console.run();

        verify(userCommandOrchestratorMock, times(2)).postMessage(USERNAME, MSG);
        verifyZeroInteractions(userOutputMock);
    }

    @Test
    public void whenAUserRequestsATimelineReadTheRequestIsForwardedOnToTheServiceLayerAndTheResultsArePrinted() throws IOException {
        String readMessageCommand = USERNAME;
        when(userInputMock.readLine()).thenReturn(readMessageCommand, readMessageCommand).thenReturn(QUIT_COMMAND);
        simulateTimelinePostedMessagesAt(ONE_SECOND_AGO_TIMESTAMP);
        when(clockMock.instant()).thenReturn(clockInstant(ONE_SECOND_AGO_TIMESTAMP, 1l));

        console.run();

        verify(userCommandOrchestratorMock, times(2)).forEachTimelinePostOf(eq(USERNAME), any(CONSUMER_CLASS));
        verify(userOutputMock, times(2)).println(format(READ_TIMELINE_OUTPUT_TEMPLATE, MSG, "1 second"));
    }

    @Test
    public void readCommandPrintsOutTimesCorrectly() throws IOException {
        String readMessageCommand = USERNAME;
        when(userInputMock.readLine()).thenReturn(readMessageCommand).thenReturn(QUIT_COMMAND);
        simulateTimelinePostedMessagesAt(
                ONE_SECOND_AGO_TIMESTAMP,
                TWO_SECONDS_AGO_TIMESTAMP,
                ONE_MINUTE_AGO_TIMESTAMP,
                TWO_MINUTES_AGO_TIMESTAMP,
                ONE_HOUR_AGO_TIMESTAMP,
                TWO_HOURS_AGO_TIMESTAMP,
                ONE_HOUR_TWO_MINUTES_ONE_SECOND_AGO_TIMESTAMP);
        when(clockMock.instant()).thenReturn(clockInstant(TWO_HOURS_AGO_TIMESTAMP, 7200l));

        console.run();

        InOrder inOrder = inOrder(userOutputMock);

        verify(userCommandOrchestratorMock).forEachTimelinePostOf(eq(USERNAME), any(CONSUMER_CLASS));
        List<String> durations = asList("1 second", "2 seconds", "1 minute", "2 minutes", "1 hour", "2 hours", "1 hour 2 minutes 1 second");
        durations.forEach(duration -> inOrder.verify(userOutputMock).println(format(READ_TIMELINE_OUTPUT_TEMPLATE, MSG, duration)));
    }

    @Test
    public void whenAUserRequestsToFollowAnotherUserTheRequestIsForwardedOnToTheServiceLayer() throws IOException {
        String followUserCommand = format(FOLLOW_USER_COMMAND_TEMPLATE, USERNAME, FOLLOWED_USERNAME);
        when(userInputMock.readLine()).thenReturn(followUserCommand, followUserCommand).thenReturn(QUIT_COMMAND);
        when(userCommandOrchestratorMock.userAFollowsUserB(USERNAME, FOLLOWED_USERNAME)).thenReturn(userCommandOrchestratorMock);

        console.run();

        verify(userCommandOrchestratorMock, times(2)).userAFollowsUserB(USERNAME, FOLLOWED_USERNAME);
        verifyNoMoreInteractions(userOutputMock);
    }

    @Test
    public void whenAUserRequestsWallTheRequestIsForwardedOnToTheServiceLayerAndTheResultsArePrinted() throws IOException {
        String readWallCommand = format(READ_WALL_COMMAND_TEMPLATE, USERNAME);
        when(userInputMock.readLine()).thenReturn(readWallCommand, readWallCommand).thenReturn(QUIT_COMMAND);
        simulateWallPostedMessages();
        when(clockMock.instant()).thenReturn(clockInstant(ONE_SECOND_AGO_TIMESTAMP, 1l));

        console.run();

        verify(userCommandOrchestratorMock, times(2)).forEachWallPostOf(eq(USERNAME), any(CONSUMER_CLASS));
        verify(userOutputMock, times(2)).println(format("%s - " + READ_TIMELINE_OUTPUT_TEMPLATE, USERNAME, MSG, "1 second"));
    }

    private Instant clockInstant(LocalDateTime timestamp, long secondsAgo) {
        return timestamp.plusSeconds(secondsAgo).atZone(systemDefault()).toInstant();
    }

    private void simulateTimelinePostedMessagesAt(LocalDateTime... timestamps) {
        postsWithTimestampsResponse(timestamps).when(userCommandOrchestratorMock).forEachTimelinePostOf(eq(USERNAME), any(CONSUMER_CLASS));
    }

    private void simulateWallPostedMessages() {
        postsWithTimestampsResponse(ONE_SECOND_AGO_TIMESTAMP).when(userCommandOrchestratorMock).forEachWallPostOf(eq(USERNAME), any(CONSUMER_CLASS));
    }

    @SuppressWarnings("unchecked")
    private Stubber postsWithTimestampsResponse(LocalDateTime... timestamps) {
        return doAnswer(invocation -> {
            Consumer<Post> consumer = (Consumer<Post>) invocation.getArguments()[1];
            for (LocalDateTime timestamp : timestamps) {
                consumer.accept(new Post(User.from(USERNAME), MSG, timestamp));
            }
            return null;
        });
    }
}