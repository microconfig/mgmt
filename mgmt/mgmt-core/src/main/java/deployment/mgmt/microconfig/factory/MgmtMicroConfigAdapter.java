package deployment.mgmt.microconfig.factory;


import io.microconfig.core.MicroconfigRunner;

import java.io.File;
import java.util.List;


public class MgmtMicroConfigAdapter {
    static final String MGMT = ".mgmt";

    public static void execute(String env, List<String> groups, List<String> components, File sourcesRootDir, File destinationComponentDir) {
        new MicroconfigRunner(sourcesRootDir, destinationComponentDir).build(env, groups, components);
    }

//    private static Command newBuildPropertiesCommand(File sourcesRootDir, File destinationComponentDir) {
//        MicroconfigFactory factory = MicroconfigFactory.init(sourcesRootDir, destinationComponentDir);
//
//        BuildConfigCommand serviceCommon = factory.newBuildCommand(APPLICATION.getType());
//        factory = factory.withServiceInnerDir(MGMT);
//        return composite(
//                serviceCommon,
//                factory.newBuildCommand(PROCESS.getType(), new WebappPostProcessor()),
//                factory.newBuildCommand(DEPLOY.getType()),
//                factory.newBuildCommand(ENV.getType()),
//                factory.newBuildCommand(SECRET.getType(), updateSecretsPostProcessor(factory.getConfigIoService())),
//                factory.newBuildCommand(LOG4J.getType()),
//                factory.newBuildCommand(LOG4J2.getType()),
//                factory.newBuildCommand(byName("sap")),
//                new GenerateComponentListCommand(destinationComponentDir, factory.getEnvironmentProvider()),
//                new CopyHelpFilesCommand(factory.getEnvironmentProvider(), factory.getComponentTree(), destinationComponentDir.toPath())
//        );
//    }
}