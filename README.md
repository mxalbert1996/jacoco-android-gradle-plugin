# jacoco-android-gradle-plugin
![Build Status](https://github.com/mxalbert1996/jacoco-android-gradle-plugin/actions/workflows/test.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/com.mxalbert.gradle/jacoco-android)](https://search.maven.org/artifact/com.mxalbert.gradle/jacoco-android)

A Gradle plugin that adds fully configured `JacocoReport` tasks for unit tests of each Android application and library project variant.

## Why
In order to generate JaCoCo unit test coverage reports for Android projects you need to create `JacocoReport` tasks and configure them by providing paths to source code, execution data and compiled classes. It can be troublesome since Android projects can have different flavors and build types thus requiring additional paths to be set. This plugin provides those tasks already configured for you.

## Usage
```Kotlin
// build.gradle.kts
plugins {
    id("com.android.application") version "8.0.0"
    id("com.mxalbert.gradle.jacoco-android") version "0.2.0"
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.withType<Test>().configureEach {
    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

android {
    ...
    productFlavors {
        val free by creating
        val paid by creating
    }
}
```

<details>
  <summary>build.gradle</summary>

```Groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.mxalbert.gradle:jacoco-android:0.2.0")
    }
}

apply plugin: "com.android.application"
apply plugin: "com.mxalbert.gradle.jacoco-android"

jacoco {
    toolVersion = "0.8.10"
}

tasks.withType(Test).configureEach {
    jacoco {
        includeNoLocationClasses = true
        excludes = ['jdk.internal.*']
    }
}

android {
    ...
    productFlavors {
        free {}
        paid {}
    }
}
```
</details>

The above configuration creates a `JacocoReport` task for each variant and an additional `jacocoTestReport` task that runs all of them.
```
jacocoTestPaidDebugUnitTestReport
jacocoTestFreeDebugUnitTestReport
jacocoTestPaidReleaseUnitTestReport
jacocoTestFreeReleaseUnitTestReport
jacocoTestReport
```

The plugin excludes Android generated classes from report generation by default. You can use `jacocoAndroidUnitTestReport` extension to add other exclusion patterns if needed.
```Kotlin
jacocoAndroidUnitTestReport {
    excludes.addAll(
        "**/AutoValue_*.*",
        "**/*JavascriptBridge.class"
    )
}
```

You can also toggle report generation by type using the extension.
```Kotlin
jacocoAndroidUnitTestReport {
    csv.required.set(false)
    html.required.set(true)
    xml.required.set(true)
}
```

By default your report will be in `[root_project]/[project_name]/build/jacoco/`
But you can change the local reporting directory :
```Kotlin
jacocoAndroidUnitTestReport {
  destination.set("/path/to/the/new/local/directory/")
}
```

To generate all reports run:
```shell
$ ./gradlew jacocoTestReport
```

Reports for each variant are available at `$buildDir/reports/jacoco` in separate subdirectories, e.g. `build/reports/jacoco/jacocoTestPaidDebugUnitTestReport`.

## Examples
* [example](example)

## Snapshot usage
[Snapshot versions](https://s01.oss.sonatype.org/content/repositories/snapshots/com/mxalbert/gradle/jacoco-android/) are available at Sonatype OSSRH's snapshot repository. These are updated on every commit to `main`.
To use it:
```Kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

// build.gradle.kts
plugins {
    id("com.mxalbert.gradle.jacoco-android") version "<version>-SNAPSHOT"
}
```
<details>
  <summary>build.gradle</summary>

```Groovy
buildscript {
    repositories {
        maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots" }
    }
    dependencies {
        classpath("com.mxalbert.gradle:jacoco-android:<version>-SNAPSHOT")
    }
}
```
</details>

## License
```
Copyright 2015-2021 Artur Stępniewski
Copyright 2023 Albert Chang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
