/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2014 Regents of the University of Minnesota and contributors
 * Work on LensKit has been funded by the National Science Foundation under
 * grants IIS 05-34939, 08-08692, 08-12148, and 10-17697.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.grouplens.lenskit.basic;


import it.unimi.dsi.fastutil.longs.*;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.collections.LongUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class SimpleCachingItemScorerTest {
    SimpleCachingItemScorer cachedScorer;
    ItemScorer mockScorer;

    @Before
    public void Setup() {
        mockScorer = PrecomputedItemScorer.newBuilder()
                                    .addScore(1, 3, 3.5)
                                    .addScore(2, 4, 5)
                                    .addScore(2, 6, 3)
                                    .addScore(3, 1, 5)
                                    .addScore(3, 2, 4.5)
                                    .addScore(3, 3, 2.5)
                                    .addScore(3, 4, 1)
                                    .build();
        cachedScorer = new SimpleCachingItemScorer(mockScorer);
    }

    @Test
    public void testScore() {
        assertThat(cachedScorer.score(1, 3), equalTo(3.5));
    }

    @Test
    public void testCacheUser() {
        assertThat(cachedScorer.score(1, 3), equalTo(3.5));
        assertThat(cachedScorer.getId(), equalTo(1L));
        assertThat(cachedScorer.score(2, 6), equalTo(3.0));
        assertThat(cachedScorer.getId(), equalTo(2L));
    }

    @Test
    public void testCachedScores() {
        Long user = 3L;
        LongSortedSet items = LongUtils.packedSet(1, 2);
        cachedScorer.score(user, items);
        assertThat(cachedScorer.getCache().keyDomain(), equalTo(items));
        cachedScorer.score(user, 4);
        assertThat(cachedScorer.getCache().keyDomain(), equalTo(LongUtils.packedSet(1, 2, 4)));
    }
}
