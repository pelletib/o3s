# to release a version
mvn -Dmaven.username=btpelletier -DdryRun=false -DaddSchema=false -DpreparationGoals='install' -DautoVersionSubmodules=true release:clean release:prepare

# svn status and revert si the swf file was modified
mvn release:perform


