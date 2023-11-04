#!/bin/bash
mvn archetype:generate -DgroupId=ToolsQA -DartifactId=$1 -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
#echo "Hello from Bash script, argument: $1"
