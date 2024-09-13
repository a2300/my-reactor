# Generating HTTP clients in Spring Boot application from OpenAPI spec

 - [https://maciejwalkowiak.com/blog/spring-boot-openapi-generate-client/](https://maciejwalkowiak.com/blog/spring-boot-openapi-generate-client/)

## Spring boot + OpenApi Generator + RestClient Demo

https://github.com/maciejwalkowiak/spring-boot-openapi-client-generator-demo

More details in a blog post: maciejwalkowiak.com/blog/spring-boot-openapi-generate-client/

The petclinic-spec.yml is copied from spring-petclinic/spring-petclinic-rest project and updated with security configuration.


Run
```bash
gradlew spring-boot-demo:tasks
```


Output
```
OpenAPI Tools tasks
-------------------
openApiGenerate - Generate code via Open API Tools Generator for Open API 2.0 or 3.x specification documents.
openApiGenerators - Lists generators available via Open API Generators.
openApiMeta - Generates a new generator to be consumed via Open API Generator.
openApiValidate - Validates an Open API 2.0 or 3.x specification document.
```

Copy [openapi.yml](https://github.com/spring-petclinic/spring-petclinic-rest/blob/master/src/main/resources/openapi.yml) 
into the root directory of project and name it petclinic-spec.yam

Run to generate openApi schema
```bash
gradlew :spring-boot-demo:clean :spring-boot-demo:assemble
```

Complete section in build.gradle
```
tasks.named('compileJava') {
    dependsOn(tasks.openApiGenerate)
}

sourceSets.main.java.srcDir "${buildDir}/generate-resources/main/src/main/java"

openApiGenerate {
    generatorName.set('java')
    configOptions.set([
            library: 'restclient',
            openApiNullable: 'false'
    ])
    inputSpec.set("$projectDir/petclinic-spec.yml")
    ignoreFileOverride.set("$projectDir/.openapi-generator-java-sources.ignore")
    invokerPackage.set('com.myapp')
    modelPackage.set('com.myapp.model')
    apiPackage.set('com.myapp.api')
}
```

