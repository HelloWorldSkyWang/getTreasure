package com.king.mavenweb.common;


import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Configuration
public class ExceptionCatch {

	private static Logger logger = LoggerFactory.getLogger(ExceptionCatch.class);
	
	@Pointcut("execution(* com.king.mavenweb.*.*(..))")
	public void execute(){}

	@Around("execute()")
	public Object catContrException(ProceedingJoinPoint pjd) throws Throwable {
		Object o = null;
		//获取所执行的类名
		String className = pjd.getTarget().getClass().getName();
		//获取所执行的方法名
		String methodName = ((MethodSignature)pjd.getSignature()).getMethod().getName();
		//获取request对象
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		try {
			o = pjd.proceed();
		} catch (Exception e) {
			
		}
		return o;
		
	}
}
