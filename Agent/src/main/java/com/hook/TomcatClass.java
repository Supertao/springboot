package com.hook;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.*;
import javassist.bytecode.AccessFlag;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TomcatClass extends AbstractClassHook implements LoadHookClass {
    byte[] bytecode;

    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool,
                             CtClass ctClass, ClassLoader loader) {
        bytecode = classfileBuffer;
        if (className.equals("org.apache.catalina.core.ApplicationFilterFactory")) {
            if (classPool != null) {
                try {
                    ctClass = classPool.makeClass(new ByteArrayInputStream(bytecode));
                    String methodChain = "createFilterChain";
                    CtMethod ctMethod = ctClass.getDeclaredMethod(methodChain);
                    String outName = "{"
                            +"System.out.println(\"tomcat startup:\"+$_);"
                            + "System.out.println(\"tomcat startup:\"+$_.INCREMENT);"
                            +"for(int i=0;i<3;i++){"
                            +"System.out.println(\"testxxx:\"+i);}"
                            + "}";
                    int pos=Modifier.isPrivate(ctMethod.getModifiers())?0:1;
                    ctClass.setModifiers(0);
                    System.out.println("私有还是共有"+pos);
                    ctMethod.insertAfter(outName);
                    String methodmatch = "matchFiltersServlet";
                    CtMethod ctMatch = ctClass.getDeclaredMethod(methodmatch);
                    ctMatch.setModifiers(AccessFlag.setPublic(ctMatch.getModifiers()));
                    String outMatch1="{"
                            +"System.out.println(\"test:\"+$2);"
                            +"}";
                    ctMatch.insertBefore(outMatch1);
                    String outMatch="{"
                            +"System.out.println(\"test:\"+$_);"
                            +"}";
                    ctMatch.insertAfter(outMatch);
                    bytecode=ctClass.toBytecode();
                } catch (IOException | NotFoundException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }

        }
        /*
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

                String methodName="toString";
                CtMethod ctName=ctClass.getDeclaredMethod(methodName);
                String outName="{System.out.println(\"tomcatServiceName:\"+$_);}";
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

*/
        return bytecode;
    }
}
