package org.marketcetera.studio.components;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import de.jensd.shichimifx.utils.TabPaneDetacher;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class MainLayout
        extends BorderPane
        implements ApplicationContextAware
{
    @PostConstruct
    public void initComponent()
    {
        mainPane = new TabPane();
        ToolBar topPane = new ToolBar();
        viewMenu = new MenuButton();
        viewMenu.setText("Open View");
        topPane.getItems().add(viewMenu);
        setTop(topPane);
        setCenter(mainPane);
//        Sphere connectionStatusIndicator = new Sphere();
//        connectionStatusIndicator.setRadius(10);
//        AmbientLight buttonBarLight = new AmbientLight();
//        buttonBarLight.setColor(Color.WHITE);
//        PhongMaterial greenMaterial = new PhongMaterial();
//        greenMaterial.setDiffuseColor(Color.GREEN);
//        greenMaterial.setSpecularColor(Color.LIGHTGREEN);
//        connectionStatusIndicator.setMaterial(greenMaterial);
//        connectionStatusIndicator.getTransforms().add(new Rotate(20,Rotate.X_AXIS));
//        connectionStatusIndicator.getTransforms().add(new Rotate(10,Rotate.Z_AXIS));
//        connectionStatusIndicator.getTransforms().add(new Rotate(30,Rotate.Y_AXIS));
//        ButtonBar buttonBar = new ButtonBar();
//        buttonBar.getButtons().add(buttonBarLight);
//        buttonBar.getButtons().add(connectionStatusIndicator);
//        setBottom(buttonBar);
        TabPaneDetacher.create().makeTabsDetachable(mainPane);
    }
    public void start()
    {
        @SuppressWarnings("rawtypes")
        Map<String,StudioViewFactory> viewFactories = applicationContext.getBeansOfType(StudioViewFactory.class);
        for(StudioViewFactory<?> viewFactory : viewFactories.values()) {
            MenuItem viewMenuItem = new MenuItem();
            viewFactory.register(mainPane,
                                 viewMenuItem);
            viewMenu.getItems().add(viewMenuItem);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    private ApplicationContext applicationContext;
    /**
     * 
     */
    private TabPane mainPane;
    private MenuButton viewMenu;
}
