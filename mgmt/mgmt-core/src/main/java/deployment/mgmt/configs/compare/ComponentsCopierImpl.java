package deployment.mgmt.configs.compare;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import io.microconfig.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import static io.microconfig.utils.FileUtils.*;

@RequiredArgsConstructor
public class ComponentsCopierImpl implements ComponentsCopier {
    private final DeployFileStructure deployFileStructure;
    private final ComponentGroupService componentGroupService;

    @Override
    public Path cloneToTemp() {
        File destinationDir = destinationTempDir();
        Consumer<File> copy = f -> copyFile(f, destinationDir);

        copy.accept(deployFileStructure.service().getServiceListFile());
        componentGroupService.getServices().forEach(service -> {
            copy.accept(deployFileStructure.service().getServicePropertiesFile(service));
            copy.accept(deployFileStructure.process().getProcessPropertiesFile(service));
            copy.accept(deployFileStructure.process().getClasspathFile(service));
        });

        return destinationDir.toPath();
    }

    private void copyFile(File file, File destinationDir) {
        File to = new File(destinationDir, file.getAbsolutePath().replace(userHomeString(), "")); //todo
        FileUtils.createFile(to);
        FileUtils.copy(file, to);
    }

    private File destinationTempDir() {
        File dir = new File(userHome(), "temp_deploy");
        delete(dir);
        return dir;
    }
}