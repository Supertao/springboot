package com.hook;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.ClassPool;
import javassist.CtClass;

public class ApplicationFilterHook  extends AbstractClassHook implements LoadHookClass {
    @Override
    public byte[] instrument(String className, byte[] classfileBuffer,
                             ClassPool classPool, CtClass ctClass, ClassLoader loader) {

        if(className.equals("org.apache.catalina.core.ApplicationFilterChain"))
        {

        }
        return new byte[0];
    }
}
