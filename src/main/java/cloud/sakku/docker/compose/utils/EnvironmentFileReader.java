package cloud.sakku.docker.compose.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class EnvironmentFileReader {

    public static Map<String, Object> read(final String envFile) throws FileNotFoundException {

        Map<String, Object> environmentMap = new HashMap<>();

        Scanner scanner = new Scanner(new File(envFile));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] environment = line.split("#");

            if(Pattern.compile("^\\s*$").matcher(environment[0]).matches()){
                continue;
            }

            environment = environment[0].replaceAll(" ", "").split("=");

            if (environment.length == 2)
                environmentMap.put(environment[0], environment[1]);
            else if (environment.length == 1)
                environmentMap.put(environment[0], "");

        }

        return environmentMap;
    }
}
