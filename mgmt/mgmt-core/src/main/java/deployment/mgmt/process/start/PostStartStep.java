package deployment.mgmt.process.start;

import deployment.mgmt.configs.service.properties.ProcessProperties;

public interface PostStartStep {
    void afterStart(String service, ProcessProperties processProperties);
}
