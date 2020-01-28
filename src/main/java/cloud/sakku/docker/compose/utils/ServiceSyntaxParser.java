package cloud.sakku.docker.compose.utils;

import cloud.sakku.docker.compose.model.Service;

public final class ServiceSyntaxParser {

    public static Service parse(final String link) {
        Service.ServiceBuilder builder = Service.builder();
        String[] parts = link.split(":");
        builder.name(parts[0]);

        if (parts.length >= 2)
            builder.alias(parts[1]);

        return builder.build();

    }
}
