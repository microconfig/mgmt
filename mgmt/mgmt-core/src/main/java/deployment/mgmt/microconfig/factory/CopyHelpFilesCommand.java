package deployment.mgmt.microconfig.factory;

import io.microconfig.commands.Command;
import io.microconfig.commands.CommandContext;
import io.microconfig.core.environments.Component;
import io.microconfig.core.environments.EnvironmentProvider;
import io.microconfig.core.properties.io.tree.ComponentTree;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static deployment.mgmt.microconfig.factory.MgmtMicroConfigAdapter.MGMT;
import static mgmt.utils.FileUtils.copy;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class CopyHelpFilesCommand implements Command {
    private static final String FILE_NAME = "help.txt";

    private final EnvironmentProvider environmentProvider;
    private final ComponentTree componentTree;
    private final Path destRootDir;

    @Override
    public void execute(CommandContext context) {
        context.components(environmentProvider)
                .forEach(this::processComponent);
    }

    private void processComponent(Component component) {
        String componentName = component.getName();
        findSourceHelpFile(componentName)
                .ifPresent(path -> copy(path.toPath(), destinationPath(componentName)));
    }

    private Optional<File> findSourceHelpFile(String componentName) {
        List<File> helpFiles = componentTree.getConfigFiles(componentName, p -> p.getName().equals(FILE_NAME))
                .collect(toList());

        if (helpFiles.isEmpty()) return empty();
        if (helpFiles.size() == 1) return of(helpFiles.get(0));
        throw new IllegalArgumentException("Multiple help files found for component " + componentName);
    }

    private Path destinationPath(String componentName) {
        return destRootDir
                .resolve(componentName)
                .resolve(MGMT)
                .resolve(FILE_NAME);
    }
}