package deployment.mgmt.microconfig.factory;

import io.microconfig.core.Microconfig;
import io.microconfig.core.MicroconfigRunner;
import io.microconfig.core.environments.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static mgmt.utils.FileUtils.userHome;
import static mgmt.utils.FileUtils.write;

public class MgmtMicroConfigAdapter {
    static final String MGMT = ".mgmt";

    public static void execute(String env, List<String> groups, List<String> components, File sourcesRootDir, File destinationComponentDir) {
        MicroconfigRunner runner = new MicroconfigRunner(sourcesRootDir, destinationComponentDir);
        runner.build(env, groups, components); //todo save all file except service.prop to .mgmt
        generateClistFile(runner.getMicroconfig(), env);
    }

    private static void generateClistFile(Microconfig microconfig, String env) {
        List<String> components = microconfig.inEnvironment(env)
                .getAllComponents().asList()
                .stream()
                .map(Component::getName)
                .collect(toList());
        write(clistFile(),  components);
    }

    private static Path clistFile() {
        return new File(new File(userHome(), "components/" + MGMT), "mgmt.clist").toPath();
    }

    //todo support .sap type
    //factory.newBuildCommand(PROCESS.getType(), new WebappPostProcessor()),
    ////                factory.newBuildCommand(DEPLOY.getType()),
    ////                factory.newBuildCommand(ENV.getType()),
    ////                factory.newBuildCommand(SECRET.getType(), updateSecretsPostProcessor(factory.getConfigIo())),
    ////                factory.newBuildCommand(LOG4J.getType()),
    ////                factory.newBuildCommand(LOG4J2.getType()),
    ////                factory.newBuildCommand(byName("sap")),
}