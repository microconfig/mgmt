package deployment.mgmt.process.status;

import deployment.mgmt.configs.componentgroup.ServiceDescription;
import io.microconfig.utils.ConsoleColor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static deployment.mgmt.process.status.ExecutionStatus.*;
import static deployment.mgmt.process.status.ServiceType.TASK;
import static mgmt.utils.LoggerUtils.align;
import static io.microconfig.utils.StringUtils.isEmpty;
import static mgmt.utils.TimeUtils.formatTimeAfter;

@Data
@AllArgsConstructor
public class ServiceStatus {
    private final ServiceDescription serviceDescription;
    private final String configVersion;
    private final ServiceType serviceType;
    private final ExecutionStatus value;
    private final Long pid;
    private final Long startTime;

    public ServiceStatus(ServiceDescription sd, String configVersion, ServiceType serviceType, ExecutionStatus value) {
        this(sd, configVersion, serviceType, value, null, null);
    }

    public static ServiceStatus unknown(String service) {
        return new ServiceStatus(new ServiceDescription(service, "?"), "?",
                ServiceType.UNKNOWN, ExecutionStatus.UNKNOWN, null, null);
    }

    public static ServiceStatus fromProcess(ServiceDescription serviceDescription,
                                            String configVersion,
                                            ServiceType serviceType,
                                            ExecutionStatus executionStatus,
                                            ProcessHandle processHandle) {
        ProcessHandle.Info info = processHandle.info();
        return new ServiceStatus(serviceDescription, configVersion, serviceType, executionStatus,
                processHandle.pid(),
                info.startInstant().map(Instant::getEpochSecond).map(l -> l * 1_000).orElse(null)
        );
    }

    public void output(Consumer<String> writeTo) {
        String statusName = value
                + (getStartTime() == null ? "" : " (" + formatTimeAfter(getStartTime()) + ")")
                + (getPid() == null ? "" : "[" + getPid() + "]")
                + (getServiceType() == TASK ? "[TASK]" : "");

        Supplier<UnaryOperator<String>> statusColor = () -> {
            if (value == RUNNING || value == EXECUTED) return ConsoleColor::green;
            return value == FAILED ? ConsoleColor::red : ConsoleColor::yellow;
        };

        Supplier<String> versionInfo = () -> {
            if (serviceDescription.getVersion().equals(configVersion)) return configVersion;

            return (isEmpty(configVersion) ? "" : configVersion + ",") + serviceDescription.getVersion();
        };

        writeTo.accept(
                align(serviceDescription.getName(), 30)
                        + statusColor.get().apply(align(statusName, 25))
                        + versionInfo.get()
        );
    }
}
