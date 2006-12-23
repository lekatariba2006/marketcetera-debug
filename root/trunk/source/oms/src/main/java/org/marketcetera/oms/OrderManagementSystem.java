package org.marketcetera.oms;

import org.marketcetera.core.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.lang.management.ManagementFactory;

//import org.quickfixj.jmx.JmxExporter;
//import org.quickfixj.jmx.mbean.connector.ConnectorJmxExporter;
import org.springframework.context.ApplicationContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.JMException;

import quickfix.Session;
import quickfix.SocketInitiator;
import quickfix.mina.SessionConnector;

/**
 * OrderManagementSystem
 * Main entrypoint for sending orders and receiving responses from a FIX engine
 *
 * The OMS is configured using Spring, using the following modules:
 * <ol>
 *   <li>{@link OutgoingMessageHandler} which handles running the received
 *      order through modifiers, sending it on and generating and returning an
 *      immediate execution report </li>
 *   <li>{@link QuickFIXApplication} - a wrapper for setting up a FIX application (listener/sender)</li>
 *   <li>{@link QuickFIXSender} = actually sends the FIX messages</li>
 * </ol>
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class OrderManagementSystem extends ApplicationBase {

    private static final String LOGGER_NAME = OrderManagementSystem.class.getName();
    public static final MessageBundleInfo OMS_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("oms", "oms_messages");

    protected List<MessageBundleInfo> getLocalMessageBundles() {
        LinkedList<MessageBundleInfo> bundles = new LinkedList<MessageBundleInfo>();
        bundles.add(OMS_MESSAGE_BUNDLE_INFO);
        return bundles;
    }

    /**
     * register the OMS mean
     * should be superseded by functionality provided by QuickfixJ
     * @param fExitOnFail
     * @deprecated
     */
    protected void registerMBean(OMSAdmin adminBean, boolean fExitOnFail)
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            String pkgName = this.getClass().getPackage().toString();
            String className = adminBean.getClass().getSimpleName();
            ObjectName name = new ObjectName(pkgName +":type="+className);
            mbs.registerMBean(adminBean, name);
        } catch (JMException ex) {
            LoggerAdapter.error(MessageKey.JMX_BEAN_FAILURE.getLocalizedMessage(), ex, this);
            if(fExitOnFail) {System.exit(-1); }
        }
    }

    public static void main(String [] args) throws ConfigFileLoadingException
    {
        try {
            OrderManagementSystem oms = new OrderManagementSystem();
            ApplicationContext appCtx = oms.createApplicationContext("oms.xml", true);

            SocketInitiator initiator = (SocketInitiator) appCtx.getBean("socketInitiator", SocketInitiator.class);
            AccessViolator violator = new AccessViolator(SessionConnector.class);
            Map allSessions = (Map) violator.getField("sessions", initiator);
            Session[] sessionArr = (Session[]) allSessions.values().toArray(new Session[0]);
            OMSAdmin adminBean = new OMSAdmin(sessionArr[0]);
            oms.registerMBean(adminBean, true);
/*
            JmxExporter exporter = new JmxExporter();
            exporter.enableStatistics();
            exporter.export(initiator);
            MBeanServer mbServer = exporter.getMBeanServer();
*/

            oms.startWaitingForever();
            if(LoggerAdapter.isDebugEnabled(LOGGER_NAME)) { LoggerAdapter.debug("OMS main finishing", LOGGER_NAME); }
        } catch (Exception ex) {
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }
}

