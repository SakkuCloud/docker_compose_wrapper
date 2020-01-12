package cloud.sakku.docker.compose.utils;

import cloud.sakku.docker.compose.model.*;
import cloud.sakku.docker.compose.model.Sakku.SakkuApp;
import cloud.sakku.docker.compose.model.Sakku.SakkuModule;
import cloud.sakku.docker.compose.model.Sakku.SakkuPort;
import cloud.sakku.docker.compose.model.Sakku.SakkuImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ComposeToSakkuPipelineConverter {

    private static final Pattern MEM_KB = Pattern.compile("^(?<size>\\d+)(kb|KB)$");
    private static final Pattern MEM_B = Pattern.compile("^(?<size>\\d+)(b|B)$");
    private static final Pattern MEM_K = Pattern.compile("^(?<size>\\d+)(k|K)$");
    private static final Pattern MEM_M = Pattern.compile("^(?<size>\\d+)(m|M)$");
    private static final Pattern MEM_GB = Pattern.compile("^(?<size>\\d+)(gb|GB)$");

    private static final String FILE_REGEX = "/(?<file>[A-Za-z0-9${}]+)\\.(?<suffix>[A-Za-z0-9${}]+)";

    public static List<SakkuApp> convert(ComposeFile composeFile) {

        List<SakkuApp> pipeline = new ArrayList<>();

        if(Objects.nonNull(composeFile)) {

            for (String serviceName : composeFile.getServices().keySet()) {

                ServiceSpec service = composeFile.getServices().get(serviceName);


                SakkuApp.SakkuAppBuilder sakkuAppBuilder = SakkuApp.builder()
                        .name(serviceName)
                        .cmd(service.getCommand())
                        .image(convertComposeImageToSakkuImage(service.getImage()))
                        .links(service.getLinks())
                        .dependsOn(service.getDependsOn())
                        .environments(service.getEnvironment())
                        .labels(service.getLabels())
                        .ports(convertComposePortsToSakkuPorts(service.getPorts()))
                        .modules(convertVolumesToModules(service.getVolumes()));


                Deploy deploy;

                if (Objects.nonNull(deploy = service.getDeploy())) {

                    Resources resources;
                    if (Objects.nonNull(deploy.getResources()) && Objects.nonNull(resources = deploy.getResources().getReservations())) {

                        sakkuAppBuilder.cpu(resources.getCpus());

                        String memory = resources.getMemory();

                        Matcher matcher;

                        if ((matcher = MEM_GB.matcher(memory)).matches()) {
                            sakkuAppBuilder.mem(Double.parseDouble(matcher.group("size")));
                        } else if ((matcher = MEM_M.matcher(memory)).matches()) {
                            sakkuAppBuilder.mem(Double.parseDouble(matcher.group("size")) / 1024);
                        }
                    }

                    long replicas = deploy.getReplicas();

                    if (replicas > 0) {
                        sakkuAppBuilder.maxInstance(replicas);
                    }
                }


                Build composeBuild;

                if (Objects.nonNull(composeBuild = service.getBuild())) {

                    List<String> args = new ArrayList<>(composeBuild.getArgs().keySet());

                    sakkuAppBuilder.args(args);
                }


                pipeline.add(sakkuAppBuilder.build());
            }
        }

        return pipeline;
    }

    private static List<SakkuModule> convertVolumesToModules(final List<Volume> volumes){

        List<SakkuModule> sakkuModules = new ArrayList<>();

        if(Objects.nonNull(volumes)){

            volumes.forEach(volume -> {

                String container = volume.getContainer();

                if(Pattern.compile(FILE_REGEX).matcher(container).find()){
                    container = container.replaceAll(FILE_REGEX, "");
                }

                sakkuModules.add(SakkuModule.createReplicatedStorageModule(container));

            });

        }

        return sakkuModules;
    }

    private static SakkuImage convertComposeImageToSakkuImage(final Image composeImage) {

        String name = "";

        if (Objects.nonNull(composeImage)) {
            name = composeImage.toString();
        }

        return SakkuImage.builder()
                .name(name)
                .build();
    }

    private static List<SakkuPort> convertComposePortsToSakkuPorts(final List<PortConfig> composePorts) {

        List<SakkuPort> sakkuPorts = new ArrayList<>();

        if (Objects.nonNull(composePorts)) {

            composePorts.forEach(portConfig -> {

                SakkuPort.SakkuPortBuilder sakkuPortBuilder = SakkuPort.builder();

                if (Objects.nonNull(portConfig.getPublished()))
                    sakkuPortBuilder.port(portConfig.getPublished().toString());

                if(Objects.nonNull(portConfig.getProtocol()))
                    sakkuPortBuilder.protocol(portConfig.getProtocol());

                sakkuPorts.add(sakkuPortBuilder.build());
            });

        }

        return sakkuPorts;
    }

}
