/*
 * Copyright 2023 CodeThink Technologies and contributors.
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

package cn.codethink.xiaoming.util;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * <h1>Files</h1>
 *
 * @author Chuanwise
 */
public class Files {
    private Files() {
        throw new UnsupportedOperationException("No cn.codethink.xiaoming.util.Files instances for you! ");
    }

    /**
     * Test if given 2 files are content equal.
     *
     * @param file1 file 1
     * @param file2 file 2
     * @return if given 2 files are content equal
     * @throws IOException if any IO error occurred
     */
    public static boolean contentEqual(File file1, File file2) throws IOException {
        Preconditions.checkNotNull(file1, "File 1 is null! ");
        Preconditions.checkNotNull(file2, "File 2 is null! ");

        if (file1.length() != file2.length()) {
            return false;
        }

        final byte[] file1Bytes = java.nio.file.Files.readAllBytes(file1.toPath());
        final byte[] file2Bytes = java.nio.file.Files.readAllBytes(file2.toPath());

        return Arrays.equals(file1Bytes, file2Bytes);
    }
}
