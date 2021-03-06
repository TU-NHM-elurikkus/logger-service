grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.8
grails.project.source.level = 1.8
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true], // configure settings for the test-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256], // configure settings for the run-app JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256], // configure settings for the run-war JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256] // configure settings for the Console UI JVM
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenLocal()
        mavenRepo("https://nexus.ala.org.au/content/groups/public/") {
            updatePolicy "daily"
        }
    }

    dependencies {
        runtime "au.org.ala:ala-logger:1.0"
        runtime "mysql:mysql-connector-java:5.1.44"

        test "org.grails:grails-datastore-test-support:1.0-grails-2.4"
    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.70"
        build ":release:3.0.1"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ":cache:1.1.8"
        compile ":csv:0.3.1"
        compile ":jsonp:0.2"
        compile ":shiro:1.2.1"  // Authentication/permissions support for endpoints
        compile ":elurikkus-commons:0.2-SNAPSHOT"

        // plugins needed at runtime but not for compilation
        runtime ":hibernate4:4.3.5.5"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"
    }
}
