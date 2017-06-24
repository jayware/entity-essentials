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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IOUtil
{
    private IOUtil()
    {
        throw new IllegalStateException("Utility class");
    }

    public static void closeQuietly(InputStream stream)
    {
        closeQuietly((Closeable) stream);
    }

    public static void closeQuietly(OutputStream stream)
    {
        closeQuietly((Closeable) stream);
    }

    public static void closeQuietly(Closeable closeable) {
        try
        {
            if (closeable != null)
            {
                closeable.close();
            }
        }
        catch (IOException ignored)
        {
            // Ignored to be quiet.
        }
    }
}
