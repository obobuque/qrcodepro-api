package com.qrpro.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.qrpro");

    @Test
    void domain_should_not_depend_on_frameworks() {
        ArchRule rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta..",
                        "org.hibernate..",
                        "io.swagger.."
                );
        rule.check(classes);
    }

    @Test
    void domain_should_not_depend_on_application() {
        ArchRule rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..application..");
        rule.check(classes);
    }

    @Test
    void application_should_not_depend_on_infrastructure() {
        ArchRule rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    @Test
    void infrastructure_should_depend_on_domain_and_application() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..infrastructure..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
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
                        "com.fasterxml.jackson.."
                );
        rule.check(classes);
    }
}
