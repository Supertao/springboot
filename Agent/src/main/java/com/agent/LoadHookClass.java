package com.agent;

import javassist.ClassPool;
import javassist.CtClass;

public interface LoadHookClass {

   byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool, CtClass ctClass, ClassLoader loader);
}

