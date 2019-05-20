package com.xiyoumusic.app.aops;

import com.xiyoumusic.app.utils.NoDoubleClickUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AspectTest {
    final String TAG = AspectTest.class.getSimpleName();

    private boolean isDoubleClick = false;

    @Before("execution(@com.xiyoumusic.app.pointcuts.DoubleClick  * *(..))")
    public void beforeEnableDoubleClcik(JoinPoint joinPoint) throws Throwable {
        isDoubleClick = true;
    }

    @Around("execution(* android.view.View.OnClickListener.onClick(..))")
    public void onClickLitener(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        if (!NoDoubleClickUtils.isDoubleClick()) {
//            proceedingJoinPoint.proceed();
//        }

        if (isDoubleClick || !NoDoubleClickUtils.isDoubleClick()) {
            proceedingJoinPoint.proceed();
            isDoubleClick = false;
        }
    }


}
