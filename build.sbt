name := "engine"

version := "1.0"

scalaVersion := "2.11.7"


resolvers := Seq(
    "lift-json" at "net.liftweb"
)

libraryDependencies ++= Seq(
    "net.liftweb" % "lift-json_2.11" % "2.6.2",
    "org.apache.spark" %% "spark-core" % "1.4.1",
    "org.apache.spark" %% "spark-mllib" % "1.4.1",
    "org.apache.spark" %% "spark-sql" % "1.4.1"
)