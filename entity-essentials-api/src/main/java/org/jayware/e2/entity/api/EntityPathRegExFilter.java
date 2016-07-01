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

import static java.util.regex.Pattern.compile;


/**
 * The <code>EntityPathRegExFilter</code> implements the {@link Filter} interface and is used to filter
 * entities ({@link EntityRef EntityRefs}) based on their {@link EntityPath}.
 * <p>
 * Instances of the <code>EntityPathRegExFilter</code> are provided by the {@link EntityPathRegExFilter#regexFilter()} method.
 * Once an instance was created, it offers a fluent-interface, to add various <i>include</i> and <i>exclude</i> pattern.
 *
 * @see EntityPath
 * @see Filter
 * @see EntityPathFilter
 *
 * @since 1.0
 */
public class EntityPathRegExFilter
implements EntityPathFilter
{
    private final Set<Pattern> myIncludePatternSet;
    private final Set<Pattern> myExcludePatternSet;

    private EntityPathRegExFilter(Set<Pattern> includePatternSet, Set<Pattern> excludePatternSet)
    {
        myIncludePatternSet = includePatternSet;
        myExcludePatternSet = excludePatternSet;
    }

    /**
     * Returns an instance of an {@link EntityPathRegExFilter}.
     *
     * @return a new {@link EntityPathRegExFilter}.
     */
    public static EntityPathRegExFilter regexFilter()
    {
        return new EntityPathRegExFilter(new HashSet<Pattern>(), new HashSet<Pattern>());
    }

    /**
     * Adds the specified regular expressions as <u>include-patterns</u> to this {@link EntityPathRegExFilter}.
     * <p>
     * Every path is converted into a {@link Pattern} by the {@link Pattern#compile(String)} operation. The returned
     * {@link Pattern} is then added to the set of include-patterns.
     *
     * @param patterns a set of regular expressions to add.
     *
     * @return this {@link EntityPathRegExFilter}.
     */
    public EntityPathRegExFilter include(String... patterns)
    {
        for (String pattern : patterns)
        {
            myIncludePatternSet.add(compile(pattern));
        }

        return this;
    }

    /**
     * Adds the specified regular expressions as <u>exclude-patterns</u> to this {@link EntityPathRegExFilter}.
     * <p>
     * Every path is converted into a {@link Pattern} by the {@link Pattern#compile(String)} operation. The returned
     * {@link Pattern} is then added to the set of exclude-patterns.
     *
     * @param patterns a set of regular expressions to add.
     *
     * @return this {@link EntityPathRegExFilter}.
     */
    public EntityPathRegExFilter exclude(String... patterns)
    {
        for (String pattern : patterns)
        {
            myExcludePatternSet.add(compile(pattern));
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
}
