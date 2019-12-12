package deployment.mgmt.atrifacts.strategies.classpathfile;

import deployment.mgmt.atrifacts.Artifact;

import java.io.File;
import java.util.List;

import static deployment.mgmt.utils.ZipUtils.readInnerFile;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class ArtifactClasspathReader implements JarClasspathReader {
    @Override
    public List<Artifact> extractClasspath(File artifactFile, Artifact artifact) {
        String classpath = new String(readInnerFile(artifactFile, ARTIFACTS_FILE));

        return stream(classpath.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Artifact::fromMavenString)
                .collect(toList());
    }
}
