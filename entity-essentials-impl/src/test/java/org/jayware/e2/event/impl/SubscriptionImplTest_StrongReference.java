package org.jayware.e2.event.impl;

import mockit.Mocked;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SubscriptionImplTest_StrongReference
{
    private SubscriptionImpl_StrongReference testee;

    private @Mocked Object testSubscriber;
    private @Mocked EventDispatcher testDispatcher;
    private @Mocked EventFilter testFilterA;
    private @Mocked EventFilter testFilterB;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testee = new SubscriptionImpl_StrongReference(testSubscriber, testDispatcher, new EventFilter[] {testFilterA, testFilterB});
    }

    @Test
    public void test()
    throws Exception
    {
        assertThat(testee.getSubscriber()).isEqualTo(testSubscriber);
        assertThat(testee.getEventDispatcher()).isEqualTo(testDispatcher);
        assertThat(testee.getFilters()).containsExactlyInAnyOrder(testFilterA, testFilterB);
    }

    @Test
    public void test_isValid_Returns_true_for_a_newly_constructed_subscription()
    throws Exception
    {
        assertThat(testee.isValid()).isTrue();
    }

    @Test
    public void test_isValid_Returns_false_when_subscription_gets_invalidated()
    throws Exception
    {
        testee.invalidate();
        assertThat(testee.isValid()).isFalse();
    }
}
