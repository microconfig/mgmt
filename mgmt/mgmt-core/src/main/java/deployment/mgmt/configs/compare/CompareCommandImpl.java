package deployment.mgmt.configs.compare;

import deployment.mgmt.atrifacts.ClasspathService;
import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.componentgroup.GroupDescription;
import deployment.mgmt.configs.diff.ShowDiffCommand;
import deployment.mgmt.configs.fetch.ConfigFetcher;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.service.properties.PropertyService;
import deployment.mgmt.microconfig.factory.MgmtMicroConfigAdapter;
import lombok.RequiredArgsConstructor;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RequiredArgsConstructor
public class CompareCommandImpl implements CompareCommand {
    private final ConfigFetcher configFetcher;
    private final ComponentGroupService componentGroupService;
    private final DeployFileStructure deployFileStructure;
    private final PropertyService propertyService;
    private final ClasspathService classpathService;
    private final ShowDiffCommand showDiffCommand;

    @Override
    public void compareTo(String configVersion, String projectFullVersionOrPostfix) {
        fetchConfigs(configVersion, projectFullVersionOrPostfix);
        buildAndCompareConfigs();
        buildAndCompareClasspath();
    }

    private void fetchConfigs(String configVersion, String projectFullVersionOrPostfix) {
        configFetcher.fetchConfigs(configVersion);
        componentGroupService.updateProjectVersion(projectFullVersionOrPostfix);
    }

    private void buildAndCompareConfigs() {
        GroupDescription groupDescription = componentGroupService.getDescription();
        MgmtMicroConfigAdapter.execute(
                groupDescription.getEnv(),
                singletonList(groupDescription.getGroup()),
                emptyList(), deployFileStructure.configs().getMicroconfigSourcesRootDir(),
                deployFileStructure.service().getComponentsDir()
        );

        showDiffCommand.showPropDiff();
    }

    private void buildAndCompareClasspath() {
        componentGroupService.getServices()
                .forEach(service -> classpathService.classpathFor(service)
                        .buildUsing(propertyService.getProcessProperties(service))
                );

        showDiffCommand.showClasspathDiff();
    }
}
