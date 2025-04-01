plugins {
	//kotlin("jvm") version "1.9.25"
	kotlin("jvm") version "2.0.21"
	//kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.spring") version "2.0.21"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"

	// Detekt
	id("io.gitlab.arturbosch.detekt") version "1.23.8"

	// Kover
	id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = "de.minhperry"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.data:spring-data-rest-hal-explorer")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// JGit
	implementation("org.eclipse.jgit:org.eclipse.jgit:7.2.0.202503040940-r")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

detekt {
	toolVersion = "1.23.8"
	config.from("detektConfig.yml")
}

kover {
	filters {
		classes {
			excludes += listOf(
				"de.minhperry.srb.SrbApplicationKt*",
				"de.minhperry.srb.constant.*",
			)
		}
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
	finalizedBy(tasks.koverReport)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
}
