package deployment.mgmt.configs.filestructure;

import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.utils.FileUtils.createDir;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class ServiceDirsImpl implements ServiceDirs {
    private final File componentsDir;

    public static ServiceDirs init(File root) {
        return new ServiceDirsImpl(createDir(new File(root, "components")));
    }

    @Override
    public File getComponentsDir() {
        return componentsDir;
    }

    @Override
    public File getServiceListFile() {
        return new File(getComponentsDir(), "/.mgmt/mgmt.clist");
    }

    @Override
    public File getServiceDir(String service) {
        return new File(componentsDir, service);
    }

    @Override
    public File getServicePropertiesDiffFile(String service) {
        return getServiceFile(service, "diff-service.properties");
    }

    @Override
    public File getServicePropertiesFile(String service) {
        return getServiceFile(service, "service.properties");
    }

    @Override
    public File getServiceFile(String service, String file) {
        return new File(getServiceDir(service), file);
    }


    @Override
    public File getPidFile(String service) {
        return getServiceFile(service, ".pid");
    }
}