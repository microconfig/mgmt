package deployment.mgmt.update.updater;

import deployment.mgmt.configs.service.properties.NexusRepository;
import io.microconfig.core.Microconfig;

import java.util.List;

public interface MgmtProperties {
    List<NexusRepository> resolveNexusRepositories();

    Microconfig microconfig();
}