package org.sydenham.blabber;

import org.sydenham.blabber.service.UserCommandOrchestrator;
import org.sydenham.blabber.ui.Console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Clock;

public class Main {

    private static Console console = new Console(new BufferedReader(new InputStreamReader(System.in)), System.out, UserCommandOrchestrator.newObj(), Clock.systemDefaultZone());

    public static void main(String[] args) {
        console.run();
    }

    public static void setConsole(Console console) {
        Main.console = console;
    }
}
