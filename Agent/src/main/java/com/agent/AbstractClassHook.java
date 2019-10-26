package com.agent;

import javassist.*;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedList;

public abstract class AbstractClassHook {

    private static final Logger logger = Logger.getLogger(AbstractClassHook.class.getName());

    public void insertBefore(CtClass ctClass, String methodName, String desc, String src) throws NotFoundException {
        //System.out.println("0000001");
        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName, desc);
        //System.out.println("methods size" + ":" + methods.size());

        if (methods != null && methods.size() > 0) {
            for (CtBehavior method : methods) {
                if (method != null) {
                    insertBefore(method, src);
                }
            }
        } else {
            // logger.warn("Cannot find" +methodName+" "+desc+" in class "+ctClass.getName());
        }

    }

    private void insertBefore(CtBehavior method, String src) {
        try {
            method.insertBefore(src);
            //System.out.println("insert before  " + method.getLongName());
            logger.info("insert before  " + method.getLongName());
        } catch (CannotCompileException e) {
            logger.error("insert before  " + method.getLongName() + " failed", e);
            //System.out.println("insert before  " + method.getLongName() + " failed");
        }

    }

    public void insertAfter(CtBehavior method, String src) throws CannotCompileException {
        try {
            method.insertAfter(src);
        } catch (CannotCompileException e) {
            //String message = "insert after method " + method.getLongName() + " failed";
        }
    }


    public void insertAfter(CtClass ctClass, String methodName, String desc, String src)
            throws NotFoundException, CannotCompileException {

        LinkedList<CtBehavior> methods = getMethod(ctClass, methodName, desc);
        if (methods != null && methods.size() > 0) {
            for (CtBehavior method : methods) {
                if (method != null) {
                    insertAfter(method, src);
                }
            }
        } else {
            //error
        }

    }

    private LinkedList<CtBehavior> getMethod(CtClass ctClass, String methodName, String desc) throws NotFoundException {
        if ("init".equals(methodName)) {
            return getConstrutor(ctClass, desc);
        }
        //System.out.println("0000003");
        LinkedList<CtBehavior> methods = new LinkedList<CtBehavior>();
        //System.out.println("0000004");
        //目前找个合适的插件包
        if (desc.equals("")) {
            //System.out.println("0000005");
            CtMethod[] allMethods = ctClass.getDeclaredMethods();
            if (allMethods != null) {
                for (CtMethod method : allMethods) {
                    if (method != null && !method.isEmpty() && method.getName().equals(methodName))
                        //System.out.println("xxxx" + method.getName());
                    methods.add(method);
                }
            }
        } else {
            //System.out.println("0000006");
            CtMethod ctMethod = ctClass.getMethod(methodName, desc);
            if (ctMethod != null && !ctMethod.isEmpty()) {
                //System.out.println("xxxx111" + methodName);
                methods.add(ctClass.getMethod(methodName, desc));
            }

        }

        return methods;

    }

    //初始化参数有两种情况，一种带参数，一种不带参数的
    private LinkedList<CtBehavior> getConstrutor(CtClass ctClass, String desc) {
        LinkedList<CtBehavior> methods = new LinkedList<CtBehavior>();
        if (desc.equals("")) {
            Collections.addAll(methods, ctClass.getDeclaredConstructors());
        } else {
            try {
                //一类中同名方法很多
                methods.add(ctClass.getConstructor(desc));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return methods;
    }

}
