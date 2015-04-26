package org.sydenham.blabber.ui;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.sydenham.blabber.domain.Post;
import org.sydenham.blabber.domain.User;
import org.sydenham.blabber.exception.ApplicationException;
import org.sydenham.blabber.service.UserCommandOrchestrator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.time.*;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.ZoneId.systemDefault;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConsoleTest {

    private static final String USERNAME = "username";
    private static final String MSG = "msg";
    private static final String POST_MESSAGE_COMMAND_TEMPLATE = "%s -> %s";
    private static final String READ_MESSAGE_OUTPUT_TEMPLATE = "%s (%s ago)";
    private static final String QUIT_COMMAND = "";
    private static final Class<Consumer<Post>> CONSUMER_CLASS = null;
    private static final LocalDateTime TWO_MINUTES_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    private static final LocalDateTime ONE_MINUTE_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 1, 0);
    private static final LocalDateTime TWO_SECONDS_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 1, 58);
    private static final LocalDateTime ONE_SECOND_AGO_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 1, 59);

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
        String unknownCommand = "mary unknownpart";
        when(userInputMock.readLine()).thenReturn(unknownCommand);

        console.run();

        verifyZeroInteractions(userCommandOrchestratorMock, userOutputMock);
    }

    @Test
    public void whenAUserPostsAMessageTheRequestIsForwardedOnToTheServiceLayer() throws IOException {
        String postMessageCommand = format(POST_MESSAGE_COMMAND_TEMPLATE, USERNAME, MSG);
        when(userInputMock.readLine()).thenReturn(postMessageCommand).thenReturn(QUIT_COMMAND);
        when(userCommandOrchestratorMock.postMessage(USERNAME, MSG)).thenReturn(userCommandOrchestratorMock);

        console.run();

        verify(userCommandOrchestratorMock).postMessage(USERNAME, MSG);
        verifyZeroInteractions(userOutputMock);
    }

    @Test
    public void whenAUserRequestsATimelineReadTheRequestIsForwardedOnToTheServiceLayerAndTheResultsArePrinted() throws IOException {
        String readMessageCommand = USERNAME;
        when(userInputMock.readLine()).thenReturn(readMessageCommand).thenReturn(QUIT_COMMAND);
        simulatePostedMessagesAt(ONE_SECOND_AGO_TIMESTAMP);
        when(clockMock.instant()).thenReturn(clockInstant(ONE_SECOND_AGO_TIMESTAMP, 1l));

        console.run();

        verify(userCommandOrchestratorMock).forEachTimelinePostOf(eq(USERNAME), any(CONSUMER_CLASS));
        verify(userOutputMock).println(format(READ_MESSAGE_OUTPUT_TEMPLATE, MSG, "1 second"));
    }

    @Test
    public void readCommandPrintsOutTimesCorrectly() throws IOException {
        String readMessageCommand = USERNAME;
        when(userInputMock.readLine()).thenReturn(readMessageCommand).thenReturn(QUIT_COMMAND);
        simulatePostedMessagesAt(ONE_SECOND_AGO_TIMESTAMP, TWO_SECONDS_AGO_TIMESTAMP, ONE_MINUTE_AGO_TIMESTAMP, TWO_MINUTES_AGO_TIMESTAMP);
        when(clockMock.instant()).thenReturn(clockInstant(ONE_MINUTE_AGO_TIMESTAMP, 60l));

        console.run();

        InOrder inOrder = inOrder(userOutputMock);

        verify(userCommandOrchestratorMock).forEachTimelinePostOf(eq(USERNAME), any(CONSUMER_CLASS));
        inOrder.verify(userOutputMock).println(format(READ_MESSAGE_OUTPUT_TEMPLATE, MSG, "1 second"));
        inOrder.verify(userOutputMock).println(format(READ_MESSAGE_OUTPUT_TEMPLATE, MSG, "2 seconds"));
        inOrder.verify(userOutputMock).println(format(READ_MESSAGE_OUTPUT_TEMPLATE, MSG, "1 minute"));
        inOrder.verify(userOutputMock).println(format(READ_MESSAGE_OUTPUT_TEMPLATE, MSG, "2 minutes"));
    }

    private Instant clockInstant(LocalDateTime timestamp, long secondsAgo) {
        return timestamp.plusSeconds(secondsAgo).atZone(systemDefault()).toInstant();
    }

    @SuppressWarnings("unchecked")
    private void simulatePostedMessagesAt(LocalDateTime... timestamps) {
        doAnswer(invocation -> {
            Consumer<Post> consumer = (Consumer<Post>) invocation.getArguments()[1];
            for (LocalDateTime timestamp : timestamps) {
                consumer.accept(new Post(User.from(USERNAME), MSG, timestamp));
            }
            return null;
        }).when(userCommandOrchestratorMock).forEachTimelinePostOf(eq(USERNAME), any(CONSUMER_CLASS));
    }
}