package org.jayware.e2.examples;


import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;


public class Quickstart
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getEntityManager();
        ComponentManager componentManager = context.getComponentManager();

        /* Initially prepare custom components */
        componentManager.prepareComponent(context, ExampleComponent.class);

        /* Create an entity */
        entityManager.createEntity(context, EntityPath.path("/example"));

        /* Lookup an entity */
        EntityRef ref = entityManager.findEntity(context, EntityPath.path("/example"));

        /* Add a component to the entity */
        componentManager.addComponent(ref, ExampleComponent.class);

        /* Lookup a component */
        ExampleComponent cmp = componentManager.getComponent(ref, ExampleComponent.class);

        /* Change the component's properties */
        cmp.setText("Fubar!");
        cmp.setTextSize(14);

        /* Commit changes */
        cmp.pushTo(ref);

        /* Shutdown everything */
        context.dispose();
    }

    /* Define a custom component by a java interface */
    public interface ExampleComponent extends Component
    {
        /* Define properties by declaration of getters and setters  */
        String getText();

        void setText(String text);

        int getTextSize();

        void setTextSize(int size);
    }
}
