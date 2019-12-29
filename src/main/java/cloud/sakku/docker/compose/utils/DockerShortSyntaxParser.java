package cloud.sakku.docker.compose.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DockerShortSyntaxParser {


    public static Map<String, String> parse(final List<String> lists) {

        Map<String, String> map = new HashMap<>();

        lists.forEach(arg -> {

            String[] parts = arg.split("=");

            if (parts.length == 1) {

                map.put(parts[0], "");

            } else if (parts.length >= 2) {

                map.put(parts[0], parts[1]);

            }
        });

        return map;
    }

}
