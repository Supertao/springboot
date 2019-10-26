package com.hook;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ProcessBuilderHook extends AbstractClassHook implements LoadHookClass {
    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool, CtClass ctClass, ClassLoader loader) {
        byte[] bytesCode = classfileBuffer;
        //windows
        if ("java.lang.ProcessImpl".equals(className)) {
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                classPool.importPackage("org.apache.log4j.Logger");
                classPool.importPackage("org.apache.log4j.*");

                CtBehavior[] ctBehaviors = ctClass.getDeclaredConstructors();
                for (CtBehavior cb : ctBehaviors) {
                    //System.out.println("testxx" + cb.getName());
                    if (cb.getName().equals("ProcessImpl")) {
                        String src =
                                "{" +
                                        "StringBuilder cmd=new StringBuilder();" +
                                        "for(int i=0;i<$1.length;i++) cmd.append($1[i]+\" \");" +
                                        "}";
                        cb.insertAfter(src);
                    }


                }

                bytesCode = ctClass.toBytecode();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
        else {
            return bytesCode;
        }

        return bytesCode;
    }

}
