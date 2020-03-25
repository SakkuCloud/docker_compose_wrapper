package cloud.sakku.docker.compose;


import cloud.sakku.docker.compose.exception.ComposeFileReaderException;
import cloud.sakku.docker.compose.model.*;
import cloud.sakku.docker.compose.model.Sakku.SakkuApp;
import cloud.sakku.docker.compose.utils.ComposeToSakkuPipelineConverter;
import cloud.sakku.docker.compose.utils.EnvironmentFileReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComposeFileReader {

    private final String YAML_DOCKER_COMPOSE_FILE_NAME = "docker-compose.yaml";
    private final String YML_DOCKER_COMPOSE_FILE_NAME = "docker-compose.yml";

    private final Pattern YAML_DOCKER_COMPOSE_SUFFIX = Pattern.compile("^.*/(?<name>[0-9a-zA-Z_\\-]+).yaml[/]?$");
    private final Pattern YML_DOCKER_COMPOSE_SUFFIX = Pattern.compile("^.*/(?<name>[0-9a-zA-Z_\\-]+).yml[/]?$");

    private final String NOT_EXIST_ENVIRONMENT_ERROR = "'{ENV}' environment is not exist ! [service: {SERVICE}]";

    private final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(?<env>[0-9a-zA-Z_]+)}");

    private ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
    private ObjectMapper jsonMapper = new ObjectMapper(new JsonFactory());

    private ComposeFile composeFile;
    private String path;
    // environments from '.env' file
    private Map<String, Object> defaultEnvironment = new HashMap<>();

    /**
     * read compose file from File
     *
     * @param file yaml docker compose file
     * @throws ComposeFileReaderException
     * @throws FileNotFoundException
     */
    public ComposeFileReader(File file) throws ComposeFileReaderException, FileNotFoundException {
        String filePath = file.getAbsolutePath();

        if ((YAML_DOCKER_COMPOSE_SUFFIX.matcher(filePath).find() || YML_DOCKER_COMPOSE_SUFFIX.matcher(filePath).find())
                && file.isFile()) {

            String composeFileInString = readFile(file);
            composeFile = readComposeFile(composeFileInString);

        } else {
            throw new FileNotFoundException("docker-compose file not found");
        }

    }

    /**
     * read compose file from path of directory or path of docker compose yaml file
     *
     * @param path docker compose files directory path or path of docker-compose yaml file
     * @throws ComposeFileReaderException
     * @throws IOException
     */
    public ComposeFileReader(String path) throws ComposeFileReaderException, IOException {
        // add '/' to end of path
        if (path.lastIndexOf('/') != path.length() - 1)
            path += '/';

        this.path = path;
        String dockerComposeFilePath;

        if ((YAML_DOCKER_COMPOSE_SUFFIX.matcher(path).find() || YML_DOCKER_COMPOSE_SUFFIX.matcher(path).find())
                && new File(path).isFile()) {
            dockerComposeFilePath = path;
        } else if (new File(path + YAML_DOCKER_COMPOSE_FILE_NAME).isFile()) {
            dockerComposeFilePath = path + YAML_DOCKER_COMPOSE_FILE_NAME;
        } else if (new File(path + YML_DOCKER_COMPOSE_FILE_NAME).isFile()) {
            dockerComposeFilePath = path + YML_DOCKER_COMPOSE_FILE_NAME;
        } else {
            throw new FileNotFoundException("docker-compose file not found");
        }

        String composeFileInString = readFile(dockerComposeFilePath);


        if (new File(path + ".env").isFile())
            EnvironmentFileReader.put(path + ".env", defaultEnvironment);

        composeFile = readComposeFile(composeFileInString);

    }

    private static List<String> getErrorPath(List<JsonMappingException.Reference> path) {
        List<String> errorPath = new ArrayList<>();

        for (JsonMappingException.Reference reference : path) {
            if (Objects.isNull(reference.getFieldName())) {
                errorPath.add(reference.toString());
                continue;
            }

            errorPath.add(reference.getFieldName());
        }
        return errorPath;

    }

    public ComposeFile getComposeFile() {
        return composeFile;
    }

    /**
     * @param composeFileInString string of docker compose file
     * @return ComposeFile
     * @throws ComposeFileReaderException
     */
    private ComposeFile readComposeFile(String composeFileInString) throws ComposeFileReaderException {
        try {
            ComposeFile composeFile = convertToComposeFile(composeFileInString);
            setEnvironmentsValueAndCheckingProperly(composeFile);
            return composeFile;
        } catch (JsonMappingException e) {
            throw ComposeFileReaderException.getInstance(e.getCause().getMessage(), e.getCause(), getErrorPath(e.getPath()), e.getLocation().getLineNr(), e.getLocation().getColumnNr());
        } catch (Exception e) {
            throw ComposeFileReaderException.getInstance(e.getMessage(), e.getCause());
        }
    }

    private void setEnvironmentsValueAndCheckingProperly(ComposeFile composeFile) {
        composeFile.getServices().forEach((serviceName, serviceSpec) -> {
            try {

                Map<String, NetworkSpec> networks = composeFile.getNetworks();

                List<String> envFiles = serviceSpec.getEnvFile();
                if (Objects.nonNull(envFiles) && Objects.nonNull(this.path)) {
                    for (String envFile : envFiles) {
                        try {
                            if (envFile.indexOf("./") == 0) {
                                envFile = envFile.replace("./", this.path);
                            } else if (envFile.indexOf("/") != 0) {
                                envFile = this.path + envFile;
                            }

                            EnvironmentFileReader.put(envFile, serviceSpec.getEnvironment());
                        } catch (FileNotFoundException e) {
                            throw ComposeFileReaderException.getInstance("'" + this.path + envFile + "' environment file not found :|");
                        }
                    }
                }

                List<Network> serviceNetworks = serviceSpec.getNetworks();
                if(Objects.nonNull(serviceNetworks) && !serviceNetworks.isEmpty()){
                    for (Network network : serviceNetworks){
                        if(!networks.containsKey(network.getName())){
                            throw ComposeFileReaderException.getInstance("There is no network with name \"" + network.getName() + "\"");
                        }
                    }
                }

                List<Service> serviceLinks = serviceSpec.getLinks();
                if(Objects.nonNull(serviceLinks) && !serviceLinks.isEmpty()){
                    for (Service link : serviceLinks){
                        if(!composeFile.getServices().containsKey(link.getName())){
                            throw ComposeFileReaderException.getInstance("There is no service with name \"" + link.getName() + "\"");
                        }
                    }
                }


                String serviceSpecJsonString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceSpec);
                Matcher matcher;
                while ((matcher = ENV_PATTERN.matcher(serviceSpecJsonString)).find()) {
                    String envKey = matcher.group("env");
                    String notExistEnvironmentError = NOT_EXIST_ENVIRONMENT_ERROR.replace("{ENV}", envKey).replace("{SERVICE}", serviceName);

                    Object defaultEnvValue = defaultEnvironment.getOrDefault(envKey, null);
                    Object envValue = serviceSpec.getEnvironment().getOrDefault(envKey, null);

                    if (Objects.isNull(envValue) || ENV_PATTERN.matcher(envValue.toString()).find()) {
                        if (Objects.isNull(defaultEnvValue))
                            throw ComposeFileReaderException.getInstance(notExistEnvironmentError);

                        envValue = defaultEnvValue;
                    }

                    String envRegex = "\\$\\{" + envKey + "}";
                    if ((Pattern.compile(envRegex).matcher(serviceSpecJsonString)).find()) {
                        serviceSpecJsonString = serviceSpecJsonString.replaceAll(envRegex, envValue.toString());
                    }
                }

                serviceSpec = new ObjectMapper().readValue(serviceSpecJsonString, ServiceSpec.class);
                composeFile.getServices().put(serviceName, serviceSpec);
            } catch (JsonProcessingException e) {
                throw ComposeFileReaderException.getInstance("set environment processing error");
            }
        });

    }

    private ComposeFile convertToComposeFile(String inputStream) throws IOException {
        return yamlReader.readValue(inputStream, ComposeFile.class);
    }

    private String readFile(final String composeFilePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(composeFilePath));
        StringBuilder composeFileInString = new StringBuilder();

        while (scanner.hasNextLine()) {
            composeFileInString.append(scanner.nextLine()).append("\n");
        }

        return composeFileInString.toString();
    }

    private String readFile(final File composeFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(composeFile);
        StringBuilder composeFileInString = new StringBuilder();

        while (scanner.hasNextLine()) {
            composeFileInString.append(scanner.nextLine()).append("\n");
        }

        return composeFileInString.toString();
    }

    /**
     * convert ComposeFile to Sakku pipeline config json
     *
     * @return Json String of Sakku pipeline config
     * @throws JsonProcessingException
     */
    public String toSakkuPipelineJson() throws JsonProcessingException {
        List<SakkuApp> pipeline = ComposeToSakkuPipelineConverter.convert(composeFile);
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pipeline);

    }
}

