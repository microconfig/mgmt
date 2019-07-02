package deployment.mgmt.configs.filestructure;

import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.utils.FileUtils.createDir;
import static io.microconfig.utils.FileUtils.userHome;
import static io.microconfig.utils.OsUtil.isWindows;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class DeployDirsImpl implements DeployDirs {
    private final File deployDir;

    public static DeployDirs init(File root) {
        return new DeployDirsImpl(createDir(new File(root, "deploy-settings")));
    }

    @Override
    public File getDeploySettingsDir() {
        return deployDir;
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