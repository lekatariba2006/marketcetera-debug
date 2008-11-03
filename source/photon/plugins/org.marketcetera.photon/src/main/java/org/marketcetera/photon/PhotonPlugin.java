package org.marketcetera.photon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.LogManager;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.HttpDatabaseIDFactory;
import org.marketcetera.core.IDFactory;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.marketcetera.photon.scripting.ScriptChangesAdapter;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.photon.views.OptionOrderTicketController;
import org.marketcetera.photon.views.OptionOrderTicketModel;
import org.marketcetera.photon.views.OrderTicketModel;
import org.marketcetera.photon.views.SecondaryIDCreator;
import org.marketcetera.photon.views.StockOrderTicketController;
import org.marketcetera.photon.views.StockOrderTicketModel;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyCore;

import quickfix.Message;

/**
 * The main plugin class to be used in the Photon application.
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PhotonPlugin 
    extends AbstractUIPlugin
    implements Messages, IPropertyChangeListener
{

	public static final String ID = "org.marketcetera.photon"; //$NON-NLS-1$

	//The shared instance.
	private static PhotonPlugin plugin;

	private FIXMessageHistory fixMessageHistory;

	
	/**
	 * Cannot be initialized until after logging infrastructure is set up
	 */
	private Logger mainConsoleLogger;
	private Logger marketDataLogger;

	private ScriptRegistry scriptRegistry;

	private PhotonController photonController;

	private IDFactory idFactory;

	private BundleContext bundleContext;
	
	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger"; //$NON-NLS-1$

	public static final String MARKETDATA_CONSOLE_LOGGER_NAME = "marketdata.console.logger"; //$NON-NLS-1$

	public static final String DEFAULT_PROJECT_NAME = "ActiveScripts"; //$NON-NLS-1$

	private static final String RUBY_NATURE_ID = ".rubynature"; //$NON-NLS-1$

	private ScriptChangesAdapter scriptChangesAdapter;

	private SimpleMessageListenerContainer registryListener;

	private FIXMessageFactory messageFactory;

	private FIXVersion fixVersion;

	private SecondaryIDCreator secondaryIDCreator = new SecondaryIDCreator();

	private OrderTicketModel stockOrderTicketModel;

	private OptionOrderTicketModel optionOrderTicketModel;

	private StockOrderTicketController stockOrderTicketController;

	private OptionOrderTicketController optionOrderTicketController;

	/**
	 * The constructor.
	 */
	public PhotonPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
		
		configureLogs();
		
		mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);
		
		marketDataLogger = Logger.getLogger(MARKETDATA_CONSOLE_LOGGER_NAME);
		
		
		new DefaultScope().getNode("org.rubypeople.rdt.launching").putBoolean("org.rubypeople.rdt.launching.us.included.jruby", true);
		
		String level = getPreferenceStore().getString(PhotonPage.LOG_LEVEL_KEY);
		changeLogLevel(level == null ? PhotonPage.LOG_LEVEL_VALUE_INFO : level);
		
		// This sets the internal broker to use on thread per "listener"?
		// Needed because the version of JRuby we're using doesn't play well
		// with mutliple threads
        System.setProperty("org.apache.activemq.UseDedicatedTaskRunner", "true"); //$NON-NLS-1$ //$NON-NLS-2$

        BSFManager.registerScriptingEngine(ScriptRegistry.RUBY_LANG_STRING,
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" }); //$NON-NLS-1$ //$NON-NLS-2$
		initMessageFactory();
		initIDFactory();
		initFIXMessageHistory();
		initScriptRegistry();
		initPhotonController();
		PhotonPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	public void initOrderTickets(){
		stockOrderTicketModel = new StockOrderTicketModel(messageFactory);
		optionOrderTicketModel = new OptionOrderTicketModel(messageFactory);
		stockOrderTicketController = new StockOrderTicketController(stockOrderTicketModel);
		optionOrderTicketController = new OptionOrderTicketController(optionOrderTicketModel);
	}
	
	private void initPhotonController() {
		photonController = new PhotonController();
		photonController.setMessageHistory(fixMessageHistory);
		photonController.setMainConsoleLogger(getMainConsoleLogger());
		photonController.setIDFactory(idFactory);
		photonController.setMessageFactory(messageFactory);

	}

	private void initFIXMessageHistory() {
		fixMessageHistory = new FIXMessageHistory(messageFactory);
	}

	private void initScriptRegistry() {
		scriptRegistry = new ScriptRegistry();
		scriptChangesAdapter = new ScriptChangesAdapter();
		scriptChangesAdapter.setRegistry(scriptRegistry);
		
	}



	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		
		if (scriptRegistry != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(scriptChangesAdapter);
			scriptRegistry = null;
		}
		if (registryListener != null)
			registryListener.stop();
	}

	/**
	 * Returns the shared instance.
	 */
	public static PhotonPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	@Override
	public ScopedPreferenceStore getPreferenceStore() {
		return (ScopedPreferenceStore) super.getPreferenceStore();
	}

	private void initIDFactory() throws MalformedURLException, UnknownHostException
	{
		ScopedPreferenceStore preferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		URL url = new URL(
				"http", //$NON-NLS-1$
				preferenceStore.getString(ConnectionConstants.WEB_APP_HOST_KEY),
				preferenceStore.getInt(ConnectionConstants.WEB_APP_PORT_KEY),
				"/id_repository/get_next_batch" //$NON-NLS-1$
		);
		idFactory = new HttpDatabaseIDFactory(url, preferenceStore.getString(ConnectionConstants.ORDER_ID_PREFIX_KEY));

	}
	
	private void initMessageFactory() throws FIXFieldConverterNotAvailable {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		String versionString = thePreferenceStore.getString(ConnectionConstants.FIX_VERSION_KEY);
		fixVersion = FIXVersion.FIX42;
		try {
			fixVersion = FIXVersion.valueOf(versionString);
		} catch (IllegalArgumentException iae) {
			// just use version 4.2
		}
		messageFactory = fixVersion.getMessageFactory();
		FIXDataDictionaryManager.initialize(FIXVersion.FIX44, "FIX44.xml"); //$NON-NLS-1$
		CurrentFIXDataDictionary.setCurrentFIXDataDictionary(
				FIXDataDictionaryManager.initialize(fixVersion, 
						fixVersion.getDataDictionaryURL()));
	}

	public void startScriptRegistry() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		try {
			scriptChangesAdapter.setInitialRegistryValueString(thePreferenceStore.getString(ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE));
			scriptRegistry.afterPropertiesSet();
			scriptChangesAdapter.afterPropertiesSet();
		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			getMainConsoleLogger().error(CANNOT_START_SCRIPT_ENGINE.getText(),
			                             targetException);
		} catch (Exception e) {
			getMainConsoleLogger().error(CANNOT_START_SCRIPT_ENGINE.getText(),
			                             e);
		}
		thePreferenceStore.addPropertyChangeListener(scriptChangesAdapter);
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(scriptChangesAdapter, 
				IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);
		
	}

	/**
	 * Accessor for the console logger singleton.  This logger writes
	 * messages into the main console displayed to the user in the application.
	 * @return the main console logger
	 */
	public Logger getMainLogger()
	{
		return mainConsoleLogger;
	}

	public Logger getMarketDataLogger() {
		return marketDataLogger;
	}
	
	public static Logger getMainConsoleLogger()
	{
		return getDefault().getMainLogger();
	}
	                                            
	/**
	 * Accessor for the FIXMessageHistory singleton.
	 * 
	 * @return the FIXMessageHistory singleton
	 */
	public FIXMessageHistory getFIXMessageHistory() {
		return fixMessageHistory;
	}

	public ScriptRegistry getScriptRegistry() {
		return scriptRegistry;
	}
	
	/** 
	 * Accessor for the OrderManager singleton.  The OrderManager is the 
	 * holder of most of the business logic for the application.
	 * @return the order manager singleton
	 */
	public PhotonController getPhotonController()
	{
		return photonController;
	}
	
	/**
	 * Accessor for the IDFactory singleton.
	 * 
	 * @return the IDFactory singleton
	 */
	public IDFactory getIDFactory() {
		return idFactory;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}


	public void ensureDefaultProject(IProgressMonitor monitor){
		monitor.beginTask("Ensure default project", 2); //$NON-NLS-1$
		

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject newProject = root.getProject(
	                DEFAULT_PROJECT_NAME);
			IProjectDescription description = workspace.newProjectDescription(newProject.getName());

			if (!newProject.exists()) {
					newProject.create(description, new SubProgressMonitor(monitor, 1));
			}
			if (!newProject.isOpen()){
				newProject.open(monitor);
			}
	
			try {
				if (!newProject.hasNature(RUBY_NATURE_ID)){
					try {
						RubyCore.addRubyNature(newProject, new SubProgressMonitor(monitor, 1));
					} catch (Throwable t){
						// RDT possibly not included...
					    mainConsoleLogger.error(CANNOT_LOAD_RUBY.getText(),
					                            t);
					}
				}
			} catch (CoreException e) {
				if (mainConsoleLogger.isDebugEnabled())
					mainConsoleLogger.debug("Exception trying to determine nature of default project.", //$NON-NLS-1$
					                        e);
			}
		} catch (Throwable t){
			mainConsoleLogger.error(CANNOT_START_DEFAULT_SCRIPT_PROJECT.getText(),
			                        t);
		}
			
		monitor.done();
	}
	
	public void changeLogLevel(String levelValue){
		if (PhotonPage.LOG_LEVEL_VALUE_ERROR.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.ERROR);
		} else if (PhotonPage.LOG_LEVEL_VALUE_WARN.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.WARN);
		} else if (PhotonPage.LOG_LEVEL_VALUE_INFO.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.INFO);
		} else if (PhotonPage.LOG_LEVEL_VALUE_DEBUG.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.DEBUG);
		}
		mainConsoleLogger.info(LOGGER_LEVEL_CHANGED.getText(levelValue));
	}

	public FIXMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public FIXDataDictionary getFIXDataDictionary() {
		return CurrentFIXDataDictionary.getCurrentFIXDataDictionary();
	}

	public FIXVersion getFIXVersion() {
		return fixVersion;
	}

	public static IViewPart getActiveView(String viewId) {
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.findView(viewId);
			}
		}
		return null;
	}
	
	/**
	 * @return a view of the expectedClass. null if not found or the found view
	 *         is not of the expected class.
	 */
	public static IViewPart getActiveView(String viewId, Class<?> expectedClass) {
		IViewPart viewPart = getActiveView(viewId);
		if (viewPart == null) {
			return null;
		}
		if (expectedClass.isAssignableFrom(viewPart.getClass())) {
			return viewPart;
		}
		return null;
	}
	
	public OrderTicketModel getStockOrderTicketModel(){
		return stockOrderTicketModel;
	}
	
	public OptionOrderTicketModel getOptionOrderTicketModel(){
		return optionOrderTicketModel;
	}
	
	public StockOrderTicketController getStockOrderTicketController() {
		return stockOrderTicketController;
	}
	
	public OptionOrderTicketController getOptionOrderTicketController() {
		return optionOrderTicketController;
	}
	
	/**
	 * Returns the order ticket appropriate for the given message (based
	 * on security type).
	 * @param orderMessage the message specifying the type of order ticket.
	 * @return the controller for the appropriate order ticket.
	 */
	public IOrderTicketController getOrderTicketController(Message orderMessage) {
		if (FIXMessageUtil.isEquityOptionOrder(orderMessage)) {
			return getOptionOrderTicketController();
		}
		return getStockOrderTicketController();
	}

	
	/**
	 * @return the next secondary ID for use in IWorkbenchPage.showView()
	 */
	public String getNextSecondaryID() {
		return secondaryIDCreator.getNextSecondaryID();
	}


	/**
	 * The system property that is set to a unique number for every photon
	 * process. An attempt is made to use the pid value as the value of this
	 * property. However, if that doesn't work, the system time at the time
	 * this property is set, is set as the value of this property. 
	 */
	private static final String PROCESS_UNIQUE_PROPERTY = "org.marketcetera.photon.unique"; //$NON-NLS-1$
	/**
	 * log4j configuration file name.
	 */
	private static final String LOG4J_CONFIG = "photon-log4j.properties"; //$NON-NLS-1$
	/**
	 * java logging configuration file name.
	 */
	private static final String JAVA_LOGGING_CONFIG = "java.util.logging.properties"; //$NON-NLS-1$

    /**
     * The system property name that contains photon installation
     * directory
     */
	private static final String APP_DIR_PROP="org.marketcetera.appDir"; //$NON-NLS-1$
	
	/**
	 * The configuration sub directory for the application
	 */
	private static final String CONF_DIR = "conf"; //$NON-NLS-1$

	/**
	 * Delay for rereading log4j configuration.
	 */
	private static final int LOGGER_WATCH_DELAY = 20*1000;


	/**
	 * Configure Logs
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void configureLogs() throws FileNotFoundException, IOException {
		// Fetch the java process ID. Do note that this mechanism relies on
		// a non-public interface of the jvm but its very useful to be able
		// to use the pid.
		String id = ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if(id == null || id.trim().length() < 1) {
			id = String.valueOf(System.currentTimeMillis());  
		}
		// Supply the pid as a system property so that it can be used in
		// log 4j configuration
		System.setProperty(PROCESS_UNIQUE_PROPERTY,id);
		//Figure out if the application install dir is specified
        String appDir=System.getProperty(APP_DIR_PROP);
        File confDir = null;
		// Configure loggers
        if(appDir != null) {
        	File dir = new File(appDir,CONF_DIR);
        	if(dir.isDirectory()) {
        		confDir = dir;
        	}
        }
        // Configure Java Logging
        boolean logConfigured = false;
        if (confDir != null) {
            File logConfig = new File(confDir,JAVA_LOGGING_CONFIG);
			if (logConfig.isFile()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(logConfig);
					LogManager.getLogManager().readConfiguration(fis);
					logConfigured = true;
				} catch (Exception ignored) {
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException ignored) {
						}
					}
				}

			}
		}
		//Do default configuration, if its not already done.
        if(!logConfigured) {
    		LogManager.getLogManager().readConfiguration(getClass().
    				getClassLoader().getResourceAsStream(
    						JAVA_LOGGING_CONFIG));
        }
        
        // Configure Log4j
		// Remove default configuration done via log4j.properties file
		// present in one of the jars that we depend on 
		BasicConfigurator.resetConfiguration();
		logConfigured = false;
		if(confDir != null) {
	        File logConfig = new File(confDir,LOG4J_CONFIG);
	        if(logConfig.isFile()) {
	        	PropertyConfigurator.configureAndWatch(
	        			logConfig.getAbsolutePath(),LOGGER_WATCH_DELAY);
	        	logConfigured = true;
	        } 			
		}
        if(!logConfigured) {
    		//Do default log4j configuration, if its not already done.
    		PropertyConfigurator.configure(getClass().
    				getClassLoader().getResource(LOG4J_CONFIG));
        }
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PhotonPage.LOG_LEVEL_KEY)){
			PhotonPlugin.getDefault().changeLogLevel(""+event.getNewValue()); //$NON-NLS-1$
		}
	}


	
}
