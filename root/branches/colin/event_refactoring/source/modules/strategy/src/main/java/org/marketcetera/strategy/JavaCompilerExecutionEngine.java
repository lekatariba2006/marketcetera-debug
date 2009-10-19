package org.marketcetera.strategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.LogEventBuilder;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Executes a Java strategy using the <a href="http://www.jcp.org/en/jsr/detail?id=199">Java Compiler API</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class JavaCompilerExecutionEngine
        implements ExecutionEngine, Messages
{
    /**
     * System properties key to set to inform the Java compiler of dependencies it should include on the classpath for compiling Java strategies
     */
    public static final String CLASSPATH_KEY = "metc.java.class.path"; //$NON-NLS-1$
    /**
     * the strategy to be executed 
     */
    private Strategy strategy;
    /**
     * the processed/prepared script from the strategy
     */
    private String processedScript;
    /**
     * map of cannonical classname to fully-qualified classname generated by the compiler and used by the classloader
     */
    private Map<String,String> fullyQualifiedClassnames = new HashMap<String,String>();
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#prepare(org.marketcetera.strategy.Strategy, java.lang.String)
     */
    @Override
    public void prepare(Strategy inStrategy,
                        String inProcessedScript)
            throws StrategyException
    {
        strategy = inStrategy;
        processedScript = inProcessedScript;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#start()
     */
    @Override
    public Object start()
            throws StrategyException
    {
        // the compiler object to use
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler == null) {
            throw new StrategyException(MISSING_JAVA_COMPILER);
        }
        // A map of class names to the InMemoryJavaFileObject that holds
        //  the compiled-code for that class. This is the cache of
        //  compiled classes.
        Map<String,InMemoryJavaFileObject> output = new HashMap<String,InMemoryJavaFileObject>();
        // Create a classloader for our new classes based on the current classloader
        //  this classloader will stop operating when the enclosing object goes out of scope or
        //  is replaced by a start/stop cycle of the strategy
        ClassLoader loader = new InMemoryClassLoader(output,
                                                     StrategyModule.class.getClassLoader());
        // this object is for the compile phase - it stores errors and warnings generated by the compilation
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        // the fileManager manages sources and targets for the compiler - this is the basic model
        //  which we'll specialize next to make compilation in-memory
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics,
                                                                                      null,
                                                                                      null);
        // this is the specialized file manager that produces source and stores byte-code all in-memory
        InMemoryFileManager specializedFileManager = new InMemoryFileManager(standardFileManager,
                                                                             output,
                                                                             loader);
        // source file objects are produced for each thing to be compiled.  For us, this is the strategy script, which
        //  contains 1 or more classes.  notice that this is where the strategy name is associated with the source.  this
        //  is required by the java compiler which dictates that a class name must match the file name
        SourceJavaFileObject sourceObject;
        try {
            sourceObject = new SourceJavaFileObject(strategy.getName(),
                                                    processedScript);
        } catch (URISyntaxException e) {
            throw new StrategyException(e,
                                        new I18NBoundMessage1P(INVALID_STRATEGY_NAME,
                                                               strategy.toString()));
        }
        // prepare the options to pass to the compiler
        // collect classpath entries
        Set<String> classpathEntries = new LinkedHashSet<String>();
        // add the system classpath
        String systemPath = System.getProperty("java.class.path"); //$NON-NLS-1$
        if(systemPath != null) {
            String[] entries = systemPath.split(File.pathSeparator);
            classpathEntries.addAll(Arrays.asList(entries));
        }
        // add jars we are given by the parent class loaders, if any
        ClassLoader currentLoader = getClass().getClassLoader();
        do {
            if(currentLoader instanceof URLClassLoader) {
                for(URL url: ((URLClassLoader)currentLoader).getURLs()) {
                    try {
                        classpathEntries.add(url.toURI().getPath());
                    } catch (URISyntaxException e) {
                        Messages.ERROR_CONVERTING_CLASSPATH_URL.warn(this,
                                                                     e,
                                                                     url);
                    }
                }
            }
        } while((currentLoader = currentLoader.getParent()) != null);
        // add our custom classpath
        String customPath = System.getProperty(CLASSPATH_KEY);
        if(customPath != null) {
            String[] entries = customPath.split(File.pathSeparator);
            classpathEntries.addAll(Arrays.asList(entries));
        }
        // put the classpath string in place with the classpath command-line option
        List<String> options = new ArrayList<String>();
        options.add("-cp"); //$NON-NLS-1$
        StringBuilder classpathString = new StringBuilder();
        for(String entry : classpathEntries) {
            classpathString.append(entry).append(File.pathSeparator);
        }
        options.add(classpathString.toString());
        SLF4JLoggerProxy.debug(JavaCompilerExecutionEngine.class,
                               "Java compiler compiling {} with options {} (classpath length: {})", //$NON-NLS-1$
                               strategy.getName(),
                               Arrays.toString(options.toArray()),
                               classpathString.length());
        // schedule the compilation task
        CompilationTask compilationJob = compiler.getTask(null, // out-writer not needed because we're using the in-memory file manager
                                                          specializedFileManager,
                                                          diagnostics,
                                                          options,
                                                          null, // no annotation processing needed
                                                          Arrays.asList(sourceObject));
        // wait for the compilation job to complete
        if (!compilationJob.call()) {
            // compilation failed, deal with the errors
            CompilationFailed failed = new CompilationFailed(strategy);
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                if(diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
                    failed.addDiagnostic(CompilationFailed.Diagnostic.error(diagnostic.toString()));
                } else {
                    failed.addDiagnostic(CompilationFailed.Diagnostic.warning(diagnostic.toString()));
                }
            }
            StrategyModule.log(LogEventBuilder.error().withMessage(COMPILATION_FAILED,
                                                                   String.valueOf(strategy),
                                                                   failed.toString())
                                                      .withException(failed).create(),
                               strategy);
            throw failed;
        } else {
            // compilation succeeded with or without warnings
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(COMPILATION_FAILED_DIAGNOSTIC,
                                                                      String.valueOf(diagnostic.getKind()),
                                                                      String.valueOf(diagnostic)).create(),
                                   strategy);
            }
        }
        // strategy has compiled successfully and is now held in our specializedFileManager
        try {
            // load the class from the specialized class loader that caches the compiled strategy classes
            // remember that the strategy name is specified without a package name, but the classloader needs
            //  to know the fully-qualified classname with package, so check the mappings we created for fully-qualified
            //  class names
            String fullyQualifiedClassname = fullyQualifiedClassnames.get(strategy.getName());
            SLF4JLoggerProxy.debug(JavaCompilerExecutionEngine.class,
                                   "The fully-qualified name of {} is {}", //$NON-NLS-1$
                                   strategy.getName(),
                                   fullyQualifiedClassname);
            assert(fullyQualifiedClassname != null);
            Class<?> c = Class.forName(fullyQualifiedClassname,
                                       true,
                                       loader);
            // the strategy class is supposed to be a subclass of Strategy and have a default constructor
            //  note that this implicitly loads helper classes as necessary
            return c.newInstance();
        } catch (Exception e) {
            // the myriad of exceptions that can be thrown with the above couple of lines all amount to the same
            //  thing: the black magic of the compiler, in-memory objects, and the classloader somehow malfunctioned.
            //  this would be a warranty repair: nothing the user can do.  might as well call it a compilation problem
            //  as well as call it anything else.
            StrategyModule.log(LogEventBuilder.error().withMessage(COMPILATION_FAILED,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(e))
                                                      .withException(e).create(),
                               strategy);
            throw new CompilationFailed(e,
                                        strategy);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#stop()
     */
    @Override
    public void stop()
            throws StrategyException
    {
        // nothing to do
    }
    /**
     * Represents the Java source of a strategy containing one or more classes to compile.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private static class SourceJavaFileObject
        extends SimpleJavaFileObject
    {
        /**
         * indicates a java file
         */
        private static final String JAVA_EXTENSION = ".java"; //$NON-NLS-1$
        /**
         * the source to compile
         */
        private final String source;
        /**
         * Create a new SourceJavaFileObject instance.
         *
         * @param inClassName a <code>String</code> value containing the name of the class
         * @param inClassSource a <code>String</code> value containing the contents of the pseudo-file
         * @throws URISyntaxException if the given <code>inClassName</code> cannot be translated to a <code>URI</code>
         */
        private SourceJavaFileObject(String inClassName,
                                     String inClassSource)
            throws URISyntaxException
        {
            super(new URI(String.format("%s%s", //$NON-NLS-1$
                                        inClassName,
                                        JAVA_EXTENSION)),
                  Kind.SOURCE);
            source = inClassSource;
        }
        /* (non-Javadoc)
         * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
         */
        @Override
        public CharSequence getCharContent(boolean inIgnoreEncodingErrors)
        {
            return source;
        }
    }
    /**
     * Classloader that caches the definitions of some classes in memory and
     * defers to its parent for others.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private static class InMemoryClassLoader
        extends ClassLoader
    {
        /**
         * the cache of class definitions by name 
         */
        private final Map<String,InMemoryJavaFileObject> cache;
        /**
         * Create a new InMemoryClassLoader instance.
         *
         * @param inOutput a <code>Map&lt;String,InMemoryJavaFileObject&gt;</code> value in which to cache class definitions
         * @param inParent a <code>ClassLoader</code> value containing the parent classloader to use
         */
        private InMemoryClassLoader(Map<String,InMemoryJavaFileObject> inOutput,
                                    ClassLoader inParent)
        {
            super(inParent);
            cache = inOutput;
        }
        /* (non-Javadoc)
         * @see java.lang.ClassLoader#findClass(java.lang.String)
         */
        @Override
        protected Class<?> findClass(String inName)
            throws ClassNotFoundException
        {
            // check our cache for the class
            InMemoryJavaFileObject fileObject = cache.get(inName);
            if (fileObject != null) {
                // class is in our cache, return that version of it
                byte[] bytes = fileObject.getBytes();
                return defineClass(inName,
                                   bytes,
                                   0,
                                   bytes.length);
            }
            // class is not in-cache, return the parent's definition (if it has one)
            return super.findClass(inName);
        }
    }
    /**
     * File manager which maintains file contents in memory.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private class InMemoryFileManager
        extends ForwardingJavaFileManager<StandardJavaFileManager>
    {
        /**
         * the mapping of classname to file object
         */
        private final Map<String,InMemoryJavaFileObject> output;
        /**
         * the classloader to use to load the classes
         */
        private final ClassLoader loader;
        /**
         * Create a new InMemoryFileManager instance.
         *
         * @param inFileManager a <code>StandardJavaFileManager</code> value to use as the parent file manager
         * @param inOutput a <code>Map&lt;String,InMemoryJavaFileObject&gt;</code> value containing class definitions by name
         * @param inClassLoader a <code>ClassLoader</code> value containing the classloader to use
         */
        private InMemoryFileManager(StandardJavaFileManager inFileManager,
                                    Map<String,InMemoryJavaFileObject> inOutput,
                                    ClassLoader inClassLoader)
        {
            super(inFileManager);
            output = inOutput;
            loader = inClassLoader;
        }
        /* (non-Javadoc)
         * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location, java.lang.String, javax.tools.JavaFileObject.Kind, javax.tools.FileObject)
         */
        @Override
        public JavaFileObject getJavaFileForOutput(Location inLocation,
                                                   String inFullyQualifiedClassname,
                                                   Kind inKind,
                                                   FileObject inSibling)
                throws IOException
        {
            InMemoryJavaFileObject javaFileObject;
            try {
                javaFileObject = new InMemoryJavaFileObject(inFullyQualifiedClassname,
                                                            inKind);
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
            // add the java file object in the cache by name
            // sort out the cannonical name from the fully-qualified name if the strategy code specified a package
            String cannonicalClassname = getCannonicalClassname(inFullyQualifiedClassname);
            // map the cannonical name to the fully-qualified name (to be used later to load the class)
            fullyQualifiedClassnames.put(cannonicalClassname,
                                         inFullyQualifiedClassname);
            // put the fully-qualified name in the classname cache
            output.put(inFullyQualifiedClassname,
                       javaFileObject);
            return javaFileObject;
        }
        /**
         * Calculates the cannonical classname from the given fully-qualified classname.
         *
         * @param inFullyQualifiedClassname a <code>String</code> value
         * @return a <code>String</code> value
         */
        private String getCannonicalClassname(String inFullyQualifiedClassname)
        {
            String[] nameSegments = inFullyQualifiedClassname.split("\\."); //$NON-NLS-1$
            return (nameSegments.length > 0 ? nameSegments[nameSegments.length-1] : inFullyQualifiedClassname);
        }
        /* (non-Javadoc)
         * @see javax.tools.ForwardingJavaFileManager#getClassLoader(javax.tools.JavaFileManager.Location)
         */
        @Override
        public ClassLoader getClassLoader(Location inLocation)
        {
            return loader;
        }
        /* (non-Javadoc)
         * @see javax.tools.ForwardingJavaFileManager#inferBinaryName(javax.tools.JavaFileManager.Location, javax.tools.JavaFileObject)
         */
        @Override
        public String inferBinaryName(Location inLocation,
                                      JavaFileObject inJavaFileObject)
        {
            String result;
            if(inLocation == StandardLocation.CLASS_PATH &&
               inJavaFileObject instanceof InMemoryJavaFileObject) {
                result = inJavaFileObject.getName();
            } else {
                result = super.inferBinaryName(inLocation,
                                               inJavaFileObject);
            }
            return result;
        }
        /* (non-Javadoc)
         * @see javax.tools.ForwardingJavaFileManager#list(javax.tools.JavaFileManager.Location, java.lang.String, java.util.Set, boolean)
         */
        @Override
        public Iterable<JavaFileObject> list(Location inLocation,
                                             String inPackageName,
                                             Set<Kind> inKinds,
                                             boolean inShouldRecurse)
                throws IOException
        {
            // get the list of files from the parent implementation
            Iterable<JavaFileObject> result = super.list(inLocation,
                                                         inPackageName,
                                                         inKinds,
                                                         inShouldRecurse);
            // the "list" function allows a classloader to indicate what classes it knows about.
            // this next bit of code allows our in-memory classloader to list the classes that were
            //  just compiled.  the package name is set oddly in this case (as implied below).  this
            //  behavior isn't strictly necessary right now, but may be helpful in the future
            if(inLocation == StandardLocation.CLASS_PATH &&
               inPackageName.equals("just.generated") && //$NON-NLS-1$
               inKinds.contains(JavaFileObject.Kind.CLASS)) {
                // take the parent's classes
                List<JavaFileObject> temp = new ArrayList<JavaFileObject>();
                for (JavaFileObject fileObject : result) {
                    temp.add(fileObject);
                }
                // and add our in-memory classes
                for (Entry<String,InMemoryJavaFileObject> entry : output.entrySet()) {
                    temp.add(entry.getValue());
                }
                result = temp;
            }
            return result;
        }
    }    
    /**
     * An in-memory representation of a Java File Object.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private static class InMemoryJavaFileObject
        extends SimpleJavaFileObject
    {
        /**
         *  the bytes of the java file object
         */
        private ByteArrayOutputStream bytes;
        /**
         * Create a new InMemoryJavaFileObject instance.
         *
         * @param name a <code>String</code> value
         * @param kind a <code>Kind</code> value
         * @throws URISyntaxException if the name cannot be made into a <code>URI</code>
         */
        private InMemoryJavaFileObject(String name,
                                       Kind kind)
            throws URISyntaxException
        {
            super(new URI(name),
                  kind);
        }
        /* (non-Javadoc)
         * @see javax.tools.SimpleJavaFileObject#openOutputStream()
         */
        @Override
        public OutputStream openOutputStream()
        {
            return bytes = new ByteArrayOutputStream();
        }
        /**
         * Returns the bytes that make up this file object. 
         *
         * @return a <code>byte[]</code> value
         */
        private byte[] getBytes()
        {
            return bytes.toByteArray();
        }
    }
}
