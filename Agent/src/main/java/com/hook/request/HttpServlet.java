package com.hook.request;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.UUID;

public class HttpServlet extends AbstractClassHook implements LoadHookClass {
    public static final String reqfix = "req";
    public static final String resfix = "res";

    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool, CtClass ctClass, ClassLoader loader) {
        byte[] bytesCode = classfileBuffer;
        if ("javax.servlet.http.HttpServlet".equals(className)) {
            try {
                String traceId = UUID.randomUUID().toString().replace("-", "");
                classPool.importPackage("javax.servlet.http.HttpServletRequest");
                ctClass = classPool.get(className);
                String methodservice = "service";

                //String outputStr="\nSystem.out.println(\"this method"+methodservice+");";
                //获取修改方法的实例
                CtMethod ctMethod = ctClass.getDeclaredMethod(methodservice);
                //获取参数类型
                CtClass[] parameterCtClasses = new CtClass[ctMethod.getParameterTypes().length];
                for (int i = 0; i < parameterCtClasses.length; i++) {
                    parameterCtClasses[i] = classPool.getCtClass(ctMethod.getParameterTypes()[i].getName());
                    System.out.println("CtClass: " + parameterCtClasses[i].getName());
                }
                String[] parameterNames = new String[parameterCtClasses.length];

                CtClass[] cls = ctMethod.getParameterTypes();

                for (CtClass cc : cls) {
                    //后续可以添加日志
                    System.out.println("CC: " + cc.getName());
                }
                MethodInfo methodInfo = ctMethod.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                TreeMap<Integer, String> sortMap = new TreeMap<Integer, String>();
                for (int i = 0; i < attribute.tableLength(); i++) {
                    sortMap.put(attribute.index(i), attribute.variableName(i));
                }//对应的值转化成Map形式值

                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                //获取参数名称
                parameterNames = Arrays.copyOfRange(sortMap.values().toArray(new String[0]), pos, parameterNames.length + pos);
                //输出方法中所有的参数信息
                StringBuilder builder = new StringBuilder();
                //builder.append("System.out.println(\"traceId is: ").append(traceId).append(".\");");
                String parameterName = "";
                for (int i = 0, j = parameterNames.length; i < j; i++) {
                    parameterName = parameterNames[i];
                    System.out.println(parameterName);
                    //可以使用$1代表第一个参数，$2代表第二个参数
                    if (reqfix.equals(parameterName)) {
                        builder.append("\nHttpServletRequest request1=(HttpServletRequest)req;\n");
                        //HttpServletRequest 请求中的 body 内容仅能调用 request.getInputStream()，
                        // request.getReader()和request.getParameter("key") 方法读取一次
                        //https://www.jianshu.com/p/e4d3cca286e4
                        String request = ""
                                + " , "
                                + "method:'\"+$1.getMethod()+\"'"
                                + ", protocol:'\"+$1.getProtocol()+\"'"
                                + ", sessionId:'\"+$1.getSession().getId()+\"'"
                                //得到访客的ip地址
                                + ", LocalAddr:'\"+$1.getLocalAddr()+\"'"
                                + ", uri:'\"+$1.getRequestURI()+\"'"
                                +",params['\"+request1.getInputStream()+\"'"
                                + ", startTime:'\"+System.currentTimeMillis()+\"'"
                                + "}\");";
                        builder.append("System.out.println(\"{ traceid:").append(traceId).append(request);
                    }
                    if (resfix.equals(parameterName)) {
                        builder.append("\nHttpServletResponse response1 = (HttpServletResponse)res;\n");
                    }
                    builder.append("System.out.println(\"").append(parameterName).append(" is \"+$").append((i + 1)).append(" );");
                    //System.out.println("builder:" + builder.toString());
                }
                ctMethod.insertBefore(builder.toString());
               // System.out.println("xxx End");


                bytesCode = ctClass.toBytecode();

            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
            }
        } else {
            return bytesCode;
        }


        return bytesCode;
    }
}
