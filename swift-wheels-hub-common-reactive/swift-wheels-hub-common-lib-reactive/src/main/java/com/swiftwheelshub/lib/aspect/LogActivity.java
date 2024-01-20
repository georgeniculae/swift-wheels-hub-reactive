package com.swiftwheelshub.lib.aspect;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface LogActivity {

    String[] sentParameters() default StringUtils.EMPTY;

    String activityDescription() default StringUtils.EMPTY;

}
