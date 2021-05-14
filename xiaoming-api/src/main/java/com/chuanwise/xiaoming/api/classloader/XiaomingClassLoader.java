package com.chuanwise.xiaoming.api.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class XiaomingClassLoader extends URLClassLoader {
    public XiaomingClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
