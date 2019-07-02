package deployment.mgmt.configs.compare;

public interface CompareCommand {
    void compareTo(String configVersion, String projectFullVersionOrPostfix);
}
