dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "logger_user"
    password = "logger_user"
    dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    properties {
        maxActive = -1
        minEvictableIdleTimeMillis = 1800000
        timeBetweenEvictionRunsMillis = 1800000
        maxWait = 10000
        numTestsPerEvictionRun = 3
        testOnBorrow = true
        testWhileIdle = true
        testOnReturn = true
        validationQuery = ""
    }
}
hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {

    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost:3306/logger"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "logger_user"
            password = "logger_user"
        }
    }
    test {
        dataSource {
            dialect = "org.hibernate.dialect.H2Dialect"
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;"
        }
    }
    production {
        dataSource {
            // defined in external configuration file
        }
    }
}
