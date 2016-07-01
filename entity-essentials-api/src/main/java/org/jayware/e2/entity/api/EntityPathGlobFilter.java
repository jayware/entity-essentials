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
package org.jayware.e2.entity.api;

import org.jayware.e2.context.api.Context;
import org.jayware.e2.util.Filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import static org.jayware.e2.entity.api.EntityPath.path;


/**
 * The <code>EntityPathGlobFilter</code> implements the {@link Filter} interface and is used to filter
 * entities ({@link EntityRef EntityRefs}) based on their {@link EntityPath}.
 * <p>
 * Instances of the <code>EntityPathGlobFilter</code> are provided by the {@link EntityPathGlobFilter#globFilter()} method.
 * Once an instance was created, it offers a fluent-interface, to add various <i>include</i> and <i>exclude</i> pattern.
 *
 * @see EntityPath
 * @see Filter
 * @see EntityPathFilter
 *
 * @since 1.0
 */
public class EntityPathGlobFilter
implements EntityPathFilter
{
    private final Set<Pattern> myIncludePatternSet;
    private final Set<Pattern> myExcludePatternSet;

    private EntityPathGlobFilter(Set<Pattern> includePatternSet, Set<Pattern> excludePatternSet)
    {
        myIncludePatternSet = includePatternSet;
        myExcludePatternSet = excludePatternSet;
    }

    /**
     * Returns an instance of an {@link EntityPathGlobFilter}.
     *
     * @return a new {@link EntityPathGlobFilter}.
     */
    public static EntityPathGlobFilter globFilter()
    {
        return new EntityPathGlobFilter(new HashSet<Pattern>(), new HashSet<Pattern>());
    }

    /**
     * Adds the specified paths as <u>include-patterns</u> to this {@link EntityPathGlobFilter}.
     * <p>
     * Every path is converted into an {@link EntityPath} by using {@link EntityPath#path(String)}. To obtain a
     * {@link Pattern} the {@link EntityPath#asPattern()} method is called. The returned {@link Pattern} is then
     * added to the set of include-patterns.
     *
     * @param paths a set of paths to add.
     *
     * @return this {@link EntityPathGlobFilter}.
     */
    public EntityPathGlobFilter include(String... paths)
    {
        for (String path : paths)
        {
            myIncludePatternSet.add(path(path).asPattern());
        }

        return this;
    }

    /**
     * Adds the specified paths as <u>include-patterns</u> to this {@link EntityPathGlobFilter}.
     * <p>
     * To obtain a {@link Pattern} the {@link EntityPath#asPattern()} method is called. The returned {@link Pattern}
     * is then added to the set of include-patterns.
     *
     * @param paths a set of paths to add.
     *
     * @return this {@link EntityPathGlobFilter}.
     */
    public EntityPathGlobFilter include(EntityPath... paths)
    {
        for (EntityPath path : paths)
        {
            myIncludePatternSet.add(path.asPattern());
        }

        return this;
    }

    /**
     * Adds the specified paths as <u>exclude-patterns</u> to this {@link EntityPathGlobFilter}.
     * <p>
     * Every path is converted into an {@link EntityPath} by using {@link EntityPath#path(String)}. To obtain a
     * {@link Pattern} the {@link EntityPath#asPattern()} method is called. The returned {@link Pattern} is then
     * added to the set of exclude-patterns.
     *
     * @param paths a set of paths to add.
     *
     * @return this {@link EntityPathGlobFilter}.
     */
    public EntityPathGlobFilter exclude(String... paths)
    {
        for (String path : paths)
        {
            myExcludePatternSet.add(path(path).asPattern());
        }

        return this;
    }

    /**
     * Adds the specified paths as <u>exclude-patterns</u> to this {@link EntityPathGlobFilter}.
     * <p>
     * To obtain a {@link Pattern} the {@link EntityPath#asPattern()} method is called. The returned {@link Pattern}
     * is then added to the set of exclude-patterns.
     *
     * @param paths a set of paths to add.
     *
     * @return this {@link EntityPathGlobFilter}.
     */
    public EntityPathGlobFilter exclude(EntityPath... paths)
    {
        for (EntityPath path : paths)
        {
            myExcludePatternSet.add(path.asPattern());
        }

        return this;
    }

    @Override
    public boolean accepts(Context context, EntityRef ref)
    {
        if (ref == null)
        {
            return false;
        }

        final Iterator<Pattern> includeIterator = myIncludePatternSet.iterator();
        final Iterator<Pattern> excludeIterator = myExcludePatternSet.iterator();

        boolean include = myIncludePatternSet.isEmpty();
        boolean exclude = !myExcludePatternSet.isEmpty();

        while (!include && includeIterator.hasNext())
        {
            final Pattern pattern = includeIterator.next();
            include = pattern.matcher(ref.getPath().asString()).matches();
        }

        while (exclude && excludeIterator.hasNext())
        {
            final Pattern pattern = excludeIterator.next();
            exclude = pattern.matcher(ref.getPath().asString()).matches();
        }

        return include && !exclude;
    }

    Set<Pattern> getIncludePatternSet()
    {
        return myIncludePatternSet;
    }

    Set<Pattern> getExcludePatternSet()
    {
        return myExcludePatternSet;
    }
}
