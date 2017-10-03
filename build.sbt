// Project name (artifact name in Maven)
name := "Count-min Recommender"

// orgnization name (e.g., the package name of the project)
organization := "cmrecommender"

version := "1.0-SNAPSHOT"

// project description
description := "Count-min based collaborative filtering recommender system"

// Enables publishing to maven repo
publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

javacOptions += "-Xlint:unchecked"

// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "23.0"

// https://mvnrepository.com/artifact/org.apache.commons/commons-math3
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"

// https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.25"

// https://mvnrepository.com/artifact/log4j/log4j
libraryDependencies += "log4j" % "log4j" % "1.2.17"

// https://mvnrepository.com/artifact/commons-cli/commons-cli
libraryDependencies += "commons-cli" % "commons-cli" % "1.4"

// https://mvnrepository.com/artifact/net.sf.trove4j/trove4j
libraryDependencies += "net.sf.trove4j" % "trove4j" % "3.0.3"
