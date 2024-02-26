# Monumenta-mixins
Monumenta-paperfork implementation using mixins w/ ignite
## TODO
- Verify things work
- Implement mcfunction control flow 
## Running
First, run the `build` task. This automatically reobfuscates the jar.

Obtain a copy of ignite and paper jars:
```shell
$ curl https://api.papermc.io/v2/projects/paper/versions/1.20.4/builds/435/downloads/paper-1.20.4-435.jar -L -o paper.jar
$ curl https://github.com/vectrix-space/ignite/releases/download/v1.0.1/ignite.jar -L -o ignite.jar
```
Copy to mods:
```shell
$ mkdir mods/
$ cp /path/to/project/root/build/libs/monumenta-mixins-1.0.0.jar mods/
```
Start server:
```shell
$ java -Dignite.locator=paper -Dmixin.debug.export=true -jar ignite.jar -nogui 
```