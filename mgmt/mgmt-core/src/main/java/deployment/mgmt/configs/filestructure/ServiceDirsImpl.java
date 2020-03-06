package deployment.mgmt.configs.filestructure;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;

import java.io.File;

import static io.microconfig.utils.FileUtils.createDir;
import static io.microconfig.utils.FileUtils.userHome;

@RequiredArgsConstructor
public class ServiceDirsImpl implements ServiceDirs {
    private final File componentsDir = createDir(new File(userHome(), "components"));

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
        File old = propertiesOrYaml(service, "diff-service");
        return old.exists() ? old : propertiesOrYaml(service, "diff-application");
    }

    @Override
    public File getServicePropertiesFile(String service) {
        File oldName = propertiesOrYaml(service, "service");
        return oldName.exists() ? oldName : propertiesOrYaml(service, "application");
    }

    @Override
    public File getServiceFile(String service, String file) {
        return new File(getServiceDir(service), file);
    }

    @Override
    public File getPidFile(String service) {
        return getServiceFile(service, ".pid");
    }

    private File propertiesOrYaml(String service, String baseFileName) {
        Function<String, File> getFile = ext -> getServiceFile(service, baseFileName + "." + ext);

        File properties = getFile.apply("properties");
        return properties.exists() ? properties : getFile.apply("yaml");
    }
}