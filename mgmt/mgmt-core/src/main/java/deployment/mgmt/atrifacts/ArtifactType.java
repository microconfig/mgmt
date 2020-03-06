package deployment.mgmt.atrifacts;

import java.util.stream.Stream;

public enum ArtifactType {
    JAR,
    POM,
    GZ;

    public static boolean isType(String type) {
        return Stream.of(values()).anyMatch(at -> at.name().equalsIgnoreCase(type));
    }

    public String extension() {
        return "." + name().toLowerCase();
    }
}
