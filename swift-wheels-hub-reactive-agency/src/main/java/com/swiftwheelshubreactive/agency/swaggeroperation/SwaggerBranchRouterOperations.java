package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.service.BranchService;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations(
        {
                @RouterOperation(method = RequestMethod.GET, beanClass = BranchService.class, beanMethod = "findAllBranches"),
                @RouterOperation(method = RequestMethod.GET, path = "/filter/{filter}", beanClass = BranchService.class, beanMethod = "findBranchByFilterInsensitiveCase"),
                @RouterOperation(method = RequestMethod.GET, path = "/count", beanClass = BranchService.class, beanMethod = "countBranches"),
                @RouterOperation(method = RequestMethod.GET, path = "/{id}", beanClass = BranchService.class, beanMethod = "findBranchById"),
                @RouterOperation(method = RequestMethod.POST, beanClass = BranchService.class, beanMethod = "saveBranch"),
                @RouterOperation(method = RequestMethod.PUT, path = "/{id}", beanClass = BranchService.class, beanMethod = "updateBranch"),
                @RouterOperation(method = RequestMethod.DELETE, path = "/{id}", beanClass = BranchService.class, beanMethod = "deleteBranchById")
        }
)
public @interface SwaggerBranchRouterOperations {
}
