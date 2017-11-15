dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "logger_user"
    password = "logger_user"
    dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    dbCreate = "update" // one of "create", "create-drop", "update", "validate", ""
    url = "jdbc:mysql://localhost:3306/logger"
    properties {
        maxActive = -1
        minEvictableIdleTimeMillis = 1800000
        timeBetweenEvictionRunsMillis = 1800000
        maxWait = 10000
        numTestsPerEvictionRun = 3
        testOnBorrow = true
        testWhileIdle = true
        testOnReturn = true
        validationQuery = "/* ping */"  // Better than "SELECT 1"
    }
}

hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = "org.hibernate.cache.ehcache.EhCacheRegionFactory" // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = "manual" // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {

    development {
        dataSource {

        }
    }

    test {
        dataSource {

        }
    }

    production {
        dataSource {
            // defined in external configuration file
        }
    }
}
