package deployment.mgmt.atrifacts.strategies.classpathfile;

import deployment.mgmt.atrifacts.Artifact;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;

import static deployment.mgmt.utils.ZipUtils.containsInnerFile;

@RequiredArgsConstructor
public class JarClasspathReaderSelector implements JarClasspathReader {
    private final ArtifactClasspathReader artifactClasspathReader;
    private final MgmtClasspathFileReader mgmtClasspathFileReader;

    @Override
    public List<Artifact> extractClasspath(File artifactFile, Artifact artifact) {
        JarClasspathReader classpathReader =
                containsInnerFile(artifactFile, CLASSPATH_GRADLE_FILE) ? artifactClasspathReader : mgmtClasspathFileReader;

        return classpathReader.extractClasspath(artifactFile, artifact);
    }
}
