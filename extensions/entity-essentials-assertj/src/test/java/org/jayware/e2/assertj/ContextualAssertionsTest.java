/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 *     This file is part of Entity Essentials.
 *
 *     Entity Essentials is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     Entity Essentials is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jayware.e2.assertj;

import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.testng.annotations.Test;

import static org.jayware.e2.assertj.Common.ID;
import static org.testng.Assert.fail;


public class ContextualAssertionsTest
{
    private @Mocked Context testContext;
    private @Mocked Contextual testContextual;
    private @Mocked Contextual testContextual2;

    @Test
    public void test_assertThat_does_not_return_null()
    throws Exception
    {
        Assertions.assertThat(ContextAssertions.assertThat(testContext)).isNotNull();
    }

    @Test
    public void test_that_belongsTo_fails_if_the_Contextual_does_not_belong_to_the_passed_Context()
    {
        new Expectations() {{
            testContext.getId(); result = ID;
            testContextual.belongsTo(testContext); result = false;
        }};

        try
        {
            ContextualAssertions.assertThat(testContextual).belongsTo(testContext);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_belongsTo_fails_if_the_Contextual_does_not_belong_to_the_same_Context_as_the_passed_Contextual()
    {
        new Expectations() {{
            testContextual.belongsTo(testContextual2); result = false;
        }};

        try
        {
            ContextualAssertions.assertThat(testContextual).belongsTo(testContextual2);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }
}