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
package org.jayware.e2.util;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class GlobUtilTest
{
    @Test
    public void starBecomesDotStar() throws Exception {
        assertEquals("gl.*b", GlobUtil.toRegex("gl*b"));
    }

    @Test
    public void escapedStarIsUnchanged() throws Exception {
        assertEquals("gl\\*b", GlobUtil.toRegex("gl\\*b"));
    }

    @Test
    public void questionMarkBecomesDot() throws Exception {
        assertEquals("gl.b", GlobUtil.toRegex("gl?b"));
    }

    @Test
    public void escapedQuestionMarkIsUnchanged() throws Exception {
        assertEquals("gl\\?b", GlobUtil.toRegex("gl\\?b"));
    }

    @Test
    public void characterClassesDontNeedConversion() throws Exception {
        assertEquals("gl[-o]b", GlobUtil.toRegex("gl[-o]b"));
    }

    @Test
    public void escapedClassesAreUnchanged() throws Exception {
        assertEquals("gl\\[-o\\]b", GlobUtil.toRegex("gl\\[-o\\]b"));
    }

    @Test
    public void negationInCharacterClasses() throws Exception {
        assertEquals("gl[^a-n!p-z]b", GlobUtil.toRegex("gl[!a-n!p-z]b"));
    }

    @Test
    public void nestedNegationInCharacterClasses() throws Exception {
        assertEquals("gl[[^a-n]!p-z]b", GlobUtil.toRegex("gl[[!a-n]!p-z]b"));
    }

    @Test
    public void escapeCaratIfItIsTheFirstCharInACharacterClass() throws Exception {
        assertEquals("gl[\\^o]b", GlobUtil.toRegex("gl[^o]b"));
    }

    @Test
    public void metacharsAreEscaped() throws Exception {
        assertEquals("gl..*\\.\\(\\)\\+\\|\\^\\$\\@\\%b", GlobUtil.toRegex("gl?*.()+|^$@%b"));
    }

    @Test
    public void metacharsInCharacterClassesDontNeedEscaping() throws Exception {
        assertEquals("gl[?*.()+|^$@%]b", GlobUtil.toRegex("gl[?*.()+|^$@%]b"));
    }

    @Test
    public void escapedBackslashIsUnchanged() throws Exception {
        assertEquals("gl\\\\b", GlobUtil.toRegex("gl\\\\b"));
    }

    @Test
    public void slashQAndSlashEAreEscaped() throws Exception {
        assertEquals("\\\\Qglob\\\\E", GlobUtil.toRegex("\\Qglob\\E"));
    }

    @Test
    public void bracesAreTurnedIntoGroups() throws Exception {
        assertEquals("(glob|regex)", GlobUtil.toRegex("{glob,regex}"));
    }

    @Test
    public void escapedBracesAreUnchanged() throws Exception {
        assertEquals("\\{glob\\}", GlobUtil.toRegex("\\{glob\\}"));
    }

    @Test
    public void commasDontNeedEscaping() throws Exception {
        assertEquals("(glob,regex),", GlobUtil.toRegex("{glob\\,regex},"));
    }
}
