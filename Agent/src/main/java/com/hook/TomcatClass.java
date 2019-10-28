package com.hook;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TomcatClass extends AbstractClassHook implements LoadHookClass {
    byte[] bytecode;

    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool,
                             CtClass ctClass, ClassLoader loader) {
        bytecode = classfileBuffer;
        if (className.equals("org.apache.catalina.startup.Tomcat")) {
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(bytecode));
                CtBehavior[] ctBehaviors = ctClass.getDeclaredConstructors();
                classPool.importPackage("org.apache.log4j.Logger");
                classPool.importPackage("org.apache.log4j.*");
                for (CtBehavior ct : ctBehaviors) {
                    System.out.println("tomcat startup:" + ct);
                }
                bytecode = ctClass.toBytecode();

            } catch (IOException | CannotCompileException e) {
                e.printStackTrace();
            }

        }
        if(className.equals("org.apache.catalina.core.StandardService"))
        {
            try {
                ctClass=classPool.makeClass(new ByteArrayInputStream(bytecode));
                String methodServer="getServer";
                System.out.println("tomcat getContainer");
                CtMethod ctMethod=ctClass.getDeclaredMethod(methodServer);
                String out="{System.out.println(\"tomcatContainer:\"+$_);}";
                ctMethod.insertAfter(out);
                System.out.println("tomcat getContainer End!");

                String methodName="getName";
                CtMethod ctName=ctClass.getDeclaredMethod(methodName);
                String outName="{System.out.println(\"tomcatServerName:\"+$_);}";
                ctMethod.insertAfter(outName);
                bytecode=ctClass.toBytecode();

            } catch (IOException | NotFoundException | CannotCompileException e) {
                e.printStackTrace();
            }
        }

        if (className.equals("org.apache.catalina.mapper.Mapper")) {
            try {
                ctClass = classPool.get(className);

                //打印所有的
                CtMethod[] ctMethods = ctClass.getDeclaredMethods();
                for (CtMethod ctm : ctMethods) {
                    System.out.println("tomcat startup1:" + ctm);
                }
                CtBehavior[] ctBehaviors = ctClass.getDeclaredBehaviors();
                for (CtBehavior ctb : ctBehaviors) {
                    System.out.println("tomcat startup2:" + ctb);
                }
                bytecode = ctClass.toBytecode();

            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return bytecode;
    }
}
