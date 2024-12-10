# Quak Generator API

Quak offers support for generating API endpoints using annotated classes. This section will talk about how to achieve this to speed up your API development.

## Quak API Generator Model Module

The `quak.framework.generator-model` module define annotations that gives hints to the [Quak generator maven plugin](#quak-api-generator-maven-plugin) on how to generate code for you API endpoints. The following list explains each annotation and their usage:

- `io.john.amiscaray.quak.generator.api.RestModel`: annotates a DTO class for HTTP request or response bodies. Has a `dataClass` argument that links to a corresponding hibernate entity class.
- `io.john.amiscaray.quak.generator.api.ModelGenerator`: annotates a static method within a hibernate entity that takes an instance of the entity class and returns an instance of a corresponding DTO class.
- `io.john.amiscaray.quak.generator.api.EntityGenerator`: annotates a static method within a DTO class that takes an instance of that DTO class and returns an instance of a corresponding entity class.
- `io.john.amiscaray.quak.generator.api.APIQuery`: annotates a static method in a hibernate entity class that returns an `io.john.amiscaray.quak.data.query.DatabaseQuery`. These may accept a `DynamicPathRequest` parameter.
- `io.john.amiscaray.quak.generator.api.APINativeQuery`: annotates a static method in a hibernate entity class that returns an `io.john.amiscaray.quak.data.query.APINativeQuery`. These may accept a `DynamicPathRequest` parameter.

## Quak API Generator Maven Plugin

The Quak API Generator plugin allows for the generation of HTTP controllers based on your defined hibernate entities and DTO classes. By annotating classes in your backend using annotations from the [Quak API generator Model Module](#quak-api-generator-model-module), this plugin is able to get hints as to how your controllers will look like. Using this information, Quak will generator your controllers based on HTTP standards. Below is an example:

### Hibernate Entity

```java
package io.john.amiscaray.test.orm;

import io.john.amiscaray.quak.data.query.DatabaseQuery;
import io.john.amiscaray.quak.data.query.NativeQuery;
import io.john.amiscaray.quak.generator.api.APINativeQuery;
import io.john.amiscaray.quak.generator.api.APIQuery;
import io.john.amiscaray.quak.generator.api.ModelGenerator;
import io.john.amiscaray.quak.http.request.DynamicPathRequest;
import io.john.amiscaray.test.models.StudentDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Map;

import static io.john.amiscaray.quak.data.query.QueryCriteria.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class StudentTableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String major;
    private String name;
    private Float gpa;

    @ModelGenerator
    public static StudentDTO createDTO(StudentTableEntry studentDataEntry) {
        return new StudentDTO(studentDataEntry.name, studentDataEntry.major, studentDataEntry.gpa);
    }

    @APIQuery(path="/cs")
    public static DatabaseQuery queryStudentsInCS(DynamicPathRequest<Void> request) {
        return DatabaseQuery.builder()
                .withCriteria(valueOfField("major", is("cs")))
                .build();
    }

    @APIQuery(path="/gpa/{gpa}")
    public static DatabaseQuery queryStudentsWithGPAGreaterThan(DynamicPathRequest<Void> request) {
        return DatabaseQuery.builder()
                .withCriteria(valueOfField("gpa", isGreaterThan(Float.parseFloat(request.pathVariables().get("gpa")))))
                .build();
    }

    @APINativeQuery(path="/major/{name}")
    public static NativeQuery queryStudentsByMajor(DynamicPathRequest<Void> request) {
        return new NativeQuery(
                "FROM StudentTableEntry WHERE major = :name",
                Map.of("name", request.pathVariables().get("name"))
        );
    }


}
```

### DTO Class

```java
package io.john.amiscaray.test.models;

import io.john.amiscaray.quak.generator.api.EntityGenerator;
import io.john.amiscaray.quak.generator.api.RestModel;
import io.john.amiscaray.test.orm.StudentTableEntry;
import lombok.*;

@RestModel(dataClass = StudentTableEntry.class)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class StudentDTO {

    private String name;
    private String major;
    private Float gpa;

    @EntityGenerator
    public static StudentTableEntry createDataEntry(StudentDTO dto) {
        return new StudentTableEntry(null, dto.major, dto.name, dto.gpa);
    }

}
```

### Generated Controller

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
public class StudentDTOController {

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

    @Handle(method = RequestMethod.GET, path = "/studentdto/cs")
    public Response<List<StudentDTO>> queryStudentsInCS(DynamicPathRequest<Void> request) {
        var query = StudentTableEntry.queryStudentsInCS(request);
        return Response.of(databaseProxy.queryAll(StudentTableEntry.class, query)
            .stream()
            .map(StudentTableEntry::createDTO)
            .toList());
    }
    @Handle(method = RequestMethod.GET, path = "/studentdto/gpa/{gpa}")
    public Response<List<StudentDTO>> queryStudentsWithGPAGreaterThan(DynamicPathRequest<Void> request) {
        var query = StudentTableEntry.queryStudentsWithGPAGreaterThan(request);
        return Response.of(databaseProxy.queryAll(StudentTableEntry.class, query)
            .stream()
            .map(StudentTableEntry::createDTO)
            .toList());
    }


    @Handle(method = RequestMethod.GET, path = "/studentdto/major/{name}")
    public Response<List<StudentDTO>> queryStudentsByMajor(DynamicPathRequest<Void> request) {
        var query = StudentTableEntry.queryStudentsByMajor(request);
        return Response.of(databaseProxy.createSelectionQuery(query, StudentTableEntry.class)
            .getResultList()
            .stream()
            .map(StudentTableEntry::createDTO)
            .toList());
    }

}
```