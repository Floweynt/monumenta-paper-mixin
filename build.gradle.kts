plugins {
    id("mod.base-conventions")
}

dependencies {
    compileOnly(libs.ignite)
    compileOnly(libs.mixin)
    compileOnly(libs.mixinExtras)
    remapper("net.fabricmc:tiny-remapper:0.10.1:fat")

    paperweight.paperDevBundle(libs.versions.paper)
}