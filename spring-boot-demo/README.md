Run service 
```bash
gradle bootRun
```

```bash
gradle test --tests LabCheckTests.Step4_PostNewQuote
curl -i -X POST http://localhost:8888/api/quotes -d "Add new quote here"
```


```bash
gradle test --tests TestRestTemplateTests
```

```bash
gradle bootRun 
curl -i http://localhost:8888/api/quotes/9
```

Gradle Jacoco Plugin
https://github.com/thombergs/code-examples/blob/master/tools/jacoco/build.gradle
https://reflectoring.io/jacoco/

