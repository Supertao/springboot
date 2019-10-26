package com.agent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;

public class JarPath {

    //添加jar文件到jdk路径下，优先加载

    public static void addJarToBootstrap(Instrumentation instrumentation)

    {

        String localPath=getLocalJar();
        try {
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(localPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //获取当前所在jar包的路径
    public static String getLocalJar()
    {
        URL localUrl=ClientAgent.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.print(localUrl);
        String path=null;

        try {
            path= URLDecoder.decode(localUrl.getFile().replace("+", "%2B"), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return path;

    }
    public static void  main(String[] args)
    {

        getLocalJar();
    }





}
