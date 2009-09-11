package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.ui.tests.MockUIConnection;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link StartHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class StartHandlerTest extends PhotonTestBase {

    private volatile WritableList mEngines;
    private volatile DeployedStrategy mStrategy;
    private volatile StrategyEnginesViewFixture mView;

    @Before
    @UI
    public void before() throws Exception {
        mStrategy = createDeployedStrategy("deployed");
        StrategyEngine engine = createConnectedEngine("My Engine");
        engine.setConnection(new MockUIConnection());
        mEngines = new WritableList(buildEngines(
                engine, mStrategy),
                StrategyEngine.class);
        mView = StrategyEnginesViewFixture.openView();
        mView.setModel(mEngines);
    }

    @After
    public void after() throws Exception {
        mView.close();
    }

    @Test
    public void test() throws Exception {
        SWTBotTree tree = mView.getView().bot().tree();
        SWTBotTreeItem engineNode = tree.getTreeItem(
                "My Engine");
        engineNode.expand();
        engineNode.select("deployed");
        ContextMenuHelper.clickContextMenu(tree, "Start");
        Thread.sleep(500);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mStrategy.getState(), is(StrategyState.RUNNING));
            }
        });
    }
}
