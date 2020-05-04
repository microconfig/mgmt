package deployment.mgmt.configs.encryption.io;

import io.microconfig.core.configtypes.StandardConfigType;
import io.microconfig.core.properties.Property;
import io.microconfig.io.FsReader;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.FileBasedComponent.fileSource;
import static io.microconfig.core.properties.PropertyImpl.isComment;
import static io.microconfig.core.properties.PropertyImpl.parse;
import static io.microconfig.utils.FileUtils.LINES_SEPARATOR;
import static io.microconfig.utils.StreamUtils.toSortedMap;

@RequiredArgsConstructor
public class SecretPropertiesReader {
    protected final File file;
    protected final List<String> lines;

    public SecretPropertiesReader(File file, FsReader fileReader) {
        this(file, fileReader.readLines(file));
    }

    public Map<String, String> propertiesAsMap() {
        return properties().stream()
                .collect(toSortedMap(Property::getKey, Property::getValue));
    }

    private List<Property> properties() {
        List<Property> result = new ArrayList<>();

        StringBuilder currentLine = new StringBuilder();
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            String trimmed = line.trim();
            if (trimmed.isEmpty() || isComment(trimmed)) continue;

            currentLine.append(trimmed);
            if (isMultilineValue(trimmed)) {
                currentLine.append(LINES_SEPARATOR);
                continue;
            }

            Property property = parse(currentLine.toString(), PROPERTIES,
                    fileSource(file, lineNumber, false, StandardConfigType.SECRET.getName(), ""));
            result.add(property);
            currentLine.setLength(0);
        }

        return result;
    }

    private boolean isMultilineValue(String line) {
        return line.endsWith("\\");
    }
}