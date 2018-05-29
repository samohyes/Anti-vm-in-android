package com.example.xudong_shao.vm_test;

import android.content.Context;

import java.lang.reflect.Method;

public class Syspro {
    public static String getProp(Context context, String property) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemProperties = classLoader.loadClass("android.os.SystemProperties");

            Method get = systemProperties.getMethod("get", String.class);

            Object[] params = new Object[1];
            params[0] = new String(property);

            return (String) get.invoke(systemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception exception) {
            throw null;
        }
    }
}
