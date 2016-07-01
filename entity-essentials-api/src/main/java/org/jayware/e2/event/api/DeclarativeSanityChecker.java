/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.event.api;


import java.util.ArrayList;
import java.util.List;


public abstract class DeclarativeSanityChecker
implements SanityChecker
{
    private final List<CheckRule> myRules = new ArrayList<CheckRule>();

    public DeclarativeSanityChecker()
    {
        setup(new CheckerRuleBuilderImpl());
    }

    @Override
    public void check(Event event)
    throws SanityCheckFailedException
    {
        for (CheckRule rule : myRules)
        {
            if (rule.getEventType().isAssignableFrom(event.getType()))
            {
                final Object parameter = event.getParameter(rule.getParam());

                if (rule.getCheckNotNull())
                {
                    if (parameter == null)
                    {
                        throw new SanityCheckFailedException(event, "Event does not provide the mandatory parameter: " + rule.getParamShortName() + " ( " + rule.getParam() + " )");
                    }
                }

                if (rule.getCheckInstanceOf() && parameter != null)
                {
                    if (!rule.getParamType().isAssignableFrom(parameter.getClass()))
                    {
                        throw new SanityCheckFailedException(event, "The parameter " + rule.getParamShortName() + " ( " + rule.getParam() + " ) is not of the appropriate type: " + rule.getParamType().getName());
                    }
                }
            }
        }
    }

    protected abstract void setup(SanityCheckerRuleBuilder checker);

    public interface SanityCheckerRuleBuilder
    {
        SanityCheckerRuleBuilderParam check(Class<? extends EventType> type);
    }

    public interface SanityCheckerRuleBuilderParam
    {
        SanityCheckerRuleBuilderPredicate param(String parameter);

        SanityCheckerRuleBuilderPredicate param(String parameter, String shortName);
    }

    public interface SanityCheckerRuleBuilderPredicate
    {
        SanityCheckerRuleBuilderPredicate notNull();

        SanityCheckerRuleBuilderPredicate instanceOf(Class<?> type);

        SanityCheckerRuleBuilder done();
    }

    private class CheckerRuleBuilderImpl
    implements SanityCheckerRuleBuilder, SanityCheckerRuleBuilderParam, SanityCheckerRuleBuilderPredicate
    {
        private Class<? extends EventType> myEventType;
        private String myParam;
        private String myParamShortName;
        private boolean myCheckNotNull;
        private boolean myCheckInstanceOf;
        private Class<?> myParamType;

        @Override
        public SanityCheckerRuleBuilderParam check(Class<? extends EventType> type)
        {
            myEventType = type;
            return this;
        }

        @Override
        public SanityCheckerRuleBuilderPredicate param(String parameter)
        {
            myParam = parameter;
            myParamShortName = null;
            return param(parameter, null);
        }

        @Override
        public SanityCheckerRuleBuilderPredicate param(String parameter, String shortName)
        {
            myParam = parameter;
            myParamShortName = shortName;
            return this;
        }

        @Override
        public SanityCheckerRuleBuilderPredicate notNull()
        {
            myCheckNotNull = true;
            return this;
        }

        @Override
        public SanityCheckerRuleBuilderPredicate instanceOf(Class<?> type)
        {
            myCheckInstanceOf = true;
            myParamType = type;
            return this;
        }

        @Override
        public SanityCheckerRuleBuilder done()
        {
            myRules.add(new CheckRule(myEventType, myParam, myParamShortName, myCheckNotNull, myCheckInstanceOf, myParamType));

            // Reset
            myEventType = null;
            myParam = null;
            myParamShortName = null;
            myCheckNotNull = false;
            myCheckInstanceOf = false;
            myParamType = null;

            return this;
        }
    }

    private static class CheckRule
    {
        private final Class<? extends EventType> myEventType;
        private final String myParam;
        private final String myParamShortName;
        private final boolean myCheckNotNull;
        private final boolean myCheckInstanceOf;
        private final Class<?> myParamType;

        public CheckRule(Class<? extends EventType> eventType, String param, String paramShortName, boolean checkNotNull, boolean checkInstanceOf, Class<?> paramType)
        {
            myEventType = eventType;
            myParam = param;
            myParamShortName = paramShortName;
            myCheckNotNull = checkNotNull;
            myCheckInstanceOf = checkInstanceOf;
            myParamType = paramType;
        }

        public Class<? extends EventType> getEventType()
        {
            return myEventType;
        }

        public String getParam()
        {
            return myParam;
        }

        public String getParamShortName()
        {
            return myParamShortName;
        }

        public boolean getCheckNotNull()
        {
            return myCheckNotNull;
        }

        public boolean getCheckInstanceOf()
        {
            return myCheckInstanceOf;
        }

        public Class<?> getParamType()
        {
            return myParamType;
        }
    }
}
