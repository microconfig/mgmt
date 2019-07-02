package deployment.mgmt.configs.filestructure;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.io.File;

import static io.microconfig.utils.FileUtils.delete;
import static io.microconfig.utils.FileUtils.userHome;

@AllArgsConstructor
public class TempDeployFileStructureDecorator implements DeployFileStructure {
    @Delegate
    private volatile DeployFileStructure delegate;
    private final DeployFileStructure main;

    public TempDeployFileStructureDecorator() {
        main = DeployFileStructureImpl.init();
        delegate = main;
    }

    public void toTemp() {
        File tempDir = cleanTempDir();
        delegate = DeployFileStructureImpl.initTo(tempDir);
    }

    public void toMain() {
        delegate = main;
    }

    public File cleanTempDir() {
        File tempDir = new File(userHome(), "temp_deploy");
        delete(tempDir);
        return tempDir;
    }
}
