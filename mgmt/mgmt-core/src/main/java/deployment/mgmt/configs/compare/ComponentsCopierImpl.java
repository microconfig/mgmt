package deployment.mgmt.configs.compare;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.deploysettings.DeploySettings;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import io.microconfig.utils.FileUtils;
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

            doCopy(deployFileStructure.process().getClasspathFile(service), destinationDir);
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
        String value = readFully(source)
                .replace(userHomeString(), destinationTempDir().getAbsolutePath())
                .replace(deploySettings.getConfigVersion(), newConfigVersion)
                .replace(deploySettings.getProjectVersion(), newProjectFullVersion);

        write(buildToPath(source, destinationDir), value);
    }

    private void doCopy(File source, File destinationDir) {
        FileUtils.copy(source, buildToPath(source, destinationDir));
    }

    private File buildToPath(File source, File destinationDir) {
        return new File(destinationDir, source.getAbsolutePath().replace(userHomeString(), ""));
    }

    private File destinationTempDir() {
        return new File(userHome(), "temp_deploy");
    }
}