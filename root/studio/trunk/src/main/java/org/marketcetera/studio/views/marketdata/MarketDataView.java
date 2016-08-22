package org.marketcetera.studio.views.marketdata;

import org.marketcetera.studio.components.AbstractStudioView;
import org.marketcetera.studio.components.StudioView;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class MarketDataView
        extends AbstractStudioView
        implements StudioView
{
    /**
     * Create a new MarketDataView instance.
     */
    MarketDataView()
    {
        super("Market Data");
        Image image = new Image("marketdata.png");
        ImageView iv2 = new ImageView();
        iv2.setImage(image);
        iv2.setFitWidth(640);
        iv2.setPreserveRatio(true);
        iv2.setSmooth(true);
        iv2.setCache(true);
        HBox box = new HBox();
        box.setPrefWidth(640);
        box.setPrefHeight(480);
        box.getChildren().add(iv2);
        getTab().setContent(box);
    }
}
