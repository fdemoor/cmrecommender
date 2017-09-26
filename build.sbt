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

// library dependencies. (orginization name) % (project name) % (version)
libraryDependencies ++= Seq(
)

//javacOptions in (Compile,doc) ++= Seq("-doclet", "info.leadinglight.umljavadoclet.UmlJavaDoclet", "-docletpath", "src/main/resources/uml-java-doclet-1.0-SNAPSHOT.jar")
javacOptions in (Compile,doc) ++= Seq(
  "-doclet", "nl.talsmasoftware.umldoclet.UMLDoclet",
  "-docletpath", "lib/umldoclet-1.0.9.jar",
  "-umlIncludePrivateFields", "true"
)
