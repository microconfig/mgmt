package deployment.mgmt.configs.diff;

import java.util.List;

public interface ShowDiffCommand {
    void showPropDiff(List<String> service);

    void showClasspathDiff(List<String> services);

    void printProperties(String name);
}
