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


import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityPath;

import java.io.File;
import java.net.URI;


/**
 * The <code>TemplateManager</code>.
 *
 * @see Template
 *
 * @since 1.0
 */
@Deprecated
public interface TemplateManager
{
    /**
     * Creates a {@link Template} with the specified {@link URI} set.
     *
     * @param uri an {@link URI}.
     *
     * @return a {@link Template}.
     */
    Template createTemplate(URI uri);

    /**
     * Creates a {@link Template} with the {@link URI} constructed from the specified {@link File}.
     *
     * @param file a {@link File} to construct the {@link URI} from.
     *
     * @return a {@link Template}.
     */
    Template createTemplate(File file);

    /**
     * Creates a {@link Template} from a {@link String} representation.
     *
     * @param data a {@link String} representing a {@link Template}.
     *
     * @return a {@link Template}.
     */
    Template createTemplate(String data);

    /**
     * Reads the specified Template.
     * <p>
     * The {@link Template Template's} {@link URI} is used to obtain the resource
     * from where the {@link Template} is loaded.
     *
     * @param template the {@link Template} to read in.
     */
    void readTemplate(Template template);

    /**
     * Reads the specified Template.
     * <p>
     * The specified {@link URI} is used to obtain the resource from where the {@link Template}
     * is loaded.
     *
     * @param template the {@link Template} to read in.
     * @param uri an {@link URI} from where the {@link Template} is loaded.
     */
    void readTemplate(Template template, URI uri);

    /**
     * Reads the specified Template.
     * <p>
     * The specified {@link File} is used to obtain the resource from where the {@link Template}
     * is loaded.
     *
     * @param template the {@link Template} to read in.
     * @param file a {@link File} from where the {@link Template} is loaded.
     */
    void readTemplate(Template template, File file);

    /**
     * Writes the specified Template.
     * <p>
     * The {@link Template Template's} {@link URI} is used to obtain the resource
     * where the {@link Template} should be stored.
     *
     * @param template the {@link Template} to write out.
     */
    void writeTemplate(Template template);

    /**
     * Writes the specified Template.
     * <p>
     * The specified {@link URI} is used to obtain the resource where the {@link Template}
     * should be stored.
     *
     * @param template the {@link Template} to write out.
     * @param uri an {@link URI} from where the {@link Template} is loaded.
     */
    void writeTemplate(Template template, URI uri);

    /**
     * Writes the specified Template.
     * <p>
     * The specified {@link File} is used to obtain the resource where the {@link Template}
     * should be stored.
     *
     * @param template the {@link Template} to read in.
     * @param file a {@link File} from where the {@link Template} is loaded.
     */
    void writeTemplate(Template template, File file);

    /**
     * Imports an {@link Entity} from the specified {@link URI} into the given {@link Context}. The location of the
     * {@link Entity} is denoted by the specified {@link EntityPath}.
     * <p>
     * This operation is a combination of {@link TemplateManager#readTemplate(Template, URI)} and
     * {@link TemplateManager#loadEntity(Context, EntityPath, Template)}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param uri an {@link URI}.
     */
    void importEntity(Context context, EntityPath path, URI uri);

    /**
     * Imports an {@link Entity} from the specified {@link File} into the given {@link Context}. The location of the
     * {@link Entity} is denoted by the specified {@link EntityPath}.
     * <p>
     * This operation is a combination of {@link TemplateManager#readTemplate(Template, File)} and
     * {@link TemplateManager#loadEntity(Context, EntityPath, Template)}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param file a {@link File}.
     */
    void importEntity(Context context, EntityPath path, File file);

    /**
     * Imports an {@link Entity} from the specified {@link Template} into the given {@link Context}. The location of the
     * {@link Entity} is denoted by the specified {@link EntityPath}.
     * <p>
     * This operation is a combination of {@link TemplateManager#readTemplate(Template)} and
     * {@link TemplateManager#loadEntity(Context, EntityPath, Template)}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param template a {@link Template}.
     */
    void importEntity(Context context, EntityPath path, Template template);

    /**
     * Exports the {@link Entity} denoted by the specified {@link EntityPath} from the given {@link Context} to the
     * specified {@link URI}.
     * <p>
     * This operation is a combination of {@link TemplateManager#storeEntity(Context, EntityPath, Template)} and
     * {@link TemplateManager#writeTemplate(Template, URI)}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param uri an {@link URI}.
     */
    void exportEntity(Context context, EntityPath path, URI uri);

    /**
     * Exports the {@link Entity} denoted by the specified {@link EntityPath} from the given {@link Context} to the
     * specified {@link File}.
     * <p>
     * This operation is a combination of {@link TemplateManager#storeEntity(Context, EntityPath, Template)} and
     * {@link TemplateManager#writeTemplate(Template, File)}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param file a {@link File}.
     */
    void exportEntity(Context context, EntityPath path, File file);

    /**
     * Exports the {@link Entity} denoted by the specified {@link EntityPath} from the given {@link Context} to the
     * specified {@link Template}.
     * <p>
     * This operation is a combination of {@link TemplateManager#storeEntity(Context, EntityPath, Template)} and
     * {@link TemplateManager#writeTemplate(Template)}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param template a {@link Template}.
     */
    void exportEntity(Context context, EntityPath path, Template template);

    /**
     * Stores the {@link Entity} denoted by the specified {@link EntityPath} from the given {@link Context} into the
     * passed {@link Template}.
     * <p>
     * In contrast to {@link TemplateManager#exportEntity(Context, EntityPath, Template)} this operation does not write
     * to the template's underlying resource. Therefore the data is just stored in the template-object.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param template a {@link Template}.
     *
     */
    void storeEntity(Context context, EntityPath path, Template template);

    /**
     * Loads an {@link Entity} from the the passed {@link Template} into the given {@link Context} at the location
     * denoted by the specified {@link EntityPath}.
     * <p>
     * In contrast to {@link TemplateManager#importEntity(Context, EntityPath, Template)} this operation does not read
     * the template's underlying resource again. Therefore this operation uses the data stored in the template-object.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     * @param template a {@link Template}.
     */
    void loadEntity(Context context, EntityPath path, Template template);

    /**
     * Returns the specified {@link Template} as {@link String}.
     *
     * @param template the {@link Template} to print.
     *
     * @return the {@link Template} as {@link String}.
     */
    String printTemplate(Template template);
}
