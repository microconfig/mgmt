package deployment.mgmt.configs.filestructure;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.io.File;

import static io.microconfig.utils.FileUtils.delete;

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
        File tempDir = new File("temp_deploy");
        delete(tempDir);
        delegate = DeployFileStructureImpl.initTo(tempDir);
    }

    public void toMain() {
        delegate = main;
    }
}
