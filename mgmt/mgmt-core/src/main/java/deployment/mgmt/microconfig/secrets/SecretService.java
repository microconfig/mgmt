package deployment.mgmt.microconfig.secrets;


import io.microconfig.core.properties.Property;

import java.util.Map;
import java.util.Set;

public interface SecretService {
    Set<String> updateSecrets(Map<String, Property> componentProperties);
}