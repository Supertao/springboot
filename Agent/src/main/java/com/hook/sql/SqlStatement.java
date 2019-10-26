package com.hook.sql;

import com.agent.AbstractClassHook;
import com.agent.LoadHookClass;
import javassist.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SqlStatement extends AbstractClassHook implements LoadHookClass {
    private static final Logger logger = Logger.getLogger(SqlStatement.class.getName());
    String[] METHOD_NAMES = new String[]{"executeQuery", "excuteUpdate"};

    @Override
    public byte[] instrument(String className, byte[] classfileBuffer, ClassPool classPool, CtClass ctClass, ClassLoader loader) {
        byte[] bytesCode = classfileBuffer;
        CtClass statement = null;
        CtClass preparedstatement = null;
        if (className.equals("com.mysql.jdbc.StatementImpl") || "com.mysql.cj.jdbc.StatementImpl".equals(className)) {
            logger.info("executeQuery" + className);
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(bytesCode));
                classPool.importPackage("org.apache.log4j.Logger");
                classPool.importPackage("org.apache.log4j.*");

                StringBuilder sql_execute=new StringBuilder();
                sql_execute.append("org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(java.sql.PreparedStatement.class);");
                sql_execute.append("logger.debug($0);");
                sql_execute.append("logger = null;");

                StringBuilder sql_query= new StringBuilder();
                sql_query.append("org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(java.sql.Statement.class);");
                sql_query.append("logger.warn($1);");
                sql_query.append("logger = null;");

                insertBefore(ctClass,"executeQuery","(Ljava/lang/String;)Ljava/sql/ResultSet;",sql_query.toString());
                insertBefore(ctClass,"excuteUpdate","(Ljava/lang/String;)I",sql_execute.toString());

                System.out.println("xxx ok");
                bytesCode = ctClass.toBytecode();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            }

        }else
        {
            return bytesCode;
        }


        return bytesCode;
    }
}
