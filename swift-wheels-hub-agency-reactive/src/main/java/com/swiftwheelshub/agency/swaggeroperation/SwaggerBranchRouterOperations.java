package com.swiftwheelshub.agency.swaggeroperation;

import com.swiftwheelshub.agency.service.BranchService;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(beanClass = BranchService.class, beanMethod = "findAllBranches"),
                @RouterOperation(path = "/filter/{filter}", beanClass = BranchService.class, beanMethod = "findBranchByFilterInsensitiveCase"),
                @RouterOperation(path = "/count", beanClass = BranchService.class, beanMethod = "countBranches"),
                @RouterOperation(path = "/{id}", beanClass = BranchService.class, beanMethod = "findBranchById"),
                @RouterOperation(beanClass = BranchService.class, beanMethod = "saveBranch"),
                @RouterOperation(path = "/{id}", beanClass = BranchService.class, beanMethod = "updateBranch"),
                @RouterOperation(path = "/{id}", beanClass = BranchService.class, beanMethod = "deleteBranchById")
        }
)
public @interface SwaggerBranchRouterOperations {
}
