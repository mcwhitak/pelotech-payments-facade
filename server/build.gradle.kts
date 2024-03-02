plugins {
  id("java")
  id("io.micronaut.openapi") version("4.3.4")
  id("io.micronaut.application") version ("4.3.4")
  id("io.micronaut.test-resources") version ("4.3.4")
  id("dev.monosoul.jooq-docker") version ("6.0.0")
  id("com.dorongold.task-tree") version ("2.1.1")
}

java {
  toolchain {
    version = JavaLanguageVersion.of(21)
  }
}

testing {
  suites {
    register<JvmTestSuite>("integrationTest") {
      dependencies {
        implementation(project())

        implementation("io.micronaut:micronaut-http-client")
        implementation("io.micronaut.test:micronaut-test-junit5")
        implementation("io.micronaut.testresources:micronaut-test-resources-extensions-junit-platform")
        implementation("org.jooq:jooq")
      }
    }
  }
}

application {
  mainClass.set("com.whitaker.payments.Application")
}

micronaut {
  version("4.3.4")
  runtime("netty")
  testRuntime("junit5")
  processing {
    incremental(true)
    annotations("com.whitaker.payments.generated.*")
    sourceSets(sourceSets.findByName("integrationTest"))

    testResources {
      additionalModules.add("jdbc-postgresql")
    }
  }

  openapi {
    server(file("${projectDir}/api.yaml")) {
      apiPackageName.set("com.whitaker.payments.generated.api")
      modelPackageName.set("com.whitaker.payments.generated.model")
      controllerPackage.set("com.whitaker.payments")

      useReactive.set(false)
      useOptional.set(true)
      typeMapping.put("Double", "java.math.BigDecimal")
      typeMapping.put("Float", "java.math.BigDecimal")

    }
  }
}

tasks {
  generateJooqClasses {
    basePackageName.set("com.whitaker.payments.generated.database");
  }
}

dependencies {
  implementation("io.micronaut.serde:micronaut-serde-jackson")
  implementation("io.micronaut:micronaut-http-client")
  implementation("io.micronaut:micronaut-runtime")
  implementation("io.micronaut.flyway:micronaut-flyway")
  implementation("io.micronaut.sql:micronaut-jdbc-hikari")
  implementation("io.micronaut.sql:micronaut-jooq")
  implementation("io.micronaut.validation:micronaut-validation")
  implementation("org.jooq:jooq")

  testImplementation("org.mockito:mockito-junit-jupiter")

  jooqCodegen("org.postgresql:postgresql:42.5.4")

  runtimeOnly("ch.qos.logback:logback-classic")
  runtimeOnly("org.yaml:snakeyaml")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")
}
