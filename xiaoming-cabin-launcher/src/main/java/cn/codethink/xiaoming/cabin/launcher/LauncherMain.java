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

package cn.codethink.xiaoming.cabin.launcher;

import cn.codethink.xiaoming.cause.Cause;

import java.io.*;

/**
 * <h1>Launcher Main</h1>
 *
 * @author Chuanwise
 */
public class LauncherMain {

    private static final String ASCII_ICON_PATH = "META-INF/xiaoming/cabin/launcher/ascii-icon.txt";

    private static void printAsciiIcon() {
        final ClassLoader classLoader = LauncherMain.class.getClassLoader();
        final String asciiIconString;

        try (final InputStream inputStream = classLoader.getResourceAsStream(ASCII_ICON_PATH)) {
            int available = inputStream.available();
            final byte[] buffer = new byte[available];

            while ((available -= inputStream.read(buffer)) != 0) {
                // nothing
            }

            asciiIconString = new String(buffer);
        } catch (final IOException e) {
            System.out.println("Fail to load the ascii icon in resource file '" + ASCII_ICON_PATH + "'! ");
            return;
        }

        System.out.println(asciiIconString);
    }

    public static void main(String[] args) {

        printAsciiIcon();

        final Launcher launcher = new Launcher();
        try {
            launcher.enable(Cause.of("Launcher main called"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
