package com.hook;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class UriPath extends AbstractClassHook implements LoadHookClass {

    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool,
                             CtClass ctClass, ClassLoader loader) {
        byte[] bytesCode = classfileBuffer;

        try {
            if (className.contains("StandardEngineValve")) {
                System.out.println("[Agent] StandardEngineValve is matched!");
                ctClass = classPool.makeClass(new ByteArrayInputStream(bytesCode));
                String src_before = ""
                        + "String jvmRoute = System.getProperty(\"jvmRoute\");"
                        + "String hostIp=$1.getServerName();"
                        + "String sessionId = $1.getSession().getId();"
                        + "String uri = $1.getRequestURI();"
                        + "String host=$1.getHost().getAppBase();"
                        + "System.out.println(\"{"
                        + "server: '\" + jvmRoute + \"' "
                        + ", hostip: '\"+hostIp+\"'"
                        + ", sessionId: '\" + sessionId + \"' "
                        + ", uri: '\" + uri + \"' "
                        + ", host: '\" + host + \"' "
                        + ", stTime: '\" + System.currentTimeMillis() + \"' "
                        + "}\");";
                // System.out.println("2");
                insertBefore(ctClass, "invoke", "", src_before);
                // System.out.println("3");
                String src_after = ""
                        + "String jvmRoute = System.getProperty(\"jvmRoute\");"
                        + "String sessionId = $1.getSession().getId();"
                        //+ "String hostIp=$1.getServerName();"
                        + "String uri = $1.getRequestURI();"
                        + "System.out.println(\"{"
                        + "server: '\" + jvmRoute + \"' "
                        //+ ", hostip: '\"+hostIp+\"'"
                        + ", sessionId: '\" + sessionId + \"' "
                        + ", uri: '\" + uri + \"' "
                        + ", edTime: '\" + System.currentTimeMillis() + \"' "
                        + ", status: '\" + $2.getStatus() + \"' "
                        + "}\");";


                bytesCode = ctClass.toBytecode();

            } else {
                return bytesCode;
            }
        } catch (IOException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null)
                ctClass.detach();
        }
        return bytesCode;
    }

}
