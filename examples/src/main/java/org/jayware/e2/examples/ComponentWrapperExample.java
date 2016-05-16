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
package org.jayware.e2.examples;


import org.jayware.e2.component.api.AbstractComponentWrapper;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;


public class ComponentWrapperExample
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);

        /* Create an entity */
        final EntityRef ref = entityManager.createEntity(context);

        /* Prepare the component and add it the entity */
        componentManager.prepareComponent(context, ExampleComponent.class);
        ExampleComponent component = componentManager.addComponent(ref, ExampleComponent.class);

        /* Wrap-up the component and its owner entity */
        ExampleComponentWrapper wrapper = new ExampleComponentWrapper().wrap(ref, component);

        // Set properties. There is no need to explicitly push the new
        // value, because we implemented the wrapper to do it on every call.
        wrapper.setText("Area: ");
        wrapper.width(100).height(50);

        /* To demonstrate the proper functioning, we request the component again... */
        component = componentManager.getComponent(ref, ExampleComponent.class);

        /* ...and print it to the console. */
        System.out.println("\n" + component.getText() + component.getArea() + "\n");

        /* Shutdown everything */
        context.dispose();
    }

    /**
     * A ComponentWrapper offers full control over the access to a component. This is useful to enhance ease of use.
     * E.g. to automatically push/pull the component when a property setter/getter is called. Or to store/retrieve th
     * result of a computation.
     *
     * A ComponentWrapper can, but does not have to implement the operations of the wrapped component. The generic
     * parameter is used to determine the type of the component.
     */
    public static class ExampleComponentWrapper
    extends AbstractComponentWrapper<ExampleComponentWrapper, ExampleComponent>
    {
        private int width, height;

        public String getText()
        {
            pull();
            return myComponent.getText();
        }

        public void setText(String text)
        {
            myComponent.setText(text);
            push();
        }

        public ExampleComponentWrapper width(int width)
        {
            updateArea(width, height);
            return this;
        }

        public ExampleComponentWrapper height(int height)
        {
            updateArea(width, height);
            return this;
        }

        public int getArea()
        {
            pull();
            return myComponent.getArea();
        }

        private void updateArea(int width, int height)
        {
            this.width = width;
            this.height = height;
            myComponent.setArea(width * height);
            push();
        }
    }

    public interface ExampleComponent extends Component
    {
        String getText();

        void setText(String text);

        int getArea();

        void setArea(int area);
    }
}
