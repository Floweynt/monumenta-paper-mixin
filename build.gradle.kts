plugins {
    id("mod.base-conventions")
    id("org.ajoberstar.grgit") version "5.2.2"
}

dependencies {
    compileOnly(libs.ignite)
    compileOnly(libs.mixin)
    compileOnly(libs.mixinExtras)
    remapper("net.fabricmc:tiny-remapper:0.10.1:fat")

    paperweight.paperDevBundle(libs.versions.paper)
}

tasks.jar {
    manifest {
        attributes["Git-Branch"] = grgit.branch.current().name
        attributes["Git-Hash"] = grgit.log().first().id
    }
}