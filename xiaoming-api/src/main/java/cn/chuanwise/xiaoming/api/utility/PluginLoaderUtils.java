package cn.chuanwise.xiaoming.api.utility;

import cn.chuanwise.utility.StaticUtility;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件类加载器
 * @author Chuanwise
 */
public class PluginLoaderUtils extends StaticUtility {
    /**
     * 设立某个 jarFile 的 URLClassLoader
     * @param jarFile
     * @return
     */
    public static ClassLoader urlClassLoader(final File jarFile,
                                             final ClassLoader father)
            throws MalformedURLException {
        return URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()}, father);
    }

    /**
     * 在一个 URL 类加载器中增加一个 URL。如果这个类加载器中已经有这个 URL，则本操作无影响
     * @param jarFile
     * @return
     */
    public static URLClassLoader extendURLClassLoader(final File jarFile,
                                                      final URLClassLoader father)
            throws Exception {
        final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        final boolean accessible = addURL.isAccessible();
        addURL.setAccessible(true);
        addURL.invoke(father, jarFile.toURI().toURL());
        addURL.setAccessible(accessible);
        return father;
    }


    @Nullable
    public static Class loadClass(final File jarFile, final String className, final URLClassLoader classLoader)
            throws Exception {
        return extendURLClassLoader(jarFile, ((URLClassLoader) classLoader)).loadClass(className);
    }
}