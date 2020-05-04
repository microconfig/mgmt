package deployment.mgmt.configs.filestructure;

import java.io.File;

import static mgmt.utils.FileUtils.createDir;
import static mgmt.utils.FileUtils.userHome;
import static mgmt.utils.OsUtil.isWindows;

public class DeployDirsImpl implements DeployDirs {
    @Override
    public File getDeploySettingsDir() {
        return createDir(new File(userHome(), "deploy-settings"));
    }

    @Override
    public File getSecretPropertiesFile() {
        return new File(userHome(), "/secret/secret.properties");
    }

    @Override
    public File getEncryptionKeyFile() {
        return new File(userHome(), "/.pwd/password");
    }

    @Override
    public File getLockFile() {
        return new File(userHome(), "lock.mgmt");
    }

    @Override
    public File getMgmtScriptFile() {
        return deployFile("mgmt");
    }

    @Override
    public File getMgmtJarFile() {
        return deployFile("mgmt.jar");
    }

    @Override
    public File getGroupDescriptionFile() {
        return deployFile("env.mgmt");
    }

    @Override
    public File getPostMgmtScriptFile() {
        return deployFile("temp_command_script");
    }

    @Override
    public File getAlteredVersionsFile() {
        return deployFile("alteredServices.deployment");
    }

    @Override
    public File getDependenciesDir() {
        return new File(isWindows() ? userHome().getAbsolutePath() : "/home", getDependenciesUser());
    }

    @Override
    public String getDependenciesUser() {
        return "rpbin";
    }

    private File deployFile(String name) {
        return new File(getDeploySettingsDir(), name);
    }
}