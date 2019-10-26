package com.hook.File;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

//https://blog.csdn.net/chenyi8888/article/details/7070270

public class FileList  extends AbstractClassHook implements LoadHookClass {
    final static Logger LOGGER=Logger.getLogger(FileList.class.getName());
    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool, CtClass ctClass, ClassLoader loader) {
      //
        byte[] bytesCode=classfileBuffer;

        if("java.io.File".equals(className))
        {
            LOGGER.info("File Method Enter!");
            //注入File类的监控
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtBehavior[] methods = ctClass.getDeclaredBehaviors();
                for(CtBehavior method:methods)
                {
                    System.out.println("File method"+method);
                }
                bytesCode=ctClass.toBytecode();
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
