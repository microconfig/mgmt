package deployment.mgmt.ssh;

import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.update.updater.MgmtProperties;
import io.microconfig.core.environments.ComponentGroup;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static deployment.mgmt.utils.ExecutorUtils.executeInParallel;
import static mgmt.utils.FilePermissionUtils.writeExecutable;
import static mgmt.utils.IoUtils.lines;
import static io.microconfig.utils.Logger.warn;
import static java.lang.Math.min;
import static java.lang.System.out;
import static java.util.stream.Collectors.toList;
import static mgmt.utils.ProcessUtil.executeAndReadOutput;

@RequiredArgsConstructor
public class SshCommandImpl implements SshCommand {
    private final MgmtProperties mgmtProperties;
    private final DeployFileStructure deployFileStructure;

    @Override
    public void ssh(String env, String group) {
        String script = sshTo(env, group);
        if (script == null) return;
        writeExecutable(deployFileStructure.deploy().getPostMgmtScriptFile(), script);
    }

    @Override
    public void executeOn(String env, String group, String command) {
        executeAndReadOutput(out, remoteFullCommand(env, group, command));
    }

    @Override
    public <T> List<T> executeOnEveryNode(String env, String command,
                                          BiFunction<ComponentGroup, String, T> outputTransformer) {
        return executeOnEveryNode(env, command, g -> {
        }, outputTransformer);
    }

    @Override
    public <T> List<T> executeOnEveryNode(String env, String command,
                                          Consumer<ComponentGroup> beforeUpdateListener,
                                          BiFunction<ComponentGroup, String, T> outputTransformer) {
        Function<ComponentGroup, T> executeAndTransform = group -> {
            beforeUpdateListener.accept(group);

            String remoteOutput = null;
            try {
                remoteOutput = executeAndReadOutput(remoteFullCommand(env, group.getName(), command));
            } catch (RuntimeException ignore) {
            }

            return outputTransformer.apply(group, remoteOutput);
        };

        List<ComponentGroup> componentGroups = mgmtProperties.getEnvironmentProvider()
                .getByName(env)
                .getComponentGroups();
        return executeInParallel(
                () -> componentGroups
                        .parallelStream()
                        .map(executeAndTransform)
                        .collect(toList()),
                min(8, componentGroups.size())
        );
    }

    private String[] remoteFullCommand(String env, String group, String command) {
        return new String[]{"sh", "-c", sshTo(env, group) + " " + command};
    }

    private String sshTo(String env, String group) {
        Optional<String> credentialPair = findCredentialsLine(group, env);
        if (!credentialPair.isPresent()) {
            warn("Can't find group " + group + " in " + env + " env");
            return null;
        }

        String[] credentials = credentialPair.get().split("\\s+");
        String password = "?".equals(credentials[1]) ? credentials[0] : credentials[1];
        return deployFileStructure.configs().getMgmtScriptsDir()
                + "/sshpass -p " + password
                + " ssh -o StrictHostKeyChecking=no "
                + credentials[0] + "@" + credentials[2];
    }

    private Optional<String> findCredentialsLine(String group, String currentEnv) {
        try (Stream<String> lines = lines(deployFileStructure.configs().getEnvCfgFile().toPath())) {
            return lines.filter(l -> l.contains(currentEnv + ":" + group)).findFirst();
        }
    }
}