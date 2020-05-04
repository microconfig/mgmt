package it;

import deployment.mgmt.atrifacts.Artifact;
import deployment.mgmt.atrifacts.ClasspathStrategy;
import deployment.mgmt.atrifacts.nexusclient.NexusClient;
import deployment.mgmt.atrifacts.nexusclient.NexusClientImpl;
import deployment.mgmt.atrifacts.nexusclient.RepositoryPriorityServiceImpl;
import deployment.mgmt.atrifacts.strategies.classpathfile.*;
import deployment.mgmt.configs.deploysettings.DeploySettingsImpl;
import deployment.mgmt.configs.deploysettings.SimpleEncryptionServiceImpl;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.filestructure.DeployFileStructureImpl;
import deployment.mgmt.configs.service.properties.MavenSettings;
import deployment.mgmt.configs.service.properties.NexusRepository;
import deployment.mgmt.configs.service.properties.impl.PropertyServiceImpl;
import io.microconfig.core.properties.io.ConfigIo;
import io.microconfig.core.properties.io.properties.PropertiesConfigIo;
import io.microconfig.core.properties.io.selector.ConfigFormatDetectorImpl;
import io.microconfig.core.properties.io.selector.ConfigIoSelector;
import io.microconfig.core.properties.io.yaml.YamlConfigIo;
import io.microconfig.io.DumpedFsReader;
import io.microconfig.io.FsReader;
import org.junit.jupiter.api.Disabled;

import java.io.File;
import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static java.util.Arrays.asList;
import static mgmt.utils.TimeUtils.secAfter;

@Disabled
public class ClasspathTestIT {
    public static void main(String[] args) {
//        List<File> nexus = doResolve(new NexusClasspathStrategy());
        List<File> gradle = doResolve(gradleStrategy());
    }

    private static List<File> doResolve(ClasspathStrategy classpathStrategy) {
        DeployFileStructure fileStructure = DeployFileStructureImpl.init();

        String service = "cr-xls-export";
        MavenSettings mavenSettings = new PropertyServiceImpl(fileStructure, configIoSelector()).getProcessProperties(service).getMavenSettings();
        List<NexusRepository> nexusRepositories = mavenSettings.getNexusRepositories();
//        Artifact artifact = Artifact.fromMavenString("ru.sbt.cr.astreya.stresstest:stresstest-reports:RP-18.24-SNAPSHOT");
        Artifact artifact = Artifact.fromMavenString("ru.sbt.risk.tradehub:th-server:TH-18.24-SNAPSHOT");
//        Artifact artifact = Artifact.fromMavenString("ru.sbt.risk.ocp:ocp-core:OCP-18.23-SNAPSHOT");

        long t = System.currentTimeMillis();
        List<File> artifacts = classpathStrategy.downloadDependencies(artifact, false, nexusRepositories, mavenSettings.getLocalRepositoryDir(),
                fileStructure.logs().getMavenLogFile(service));
        announce("\n resolved " + artifacts.size() + " artifacts in " + secAfter(t));
        return artifacts;
    }

    private static ClasspathFileStrategy gradleStrategy() {
        NexusClient nexusClient = new NexusClientImpl(
                new RepositoryPriorityServiceImpl(asList("ru", "deployment")),
                new DeploySettingsImpl(DeployFileStructureImpl.init(), null, new SimpleEncryptionServiceImpl(), configIoSelector())
        );
        return new ClasspathFileStrategy(
                new JarClasspathReaderSelector(
                        new ArtifactClasspathReader(),
                        new MgmtClasspathFileReader()
                ),
                new UnknownGroupResolverImpl(nexusClient),
                nexusClient
        );
    }

    private static ConfigIo configIoSelector() {
        FsReader fileReader = new DumpedFsReader();
        return new ConfigIoSelector(new ConfigFormatDetectorImpl(fileReader),
                new YamlConfigIo(fileReader), new PropertiesConfigIo(fileReader));
    }
}