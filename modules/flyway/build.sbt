enablePlugins(FlywayPlugin)

// Database Migrations:
// run with "sbt flywayMigrate"
// http://flywaydb.org/getstarted/firststeps/sbt.html

//$ export DB_DEFAULT_URL="jdbc:h2:/tmp/example.db"
//$ export DB_DEFAULT_USER="sa"
//$ export DB_DEFAULT_PASSWORD=""

libraryDependencies += "org.flywaydb" % "flyway-core" % "8.5.10"


lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:postgresql://docker:5435/ecom_point_db")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "ecom_point_db_admin")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "f23_[RudStw8)")

flywayLocations := Seq("classpath:db.migration")

flywayUrl := databaseUrl
flywayUser := databaseUser
flywayPassword := databasePassword
flywayBaselineVersion := "5"
flywayTable := "schema_history"