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

import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.regex.Pattern.compile;
import static org.jayware.e2.util.GlobUtil.toRegex;


/**
 * Due to the fact that entities form a tree-like structure, this class is used to describe the path of an {@link Entity}
 * and provides operations to create and manipulate an <code>EntityPath</code> by preserving its consistency.
 * <p>
 * In contrast to an {@link EntityRef} an <code>EntityPath</code> is context-unaware, this means that an
 * <code>EntityPath</code> doesn't refer to an {@link Entity} instance. Instead an <code>EntityPath</code> specifies the
 * location of an {@link Entity} in any {@link Context} and is used to query the instance of an {@link Entity} the
 * <code>EntityPath</code> points to.
 * <p>
 * <b>Note:</b> An <code>EntityPath</code> instance is immutable. Therefore all mutating operations return a new
 * instance as the result of the operation.
 * <p>
 * <b>Syntax</b><br>
 * The path of an {@link Entity} is a string constrained by the the regular expression: {@link EntityPath#PATTERN}.
 * This pattern allows a path to consist of any word/digest characters separated by slashes (/). An example would
 * be: <i>'/a1/b2/3c'</i> or <i>'foo/bar1'</i>
 * <p>
 * <b>Semantic</b><br>
 * A <code>EntityPath</code> is separated into segments with the slash (/) as separator. <u>Every segment represents
 * an {@link Entity} along the path in an entity tree.</u> the The example below shows how an <code>EntityPath</code>
 * fragments into segments:
 * <pre>
 *  path: <i>/a/b/c</i>
 *  segment[0]: / -- Entity(/)
 *  segment[1]: a -- Entity(a)
 *  segment[2]: b -- Entity(b)
 *  segment[3]: c -- Entity(c)
 * </pre>
 * The first (segment[0]) and the last (segment[3]) segments are special. Segment 3 is special, because the last segment
 * in an <code>EntityPath</code> is the entity's name. The first segment is special, because its the root entity. Every
 * entity tree has a root entity. <u>A root entity is represented as the leading slash (/) in an
 * <code>EntityPath</code>.</u> Therefore, every path starting with a leading slash is an absolute path and vice versa
 * every path which doesn't start with a slash is a relative path. Examples would be:
 * <pre>
 * absolute:    relative:
 *  <i>/</i>            <i>b</i>
 *  <i>/a/b/c</i>       <i>a/b/c</i>
 *  <i>/foo</i>         <i>foo/bar</i>
 * </pre>
 * Whether to use an absolute or relative path depends on the operation the path is used for. There are operations
 * which require an absolute path and there are operations which require a relative path.
 * <p>
 * In contrast to the leading slash of an <code>EntityPath</code> a trailing slash has no meaning. The path
 * <i>'/a/b/c/'</i> is equivalent to the path <i>'/a/b/c'</i>.
 *
 * @see Entity
 * @see EntityRef
 *
 * @since 1.0
 */
public class EntityPath
{
    public static final String SEPARATOR = "/";

    public static final String PATTERN = "^[-\\w\\d/]*$";

    public static final EntityPath ROOT_PATH = new EntityPath("/", new String[] {SEPARATOR});
    public static final EntityPath EMPTY_PATH = new EntityPath("", new String[0]);

    private final String myPath;
    private final String myName;
    private final List<String> mySegments;

    private EntityPath(String path, String[] segments)
    {
        if (!(path.endsWith(SEPARATOR) || path.isEmpty()) && !path.contains("*")) // TODO: Refactor EntityPath to handle path patterns
        {
            path += SEPARATOR;
        }

        myPath = path;

        if (segments.length == 0)
        {
            myName = "";
        }
        else
        {
            myName = segments[segments.length - 1];
        }

        List<String> list = asList(segments);
        if (myPath.startsWith(SEPARATOR) && !myPath.equals("/"))
        {
            list.set(0, SEPARATOR);
        }

        mySegments = unmodifiableList(list);
    }

    /**
     * Creates an {@link EntityPath} from the specified {@link String}.
     *
     * @param path a {@link String}.
     *
     * @return the newly created {@link EntityPath}.
     */
    public static EntityPath path(String path)
    {
        if (path.equals(SEPARATOR))
        {
            return ROOT_PATH;
        }
        else if (path.isEmpty())
        {
            return EMPTY_PATH;
        }
        else
        {
            return new EntityPath(path, path.split(SEPARATOR));
        }
    }

    /**
     * Joins this {@link EntityPath} with the specified {@link EntityPath} and returns a new resulting {@link EntityPath}.
     * <p>
     * Joining two paths <b>p1</b> and <b>p2</b> behaves in exactly the same way as:
     * <code>
     *      <b>p1</b>.{@link EntityPath#append(String) append}(<b>p2</b>.{@link EntityPath#asString() asString()})
     * </code>
     *
     * @param other an {@link EntityPath} to join with this.
     *
     * @return a new {@link EntityPath} as result.
     *
     * @see EntityPath#append(String)
     * @see EntityPath#asString()
     */
    public EntityPath join(EntityPath other)
    {
        return append(other.asString());
    }

    /**
     * <table>
     *     <tr><th>this</th><th>other</th><th>result</th></tr>
     *     <tr><td>/</td><td>/a/b/</td><td>a/b/</td></tr>
     *     <tr><td>/a/b/</td><td>/a/b/c/d/</td><td>c/d/</td></tr>
     *     <tr><td>/a/b/c/</td><td>/a/b/c/</td><td><i>fails</i></td></tr>
     *     <tr><td>/a/b/c/</td><td>/a/b/</td><td><i>fails</i></td></tr>
     *     <caption>Examples</caption>
     * </table>
     *
     * @param other
     * @return
     * @throws IllegalArgumentException
     */
    public EntityPath relativize(EntityPath other)
    throws IllegalArgumentException
    {
        if (this.equals(other))
        {
            throw new IllegalArgumentException("Can not relativize a path against it self!\n\tthis: " + myPath + "\n\tother: " + other.myPath + "\n");
        }

        if (isRelative() || other.isRelative())
        {
            throw new IllegalArgumentException("To relativize a path against an other path, both paths have to be absolute!\n\tthis: " + myPath + "\n\tother: " + other.myPath + "\n");
        }

        if (depth() >= other.depth())
        {
            throw new IllegalArgumentException("To relativize a path against an other path, the path have to be deeper!\n\tthis: " + myPath + "\n\tother: " + other.myPath + "\n");
        }

        final int otherDepth = other.depth();
        String iSegment, jSegment;

        int index = 0;
        do
        {
            iSegment = mySegments.get(index);
            jSegment = other.mySegments.get(index);

            ++index;
        }
        while (iSegment.equals(jSegment) && index <= depth());

        EntityPath result = EMPTY_PATH;
        for (int i = index; i <= otherDepth; ++i)
        {
            result = result.append(other.mySegments.get(i));
        }

        return result;
    }

    /**
     * <table>
     *     <tr><th>this</th><th>other</th><th>result</th></tr>
     *     <tr><td>/a/b/</td><td>c/d/</td><td>/a/b/c/d/</td></tr>
     *     <caption>Examples</caption>
     * </table>
     *
     * @param other
     * @return
     */
    public EntityPath resolve(EntityPath other)
    {
        return join(other);
    }

    /**
     * Appends the specified {@link String} to the path of this {@link EntityPath} and returns a new {@link EntityPath}
     * as result.
     * <p>
     * This operation is similar to string-concatenation. A difference to string-concatenation is, that this operation
     * preserves the integrity of the resulting path. Therefor if <code>"/c/d"</code> is appended to <code>"/a/b/"</code>
     * the result will be <code>"/a/b/c/d"</code> and not <code>"/a/b//c/d"</code> which would be the result of
     * simple string-concatenation.
     *
     * @param segment a {@link String} to append.
     *
     * @return a new {@link EntityPath}.
     */
    public EntityPath append(String segment)
    {
        if (myPath.isEmpty())
        {
            return path(segment);
        }

        if (segment.isEmpty())
        {
            return this;
        }

        if (segment.startsWith(SEPARATOR))
        {
            segment = segment.substring(1);
        }

        return path(myPath + segment);
    }

    public EntityPath prepend(String segment)
    {
        String path = myPath;

        if (segment.isEmpty())
        {
            return this;
        }

        if (this.equals(ROOT_PATH))
        {
            return path(segment);
        }

        if (path.startsWith(SEPARATOR) || segment.endsWith(SEPARATOR))
        {
            path = segment + path;
        }
        else
        {
            path = segment + SEPARATOR + path;
        }

        return path(path);
    }

    public EntityPath getParent()
    {
        if (this.equals(ROOT_PATH))
        {
            return ROOT_PATH;
        }

        if (this.equals(EMPTY_PATH))
        {
            return EMPTY_PATH;
        }

        String basePath = "";
        String[] baseSegments = new String[mySegments.size() - 1];

        for (int i = 0; i < baseSegments.length; ++i)
        {
            final String segment = mySegments.get(i);

            basePath += segment;
            baseSegments[i] = mySegments.get(i);

            if (!segment.endsWith(SEPARATOR))
            {
                basePath += SEPARATOR;
            }
        }

        return new EntityPath(basePath, baseSegments);
    }

    public String asString()
    {
        return myPath;
    }

    public Pattern asPattern()
    {
        return compile(toRegex(asString()));
    }

    public String getName()
    {
        return myName;
    }

    public Iterable<String> segments()
    {
        return mySegments;
    }

    public int depth()
    {
        return max(mySegments.size() - 1, 0);
    }

    public boolean isAbsolute()
    {
        return myPath.startsWith(SEPARATOR);
    }

    public boolean isRelative()
    {
        return !isAbsolute();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final EntityPath that = (EntityPath) o;

        return !(myPath != null ? !myPath.equals(that.myPath) : that.myPath != null);

    }

    @Override
    public int hashCode()
    {
        return myPath != null ? myPath.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return myPath;
    }

}