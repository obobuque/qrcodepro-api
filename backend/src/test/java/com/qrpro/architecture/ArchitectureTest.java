package com.qrpro.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static final JavaClasses classes = new ClassFileImporter()
            .importPackages("com.qrpro");

    private static final String[] ALLOWED_INFRA_DEPS = {
        "com.qrpro.domain..",
        "com.qrpro.application..",
        "com.qrpro.shared..",
        "com.qrpro.infrastructure..",
        "com.qrpro.config..",
        "java..",
        "javax..",
        "jakarta..",
        "org.springframework..",
        "org.hibernate..",
        "com.google.zxing..",
        "io.minio..",
        "io.lettuce..",
        "io.jsonwebtoken..",
        "io.swagger..",
        "org.slf4j..",
        "lombok..",
        "com.fasterxml.jackson..",
        "com.amazonaws..",
        "org.apache.http.."
    };

    @Test
    void domain_should_not_depend_on_application_or_infrastructure() {
        ArchRule rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.qrpro.application..",
                        "com.qrpro.infrastructure.."
                );
        rule.check(classes);
    }

    @Test
    void application_should_not_depend_on_infrastructure() {
        ArchRule rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    @Test
    void infrastructure_should_depend_on_domain_and_application() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..infrastructure..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(ALLOWED_INFRA_DEPS);
        rule.check(classes);
    }

    @Test
    void no_cycles_between_layers() {
        ArchRule rule = SlicesRuleDefinition.slices()
                .matching("com.qrpro.(*)..")
                .should().beFreeOfCycles();
        rule.check(classes);
    }
}
