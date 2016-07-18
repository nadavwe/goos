name := "goos"

version := "1.0"

scalaVersion := "2.11.4"



libraryDependencies ++= Seq(
  "org.hamcrest" % "hamcrest-all" % "1.3" % "test",
  "com.googlecode.windowlicker" % "windowlicker-swing" % "r268" % "test",
  "org.igniterealtime.smack" % "smack" % "3.2.1",
  "org.igniterealtime.smack" % "smackx" % "3.2.1",
  "org.specs2" %% "specs2-core" % "2.4.13" % "test",
  "com.wixpress" %% "specs2-jmock" % "0.1.4" % "test"
)
    
