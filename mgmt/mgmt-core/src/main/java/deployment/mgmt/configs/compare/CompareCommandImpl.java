package deployment.mgmt.configs.compare;

import deployment.mgmt.configs.filestructure.DeployFileStructure;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompareCommandImpl implements CompareCommand {
    private final DeployFileStructure deployFileStructure;

    @Override
    public void compareTo(String configVersion, String projectFullVersionOrPostfix) {

    }
}
