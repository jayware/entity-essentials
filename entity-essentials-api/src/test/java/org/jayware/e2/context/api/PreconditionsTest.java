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
package org.jayware.e2.context.api;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualNotNullAndBelongsToContext;
import static org.jayware.e2.context.api.Preconditions.checkContextualsNotNullAndSameContext;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PreconditionsTest
{
    private Context testContextA;
    private Context testContextB;

    private Contextual firstTestContextual;
    private Contextual secondTestContextual;
    private Contextual otherTestContextual;

    @BeforeMethod
    public void setUp()
    {
        testContextA = mock(Context.class);
        testContextB = mock(Context.class);

        firstTestContextual = mock(Contextual.class);
        secondTestContextual = mock(Contextual.class);
        otherTestContextual = mock(Contextual.class);

        when(testContextA.isDisposed()).thenReturn(false);
        when(testContextB.isDisposed()).thenReturn(false);

        when(firstTestContextual.getContext()).thenReturn(testContextA);
        when(firstTestContextual.belongsTo(any(Context.class))).thenAnswer(invocation -> invocation.getArguments()[0].equals(testContextA));
        when(firstTestContextual.belongsTo(secondTestContextual)).thenReturn(true);
        when(secondTestContextual.getContext()).thenReturn(testContextA);
        when(secondTestContextual.belongsTo(any(Context.class))).thenAnswer(invocation -> invocation.getArguments()[0].equals(testContextA));
        when(secondTestContextual.belongsTo(firstTestContextual)).thenReturn(true);

        when(otherTestContextual.getContext()).thenReturn(testContextB);
        when(otherTestContextual.belongsTo(any(Context.class))).thenAnswer(invocation -> invocation.getArguments()[0].equals(testContextB));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextNotNullAndNotDisposed_ReturnsWithNoExceptionIfContextIsNotNullAndNotDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(false);

        assertThat(checkContextNotNullAndNotDisposed(null)).isEqualTo(context);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextNotNullAndNotDisposed_ThrowsIllegalArgumentExceptionIfContextIsNull()
    {
        checkContextNotNullAndNotDisposed(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_checkContextNotNullAndNotDisposed_ThrowsIllegalStateExceptionIfContextIsDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(true);

        checkContextNotNullAndNotDisposed(context);
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ReturnsWithNoExceptionIfBothContextualsBelongToTheSameValidContext()
    {
        checkContextualsNotNullAndSameContext(firstTestContextual, secondTestContextual);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalArgumentExceptionIfFirstContextualIsNull()
    {
        checkContextualsNotNullAndSameContext(null, secondTestContextual);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalArgumentExceptionIfSecondContextualIsNull()
    {
        checkContextualsNotNullAndSameContext(firstTestContextual, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalArgumentExceptionIfBothContextualsAreNull()
    {
        checkContextualsNotNullAndSameContext(null, null);
    }

    @Test(expectedExceptions = IllegalContextException.class)
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalContextExceptionIfFirstContextualBelongsToAnotherContext()
    {
        checkContextualsNotNullAndSameContext(otherTestContextual, secondTestContextual);
    }

    @Test(expectedExceptions = IllegalContextException.class)
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalContextExceptionIfSecondContextualBelongsToAnotherContext()
    {
        checkContextualsNotNullAndSameContext(firstTestContextual, otherTestContextual);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalStateExceptionIfContextIsDisposed()
    {
        when(testContextA.isDisposed()).thenReturn(true);

        checkContextualsNotNullAndSameContext(firstTestContextual, secondTestContextual);
    }

    @Test
    public void test_checkContextualNotNullAndBelongsToContext_ReturnsContextualWithoutException()
    {
        assertThat(checkContextualNotNullAndBelongsToContext(firstTestContextual, testContextA)).isEqualTo(firstTestContextual);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextualNotNullAndBelongsToContext_ThrowsIllegalArgumentExceptionIfContextulIsNull()
    {
        checkContextualNotNullAndBelongsToContext(null, testContextA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_checkContextualNotNullAndBelongsToContext_ThrowsIllegalArgumentExceptionIfContextIsNull()
    {
        checkContextualNotNullAndBelongsToContext(firstTestContextual, null);
    }

    @Test(expectedExceptions = IllegalContextException.class)
    public void test_checkContextualNotNullAndBelongsToContext_ThrowsIllegalContextExceptionIfContextualBelongsToAnotherContext()
    {
        checkContextualNotNullAndBelongsToContext(firstTestContextual, testContextB);
    }
}