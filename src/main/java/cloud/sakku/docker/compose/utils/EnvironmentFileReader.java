package cloud.sakku.docker.compose.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EnvironmentFileReader {

    public static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(?<env>[0-9a-zA-Z_]+)}");
    private static final Pattern ENV_FILE = Pattern.compile("[/]?(?<name>[^./]*)(.env)[/]?");

    public static void put(final String envFile, final Map<String, Object> environments) throws FileNotFoundException {

        if (!ENV_FILE.matcher(envFile).find())
            throw new FileNotFoundException("env file not exist !");

        Scanner scanner = new Scanner(new File(envFile));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] environment = line.split("#");

            if (Pattern.compile("^\\s*$").matcher(environment[0]).matches())
                continue;

            environment = environment[0].replaceAll(" ", "").split("=");

            if (environment.length == 2) {
                Matcher matcher;
                if ((matcher = ENV_PATTERN.matcher(environment[1])).find()) {
                    String envKey = matcher.group("env");
                    if (environments.containsKey(envKey))
                        continue;
                }

                environments.put(environment[0], environment[1]);
            } else if (environment.length == 1) {
                environments.put(environment[0], "");
            }

        }

    }
}
