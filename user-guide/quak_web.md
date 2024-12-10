# Quak Web

Quak offers a feature-rich API for handling web requests using a `WebApplication` class. As mentioned above, Quak
offers a `Application` class used to represent the running application. `quak.framework.web`'s `io.john.amiscaray.quak.web.application.WebApplication` extends this class to implement web-specific functionality. This is used to configure
implementations of a `io.john.amiscaray.quak.web.controller.PathController` interface to handle requests to specific
endpoints. As a developer, you can instantiate an instance of this `WebApplication` class yourself and pass it a
configuration of `PathController`s to get your web application started. However, as the more useful approach, you
can utilize an annotation-based approach (similar to Spring Boot) where you can define annotated controller classes and
methods. Under the hood, the framework will then use your annotated classes and methods to configure a
`WebApplication` for you. The sections below will cover this annotation-based approach as I figure this will be the
approach you use most often.

## Quak Controllers

Similar to Spring Boot, Quak allows you to define web controllers using annotated classes and methods. Below is an example of a Quak controller implementing a CRUD API:

```java
package io.john.amiscaray.test.controllers;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.http.request.DynamicPathRequest;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.request.RequestMethod;
import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.test.models.StudentDTO;
import io.john.amiscaray.test.orm.StudentTableEntry;
import io.john.amiscaray.quak.data.DatabaseProxy;
import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;

import java.util.HashMap;
import java.util.List;

@Controller
public class StudentController {

    private DatabaseProxy databaseProxy;

    @Instantiate
    public StudentDTOController(DatabaseProxy databaseProxy) {
        this.databaseProxy = databaseProxy;
    }

    @Handle(method = RequestMethod.POST, path = "/studentdto")
    public Response<Void> saveStudentDTO(Request<StudentDTO> request) {
        var studentdto = request.body();
        var entity = StudentDTO.createDataEntry(studentdto);
        databaseProxy.persist(entity);

        var headers = new HashMap<String, String>();
        headers.put("Location", "/studentdto/" + entity.getId());

        return new Response(headers, 201, null);
    }

    @Handle(method = RequestMethod.GET, path = "/studentdto")
    public Response<List<StudentDTO>> getAllStudentDTO(Request<Void> request) {
        return Response.of(
            databaseProxy.queryAll(StudentTableEntry.class)
                .stream()
                .map(StudentTableEntry::createDTO)
                .toList()
        );
    }

    @Handle(method = RequestMethod.GET, path = "/studentdto/{id}Long")
    public Response<StudentDTO> getStudentDTO(DynamicPathRequest<Void> request) {
        try {
            var id = Long.parseLong(request.pathVariables().get("id"));
            var fetched = databaseProxy.fetchById(id, StudentTableEntry.class);

            if (fetched == null) {
                return new Response(404, null);
            }

            return Response.of(StudentTableEntry.createDTO(fetched));
        } catch (NumberFormatException e) {
            return new Response(404, null);
        }
    }

    @Handle(method = RequestMethod.DELETE, path = "/studentdto/{id}Long")
    public Response<Void> deleteStudentDTO(DynamicPathRequest<Void> request) {
        try {
            var id = Long.parseLong(request.pathVariables().get("id"));

            try {
                databaseProxy.delete(id, StudentTableEntry.class);
            } catch (IllegalArgumentException e) {
                return new Response(404, null);
            }

            return new Response(204, null);
        } catch (NumberFormatException e) {
            return new Response(404, null);
        }
    }

    @Handle(method = RequestMethod.PUT, path = "/studentdto/{id}Long")
    public Response<Void> putStudentDTO(DynamicPathRequest<StudentDTO> request) {
        try {
            var id = Long.parseLong(request.pathVariables().get("id"));

            var entity = StudentDTO.createDataEntry(request.body());
            entity.setId(id);
            var isUpdate = databaseProxy.put(entity, id, StudentTableEntry.class);

            if (isUpdate) {
                return new Response(204, null);
            } else {
                var headers = new HashMap<String, String>();
                headers.put("Location", "/studentdto/" + entity.getId());

                return new Response(headers, 201, null);
            }
        } catch (NumberFormatException e) {
            return new Response(404, null);
        }
    }

    @Handle(method = RequestMethod.PATCH, path = "/studentdto/{id}Long")
    public Response<Void> patchStudentDTO(DynamicPathRequest<StudentDTO> request) {
        try {
            var id = Long.parseLong(request.pathVariables().get("id"));

            var entity = StudentDTO.createDataEntry(request.body());
            entity.setId(id);
            var foundEntity = databaseProxy.patch(entity, id, StudentTableEntry.class);

            if (foundEntity) {
                return new Response(204, null);
            } else {
                return new Response(404, null);
            }
        } catch (NumberFormatException e) {
            return new Response(404, null);
        }
    }
    
}
```

In this example, each method handling web requests accepts some type of request and returns a response. Requests and
responses have generic type arguments of the type of the request and response bodies respectively. Additionally, requests can contain path arguments which can be accessed using instances of `DynamicPathRequest`. These can be retrieved through maps with keys for the path variables and values corresponding to the parts of the actual URL. Path variables are defined in the `Handle` annotation's `path` parameter where path variable names are wrapped in curly brackets. The type of the path variable can be declared immediately after the closing curly bracket.

## Starting a Web Application

After defining controllers and handler methods, the following `main` method can be used to start your web application:

```java
package io.john.amiscaray.test;

import io.john.amiscaray.quak.web.application.WebStarter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        var application = WebStarter.beginWebApplication(Main.class, args)
                .get(10, TimeUnit.SECONDS);

        application.await();
    }
}
```

## WebConfig

Lastly, `quak.framework.web` provides a `WebConfig` class. This can be added to the application context for `quak.framework.web` to read to configure your web application. Currently, the only config this contains is a `exceptionHttpStatusMapping` map. This map associates exceptions with different HTTP response codes so that if your handler throws an exception, a specific HTTP response code can be returned.
