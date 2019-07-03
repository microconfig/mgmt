package deployment.mgmt.configs.compare;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.deploysettings.DeploySettings;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import static io.microconfig.utils.FileUtils.*;
import static io.microconfig.utils.IoUtils.readFully;
import static org.springframework.util.FileSystemUtils.copyRecursively;

@RequiredArgsConstructor
public class ComponentsCopierImpl implements ComponentsCopier {
    private final DeploySettings deploySettings;
    private final ComponentGroupService componentGroupService;
    private final DeployFileStructure deployFileStructure;

    @Override
    public Path cloneToTemp(String newConfigVersion, String newProjectFullVersion) {
        File destinationDir = destinationTempDir();
        delete(destinationDir);

        copyComponents(destinationDir, newConfigVersion, newProjectFullVersion);
        copyDeploySettings(destinationDir);

        return destinationDir.toPath();
    }

    private void copyComponents(File destinationDir, String newConfigVersion, String newProjectFullVersion) {
        Consumer<File> copy = f -> copyWithoutMeaninglessValues(f, destinationDir, newConfigVersion, newProjectFullVersion);

        copy.accept(deployFileStructure.service().getServiceListFile());
        componentGroupService.getServices().forEach(service -> {
            copy.accept(deployFileStructure.service().getServicePropertiesFile(service));
            copy.accept(deployFileStructure.process().getProcessPropertiesFile(service));
            copyWithoutMeaninglessValues(deployFileStructure.process().getClasspathFile(service), destinationDir, null, null);
        });
    }

    private void copyDeploySettings(File destinationDir) {
        try {
            File from = deployFileStructure.deploy().getDeploySettingsDir();
            copyRecursively(from, new File(destinationDir, from.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyWithoutMeaninglessValues(File source, File destinationDir, String newConfigVersion, String newProjectFullVersion) {
        String value = readFully(source).replace(userHomeString(), destinationTempDir().getAbsolutePath());

        if (newConfigVersion != null) {
            value = value.replace(deploySettings.getConfigVersion(), newConfigVersion);
        }
        if (newProjectFullVersion != null) {
            value = value.replace(deploySettings.getProjectVersion(), newProjectFullVersion);
        }

        File to = new File(destinationDir, source.getAbsolutePath().replace(userHomeString(), ""));
        write(to, value);
    }

    private File destinationTempDir() {
        return new File(userHome(), "temp_deploy");
    }
}