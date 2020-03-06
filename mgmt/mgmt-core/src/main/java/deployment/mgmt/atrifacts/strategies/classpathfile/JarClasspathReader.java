package deployment.mgmt.atrifacts.strategies.classpathfile;

import deployment.mgmt.atrifacts.Artifact;

import java.io.File;
import java.util.List;

public interface JarClasspathReader {
    String CLASSPATH_FILE = "classpath.mgmt";
    String ARTIFACTS_FILE = "artifacts.mgmt";

    List<Artifact> extractClasspath(File artifactFile, Artifact artifact);
}