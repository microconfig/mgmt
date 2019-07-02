package deployment.mgmt.configs.filestructure;

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
        return initTo(userHome());
    }

    static DeployFileStructure initTo(File root) {
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
