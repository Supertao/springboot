package com.hook;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.*;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XXEHook extends AbstractClassHook implements LoadHookClass {
    private static final Logger logger= Logger.getLogger(AbstractClassHook.class.getName());
    private static String doctype="http://apache.org/xml/features/disallow-doctype-decl";
    private static String general_entities="http://xml.org/sax/features/external-general-entities";
    private static String parameter_entities="http://xml.org/sax/features/external-parameter-entities";



    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool,
                             CtClass ctClass, ClassLoader loader) {

        byte[] bytesCode = classfileBuffer;

        //测试xxe
        if (className.equals("com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl")
                || className.equals("org.apache.xerces.util.XMLResourceIdentifierImpl")) {
            System.out.println("[Agent] jXMLResourceIdentifierImpl is matched!");
            // logger.info("[Agent] xxe is matched!");
            if (classPool != null) {
                try {
                    ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    //CtBehavior[] methods = ctClass.getDeclaredBehaviors();
                    String src = "" +
                            "if($4!=null)System.out.println(\"args4 \"+$4);";
                    insertBefore(ctClass, "setValues",
                            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
                            , src);
                    classfileBuffer = ctClass.toBytecode();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        //XMLInputFactory
        if(className.equals("com.sun.xml.internal.stream.XMLInputFactoryImpl"))
        {
            System.out.println("[Agent] org.dom4j.io.SAXReader is matched!");
            if (classPool != null) {
                try {
                    ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    xmlInputFactoryImplRead(ctClass);
                    classfileBuffer = ctClass.toBytecode();
                } catch (IOException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }

        }

        if (className.equals("org.dom4j.io.SAXReader")) {

            System.out.println("[Agent] org.dom4j.io.SAXReader is matched!");
            if (classPool != null) {
                try {
                    ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    SaxReaderread(ctClass);
                    classfileBuffer = ctClass.toBytecode();
                } catch (IOException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }

        if (className.equals("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl")) {

            System.out.println("[Agent] DocumentBuilderFactoryImpl is matched!");
            if (classPool != null) {
                try {
                    ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    classPool.importPackage("java.util.Map");
                    CtBehavior[] ctBehaviors=ctClass.getDeclaredBehaviors();

                    for(CtBehavior ctBehavior:ctBehaviors)
                    {
                        System.out.println("xxx:"+ctBehavior);
                    }
                    DocumentBuilderFactoryImpl(ctClass);
                    classfileBuffer = ctClass.toBytecode();
                } catch (IOException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }
/*
        if (className.equals("javax.xml.parsers.DocumentBuilderFactory")) {

            System.out.println("[Agent] DocumentBuilderFactory is matched!");
            if (classPool != null) {
                try {
                    ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    DocumentBuilderFactoryImpl(ctClass);
                    classfileBuffer = ctClass.toBytecode();
                } catch (IOException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }
*/
        return classfileBuffer;
    }

    private void DocumentBuilderFactoryImpl(CtClass ctClass) {
        /*
        try {
            String src = "" +
                    "System.out.println(\"args1 \"+$1+\"args2 \"+$2);";
            insertBefore(ctClass,"setFeature","(Ljava/lang/String;Z)V",src);
            System.out.println("get DocumentBuilderFactoryImpl method!");

            System.out.println("get DocumentBuilderFactoryImpl end!");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }*/

        String src="" +
               // "System.out.println(\"args2 \"+$0+\" \"+this.features.size());"+""+
        "for (Map.Entry<String, Boolean> entry : features.entrySet()){System.out.println(\"999\"+entry.getKey());}";
               // "for(int i=0;i<this.features.size();i++){System.out.println(this.features.get(i));}";
        try {
            insertAfter(ctClass,"newDocumentBuilder","()Ljavax/xml/parsers/DocumentBuilder;",src);
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }

    }

    private void xmlInputFactoryImplRead(CtClass ctClass) {

        try {
            CtClass[] paramTypes = {ctClass.getClassPool().get(InputSource.class.getName())};
            CtMethod method = ctClass.getDeclaredMethod("getXMLStreamReaderImpl", paramTypes);
            System.out.println("get read method!");
            method.insertAfter("{" + this.getClass().getName() + ".handRead(this);}");
            System.out.println("get read method end");
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    private void SaxReaderread(CtClass ctClass) {

        try {
            CtClass[] paramTypes = {ctClass.getClassPool().get(InputSource.class.getName())};
            CtMethod method = ctClass.getDeclaredMethod("read", paramTypes);
            System.out.println("get read method!");
            method.insertAfter("{" + this.getClass().getName() + ".handRead($0.getXMLReader());}");
            System.out.println("get read method end");
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }

    }

    public static void handRead(Object obj) {
        try {
            if(obj instanceof XMLInputFactory)
            {
                XMLInputFactory factory=(XMLInputFactory) obj;

                if(!Boolean.FALSE.equals(factory.getProperty("javax.xml.stream.SUPPORT_DTD"))||!Boolean.FALSE.equals(factory.getProperty("javax.xml.stream.isSupportingExternalEntities")))
                {
                    System.out.print("Find XXE!");
                }

            }else {
                Class xmlParserClass = obj.getClass();
                Method method = xmlParserClass.getMethod("getFeature", new Class[]{String.class});
                boolean disallow_doctype_decl = (Boolean) method.invoke(obj, doctype);
                boolean external_general_entities = (Boolean) method.invoke(obj, general_entities);
                boolean external_parameter_entities = (Boolean) method.invoke(obj, parameter_entities);

                if (!disallow_doctype_decl && (external_general_entities || external_parameter_entities)) {
                    logger.info("Find XXE!!!");
                    logger.info("disallow_doctype_decl:"+disallow_doctype_decl);
                    logger.info("external_general_entities:"+disallow_doctype_decl);
                    logger.info("external_parameter_entities:"+external_parameter_entities +" "+obj.getClass().getName());
                    System.out.print("Find XXE!");
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }


}
