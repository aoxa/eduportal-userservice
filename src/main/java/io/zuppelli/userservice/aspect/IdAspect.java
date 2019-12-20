package io.zuppelli.userservice.aspect;

import io.zuppelli.userservice.annotation.GenerateUUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.UUID;

@Aspect
@Component
public class IdAspect {
    @Before("execution(* io.zuppelli.userservice.repository.*.save(..))")
    public void updateId(JoinPoint joinPoint) throws Exception {
        Object entity = joinPoint.getArgs()[0];
        Class clazz = entity.getClass();

        Annotation[] annotations = clazz.getAnnotationsByType(GenerateUUID.class);

        if(annotations.length > 0) {
            GenerateUUID annotation = (GenerateUUID)annotations[0];

            if(null == clazz.getMethod(annotation.valueMethodName()).invoke(entity)) {
                clazz.getMethod(annotation.updateMethodName(), UUID.class).invoke(entity, UUID.randomUUID());
            }
        }
    }
}
