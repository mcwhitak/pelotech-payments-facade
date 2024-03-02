plugins {
  id("java")
  id("org.openapi.generator") version("7.3.0")
  id("io.micronaut.application") version ("4.3.4")
  id("io.micronaut.test-resources") version ("4.3.4")
}

java {
  toolchain {
    version = JavaLanguageVersion.of(21)
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
  }
}

dependencies {
  annotationProcessor("io.micronaut:micronaut-http-validation")
  annotationProcessor("io.micronaut.security:micronaut-security-annotations")
  annotationProcessor("io.micronaut.serde:micronaut-serde-processor")

  implementation("io.micronaut.serde:micronaut-serde-jackson")
  implementation("io.micronaut:micronaut-http-client")
  implementation("io.micronaut:micronaut-runtime")
  implementation("io.micronaut.security:micronaut-security")
  implementation("io.micronaut.security:micronaut-security-oauth2")
  implementation("io.micronaut.validation:micronaut-validation")

  runtimeOnly("ch.qos.logback:logback-classic")
}

openApiGenerate {
  inputSpec = "${projectDir}/api.yaml"
  generatorName = "java-micronaut-server"

  modelPackage = "com.whitaker.payments.generated.model"
  apiPackage = "com.whitaker.payments.generated.api"

  generateModelTests.set(false)
  generateApiTests.set(false)

  globalProperties.set(mapOf(
    "modelDocs" to "false"
  ))

  configOptions.set(mapOf(
    "generateControllerAsAbstract" to "true",
    "generateSwaggerAnnotations" to "false",
    "reactive" to "false",
    "requiredPropertiesInConstructor" to "true",
    "serializationLibrary" to "micronaut_serde_jackson",
    "useOptional" to "true",
    "useJakartaEe" to "true"
  ))
}

sourceSets["main"].java.srcDir(tasks.openApiGenerate)

