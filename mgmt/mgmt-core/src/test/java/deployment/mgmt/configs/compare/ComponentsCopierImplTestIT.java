package deployment.mgmt.configs.compare;

import deployment.mgmt.configs.componentgroup.ComponentGroupServiceImpl;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.filestructure.DeployFileStructureImpl;
import deployment.mgmt.configs.service.properties.impl.PropertyServiceImpl;
import deployment.mgmt.factory.MgmtFactory;
import io.microconfig.configs.io.ioservice.selector.ConfigIoServiceSelector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentsCopierImplTestIT {
    @Test
    void cloneToTemp() {
        ComponentsCopier componentsCopier = new MgmtFactory().componentsCopier();;
        componentsCopier.cloneToTemp();
    }
}