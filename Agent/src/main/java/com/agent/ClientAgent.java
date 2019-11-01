package com.agent;

import org.apache.log4j.Logger;

import java.lang.instrument.Instrumentation;

public class ClientAgent {

    // private static final  Logger logger=
    //private static final Logger logger = Logger.getLogger(ClientAgent.class.getName());

    /*
     *启动时加载的agent入口方法
     */
    public static void premain(String args, Instrumentation instrumentation) {

        //解决我们jdk中jar被优先加载
        //JarPath.addJarToBootstrap(instrumentation);
        main(args, instrumentation, true);
        /*
        Class[] classes=instrumentation.getAllLoadedClasses();
        for(int i=0;i<classes.length;i++)
        {
            System.out.println("已被加载："+classes[i]);
        }

       */

    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        main(args, instrumentation, false);

    }


    private static void main(String args, Instrumentation instrumentation, boolean b) {

        if (b) {
            //logger.info("Static Agent start!");
            //System.out.println("Static Agent start!");
            instrumentation.addTransformer(new TransformerClass());
            //System.out.println("Static Agent end!");

        }


    }
    //maven  好贴
    //https://blog.csdn.net/SilenceCarrot/article/details/78216229
    // mvn assembly:assembly
    //-Dlog4j.configuration=file:/Users/tao/Downloads/web_hack/vulns/src/log4j.properties
    //-javaagent:"/Users/tao/Downloads/Agent/target/Agent-jar-with-dependencies.jar"
    //https://blog.csdn.net/xiaoguang44/article/details/80656634 解决mysql Client does not support authentication protocol requested by server;

}
