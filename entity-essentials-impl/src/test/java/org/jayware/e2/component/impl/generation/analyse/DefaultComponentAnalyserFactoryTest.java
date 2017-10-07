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
package org.jayware.e2.component.impl.generation.analyse;

import mockit.Mocked;
import org.jayware.e2.component.api.generation.analyse.ComponentAnalyserFactory;
import org.jayware.e2.context.api.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class DefaultComponentAnalyserFactoryTest
{
    private ComponentAnalyserFactory testee;

    private @Mocked Context context;

    @BeforeEach
    void setUp()
    {
        testee = new DefaultComponentAnalyserFactory(context);
    }

    @Test
    void should_create_a_HierarchyAnalyser()
    {
        assertThat(testee.createHierarchyAnalyser()).isNotNull();
    }

    @Test
    void should_create_a_PropertyAccessorAnalyser()
    {
        assertThat(testee.createPropertyAccessorAnalyser()).isNotNull();
    }

    @Test
    void should_create_a_PropertyDeclarationAnalyser()
    {
        assertThat(testee.createPropertyDeclarationAnalyser()).isNotNull();
    }

    @Test
    void should_create_a_DescriptorBuilder()
    {
        assertThat(testee.createDescriptorBuilder()).isNotNull();
    }
}