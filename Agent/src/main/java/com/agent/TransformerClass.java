package com.agent;

import com.hook.ProcessBuilderHook;
import com.hook.TomcatClass;
import com.hook.request.HttpServlet;
import com.hook.UriPath;
import com.hook.XXEHook;
import javassist.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class TransformerClass implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(TransformerClass.class.getName());
    LoadHookClass[] transforms = new LoadHookClass[]
            {

                     new UriPath(),
                    new XXEHook(),
                    new HttpServlet(),
                   // new SqlStatement(),
                    //new FileList()
                    new ProcessBuilderHook(),
                    new TomcatClass()
            };

    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)  {
        String replacedClassName = null;
        if (className != null) {
            replacedClassName = className.replace("/", ".");
            logger.info("ClassName: " + replacedClassName);
            //System.out.println("ClassName" + replacedClassName);
            /*
            CtClass ctClassxx = null;
            try {
                ClassLoader classLoaderxx = Thread.currentThread().getContextClassLoader();
                System.out.println(classLoaderxx);
                ClassPool classPoolxx = ClassPool.getDefault();
                classPoolxx.insertClassPath(new LoaderClassPath(classLoaderxx));
                ctClassxx = classPoolxx.makeClass(new ByteArrayInputStream(classfileBuffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
            CtBehavior[] methods = ctClassxx.getDeclaredBehaviors();
            for (CtBehavior method : methods) {
                System.out.println("method" + method);
            }
            */

        }

        byte[] bytesCode = classfileBuffer;
        ClassPool classPool = ClassPool.getDefault();
        //定义好CtClass和classLoder
        CtClass ctClass = null;
        classLoader=Thread.currentThread().getContextClassLoader();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        //打印所有class的方法
        boolean debug = false;
        if (debug) {
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtBehavior[] methods = ctClass.getDeclaredBehaviors();
                for (CtBehavior method : methods) {
                    System.out.println("method" + method);
                }

                bytesCode = ctClass.toBytecode();
            } catch (IOException | CannotCompileException e) {
                e.printStackTrace();
            }

        } else {
            for (LoadHookClass transform : transforms) {
                bytesCode = transform.instrument(replacedClassName, bytesCode, classPool, ctClass, classLoader);
            }
        }


        return bytesCode;
    }
}
