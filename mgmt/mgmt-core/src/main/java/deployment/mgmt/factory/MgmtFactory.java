package deployment.mgmt.factory;

import deployment.mgmt.api.console.Mgmt;
import deployment.mgmt.api.console.MgmtImpl;
import deployment.mgmt.api.console.MgmtLockDecorator;
import deployment.mgmt.api.console.MgmtServiceNameResolverDecorator;
import deployment.mgmt.api.json.MgmtJsonApi;
import deployment.mgmt.api.json.MgmtJsonApiImpl;
import deployment.mgmt.atrifacts.ClasspathService;
import deployment.mgmt.atrifacts.ClasspathServiceImpl;
import deployment.mgmt.atrifacts.ClasspathStoreImpl;
import deployment.mgmt.atrifacts.ClasspathStrategySelectorImpl;
import deployment.mgmt.atrifacts.nexusclient.NexusClient;
import deployment.mgmt.atrifacts.nexusclient.NexusClientImpl;
import deployment.mgmt.atrifacts.nexusclient.RepositoryPriorityServiceImpl;
import deployment.mgmt.atrifacts.strategies.classpathfile.*;
import deployment.mgmt.atrifacts.strategies.nexus.NexusClasspathStrategy;
import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.componentgroup.ComponentGroupServiceImpl;
import deployment.mgmt.configs.componentgroup.ServiceGroupManagerImpl;
import deployment.mgmt.configs.deploysettings.DeploySettings;
import deployment.mgmt.configs.deploysettings.DeploySettingsImpl;
import deployment.mgmt.configs.deploysettings.SimpleEncryptionServiceImpl;
import deployment.mgmt.configs.diff.ShowDiffCommandImpl;
import deployment.mgmt.configs.encryption.EncryptPropertiesCommandImpl;
import deployment.mgmt.configs.fetch.ConfigFetcher;
import deployment.mgmt.configs.fetch.ConfigFetcherImpl;
import deployment.mgmt.configs.fetch.strategy.GitConfigStrategy;
import deployment.mgmt.configs.fetch.strategy.NexusConfigStrategy;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.configs.filestructure.DeployFileStructureImpl;
import deployment.mgmt.configs.service.metadata.MetadataProvider;
import deployment.mgmt.configs.service.metadata.MetadataProviderImpl;
import deployment.mgmt.configs.service.properties.PropertyService;
import deployment.mgmt.configs.service.properties.impl.PropertyServiceImpl;
import deployment.mgmt.configs.servicenameresolver.ServiceNameResolverImpl;
import deployment.mgmt.configs.updateconfigs.*;
import deployment.mgmt.init.*;
import deployment.mgmt.lock.LockService;
import deployment.mgmt.lock.OsLockService;
import deployment.mgmt.process.log.LessLogCommand;
import deployment.mgmt.process.runner.ScriptRunner;
import deployment.mgmt.process.runner.ScriptRunnerImpl;
import deployment.mgmt.process.start.StartCommand;
import deployment.mgmt.process.start.StartCommandImpl;
import deployment.mgmt.process.start.poststart.RunPostStartScript;
import deployment.mgmt.process.start.prestart.*;
import deployment.mgmt.process.start.strategy.AppStartStrategy;
import deployment.mgmt.process.start.strategy.JavaStartStrategy;
import deployment.mgmt.process.start.strategy.NotExecutableStartStrategy;
import deployment.mgmt.process.start.strategy.StartStrategySelector;
import deployment.mgmt.process.status.StatusCommand;
import deployment.mgmt.process.status.StatusCommandImpl;
import deployment.mgmt.process.stop.*;
import deployment.mgmt.ssh.SshCommand;
import deployment.mgmt.ssh.SshCommandImpl;
import deployment.mgmt.stat.monitoring.MonitoringService;
import deployment.mgmt.stat.monitoring.MonitoringServiceImpl;
import deployment.mgmt.stat.releases.ReadyReleasesService;
import deployment.mgmt.stat.releases.ReadyReleasesServiceImpl;
import deployment.mgmt.update.restarter.RestarterImpl;
import deployment.mgmt.update.restarter.UpdateListener;
import deployment.mgmt.update.restarter.UpdateListenerImpl;
import deployment.mgmt.update.scriptgenerator.AutocompleteImpl;
import deployment.mgmt.update.scriptgenerator.MgmtScriptGenerator;
import deployment.mgmt.update.scriptgenerator.MgmtScriptGeneratorImpl;
import deployment.mgmt.update.updater.MgmtAutoUpdater;
import deployment.mgmt.update.updater.MgmtAutoUpdaterImpl;
import deployment.mgmt.update.updater.MgmtProperties;
import deployment.mgmt.update.updater.MgmtPropertiesImpl;
import io.microconfig.commands.buildconfig.features.templates.CopyTemplatesServiceImpl;
import io.microconfig.core.properties.io.ioservice.ConfigIoService;
import io.microconfig.core.properties.io.ioservice.properties.PropertiesConfigIoService;
import io.microconfig.core.properties.io.ioservice.selector.ConfigFormatDetectorImpl;
import io.microconfig.core.properties.io.ioservice.selector.ConfigIoServiceSelector;
import io.microconfig.core.properties.io.ioservice.yaml.YamlConfigIoService;
import io.microconfig.io.FsReader;
import io.microconfig.io.DumpedFsReader;
import lombok.Getter;

import java.util.List;

import static io.microconfig.commands.buildconfig.features.templates.TemplatePattern.DEFAULT_TEMPLATE_PREFIX;
import static io.microconfig.commands.buildconfig.features.templates.TemplatePattern.defaultPattern;
import static io.microconfig.utils.CacheProxy.cache;
import static java.util.Arrays.asList;
import static java.util.List.of;

@Getter
public class MgmtFactory {
    private final DeployFileStructure deployFileStructure;
    private final LockService lockService;
    private final ConfigIoService configIoService;
    private final ComponentGroupService componentGroupService;
    private final PropertyService propertyService;
    private final MetadataProvider metadataProvider;
    private final NexusClient nexusClient;
    private final ClasspathService classpathService;
    private final ScriptRunner scriptRunner;
    private final DeploySettings deploySettings;
    private final StopCommand stopCommand;
    private final KillCommand killCommand;
    private final UpdateConfigCommand updateConfigCommand;
    private final MgmtAutoUpdater mgmgUpdater;
    private final StopService stopService;
    private final MgmtProperties mgmtProperties;

    private final StatusCommand statusCommand;
    private final SshCommand sshCommand;
    private final MonitoringService monitoringService;
    private final ReadyReleasesService readyReleasesService;
    private final MgmtScriptGenerator mgmtScriptGenerator;
    private final ConfigFetcher configFetcher;

    public MgmtFactory() {
        this.deployFileStructure = DeployFileStructureImpl.init();
        this.lockService = new OsLockService(deployFileStructure);
        FsReader fileReader = new DumpedFsReader();
        this.configIoService = new ConfigIoServiceSelector(
                cache(new ConfigFormatDetectorImpl(fileReader)),
                new YamlConfigIoService(fileReader),
                new PropertiesConfigIoService(fileReader)
        );
        this.propertyService = new PropertyServiceImpl(deployFileStructure, configIoService);
        this.metadataProvider = new MetadataProviderImpl(deployFileStructure);
        this.componentGroupService = new ComponentGroupServiceImpl(deployFileStructure, propertyService, configIoService);
        this.deploySettings = new DeploySettingsImpl(deployFileStructure, componentGroupService, new SimpleEncryptionServiceImpl(), configIoService);
        this.nexusClient = new NexusClientImpl(
                new RepositoryPriorityServiceImpl(asList("ru", Mgmt.class.getPackage().getName().split("\\.")[0])),
                deploySettings
        );
        this.classpathService = new ClasspathServiceImpl(
                deployFileStructure,
                new ClasspathStoreImpl(deployFileStructure, propertyService),
                ClasspathStrategySelectorImpl.from(
                        new NexusClasspathStrategy(),
                        new ClasspathFileStrategy(
                                new JarClasspathReaderSelector(new ArtifactClasspathReader(), new MgmtClasspathFileReader()),
                                new UnknownGroupResolverImpl(nexusClient),
                                nexusClient
                        )
                )
        );
        this.scriptRunner = new ScriptRunnerImpl(deployFileStructure);
        this.mgmtProperties = new MgmtPropertiesImpl(deployFileStructure, componentGroupService);
        this.mgmgUpdater = new MgmtAutoUpdaterImpl(
                deploySettings,
                deployFileStructure,
                nexusClient,
                new RestarterImpl(lockService)
        );
        this.stopCommand = new StopCommandImpl(propertyService, metadataProvider);
        this.killCommand = new KillCommandImpl(deployFileStructure);
        this.mgmtScriptGenerator = new MgmtScriptGeneratorImpl(
                deployFileStructure,
                new AutocompleteImpl(componentGroupService, mgmtProperties)
        );
        this.updateConfigCommand = new UpdateConfigCommandImpl(
                componentGroupService,
                deployFileStructure,
                stopCommand,
                new NewServicePreparerImpl(
                        propertyService,
                        deployFileStructure,
                        classpathService,
                        new ExtractServiceImpl(classpathService),
                        scriptRunner,
                        microconfigTemplateService()
                ),
                mgmtScriptGenerator
        );
        this.stopService = new StopServiceImpl(componentGroupService, stopCommand, killCommand);
        this.statusCommand = new StatusCommandImpl(metadataProvider, propertyService);
        this.sshCommand = new SshCommandImpl(mgmtProperties, deployFileStructure);
        this.monitoringService = new MonitoringServiceImpl(componentGroupService, sshCommand, statusCommand);
        this.configFetcher = new ConfigFetcherImpl(
                deploySettings,
                deployFileStructure,
                propertyService,
                new GitConfigStrategy(deploySettings, deployFileStructure),
                new NexusConfigStrategy(nexusClient, deploySettings)
        );
        this.readyReleasesService = new ReadyReleasesServiceImpl(propertyService, deploySettings, nexusClient, configFetcher);
    }

    private TemplateService microconfigTemplateService() {
        return new TemplateServiceImpl(
                new CopyTemplatesServiceImpl(
                        defaultPattern().withTemplatePrefixes(List.of("mgmt.template.", DEFAULT_TEMPLATE_PREFIX)),
                        new OldConfigsRelativePathResolver(deployFileStructure.configs().getConfigsRootDir())
                ),
                componentGroupService,
                deployFileStructure,
                propertyService
        );
    }

    public Mgmt getMgmt() {
        return new MgmtLockDecorator(
                new MgmtServiceNameResolverDecorator(
                        mgmt(),
                        new ServiceNameResolverImpl(deploySettings, componentGroupService, propertyService)
                ), lockService
        );
    }

    private Mgmt mgmt() {
        return new MgmtImpl(
                componentGroupService,
                statusCommand,
                new ServiceGroupManagerImpl(componentGroupService, statusCommand, deployFileStructure, propertyService),
                newStartCommand(),
                stopService,
                newInitService(),
                new EncryptPropertiesCommandImpl(deployFileStructure, configIoService),
                updateConfigCommand,
                new ShowDiffCommandImpl(componentGroupService, propertyService, deployFileStructure, configIoService),
                new LessLogCommand(propertyService, deployFileStructure),
                deploySettings,
                mgmgUpdater,
                readyReleasesService,
                sshCommand,
                monitoringService
        );
    }

    public MgmtJsonApi getMgmtJsonApi() {
        return new MgmtJsonApiImpl(monitoringService, readyReleasesService, componentGroupService);
    }

    private InitService newInitService() {
        return new InitServiceImpl(
                mgmtScriptGenerator,
                stopService,
                new OldFilesCleanerImpl(componentGroupService, deployFileStructure),
                deploySettings,
                componentGroupService,
                new PwdServiceImpl(deployFileStructure),
                newEnvDependenciesDownloader(),
                newInitConfigCommand()
        );
    }

    public EnvDependenciesDownloader newEnvDependenciesDownloader() {
        return new EnvDependenciesDownloaderImpl(mgmtProperties, deployFileStructure, componentGroupService, nexusClient);
    }

    private InitConfigsCommand newInitConfigCommand() {
        return new InitConfigsCommandImpl(
                configFetcher,
                componentGroupService,
                updateConfigCommand,
                mgmgUpdater
        );
    }

    private StartCommand newStartCommand() {
        return new StartCommandImpl(
                propertyService,
                new CompositePreStar(of(
                        new ApplyAlteredVersion(componentGroupService),
                        new ArchiveLogs(deployFileStructure),
                        new BuildClasspath(classpathService),
                        new RunPreStartScript(scriptRunner)
                )),
                new RunPostStartScript(scriptRunner),
                new StartStrategySelector(of(
                        new NotExecutableStartStrategy(metadataProvider),
                        new JavaStartStrategy(deployFileStructure, metadataProvider, classpathService),
                        new AppStartStrategy(deployFileStructure, metadataProvider)
                ))
        );
    }

    public UpdateListener newUpdateListener() {
        return new UpdateListenerImpl(this);
    }
}