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
                @RouterOperation(method = RequestMethod.GET, beanClass = EmployeeService.class, beanMethod = "findAllRentalOffices"),
                @RouterOperation(method = RequestMethod.GET, path = "/office/{name}", params = "name", beanClass = EmployeeService.class, beanMethod = "findRentalOfficesByNameInsensitiveCase"),
                @RouterOperation(method = RequestMethod.GET, path = "/count", beanClass = EmployeeService.class, beanMethod = "countRentalOffices"),
                @RouterOperation(method = RequestMethod.GET, path = "/{id}", params = "id", beanClass = EmployeeService.class, beanMethod = "findRentalOfficeById"),
                @RouterOperation(method = RequestMethod.POST, beanClass = EmployeeService.class, beanMethod = "saveRentalOffice"),
                @RouterOperation(method = RequestMethod.PUT, path = "/{id}", params = "id", beanClass = EmployeeService.class, beanMethod = "updateRentalOffice"),
                @RouterOperation(method = RequestMethod.DELETE, path = "/{id}", params = "id", beanClass = EmployeeService.class, beanMethod = "deleteRentalOfficeById")
        }
)
public @interface SwaggerRentalOfficeRouterOperations {
}
