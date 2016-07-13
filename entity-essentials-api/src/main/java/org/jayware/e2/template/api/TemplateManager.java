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
package org.jayware.e2.template.api;


import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentNotFoundException;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;


/**
 * The <code>TemplateManager</code>.
 */
public interface TemplateManager
{
//    <T extends EntityTemplate> T exportEntity(EntityRef ref, T template) throws IllegalArgumentException, IllegalStateException, ExportException;

//    EntityRef importEntity(Context context, EntityTemplate template) throws IllegalArgumentException, IllegalStateException, ImportException;

    <T extends ComponentTemplate> T exportComponent(Component component, TemplateProvider provider) throws IllegalArgumentException, IllegalStateException, ExportException;

    <T extends ComponentTemplate> T exportComponent(EntityRef ref, Class<? extends Component> type, TemplateProvider provider) throws IllegalArgumentException, IllegalStateException, ComponentNotFoundException, ExportException;

    <T extends ComponentTemplate> Component importComponent(Context context, T template) throws IllegalArgumentException, IllegalStateException, ImportException;

    <T extends ComponentTemplate> Component importComponent(EntityRef ref, T template) throws IllegalArgumentException, IllegalStateException, ImportException;
}
