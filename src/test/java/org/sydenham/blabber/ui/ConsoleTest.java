package org.sydenham.blabber.ui;

import org.mockito.Mock;
import org.sydenham.blabber.exception.ApplicationException;
import org.sydenham.blabber.service.UserCommandOrchestrator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;

import static java.lang.String.format;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConsoleTest {

    public static final String USERNAME = "username";
    public static final String POST_MESSAGE_COMMAND_TEMPLATE = "%s -> %s";
    public static final String QUIT_COMMAND = "";

    private Console console;
    @Mock
    private UserCommandOrchestrator userCommandOrchestratorMock;
    @Mock
    private BufferedReader userInputMock;
    @Mock
    private PrintStream userOutputMock;

    @BeforeMethod
    public void setUp() {
        initMocks(this);

        console = new Console(userInputMock, userOutputMock, userCommandOrchestratorMock);
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
        String msg = "msg";
        String postMessageCommand = format(POST_MESSAGE_COMMAND_TEMPLATE, USERNAME, msg);
        when(userInputMock.readLine()).thenReturn(postMessageCommand).thenReturn(QUIT_COMMAND);
        when(userCommandOrchestratorMock.postMessage(USERNAME, msg)).thenReturn(userCommandOrchestratorMock);

        console.run();

        verify(userCommandOrchestratorMock).postMessage(USERNAME, msg);
        verifyZeroInteractions(userOutputMock);
    }
}