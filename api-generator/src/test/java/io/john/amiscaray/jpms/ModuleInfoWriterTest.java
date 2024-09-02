package io.john.amiscaray.jpms;

import io.john.amiscaray.model.VisitedSourcesState;
import io.john.amiscaray.stub.data.EmployeeTableEntry;
import io.john.amiscaray.stub.data.StudentTableEntry;
import io.john.amiscaray.stub.model.Employee;
import io.john.amiscaray.stub.model.Student;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static io.john.amiscaray.assertions.TestSourceUtil.parsedClassOrInterfaceDeclarationOf;
import static io.john.amiscaray.stub.MockSource.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.mockito.Mockito.mock;

public class ModuleInfoWriterTest {

    public VisitedSourcesState mockFinalVisitedSourcesStatue() {
        return new VisitedSourcesState(new HashMap<>(), List.of(
                parsedClassOrInterfaceDeclarationOf(studentRestModelSourceCode()),
                parsedClassOrInterfaceDeclarationOf(employeeRestModelSourceCode())
        ), List.of(
                parsedClassOrInterfaceDeclarationOf(studentTableSourceCode()),
                parsedClassOrInterfaceDeclarationOf(employeeTableSourceCode())
        ));
    }

    @Test
    public void testWritesModuleInfoForStudentAndEmployeeStubs() {
        var moduleInfoWriter = new ModuleInfoWriter(
                mockFinalVisitedSourcesStatue(),
                "io.john.amiscaray"
        );

        assertThat(
                moduleInfoWriter.writeModuleInfo(),
                equalToCompressingWhiteSpace("""
                module io.john.amiscaray {
                
                    exports io.john.amiscaray.controllers to backend.framework.core, backend.framework.web;
                    
                    opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                    opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                    
                    requires backend.framework.core;
                    requires backend.framework.data;
                    requires backend.framework.generator;
                    requires backend.framework.web;
                    requires jakarta.persistence;
                    requires static lombok;
                    requires org.reflections;
                
                }
                """)
        );
    }

    @Test
    public void testWritesModuleInfoStatementsFromTemplate() {
        var moduleInfoWriter = new ModuleInfoWriter(
                mockFinalVisitedSourcesStatue(),
                "io.john.amiscaray",
                """
                        module my.module {
                        
                            requires org.slf4j;
                        
                        }
                        """
        );

        assertThat(
                moduleInfoWriter.writeModuleInfo(),
                equalToCompressingWhiteSpace("""
                        module my.module {
                        
                            requires org.slf4j;
                            // GENERATED SOURCES:
                            exports io.john.amiscaray.controllers to backend.framework.core, backend.framework.web;
                    
                            opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                            opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                            
                            requires backend.framework.core;
                            requires backend.framework.data;
                            requires backend.framework.generator;
                            requires backend.framework.web;
                            requires jakarta.persistence;
                            requires static lombok;
                            requires org.reflections;
                        }
                        """)
        );

    }

}
