package org.marketcetera.studio.application;

import org.marketcetera.studio.components.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication
@ComponentScan("org.marketcetera.studio")
public class StudioApplication
        extends AbstractJavaFxApplicationSupport
{

    /**
     * Note that this is configured in application.properties
     */
    @Value("${app.ui.title:Marketcetera Automated Trading Platform Studio}")
    private String windowTitle;

    @Autowired
    private MainLayout mainLayout;

    @Override
    public void start(Stage stage) throws Exception {

        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));

        stage.setTitle(windowTitle);
        stage.setScene(new Scene(mainLayout,640,480));
        stage.setResizable(true);
        stage.centerOnScreen();
        mainLayout.start();
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(StudioApplication.class, args);
    }
}
