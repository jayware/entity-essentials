/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2017 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jayware.e2.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
