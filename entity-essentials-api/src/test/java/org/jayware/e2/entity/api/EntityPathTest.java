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

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityPath.path;
import static org.testng.Assert.fail;


public class EntityPathTest
{
    String stringPathRoot = "/";     EntityPath pathRoot = path(stringPathRoot);   int depthRoot = 0;  String[] segmentsRoot = {"/"};
    String stringPathEmpty = "";    EntityPath pathEmpty = path(stringPathEmpty); int depthEmpty = 0; String[] segmentsEmpty = new String[0];

    String stringPathA = "/a/b";            EntityPath pathA = path(stringPathA);  int depthA = 2;     String[] segmentsA = {"/", "a", "b"};
    String stringPathB = "/a/b/c/d";        EntityPath pathB = path(stringPathB);  int depthB = 4;     String[] segmentsB = {"/", "a", "b", "c", "d"};
    String stringPathC = "/a/b";            EntityPath pathC = path(stringPathC);  int depthC = 2;     String[] segmentsC = {"/", "a", "b"};
    String stringPathD = "a/b/c/d";         EntityPath pathD = path(stringPathD);  int depthD = 3;     String[] segmentsD = {"a", "b", "c", "d"};
    String stringPathE = "a/b";             EntityPath pathE = path(stringPathE);  int depthE = 1;     String[] segmentsE = {"a", "b"};
    String stringPathF = "/c/d";            EntityPath pathF = path(stringPathF);  int depthF = 2;     String[] segmentsF = {"/", "c", "d"};
    String stringPathG = "/a/b/c/d/a/b";    EntityPath pathG = path(stringPathG);  int depthG = 6;     String[] segmentsG = {"/", "a", "b", "c", "d", "a", "b"};

    @Test
    public void testFromString()
    throws Exception
    {
        EntityPath result = path("/a/b");
        assertThat(result.asString()).isEqualTo("/a/b/");

        result = path("");
        assertThat(result.asString()).isEqualTo("");

        result = path("a/b");
        assertThat(result.asString()).isEqualTo("a/b/");

        result = path("!\"�$%&/()=?");
        assertThat(result.asString()).isEqualTo("!\"�$%&/()=?/");
    }

    @Test
    public void testJoin()
    throws Exception
    {
        assertThat(pathA.join(pathF)).isEqualTo(pathB);
        assertThat(pathA.join(pathE).asString()).isEqualTo("/a/b/a/b/");

        assertThat(pathE.join(pathA).asString()).isEqualTo("a/b/a/b/");

        assertThat(pathA.join(pathEmpty)).isEqualTo(pathA);
        assertThat(pathE.join(pathEmpty)).isEqualTo(pathE);

        assertThat(pathA.join(pathRoot)).isEqualTo(pathA);
    }

    @Test
    public void testRelativize()
    throws Exception
    {
        EntityPath result = pathA.relativize(pathB);
        assertThat(result.asString()).isEqualTo("c/d/");

        result = pathRoot.relativize(pathA);
        assertThat(result.asString()).isEqualTo("a/b/");

        try
        {
            pathA.relativize(pathA);
            // TODO: Check Javadoc /a/b/c/ relativize /a/b/c/ = -
            // Hier fliegt aber eine Exception
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}

        try
        {
            pathA.relativize(pathC);
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}

        try
        {
            pathA.relativize(pathD);
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}

        try
        {
            pathE.relativize(pathA);
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}

        try
        {
            pathA.relativize(pathF);
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}

        try
        {
            pathRoot.relativize(pathEmpty);
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}

        try
        {
            pathEmpty.relativize(pathA);
            fail("Exception expected!");
        }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void testResolve()
    throws Exception
    {

    }

    @Test
    public void testAppend()
    throws Exception
    {
        assertThat(pathA.append(stringPathF)).isEqualTo(pathB);
        assertThat(pathA.append(stringPathE).asString()).isEqualTo("/a/b/a/b/");
        assertThat(pathA.append(stringPathA).asString()).isEqualTo("/a/b/a/b/");

        assertThat(pathE.append(stringPathF).asString()).isEqualTo("a/b/c/d/");
        assertThat(pathE.append(stringPathD).asString()).isEqualTo("a/b/a/b/c/d/");
        assertThat(pathE.append(stringPathE).asString()).isEqualTo("a/b/a/b/");

        assertThat(pathEmpty.append(stringPathA)).isEqualTo(pathA);
        assertThat(pathEmpty.append(stringPathE)).isEqualTo(pathE);
        assertThat(pathEmpty.append(stringPathEmpty)).isEqualTo(pathEmpty);

        assertThat(pathRoot.append(stringPathA)).isEqualTo(pathA);
        assertThat(pathRoot.append(stringPathE)).isEqualTo(pathA);
        assertThat(pathRoot.append(stringPathRoot)).isEqualTo(pathRoot);

        assertThat(pathA.append(stringPathEmpty)).isEqualTo(pathA);
        assertThat(pathA.append(stringPathRoot)).isEqualTo(pathA);

        assertThat(pathE.append(stringPathEmpty)).isEqualTo(pathE);
        assertThat(pathE.append(stringPathRoot)).isEqualTo(pathE);
    }

    @Test
    public void testPrepend()
    throws Exception
    {
        assertThat(pathA.prepend(stringPathF).asString()).isEqualTo("/c/d/a/b/");
        assertThat(pathA.prepend(stringPathE).asString()).isEqualTo("a/b/a/b/");
        assertThat(pathA.prepend(stringPathA).asString()).isEqualTo("/a/b/a/b/");

        assertThat(pathE.prepend(stringPathA).asString()).isEqualTo("/a/b/a/b/");
        assertThat(pathE.prepend(stringPathD).asString()).isEqualTo("a/b/c/d/a/b/");
        assertThat(pathE.prepend(stringPathE).asString()).isEqualTo("a/b/a/b/");

        assertThat(pathEmpty.prepend(stringPathA)).isEqualTo(pathA);
        assertThat(pathEmpty.prepend(stringPathD)).isEqualTo(pathD);
        assertThat(pathEmpty.prepend(stringPathEmpty)).isEqualTo(pathEmpty);
        assertThat(pathEmpty.prepend(stringPathRoot)).isEqualTo(pathRoot);

        assertThat(pathRoot.prepend(stringPathA)).isEqualTo(pathA);
        assertThat(pathRoot.prepend(stringPathE)).isEqualTo(pathE);
        assertThat(pathRoot.prepend(stringPathRoot)).isEqualTo(pathRoot);
        assertThat(pathRoot.prepend(stringPathEmpty)).isEqualTo(pathRoot);

        assertThat(pathE.prepend(stringPathRoot)).isEqualTo(pathA);
        assertThat(pathE.prepend(stringPathEmpty)).isEqualTo(pathE);
    }

    @Test
    public void testName()
    throws Exception
    {
        assertThat(pathA.getName()).isEqualTo(segmentsA[segmentsA.length - 1]);
        assertThat(pathE.getName()).isEqualTo(segmentsE[segmentsE.length - 1]);
        assertThat(pathRoot.getName()).isEqualTo(segmentsRoot[segmentsRoot.length - 1]);
        assertThat(pathEmpty.getName()).isEqualTo("");
    }

    @Test
    public void testSegments()
    throws Exception
    {
        assertThat(pathA.segments()).containsExactly(segmentsA);
        assertThat(pathE.segments()).containsExactly(segmentsE);
        assertThat(pathRoot.segments()).containsExactly(segmentsRoot);
        assertThat(pathEmpty.segments()).containsExactly(segmentsEmpty);
    }

    @Test
    public void testDepth()
    throws Exception
    {
        assertThat(pathA.depth()).isEqualTo(depthA);
        assertThat(pathE.depth()).isEqualTo(depthE);
        assertThat(pathRoot.depth()).isEqualTo(depthRoot);
        assertThat(pathEmpty.depth()).isEqualTo(depthEmpty);
    }

    @Test
    public void testIsAbsolute()
    throws Exception
    {
        assertThat(pathA.isAbsolute()).isTrue();
        assertThat(pathD.isAbsolute()).isFalse();
        assertThat(pathRoot.isAbsolute()).isTrue();
        assertThat(pathEmpty.isAbsolute()).isFalse();
    }

    @Test
    public void testIsRelative()
    throws Exception
    {
        assertThat(pathA.isRelative()).isFalse();
        assertThat(pathD.isRelative()).isTrue();
        assertThat(pathRoot.isRelative()).isFalse();
        assertThat(pathEmpty.isRelative()).isTrue();
    }
}