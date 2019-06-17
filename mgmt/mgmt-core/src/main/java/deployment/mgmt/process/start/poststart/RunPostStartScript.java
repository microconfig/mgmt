package deployment.mgmt.process.start.poststart;

import deployment.mgmt.configs.service.properties.ProcessProperties;
import deployment.mgmt.process.runner.ScriptRunner;
import deployment.mgmt.process.start.PostStartStep;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RunPostStartScript implements PostStartStep {
    private final ScriptRunner scriptRunner;

    @Override
    public void afterStart(String service, ProcessProperties processProperties) {
        scriptRunner.runScript(processProperties.getPoststartScriptName(), service);
    }
}