/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2017 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jayware.e2.context.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualNotNullAndBelongsToContext;
import static org.jayware.e2.context.api.Preconditions.checkContextualsNotNullAndSameContext;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
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
        when(firstTestContextual.belongsTo(any(Context.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation)
            throws Throwable
            {
                return invocation.getArguments()[0].equals(testContextA);
            }
        });
        when(firstTestContextual.belongsTo(secondTestContextual)).thenReturn(true);
        when(secondTestContextual.getContext()).thenReturn(testContextA);
        when(secondTestContextual.belongsTo(any(Context.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation)
            throws Throwable
            {
                return invocation.getArguments()[0].equals(testContextA);
            }
        });
        when(secondTestContextual.belongsTo(firstTestContextual)).thenReturn(true);

        when(otherTestContextual.getContext()).thenReturn(testContextB);
        when(otherTestContextual.belongsTo(any(Context.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation)
            throws Throwable
            {
                return invocation.getArguments()[0].equals(testContextB);
            }
        });
    }

    @Test
    public void test_checkContextNotNullAndNotDisposed_ReturnsWithNoExceptionIfContextIsNotNullAndNotDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                assertThat(checkContextNotNullAndNotDisposed(null)).isEqualTo(context);
            }
        });
    }

    @Test
    public void test_checkContextNotNullAndNotDisposed_ThrowsIllegalArgumentExceptionIfContextIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextNotNullAndNotDisposed(null);
            }
        });
    }

    @Test
    public void test_checkContextNotNullAndNotDisposed_ThrowsIllegalStateExceptionIfContextIsDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(true);

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextNotNullAndNotDisposed(context);
            }
        });
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ReturnsWithNoExceptionIfBothContextualsBelongToTheSameValidContext()
    {
        checkContextualsNotNullAndSameContext(firstTestContextual, secondTestContextual);
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalArgumentExceptionIfFirstContextualIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualsNotNullAndSameContext(null, secondTestContextual);
            }
        });
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalArgumentExceptionIfSecondContextualIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualsNotNullAndSameContext(firstTestContextual, null);
            }
        });
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalArgumentExceptionIfBothContextualsAreNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualsNotNullAndSameContext(null, null);
            }
        });
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalContextExceptionIfFirstContextualBelongsToAnotherContext()
    {
        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualsNotNullAndSameContext(otherTestContextual, secondTestContextual);
            }
        });
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalContextExceptionIfSecondContextualBelongsToAnotherContext()
    {
        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualsNotNullAndSameContext(firstTestContextual, otherTestContextual);
            }
        });
    }

    @Test
    public void test_checkContextualsNotNullAndSameValidContext_ThrowsIllegalStateExceptionIfContextIsDisposed()
    {
        when(testContextA.isDisposed()).thenReturn(true);

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualsNotNullAndSameContext(firstTestContextual, secondTestContextual);
            }
        });
    }

    @Test
    public void test_checkContextualNotNullAndBelongsToContext_ReturnsContextualWithoutException()
    {
        assertThat(checkContextualNotNullAndBelongsToContext(firstTestContextual, testContextA)).isEqualTo(firstTestContextual);
    }

    @Test
    public void test_checkContextualNotNullAndBelongsToContext_ThrowsIllegalArgumentExceptionIfContextulIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualNotNullAndBelongsToContext(null, testContextA);
            }
        });
    }

    @Test
    public void test_checkContextualNotNullAndBelongsToContext_ThrowsIllegalArgumentExceptionIfContextIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualNotNullAndBelongsToContext(firstTestContextual, null);
            }
        });
    }

    @Test
    public void test_checkContextualNotNullAndBelongsToContext_ThrowsIllegalContextExceptionIfContextualBelongsToAnotherContext()
    {
        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                checkContextualNotNullAndBelongsToContext(firstTestContextual, testContextB);
            }
        });
    }
}