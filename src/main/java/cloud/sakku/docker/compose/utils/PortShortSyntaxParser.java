package cloud.sakku.docker.compose.utils;


import cloud.sakku.docker.compose.constant.PortMode;
import cloud.sakku.docker.compose.constant.Protocol;
import cloud.sakku.docker.compose.exception.UnsupportedSyntaxException;
import cloud.sakku.docker.compose.model.PortConfig;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PortShortSyntaxParser {
    private static final Pattern SINGLE_CONTAINER_PORT = Pattern.compile("^(?<container>[$,A-Za-z{}0-9]+)$");
    private static final Pattern RANGE_CONTAINER_PORTS = Pattern.compile("^(?<containerRangeStart>[$,A-Za-z{}0-9]+)-(?<containerRangeEnd>[$,A-Za-z{}0-9]+)$");
    private static final Pattern SINGLE_HOST_CONTAINER_PORTS = Pattern.compile("^(?<host>[\\s,\\S]+):(?<container>[\\s,\\S]+)$");
    private static final Pattern SINGLE_HOST_CONTAINER_PORTS_WITH_PROTOCOL = Pattern.compile("^(?<host>[\\s,\\S]+):(?<container>[\\s,\\S]+)/(?<protocol>[a-zA-Z]+)$");
    private static final Pattern RANGE_HOST_CONTAINER_PORTS = Pattern.compile("^(?<hostRangeStart>\\d+)-(?<hostRangeEnd>\\d+):(?<containerRangeStart>\\d+)-(?<containerRangeEnd>\\d+)$");
    private static final Pattern RANGE_HOST_INTERFACE_CONTAINER_PORTS = Pattern.compile("^(?<interface>.+?):(?<hostRangeStart>\\d+)-(?<hostRangeEnd>\\d+):(?<containerRangeStart>\\d+)-(?<containerRangeEnd>\\d+)");

    public static PortConfig parse(final String source) throws IOException {
        Matcher matcher;
        final PortConfig.PortConfigBuilder builder = PortConfig.builder();

        if ((matcher = SINGLE_CONTAINER_PORT.matcher(source)).matches()) {
            builder.mode(PortMode.HOST)
                    .target(matcher.group("container"));

            return builder.build();
        }

        if ((matcher = SINGLE_HOST_CONTAINER_PORTS_WITH_PROTOCOL.matcher(source)).matches()) {
            builder
                    .mode(PortMode.INGRESS)
                    .published(matcher.group("host"))
                    .target(matcher.group("container"))
                    .protocol(Protocol.find(matcher.group("protocol")));

            return builder.build();
        }

        if ((matcher = SINGLE_HOST_CONTAINER_PORTS.matcher(source)).matches()) {
            builder
                    .mode(PortMode.INGRESS)
                    .published(matcher.group("host"))
                    .target(matcher.group("container"));

            return builder.build();
        }

        if ((matcher = RANGE_CONTAINER_PORTS.matcher(source)).matches()) {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "port", source);
        } else if ((matcher = RANGE_HOST_CONTAINER_PORTS.matcher(source)).matches()) {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "port", source);
        } else if ((matcher = RANGE_HOST_INTERFACE_CONTAINER_PORTS.matcher(source)).matches()) {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "port", source);
        }

        throw new IOException("Failed to parser shorthand syntax '" + source + "'");
    }
}
