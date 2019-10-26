package com.Exception;

import javassist.*;

public class Exception {


    /**
     * 设置方法异常捕获逻辑
     * @param pool			:
     * @param ctMethod		:
     * @throws NotFoundException  if not found
     * @throws CannotCompileException if can't compile
     */
    public static void methodCatch(ClassPool pool, CtMethod ctMethod) throws NotFoundException, CannotCompileException {

        // 构造异常处理逻辑
        CtClass etype = pool.get("java.lang.Exception");
        ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);

    }

}
