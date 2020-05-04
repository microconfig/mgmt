package deployment.mgmt.update.updater;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.componentgroup.GroupDescription;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.service.properties.NexusRepository;
import deployment.mgmt.configs.service.properties.impl.ProcessPropertiesImpl;
import io.microconfig.core.Microconfig;
import io.microconfig.core.environments.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configType;
import static io.microconfig.core.configtypes.StandardConfigType.PROCESS;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class MgmtPropertiesImpl implements MgmtProperties {
    private final DeployFileStructure deployFileStructure;
    private final ComponentGroupService componentGroupService;

    @Override
    public List<NexusRepository> resolveNexusRepositories() {
        Microconfig microconfig = microconfig();
        Component service = anyServiceFromCurrentGroup(microconfig);
        return resolveNexusUrlProperty(service, microconfig);
    }

    @Override
    public Microconfig microconfig() {
        return searchConfigsIn(deployFileStructure.configs().getMicroconfigSourcesRootDir())
                .withDestinationDir(deployFileStructure.service().getComponentsDir());

    }

    private List<NexusRepository> resolveNexusUrlProperty(Component component, Microconfig microconfig) {
        info("Resolving nexus repositories");
        Map<String, String> properties = component.getPropertiesFor(configType(PROCESS))
                .resolveBy(microconfig.resolver())
                .withoutVars()
                .getPropertiesAsKeyValue();
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("Can't resolve process properties for " + component);
        }

        return ProcessPropertiesImpl.fromMap(properties).getMavenSettings().getNexusRepositories();
    }

    private Component anyServiceFromCurrentGroup(Microconfig microconfig) {
        GroupDescription cg = componentGroupService.getDescription();

        return microconfig.inEnvironment(cg.getEnv())
                .getGroupWithName(cg.getGroup())
                .getComponents()
                .asList()
                .get(0);
    }
}