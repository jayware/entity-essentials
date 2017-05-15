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
package org.jayware.e2.component.impl;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentB;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ContextualComponentManagerImplTest
{
    private @Mocked Context testContext, otherContext;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked Contextual testContextual;
    private @Mocked EntityRef testRefA;
    private @Mocked TestComponentA testComponentA;
    private @Mocked AbstractComponent testAbstractComponent;

    private ContextualComponentManagerImpl testee;

    @BeforeEach
    public void setUp()
    {
        testee = new ContextualComponentManagerImpl(testContext, testComponentManager);
    }

    @Test
    public void test_prepareComponent_Throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.prepareComponent(null);
            }
        });
    }

    @Test
    public void test_prepareComponent_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.prepareComponent(TestComponentA.class);
            }
        });
    }


    @Test
    public void test_prepareComponent_Calls_its_delegate()
    {
        testee.prepareComponent(TestComponentA.class);

        new Verifications()
        {{
            testComponentManager.prepareComponent(testContext, TestComponentA.class);
        }};
    }

    @Test
    public void test_createComponent_With_Class_Throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createComponent(null);
            }
        });
    }

    @Test
    public void test_createComponent_With_Class_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createComponent(TestComponentA.class);
            }
        });
    }


    @Test
    public void test_createComponent_Calls_its_delegate()
    {
        testee.createComponent(TestComponentA.class);

        new Verifications()
        {{
            testComponentManager.createComponent(testContext, TestComponentA.class);
        }};
    }

    @Test
    public void test_addComponent_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_Throws_IllegalArgumentException_if_the_passed_Class_is_null()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, (Class<Component>) null);
            }
        });
    }

    @Test
    public void test_addComponent_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_Calls_its_delegate()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        testee.addComponent(testRefA, TestComponentA.class);

        new Verifications()
        {{
            testComponentManager.addComponent(testRefA, TestComponentA.class);
        }};
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(null, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalArgumentException_if_the_passed_Component_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, (Component) null);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalContextException_if_the_passed_Component_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testAbstractComponent.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addComponent(testRefA, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Calls_its_delegate()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testAbstractComponent.belongsTo(testContext); result = true;
        }};

        testee.addComponent(testRefA, testAbstractComponent);

        new Verifications()
        {{
            testComponentManager.addComponent(testRefA, testAbstractComponent);
        }};
    }

    @Test
    public void test_removeComponent_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_Throws_IllegalArgumentException_if_the_passed_Class_is_null()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeComponent(testRefA, null);
            }
        });
    }

    @Test
    public void test_removeComponent_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_Calls_its_delegate()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        testee.removeComponent(testRefA, TestComponentA.class);

        new Verifications()
        {{
            testComponentManager.removeComponent(testRefA, TestComponentA.class);
        }};
    }

    @Test
    public void test_getComponent_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_getComponent_Throws_IllegalArgumentException_if_the_passed_Class_is_null()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getComponent(testRefA, null);
            }
        });
    }

    @Test
    public void test_getComponent_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_getComponent_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_getComponent_Returns_the_expected_Component()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testComponentManager.getComponent(testRefA, TestComponentA.class); result = testComponentA;
        }};

        assertThat(testee.getComponent(testRefA, TestComponentA.class)).isEqualTo(testComponentA);
    }

    @Test
    public void test_findComponent_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.findComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_findComponent_Throws_IllegalArgumentException_if_the_passed_Class_is_null()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.findComponent(testRefA, null);
            }
        });
    }

    @Test
    public void test_findComponent_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.findComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_findComponent_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.findComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_findComponent_Returns_the_expected_Component()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testComponentManager.findComponent(testRefA, TestComponentA.class); result = testComponentA;
        }};

        assertThat(testee.findComponent(testRefA, TestComponentA.class)).isEqualTo(testComponentA);
    }

    @Test
    public void test_findComponent_With_EntityRef_and_VarArg_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.hasComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_findComponent_With_EntityRef_and_VarArg_Throws_IllegalArgumentException_if_the_passed_Component_Classes_are_null()
    {
        new Expectations()
        {{
            testRefA.isInvalid(); result = false;
            testRefA.belongsTo(testContext); result = true;
        }};

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.hasComponent(testRefA, (Class<? extends Component>[]) null);
            }
        });
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_VarArg_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRefA.isInvalid(); result = false;
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.hasComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_VarArg_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.isInvalid(); result = false;
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.hasComponent(testRefA, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_VarArg_Returns_true_if_the_specified_type_of_Component_is_present_at_the_referenced_Entity()
    {
        final List<Collection<Class<? extends Component>>> capturedComponentTypes = new ArrayList<Collection<Class<? extends Component>>>();

        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testComponentManager.hasComponents(testRefA, withCapture(capturedComponentTypes)); result = true; times = 1;
        }};

        assertThat(testee.hasComponent(testRefA, TestComponentA.class)).isTrue();
        assertThat(capturedComponentTypes.get(0)).contains(TestComponentA.class);
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_VarArg_Returns_false_if_the_specified_type_of_Component_is_not_present_at_the_referenced_Entity()
    {
        final List<Collection<Class<? extends Component>>> capturedComponentTypes = new ArrayList<Collection<Class<? extends Component>>>();

        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testComponentManager.hasComponents(testRefA, withCapture(capturedComponentTypes)); result = false; times = 1;
        }};

        assertThat(testee.hasComponent(testRefA, TestComponentA.class)).isFalse();
        assertThat(capturedComponentTypes.get(0)).contains(TestComponentA.class);
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_Collection_Throws_IllegalStateException_if_the_Context_to_which_the_ContextualComponentManager_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRefA.isInvalid(); result = false;
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.hasComponent(testRefA, Arrays.<Class<? extends Component>>asList(TestComponentA.class, TestComponentB.class));
            }
        });
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_Collection_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.isInvalid(); result = false;
            testRefA.belongsTo(testContext); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.hasComponent(testRefA, Arrays.<Class<? extends Component>>asList(TestComponentA.class, TestComponentB.class));
            }
        });
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_Collection_Returns_true_if_the_specified_type_of_Component_is_present_at_the_referenced_Entity()
    {
        final List<Collection<Class<? extends Component>>> capturedComponentTypes = new ArrayList<Collection<Class<? extends Component>>>();

        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testComponentManager.hasComponents(testRefA, withCapture(capturedComponentTypes)); result = true; times = 1;
        }};

        assertThat(testee.hasComponent(testRefA, Arrays.<Class<? extends Component>>asList(TestComponentA.class, TestComponentB.class))).isTrue();
        assertThat(capturedComponentTypes.get(0)).contains(TestComponentA.class, TestComponentB.class);
    }

    @Test
    public void test_hasComponent_With_EntityRef_and_Collection_Returns_false_if_the_specified_type_of_Component_is_not_present_at_the_referenced_Entity()
    {
        final List<Collection<Class<? extends Component>>> capturedComponentTypes = new ArrayList<Collection<Class<? extends Component>>>();

        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
            testComponentManager.hasComponents(testRefA, withCapture(capturedComponentTypes)); result = false; times = 1;
        }};

        assertThat(testee.hasComponent(testRefA, Arrays.<Class<? extends Component>>asList(TestComponentA.class, TestComponentB.class))).isFalse();
        assertThat(capturedComponentTypes.get(0)).contains(TestComponentA.class, TestComponentB.class);
    }

    @Test
    public void test_getContext_Returns_the_correct_Context()
    {
        assertThat(testee.getContext()).isEqualTo(testContext);
    }

    @Test
    public void test_belongsTo_With_Context_Returns_true_if_the_passed_Context_is_the_same()
    {
        assertThat(testee.belongsTo(testContext)).isTrue();
    }

    @Test
    public void test_belongsTo_With_Context_Returns_false_if_another_Context_is_passed()
    {
        assertThat(testee.belongsTo(otherContext)).isFalse();
    }

    @Test
    public void test_belongsTo_With_Contextual_Returns_true_if_the_passed_Contextual_belongs_to_the_same_Context()
    {
        new Expectations()
        {{
            testContextual.getContext(); result = testContext;
        }};

        assertThat(testee.belongsTo(testContextual)).isTrue();
    }

    @Test
    public void test_belongsTo_With_Contextual_Returns_false_if_the_passed_Contextual_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testContextual.getContext(); result = otherContext;
        }};

        assertThat(testee.belongsTo(testContextual)).isFalse();
    }
}