package com.agent;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClientAgentAttach {
    private static final String VM_CLASS = "com.sun.tools.attach.VirtualMachine";

    public static File findToToolsJar() {
        File javahomeFile = new File(System.getProperty("java.home"));
        File toolsJarFile = new File(javahomeFile, "lib" + File.separator + "tools.jar");

        if (!toolsJarFile.exists()) {
            if (javahomeFile.getAbsolutePath().endsWith("jre")) {
                javahomeFile = javahomeFile.getParentFile();
                toolsJarFile = new File(javahomeFile, "lib" + File.separator + "tools.jar");
                if (!toolsJarFile.exists()) {
                    //not found
                    toolsJarFile = null;
                }
            } else {

                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    if (javahomeFile.getAbsolutePath().endsWith(File.separator + "Home")) {
                        javahomeFile = javahomeFile.getParentFile();
                        toolsJarFile = new File(javahomeFile, "Classes" + File.separator + "classes.jar");
                        if (!toolsJarFile.exists()) {
                            // not found ...
                            toolsJarFile = null;
                        }
                    }
                }
            }

        }
        return toolsJarFile;
    }

    private static Class<?> loadVirtualMachineClass(final File toolsJar) {
        Class<?> vmClass;
        try {
            vmClass = ClassLoader.getSystemClassLoader().loadClass(VM_CLASS);
            System.out.println(vmClass);
        } catch (ClassNotFoundException cnfe) {
            try {
                vmClass = new URLClassLoader(new URL[]{toolsJar.toURI().toURL()}).loadClass(VM_CLASS);
            } catch (ClassNotFoundException | MalformedURLException e) {
                vmClass = null;
            }
        }
        return vmClass;
    }

    public static void loadAgent() {

        Class<?> vmClass;
        File toolsJarFile = findToToolsJar();
        vmClass = loadVirtualMachineClass(toolsJarFile);
        try {
            if (vmClass != null) {
                //获取用户输入的pid
                VirtualMachine vm = VirtualMachine.attach("pid");

            }
        } catch (AttachNotSupportedException | IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        File toolsJarFile = findToToolsJar();
        loadVirtualMachineClass(toolsJarFile);

    }


}
