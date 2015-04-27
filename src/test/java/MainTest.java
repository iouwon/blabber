import org.sydenham.blabber.Main;
import org.sydenham.blabber.ui.Console;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MainTest {

    private final Console consoleMock = mock(Console.class);

    @Test
    public void retrievesConsoleUiAndCallsRun() {
        Main.setConsole(consoleMock);

        Main.main(new String[]{});

        verify(consoleMock).run();
    }
}
