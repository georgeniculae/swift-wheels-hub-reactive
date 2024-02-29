package com.swiftwheelshubreactive.agency.swaggeroperation;

import com.swiftwheelshubreactive.agency.service.EmployeeService;
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
                @RouterOperation(method = RequestMethod.GET, beanClass = EmployeeService.class, beanMethod = "findAllEmployees"),
                @RouterOperation(method = RequestMethod.GET, path = "/branch/{id}", beanClass = EmployeeService.class, beanMethod = "findAllEmployees"),
                @RouterOperation(method = RequestMethod.GET, path = "/filter/{filter}", beanClass = EmployeeService.class, beanMethod = "findEmployeeByFilterInsensitiveCase"),
                @RouterOperation(method = RequestMethod.GET, path = "/count", beanClass = EmployeeService.class, beanMethod = "countEmployees"),
                @RouterOperation(method = RequestMethod.GET, path = "/{id}", beanClass = EmployeeService.class, beanMethod = "findEmployeeById"),
                @RouterOperation(method = RequestMethod.POST, beanClass = EmployeeService.class, beanMethod = "saveEmployee"),
                @RouterOperation(method = RequestMethod.PUT, beanClass = EmployeeService.class, beanMethod = "updateEmployee"),
                @RouterOperation(method = RequestMethod.DELETE, beanClass = EmployeeService.class, beanMethod = "deleteEmployeeById")
        }
)
public @interface SwaggerEmployeeRouterOperations {
}
