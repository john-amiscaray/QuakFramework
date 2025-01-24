package io.john.amiscaray.quak.generator.test.jpms;

import io.john.amiscaray.quak.generator.jpms.ModuleInfoWriter;
import io.john.amiscaray.quak.generator.model.VisitedSourcesState;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static io.john.amiscaray.quak.generator.test.assertions.TestSourceUtil.parsedClassOrInterfaceDeclarationOf;
import static io.john.amiscaray.quak.generator.test.stub.MockSource.*;
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
                "io.john.amiscaray",
                "io.john.amiscaray.controllers",
                null

        );

        assertThat(
                moduleInfoWriter.writeModuleInfo(),
                equalToCompressingWhiteSpace("""
                module io.john.amiscaray {
                    exports io.john.amiscaray.controllers to quak.framework.core, quak.framework.web;
                   \s
                    opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                    opens io.john.amiscaray.quak.data.di to quak.framework.core;
                    opens io.john.amiscaray.domain to quak.framework.core;
                    opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                   \s
                    requires quak.framework.core;
                    requires quak.framework.data;
                    requires quak.framework.generator.model;
                    requires quak.framework.web;
                    requires quak.framework.web.model;
                    requires jakarta.persistence;
                    requires static lombok;
                    requires org.reflections;

                }
                """)
        );
    }

    @Test
    public void testWritesModuleInfoForStudentAndEmployeeStubsWithTargetControllerPackageSameAsRootPackage() {
        var moduleInfoWriter = new ModuleInfoWriter(
                mockFinalVisitedSourcesStatue(),
                "io.john.amiscaray",
                "io.john.amiscaray",
                null

        );

        assertThat(
                moduleInfoWriter.writeModuleInfo(),
                equalToCompressingWhiteSpace("""
                module io.john.amiscaray {
                    exports io.john.amiscaray to quak.framework.core, quak.framework.web;
                   \s
                    opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                    opens io.john.amiscaray.quak.data.di to quak.framework.core;
                    opens io.john.amiscaray.domain to quak.framework.core;
                    opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                   \s
                    requires quak.framework.core;
                    requires quak.framework.data;
                    requires quak.framework.generator.model;
                    requires quak.framework.web;
                    requires quak.framework.web.model;
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
                "io.john.amiscaray.http",
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
                             exports io.john.amiscaray.http to quak.framework.core, quak.framework.web;
                            \s
                             opens io.john.amiscaray.stub.model to com.fasterxml.jackson.databind;
                             opens io.john.amiscaray.quak.data.di to quak.framework.core;
                             opens io.john.amiscaray.domain to quak.framework.core;
                             opens io.john.amiscaray.stub.data to org.hibernate.orm.core;
                            \s
                             requires quak.framework.core;
                             requires quak.framework.data;
                             requires quak.framework.generator.model;
                             requires quak.framework.web;
                             requires quak.framework.web.model;
                             requires jakarta.persistence;
                             requires static lombok;
                             requires org.reflections;
                        }
                        """)
        );

    }

}
