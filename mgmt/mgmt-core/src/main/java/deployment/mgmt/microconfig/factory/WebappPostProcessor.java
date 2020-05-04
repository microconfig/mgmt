//package deployment.mgmt.microconfig.factory;
//
//import io.microconfig.commands.buildconfig.BuildConfigPostProcessor;
//import io.microconfig.core.properties.ConfigProvider;
//import io.microconfig.core.properties.Property;
//import io.microconfig.core.properties.resolver.EnvComponent;
//import mgmt.utils.SystemPropertiesUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//
//import static io.microconfig.utils.FileUtils.delete;
//import static io.microconfig.utils.FileUtils.write;
//import static io.microconfig.utils.Logger.error;
//
//public class WebappPostProcessor implements BuildConfigPostProcessor {
//    private static final String WEBAPP_FILE = "mgmt.webapp";
//    private static final String DEPENDSON_FILE = "mgmt.dependson.list";
//    private static final String FORCED_STATUS_FILE = "mgmt.forced.status";
//
//    @Override
//    public void process(EnvComponent currentComponent,
//                        Map<String, Property> componentProperties,
//                        ConfigProvider configProvider, File resultFile) {
//        File destinationDir = resultFile.getParentFile();
//        delete(new File(destinationDir, WEBAPP_FILE));
//
//        if (!SystemPropertiesUtils.hasTrueValue("mgmt.tomcat.webapp.enabled", componentProperties)) return;
//
//        write(new File(destinationDir, WEBAPP_FILE), "");
//        delete(new File(destinationDir, DEPENDSON_FILE));
//        Property container = componentProperties.get("mgmt.webapp.container");
//        if (container == null) {
//            error("No container for webapp " + destinationDir.getParentFile().getAbsolutePath());
//            return;
//        }
//
//        write(new File(destinationDir, DEPENDSON_FILE), container.getValue());
//        write(new File(destinationDir, FORCED_STATUS_FILE), "WebApp(" + container + ")");
//
//        File parentFile = canonical(destinationDir);
//        String componentDirName = parentFile.getName();
//        String contextFileName = "mgmt.tomcat.context." + container + "" + componentDirName + ".xml";
//        String contextFile = "<?xml version='1.0' encoding='utf-8'?>\n" +
//                "<Context path=\"/" + componentDirName + "\" docBase=\"" + parentFile.getAbsoluteFile() + "/webapp\" />";
//
//        write(new File(destinationDir, contextFileName), contextFile);
//    }
//
//    private File canonical(File dir) {
//        try {
//            return dir.getCanonicalFile().getParentFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}