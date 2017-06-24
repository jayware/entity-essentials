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

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;
import org.testng.annotations.Test;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.fail;


public class IOUtilTest
{
    private @Injectable Closeable testCloseable;
    private @Injectable InputStream testInputStream;
    private @Injectable OutputStream testOutputStream;

    @Test
    public void test_that_the_close_operation_is_called()
    throws Exception
    {
        IOUtil.closeQuietly(testCloseable);
        IOUtil.closeQuietly(testInputStream);
        IOUtil.closeQuietly(testOutputStream);

        new Verifications() {{
            testCloseable.close();
            testInputStream.close();
            testOutputStream.close();
        }};
    }

    @Test
    public void test_that_thrown_exceptions_are_swallowed()
    throws Exception
    {
        new Expectations() {{
            testCloseable.close(); result = new IOException();
            testInputStream.close(); result = new IOException();
            testOutputStream.close(); result = new IOException();
        }};

        try
        {
            IOUtil.closeQuietly(testCloseable);
            IOUtil.closeQuietly(testInputStream);
            IOUtil.closeQuietly(testOutputStream);
        }
        catch (Exception e)
        {
            fail("Expected no exception is thrown!");
        }
    }

    @Test
    public void test_that_null_can_be_passed()
    {
        IOUtil.closeQuietly((Closeable) null);
        IOUtil.closeQuietly((InputStream) null);
        IOUtil.closeQuietly((OutputStream) null);
    }
}
