package deployment.mgmt.configs.compare;

import deployment.mgmt.atrifacts.ClasspathService;
import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.componentgroup.GroupDescription;
import deployment.mgmt.configs.diff.ShowDiffCommand;
import deployment.mgmt.configs.fetch.ConfigFetcher;
import deployment.mgmt.configs.filestructure.TempDeployFileStructureDecorator;
import deployment.mgmt.configs.service.properties.PropertyService;
import deployment.mgmt.microconfig.factory.MgmtMicroConfigAdapter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.utils.FileUtils.delete;
import static io.microconfig.utils.Logger.warn;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RequiredArgsConstructor
public class CompareCommandImpl implements CompareCommand {
    private final ComponentsCopier componentsCopier;
    private final ConfigFetcher configFetcher;
    private final ComponentGroupService componentGroupService;
    private final TempDeployFileStructureDecorator deployFileStructure;
    private final PropertyService propertyService;
    private final ClasspathService classpathService;
    private final ShowDiffCommand showDiffCommand;

    @Override
    public void compareTo(String configVersion, String projectFullVersion) {
        Path dir = componentsCopier.cloneToTemp(configVersion, projectFullVersion);
        deployFileStructure.changeRootDir(dir);

        try {
            doCompare(configVersion, projectFullVersion);
        } finally {
            deployFileStructure.undo();
            delete(dir.toFile());
        }
    }

    private void doCompare(String configVersion, String projectFullVersion) {
        fetchConfigs(configVersion, projectFullVersion);

        buildConfigs();
        buildClasspath();
        showDiff();
    }

    private void fetchConfigs(String configVersion, String projectFullVersion) {
        configFetcher.fetchConfigs(configVersion);
        componentGroupService.updateConfigVersion(configVersion);
        componentGroupService.updateProjectVersion(projectFullVersion);
    }

    private void buildConfigs() {
        GroupDescription groupDescription = componentGroupService.getDescription();
        MgmtMicroConfigAdapter.execute(
                groupDescription.getEnv(),
                singletonList(groupDescription.getGroup()),
                emptyList(), deployFileStructure.configs().getMicroconfigSourcesRootDir(),
                componentDir()
        );
    }

    private void buildClasspath() {
        componentGroupService.getServices()
                .forEach(service -> classpathService.classpathFor(service)
                        .buildUsing(propertyService.getProcessProperties(service))
                );
    }

    private void showDiff() {
        List<String> services = componentGroupService.getServices();

        warn("\n\nPROPERTIES DIFF:");
        showDiffCommand.showPropDiff(services);

        warn("\n\nCLASSPATH DIFF:");
        showDiffCommand.showClasspathDiff(services);
    }

    private File componentDir() {
        return deployFileStructure.service().getComponentsDir();
    }
}