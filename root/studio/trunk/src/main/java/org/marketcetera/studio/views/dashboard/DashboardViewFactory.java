package org.marketcetera.studio.views.dashboard;

import java.util.UUID;

import org.marketcetera.studio.components.StudioViewFactory;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

@Component
public class DashboardViewFactory
        implements StudioViewFactory<DashboardView>
{
    /* (non-Javadoc)
     * @see org.marketcetera.studio.components.MainLayout.ViewFactory#register(javafx.scene.control.MenuItem)
     */
    @Override
    public void register(final TabPane inMainPane,
                         MenuItem dashboardMenuItem)
    {
        dashboardMenuItem.setId(id);
        dashboardMenuItem.setText("Dashboard View");
        dashboardMenuItem.addEventHandler(ActionEvent.ACTION,
                                          new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent inEvent)
                                            {
                                                DashboardView view = new DashboardView();
                                                view.newView(inMainPane);
                                            }
        });
    }
    private final String id = UUID.randomUUID().toString();
}
