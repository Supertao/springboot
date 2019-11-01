package com.hook.server;


import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import org.apache.catalina.core.ApplicationFilterChain;

public class HookHandler {

    public static void showChain(Object object)
    {

        if(object instanceof ApplicationFilterChain)
        {
            ApplicationFilterChain applicationFilterChain=(ApplicationFilterChain)object;

            System.out.println("tomcatChain:"+applicationFilterChain+":"+applicationFilterChain.INCREMENT);
        }


    }


}
