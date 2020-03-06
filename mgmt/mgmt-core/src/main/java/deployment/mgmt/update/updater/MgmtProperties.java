package deployment.mgmt.update.updater;

import deployment.mgmt.configs.service.properties.NexusRepository;
import io.microconfig.core.environments.EnvironmentProvider;
import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.factory.ConfigType;

import java.util.List;

public interface MgmtProperties {
    List<NexusRepository> resolveNexusRepositories();

    ConfigProvider getConfigProvider(ConfigType configType);

    EnvironmentProvider getEnvironmentProvider();
}