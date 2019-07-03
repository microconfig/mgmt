package deployment.mgmt.configs.filestructure;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.io.File;
import java.nio.file.Path;

@AllArgsConstructor
public class TempDeployFileStructureDecorator implements DeployFileStructure {
    @Delegate
    private volatile DeployFileStructure delegate;
    private final DeployFileStructure main;

    public TempDeployFileStructureDecorator() {
        main = DeployFileStructureImpl.init();
        delegate = main;
    }

    public void changeRootDir(Path dir) {
        delegate = DeployFileStructureImpl.initTo(dir.toFile());
    }

    public void undo() {
        delegate = main;
    }
}
