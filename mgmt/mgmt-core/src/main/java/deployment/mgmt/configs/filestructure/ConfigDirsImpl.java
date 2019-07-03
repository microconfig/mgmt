package deployment.mgmt.configs.filestructure;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.function.Function;

import static io.microconfig.utils.FileUtils.createDir;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class ConfigDirsImpl implements ConfigDirs {
    private final File configRootDir;

    public static ConfigDirs init(File root) {
        return new ConfigDirsImpl(createDir(new File(root, "config-repo")));
    }

    @Override
    public File getConfigsRootDir() {
        return configRootDir;
    }

    @Override
    public File getMicroconfigSourcesRootDir() {
        return createDir(new File(getConfigsRootDir(), "repo"));
    }

    @Override
    public File getConfigVersionFile() {
        return configFile("/components/system/versions/config-version.proc");
    }

    @Override
    public File getProjectVersionFile(String env) {
        return configFile("/components/system/versions/project-version" + (env == null ? "" : "." + env) + ".proc");
    }

    @Override
    public File getScriptsDir() {
        return new File(getConfigsRootDir(), "/bin/scripts");
    }

    @Override
    public File getMgmtScriptsDir() {
        return new File(getConfigsRootDir(), "/mgmt");
    }

    @Override
    public File getMgmtArtifactFile(String env) {
        Function<String, File> mgmtFile = suffix -> configFile("/components/mgmt/mgmt-version/mgmt" + suffix + ".proc");

        File envFile = mgmtFile.apply("." + env);
        return envFile.exists() ? envFile : mgmtFile.apply("");
    }

    @Override
    public File getEnvCfgFile() {
        return new File(getConfigsRootDir(), "share/env/env.cfg");
    }

    private File configFile(String file) {
        return new File(getMicroconfigSourcesRootDir(), file);
    }
}