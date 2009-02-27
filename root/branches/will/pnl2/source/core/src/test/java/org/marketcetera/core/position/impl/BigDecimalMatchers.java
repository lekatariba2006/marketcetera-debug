package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.hamcrest.Matcher;

/* $License$ */

/**
 * Utility matchers for BigDecimals.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class BigDecimalMatchers {
	public static Matcher<? super BigDecimal> comparesEqualTo(String value) {
        return OrderingComparison.comparesEqualTo(new BigDecimal(value));
    }
}
