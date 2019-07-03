package deployment.mgmt.configs.filestructure;

import lombok.experimental.Delegate;

import java.nio.file.Path;

public class TempDeployFileStructureDecorator implements DeployFileStructure {
    @Delegate
    private volatile DeployFileStructure delegate;
    private final DeployFileStructure main;

    public TempDeployFileStructureDecorator() {
        this.main = DeployFileStructureImpl.init();
        this.delegate = main;
    }

    public void changeRootDir(Path dir) {
        delegate = DeployFileStructureImpl.initTo(dir.toFile());
    }

    public void undo() {
        delegate = main;
    }
}
