package deployment.mgmt.configs.filestructure;

import io.microconfig.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.utils.FileUtils.delete;
import static io.microconfig.utils.FileUtils.userHome;

@RequiredArgsConstructor
public class DeployFileStructureImpl implements DeployFileStructure {
    private final DeployDirs deployDirs;
    private final ConfigDirs configDirs;
    private final ServiceDirs serviceDirs;
    private final ServiceLogDirs serviceLogDirs;
    private final ProcessDirs processDirs;

    public static DeployFileStructure init() {
        return doInit(userHome());
    }

    public static DeployFileStructure initToTempDir() {
        File tempDir = new File("temp_deploy");
        delete(tempDir);
        return doInit(tempDir);
    }

    private static DeployFileStructure doInit(File root) {
        ServiceDirs serviceDirs = ServiceDirsImpl.init(root);
        return new DeployFileStructureImpl(
                DeployDirsImpl.init(root),
                ConfigDirsImpl.init(root),
                serviceDirs,
                new ServiceLogDirsImpl(serviceDirs), new ProcessDirsImpl(serviceDirs)
        );
    }

    @Override
    public DeployDirs deploy() {
        return deployDirs;
    }

    @Override
    public ConfigDirs configs() {
        return configDirs;
    }

    @Override
    public ServiceDirs service() {
        return serviceDirs;
    }

    @Override
    public ServiceLogDirs logs() {
        return serviceLogDirs;
    }

    @Override
    public ProcessDirs process() {
        return processDirs;
    }
}
