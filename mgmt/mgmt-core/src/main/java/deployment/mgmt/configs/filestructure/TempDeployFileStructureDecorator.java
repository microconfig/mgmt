package deployment.mgmt.configs.filestructure;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

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
        delegate = DeployFileStructureImpl.initToTempDir();
    }

    public void toMain() {
        delegate = main;
    }
}
