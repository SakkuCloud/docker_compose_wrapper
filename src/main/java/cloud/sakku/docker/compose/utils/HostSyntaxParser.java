package cloud.sakku.docker.compose.utils;

import cloud.sakku.docker.compose.model.Host;


public final class HostSyntaxParser {

    public static Host parse(final String host) {

        String[] parts = host.split(":");

        Host.HostBuilder builder = Host.builder();

        builder.name(parts[0]);

        if(parts.length >= 2){
            builder.ip(parts[1]);
        }

        return builder.build();
    }

}
