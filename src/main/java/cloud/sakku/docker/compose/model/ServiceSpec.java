package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.constant.Capability;
import cloud.sakku.docker.compose.constant.Restart;
import cloud.sakku.docker.compose.exception.UnsupportedSyntaxException;
import cloud.sakku.docker.compose.utils.DockerShortSyntaxParser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.io.FileNotFoundException;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties({"cgroup_parent", "credential_spec", "tty",  "stdin_open", "devices", "network_mode", "privileged",
        "mac_address", "ipc", "hostname", "domainname", "userns_mode", "ulimits", "tmpfs", "sysctls", "stop_signal", "pid",
        "stop_grace_period", "security_opt", "secrets", "expose", "external_links", "init", "isolation", "logging","user"})
public class ServiceSpec {

    @SerializedName("cap_add")
    @JsonProperty("cap_add")
    private List<Capability> capAdd;
    @SerializedName("cap_drop")
    @JsonProperty("cap_drop")
    private List<Capability> capDrop;

    @SerializedName("build")
    @JsonProperty("build")
    private Build build;
    @SerializedName("image")
    @JsonProperty("image")
    private Image image;

    @SerializedName("command")
    @JsonProperty(value = "command", defaultValue = "")
    private String command = "";
    @SerializedName("configs")
    @JsonProperty("configs")
    @Builder.Default
    private List<Config> configs = new ArrayList<>();
    @SerializedName("container_name")
    @JsonProperty("container_name")
    private String containerName;
    @SerializedName("depends_on")
    @JsonProperty("depends_on")
    @Builder.Default
    private List<String> dependsOn = new ArrayList<>();
    @SerializedName("deploy")
    @JsonProperty("deploy")
    private Deploy deploy;
    @SerializedName("dns")
    @Builder.Default
    private List<String> dns = new ArrayList<>();
    @SerializedName("dns_search")
    @Builder.Default
    private List<String> dnsSearch = new ArrayList<>();
    @SerializedName("entrypoint")
    @Builder.Default
    private List<String> entrypoint = new ArrayList<>();
    @SerializedName("environment")
    private Map<String, Object> environment = new HashMap<>();
    @SerializedName("env_file")
    private List<String> envFile = new ArrayList<>();
    @SerializedName("extra_hosts")
    @JsonProperty("extra_hosts")
    private List<Host> extraHosts;
    @SerializedName("healthcheck")
    @JsonProperty("healthcheck")
    private HealthCheckConfig healthCheck;
    @SerializedName("labels")
    private Map<String, Object> labels = new HashMap<>();
    @SerializedName("links")
    @JsonProperty("links")
    @Builder.Default
    private List<Service> links = new ArrayList<>();
    @SerializedName("networks")
    @JsonProperty("networks")
    @Builder.Default
    private List<String> networks = new ArrayList<>();
    @SerializedName("ports")
    @JsonProperty("ports")
    @Builder.Default
    private List<PortConfig> ports = new ArrayList<>();
    @SerializedName("restart")
    @JsonProperty(value = "restart", defaultValue = "no")
    @Builder.Default
    private Restart restart = Restart.NO;
    @SerializedName("volumes")
    @JsonProperty("volumes")
    @Builder.Default
    private List<Volume> volumes = new ArrayList<>();
    @SerializedName("working_dir")
    private String workingDir;

    @JsonSetter("command")
    public void setCommand(Object command) {
        if (command instanceof String) {
            this.command = command.toString();
        } else if (command instanceof List) {
            List<String> commandList = (List) command;
            this.command = "";
            commandList.forEach(cmd -> {
                this.command += cmd + " ";
            });
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "command", command.toString());
        }
    }

    @JsonSetter("dns")
    public void setDns(Object dnsObject) {
        if (dnsObject instanceof List) {
            List<String> dnsList = (List) dnsObject;
            dnsList.forEach(dns -> {
                this.dns.add(dns);
            });
        } else if (dnsObject instanceof String) {
            this.dns.add(dnsObject.toString());
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "dns", dnsObject.toString());
        }

    }

    @JsonSetter("dns_search")
    public void setDnsSearch(Object dnsSearchObject) {
        if (dnsSearchObject instanceof List) {
            List<String> dnsList = (List) dnsSearchObject;
            dnsList.forEach(dnsSearch -> {
                this.dnsSearch.add(dnsSearch);
            });
        } else if (dnsSearchObject instanceof String) {
            this.dnsSearch.add(dnsSearchObject.toString());
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "dns_search", dnsSearchObject.toString());
        }
    }

    @JsonSetter("entrypoint")
    public void setEntrypoint(Object entrypointObject) {
        if (entrypointObject instanceof List) {
            List<String> entrypointList = (List) entrypointObject;

            entrypointList.forEach(entrypoint -> {
                this.entrypoint.add(entrypoint);
            });

        } else if (entrypointObject instanceof String) {
            this.entrypoint.add(entrypointObject.toString());

        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "entrypoint", entrypointObject.toString());

        }
    }

    @JsonSetter("environment")
    public void setEnvironment(Object environment) {
        if (environment instanceof Map) {
            this.environment.putAll((Map<String, Object>) environment);
        } else if (environment instanceof List) {
            this.environment.putAll(DockerShortSyntaxParser.parse((List<String>) environment));
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "environment", environment.toString());
        }
    }

    @JsonSetter("env_file")
    public void setEnvFile(Object envFileObject) throws FileNotFoundException {
        if (envFileObject instanceof List) {
            this.envFile.addAll((List) envFileObject);
        } else if (envFileObject instanceof String) {
            this.envFile.add(envFileObject.toString());
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "env_file", envFileObject.toString());
        }
    }

    @JsonSetter("labels")
    public void setLabels(Object labels) {
        if (labels instanceof Map) {
            this.labels.putAll((Map) labels);
        } else if (labels instanceof List) {
            this.labels.putAll(DockerShortSyntaxParser.parse((List) labels));
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "labels", labels.toString());
        }
    }

    @JsonSetter("networks")
    public void setNetworks(Object networks) {
        if (networks instanceof Map) {
            Map<String, Object> networksMap = (Map) networks;
            networksMap.keySet().forEach(key -> {
                this.networks.add(key);
            });
        } else if (networks instanceof List) {
            this.networks.addAll((Collection<? extends String>) networks);
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "networks", networks.toString());
        }
    }

    @JsonSetter("working_dir")
    public void setWorkingDir(String workingDir) {
        if (Objects.nonNull(workingDir) && workingDir.startsWith(".")) {
            throw UnsupportedSyntaxException.getInstance("relative path to host is not allowed", "working_dir", workingDir);
        }

        this.workingDir = workingDir;
    }
}
