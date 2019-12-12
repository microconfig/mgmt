package deployment.mgmt.atrifacts.strategies.classpathfile;

import deployment.mgmt.atrifacts.Artifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtifactClasspathReaderTest {
    private ArtifactClasspathReader artifactClasspathReader;

    @BeforeEach
    void setUp() {
        artifactClasspathReader = new ArtifactClasspathReader();
    }

    @Test
    public void shouldCorrectlyCollectClasspathArtifacts() {
        List<String> expected = List.of(
                "antlr:antlr:2.7.7",
                "com.fasterxml.jackson.core:jackson-annotations:2.9.0",
                "com.google.guava:guava:18.0",
                "com.intellij:annotations:9.0.4",
                "dom4j:dom4j:1.6.1",
                "joda-time:joda-time:2.9.9",
                "org.hibernate.common:hibernate-commons-annotations:4.0.5.Final",
                "org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final",
                "org.hibernate:hibernate-core:4.3.10.Final",
                "org.javassist:javassist:3.18.1-GA",
                "org.jboss.logging:jboss-logging-annotations:1.2.0.Beta1",
                "org.jboss.logging:jboss-logging:3.3.2.Final",
                "org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec:1.0.0.Final",
                "org.jboss:jandex:1.1.0.Final",
                "org.slf4j:slf4j-api:1.7.25",
                "org.springframework:spring-aop:5.0.8.RELEASE",
                "org.springframework:spring-beans:5.0.8.RELEASE",
                "org.springframework:spring-context:5.0.8.RELEASE",
                "org.springframework:spring-core:5.0.8.RELEASE",
                "org.springframework:spring-expression:5.0.8.RELEASE",
                "org.springframework:spring-jcl:5.0.8.RELEASE",
                "ru.sbt.risk.tradehub.commons:date-utils:TH-19.24-SNAPSHOT",
                "ru.sbt.risk.tradehub.commons:fx:TH-19.24-SNAPSHOT",
                "ru.sbt.risk.tradehub.commons:util:TH-19.24-SNAPSHOT",
                "ru.sbt.risk.tradehub.integration:external-systems-data:TH-19.24-SNAPSHOT");

        File artifactFile = getFileByResourceName("/classpath-test.jar");
        List<Artifact> artifacts = artifactClasspathReader.extractClasspath(artifactFile, null);
        List<String> artifactFullNames = artifacts.stream().map(Artifact::getMavenFormatString).collect(toList());

        assertEquals(expected, artifactFullNames);
    }

    private File getFileByResourceName(String resName) {
        try {
            return Paths.get(getClass().getResource(resName).toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}