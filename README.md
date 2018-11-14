**create a project**

mvn archetype:generate \
-DinteractiveMode=false \
-DarchetypeGroupId=org.openjdk.jmh \
-DarchetypeArtifactId=jmh-java-benchmark-archetype \
-DgroupId=com.example \
-DartifactId=jmh-number-verification-performance-test \
-Dversion=1.0

**run**
java -jar target/benchmarks.jar