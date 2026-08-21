package com.pharmaguard.api.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArchitectureRulesTest {

    private static final String AUTH_DOMAIN = "com.pharmaguard.api.auth.domain..";
    private static final String AUTH_APPLICATION = "com.pharmaguard.api.auth.application..";
        private static final String AUTH_ADAPTERS_IN = "com.pharmaguard.api.auth.adapters.in..";
    private static final String SHARED_DOMAIN = "com.pharmaguard.api.shared.domain..";
    private static final String SHARED_CONFIG = "com.pharmaguard.api.shared.config..";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.pharmaguard.api");

    @Test
    void domain_should_not_depend_on_frameworks_or_infrastructure() {
        ArchRule rule = classes()
                .that().resideInAnyPackage(AUTH_DOMAIN)
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        AUTH_DOMAIN,
                        "java..",
                        "jakarta..",
                        "org.slf4j.."
                );

        rule.check(CLASSES);
    }

    @Test
        void application_should_not_depend_on_adapters() {
        ArchRule rule = classes()
                .that().resideInAnyPackage(AUTH_APPLICATION)
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        AUTH_APPLICATION,
                        AUTH_DOMAIN,
                        SHARED_DOMAIN,
                        "java..",
                        "jakarta..",
                        "org.slf4j.."
                );

        rule.check(CLASSES);
    }

    @Test
    void adapters_in_should_not_depend_on_adapters_out() {
        ArchRule rule = classes()
                .that().resideInAnyPackage(AUTH_ADAPTERS_IN)
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        AUTH_ADAPTERS_IN,
                        AUTH_APPLICATION,
                        AUTH_DOMAIN,
                        SHARED_DOMAIN,
                        SHARED_CONFIG,
                        "java..",
                        "jakarta..",
                        "io.swagger.v3..",
                        "org.springframework..",
                        "org.slf4j.."
                );

        rule.check(CLASSES);
    }
}
