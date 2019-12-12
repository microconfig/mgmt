package mgmt.utils;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@Disabled
class ProcessUtilTest {
    @Test
    void waitTerminationOrLogMarkerShouldReadTerminationMarker() {
        assertTimeoutPreemptively(ofSeconds(200), () -> {
            File log = new File(getClass().getClassLoader().getResource("waitTerminationOrLogMarkerTest.log").getFile());

            ProcessUtil.waitTerminationOrLogMarker(
                    ProcessHandle.current(),
                    log,
                    Set.of("Guardian control mock started on"),
                    currentTimeMillis(),
                    300
            );
        });
    }
}