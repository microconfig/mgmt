package deployment.mgmt.configs.diff;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.service.properties.PropertyService;
import io.microconfig.configs.io.ioservice.ConfigIoService;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.microconfig.utils.ConsoleColor.*;
import static io.microconfig.utils.IoUtils.readFully;
import static io.microconfig.utils.Logger.*;

@RequiredArgsConstructor
public class ShowDiffCommandImpl implements ShowDiffCommand {
    private final ComponentGroupService componentGroupService;
    private final PropertyService propertyService;
    private final DeployFileStructure deployFileStructure;
    private final ConfigIoService configIo;

    @Override
    public void showPropDiff(List<String> services) {
        Consumer<Function<String, File>> diffPrinter = fileFetcher -> {
            showDiff(services, fileFetcher, f -> configIo.read(f).propertiesAsMap().forEach(this::colorOutput));
        };

        diffPrinter.accept(deployFileStructure.service()::getServicePropertiesDiffFile);
        diffPrinter.accept(deployFileStructure.process()::getProcessDiffFile);
    }

    @Override
    public void showClasspathDiff(List<String> services) {
        showDiff(services,
                deployFileStructure.process()::getClasspathDiffFile,
                f -> info(readFully(f))
        );
    }

    @Override
    public void printProperties(String key) {
        componentGroupService.getServices().forEach(s -> {
            String process = propertyService.getProcessProperties(s).getOrDefault(key, "");
            String system = propertyService.getServiceProperties(s).getOrDefault(key, "");
            info(green(s) + " -> " + (yellow(process) + (system.isEmpty() ? "" : " " + system)));
        });
    }

    private void showDiff(List<String> services, Function<String, File> fileFetcher, Consumer<File> writer) {
        services.forEach(s -> {
            File file = fileFetcher.apply(s);
            if (!file.exists()) return;

            announce(s + " " + file.getName() + ":");
            writer.accept(file);
            logLineBreak();
        });
    }

    private void colorOutput(String key, String value) {
        if (key.startsWith("+")) {
            key = key.replaceFirst("\\+", green("+"));
        } else if (key.startsWith("-")) {
            key = key.replaceFirst("-", red("-"));
        } else {
            key = " " + key;
        }

        info(key + " = " + value.replaceFirst(" -> ", green(" -> ")));
    }
}