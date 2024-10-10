package io.john.amiscaray.jpms;

import io.john.amiscaray.model.VisitedSourcesState;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static io.john.amiscaray.assertions.TestSourceUtil.parsedClassOrInterfaceDeclarationOf;
import static io.john.amiscaray.stub.MockSource.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

public class ModuleInfoWriterTest {

    public VisitedSourcesState mockFinalVisitedSourcesStatue() {
        return new VisitedSourcesState(new HashMap<>(), List.of(
                parsedClassOrInterfaceDeclarationOf(studentRestModelSourceCode()),
                parsedClassOrInterfaceDeclarationOf(employeeRestModelSourceCode())
        ), List.of(
                parsedClassOrInterfaceDeclarationOf(studentTableSourceCode()),
                parsedClassOrInterfaceDeclarationOf(employeeTableSourceCode())
        ), List.of(
                parsedClassOrInterfaceDeclarationOf(managedTypeSourceCode()),
                parsedClassOrInterfaceDeclarationOf(dependencyProviderSourceCode())
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
                    
                    // Rules for RestModels
                    opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                    // Rules for Entities
                    opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                    // Rules for DI Components
                    opens io.john.amiscaray.backend.framework.data.di to backend.framework.core;
                    opens io.john.amiscaray.domain to backend.framework.core;
                    
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
                    
                            // Rules for RestModels
                            opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                            // Rules for Entities
                            opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                            // Rules for DI Components
                            opens io.john.amiscaray.backend.framework.data.di to backend.framework.core;
                            opens io.john.amiscaray.domain to backend.framework.core;
                            
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
