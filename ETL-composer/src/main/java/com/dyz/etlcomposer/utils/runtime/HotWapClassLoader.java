package com.dyz.etlcomposer.utils.runtime;

public class HotWapClassLoader extends ClassLoader {
    public HotWapClassLoader() {
        super(HotWapClassLoader.class.getClassLoader());
    }

    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0 , classByte.length);
    }
}
