package io.john.amiscaray.quak.cli;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import io.john.amiscaray.quak.cli.cfg.ProjectConfig;
import io.john.amiscaray.quak.cli.generator.ProjectGenerator;
import io.john.amiscaray.quak.cli.templates.Template;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QuakCLIMain {

    private static final String banner = """
            ================================================================
             _____         _      _____                                 _  \s
            |     |_ _ ___| |_   |   __|___ ___ _____ ___ _ _ _ ___ ___| |_\s
            |  |  | | | .'| '_|  |   __|  _| .'|     | -_| | | | . |  _| '_|
            |__  _|___|__,|_,_|  |__|  |_| |__,|_|_|_|___|_____|___|_| |_,_|
               |__|                                                        \s
            ================================================================
            """;
    private static final Map<String, String> commandDescriptions = Map.of(
            "create", "Used to create a new Quak Application. Accepts a single optional argument for the directory to put your application's root folder. If the directory is not specified, the project will be located in the directory where you ran the command. Example: './quak-cli.bat create C:/Projects/'",
            "help", "Used to give help information about possible commands. Accepts a single optional argument for the name of a command you need help with. If this isn't specified then it will list all available commands."
    );

    public static void main(String[] args) {
        var defaultTerminalFactory = new DefaultTerminalFactory();
        var isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (args.length == 0 || !commandDescriptions.containsKey(args[0])) {
            System.out.println("Usage: java -jar QuakCLI.jar <command>. Where command is one of: "
                    + listCommands());
            System.exit(-1);
            return;
        }
        try(var terminal = isWindows ? defaultTerminalFactory.createTerminalEmulator() : defaultTerminalFactory.createTerminal()) {
            terminal.setCursorVisible(false);
            showBanner(terminal);
            var command = args[0];
            QuakCLIMain.class.getDeclaredMethod(command, Terminal.class, String[].class).invoke(null, terminal, Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void showBanner(Terminal terminal) throws IOException {
        terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        for(var line : banner.split("\n")) {
            terminal.putString(line);
            terminal.putCharacter('\n');
            terminal.flush();
        }
        putLines(terminal, 1);
        terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
    }

    private static String readText(Terminal terminal, String prompt) throws IOException {
        terminal.putString(prompt);
        terminal.flush();
        var answer = new StringBuilder();
        var answerPosition = terminal.getCursorPosition();
        while (true) {
            var keyStroke = terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.Enter) {
                break;
            } else if (keyStroke.getKeyType() == KeyType.Backspace || keyStroke.getKeyType() == KeyType.Delete) {
                if (answer.isEmpty()) {
                    continue;
                }
                answer = new StringBuilder(answer.substring(0, answer.length() - 1));
                var currentPosition = terminal.getCursorPosition();
                terminal.setCursorPosition(currentPosition.getColumn() - 1, currentPosition.getRow());
                terminal.putString(" ");
                terminal.flush();
            } else if (keyStroke.getKeyType() == KeyType.Character){
                answer.append(keyStroke.getCharacter());
            }
            terminal.setCursorPosition(answerPosition.getColumn(), answerPosition.getRow());
            terminal.putString(answer.toString());
            terminal.flush();
        }
        return answer.toString().strip();
    }

    private static <T> Object pickOption(Terminal terminal, String prompt, List<T> options) throws IOException {
        if (options.isEmpty()) {
            return "";
        }
        terminal.putString(prompt);
        terminal.putCharacter('\n');
        var selected = 0;
        var idx = 0;
        TerminalPosition firstCheckPosition = null;
        TerminalPosition endTerminalPosition;
        for (var option : options) {
            if (idx == selected) {
                firstCheckPosition = terminal.getCursorPosition();
                terminal.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
                terminal.putString("●");
                terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
                terminal.putString(" " + option);
            } else {
                terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
                terminal.putString("○");
                terminal.putString(" " + option);
            }
            terminal.putCharacter('\n');
            idx++;
        }
        terminal.flush();
        endTerminalPosition = terminal.getCursorPosition();
        while (true) {
            var keyStroke = terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.Enter) {
                break;
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                terminal.setCursorPosition(firstCheckPosition.getColumn(), firstCheckPosition.getRow() + selected);
                terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
                terminal.putString("○");
                selected = (selected + 1) % options.size();
                terminal.setCursorPosition(firstCheckPosition.getColumn(), firstCheckPosition.getRow() + selected);
                terminal.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
                terminal.putString("●");
                terminal.flush();
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                terminal.setCursorPosition(firstCheckPosition.getColumn(), firstCheckPosition.getRow() + selected);
                terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
                terminal.putString("○");
                selected = selected - 1 >= 0 ? selected - 1 : options.size() - 1;
                terminal.setCursorPosition(firstCheckPosition.getColumn(), firstCheckPosition.getRow() + selected);
                terminal.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
                terminal.putString("●");
                terminal.flush();
            }
        }
        terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        terminal.setCursorPosition(endTerminalPosition);
        return options.get(selected);
    }

    private static void putLines(Terminal terminal, int lines) throws IOException {
        for (int i = 0; i < lines; i++) {
            terminal.putCharacter('\n');
        }
        terminal.flush();
    }

    private static void create(Terminal terminal, String[] args) throws IOException, ParserConfigurationException, InterruptedException, TransformerException, SAXException {
        String workingDirectory = "";
        if (args.length > 0) {
            workingDirectory = args[0];
        }
        var artifactID = readText(terminal, "Enter an artifact ID: ");
        putLines(terminal, 2);
        var groupID = readText(terminal, "Enter a group ID: ");
        putLines(terminal, 2);
        terminal.flush();
        var projectTemplate = (Template) pickOption(terminal, "Select a template: ",
                Arrays.asList(Template.values()));
        var projectGenerator = ProjectGenerator.getInstance();
        projectGenerator.init(terminal);
        projectGenerator.generateProject(new ProjectConfig(artifactID, groupID, projectTemplate), workingDirectory);
    }

    private static void help(Terminal terminal, String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            var command = args[0];
            if (!commandDescriptions.containsKey(command)) {
                terminal.putString("Command not found. You can ask for help about the following commands: " + listCommands());
            } else {
                terminal.putString(commandDescriptions.get(command));
            }
        } else {
            terminal.putString("Using this tool you can use the following commands: " + listCommands() + " Try 'quak-cli help <command> for help about a specific command.'");
        }
        terminal.flush();
        putLines(terminal, 2);
        terminal.putString("Press Any Key to Exit...");
        terminal.flush();
        while (true) {
            var keyStroke = terminal.readInput();
            if (keyStroke != null) {
                break;
            }
        }
    }

    private static String listCommands() {
        return String.join(", ", commandDescriptions.keySet().stream().map(command -> "'" + command + "'").toList()) + ".";
    }

}
