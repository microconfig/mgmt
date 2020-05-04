package deployment.mgmt.configs.updateconfigs;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.service.properties.PropertyService;
import io.microconfig.core.configtypes.StandardConfigType;
import io.microconfig.core.properties.*;
import io.microconfig.core.properties.templates.TemplatesService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.PropertyImpl.property;
import static java.lang.ThreadLocal.withInitial;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final TemplatesService copyTemplatesService;

    private final ComponentGroupService componentGroupService;
    private final DeployFileStructure deployFileStructure;
    private final PropertyService propertyService;

    private final ThreadLocal<Resolver> resolver = withInitial(this::newPropertyResolver);

    @Override
    public void copyTemplates(String service) {
        copyTemplatesService.resolveTemplate(propertiesOf(service), resolver.get());
    }

    private TypedProperties propertiesOf(String service) {
        return new TypedPropertiesImpl(StandardConfigType.APPLICATION, service, componentGroupService.getEnv(),
                getServiceProperties(service)
        );
    }

    private Map<String, Property> getServiceProperties(String service) {
        Map<String, String> properties = propertyService.getServiceProperties(service);
        DeclaringComponent declaringComponent = new DeclaringComponentImpl("app", service, componentGroupService.getEnv());
        return properties.entrySet().stream()
                .map(e -> property(e.getKey(), e.getValue(), PROPERTIES, declaringComponent))
                .collect(toMap(Property::getKey, identity()));
    }

    private Resolver newPropertyResolver() {
        return searchConfigsIn(deployFileStructure.configs().getMicroconfigSourcesRootDir())
                .withDestinationDir(deployFileStructure.service().getComponentsDir())
                .resolver();
    }
}
