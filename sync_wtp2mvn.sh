#!/bin/bash

O3SAPIS_JAVA_SRCDIR="wtp/o3s-apis/src/main/java" 
O3SAPIS_JAVA_DSTDIR="mvn/modules/o3s-apis/src/main/java"

O3SAPPCLIENTINIT_JAVA_SRCDIR="wtp/o3s-appclient-init/src/main/java" 
O3SAPPCLIENTINIT_JAVA_DSTDIR="mvn/modules/o3s-appclient-init/src/main/java"

O3SAPPCLIENTINIT_JAVA_SRCDIR="wtp/o3s-appclient-init/src/main/java" 
O3SAPPCLIENTINIT_JAVA_DSTDIR="mvn/modules/o3s-appclient-init/src/main/java"

O3SBEANS_JAVA_SRCDIR="wtp/o3s-beans/src/main/java"
O3SBEANS_JAVA_DSTDIR="mvn/modules/o3s-beans/src/main/java"

O3SBEANS_RESOURCES_SRCDIR="wtp/o3s-beans/src/main/resources"
O3SBEANS_RESOURCES_DSTDIR="mvn/modules/o3s-beans/src/main/resources"

O3SPERSISTENCE_JAVA_SRCDIR="wtp/o3s-persistence/src/main/java"
O3SPERSISTENCE_JAVA_DSTDIR="mvn/modules/o3s-persistence/src/main/java"

O3SWEBDS_JAVA_SRCDIR="wtp/o3s-webds/src"
O3SWEBDS_JAVA_DSTDIR="mvn/modules/o3s-webds/src/main/java"

O3SWEBDS_JAVA_SRCDIR="wtp/o3s-webds/src"
O3SWEBDS_JAVA_DSTDIR="mvn/modules/o3s-webds/src/main/java"

O3SWEBDS_FLEX_SRCDIR="wtp/o3s-webds/WebContent/WEB-INF/flex"
O3SWEBDS_FLEX_DSTDIR="mvn/modules/o3s-webds/src/main/webapp/WEB-INF/flex"

O3SWEBDS_LIB_SRCDIR="wtp/o3s-webds/WebContent/WEB-INF/lib"
O3SWEBDS_LIB_DSTDIR="mvn/modules/o3s-webds/src/main/webapp/WEB-INF/lib"

O3SWEBDS_XML_SRCDIR="wtp/o3s-webds/WebContent/WEB-INF/web.xml"
O3SWEBDS_XML_DSTDIR="mvn/modules/o3s-webds/src/main/webapp/WEB-INF/web.xml"

O3SWEBFLEX_MXML_SRCDIR="wtp/o3s-webflex/src"
O3SWEBFLEX_MXML_DSTDIR="mvn/modules/o3s-webflex/src"


MODULES="O3SAPIS_JAVA O3SAPPCLIENTINIT_JAVA O3SBEANS_JAVA O3SBEANS_RESOURCES O3SPERSISTENCE_JAVA O3SWEBDS_JAVA O3SWEBDS_FLEX O3SWEBDS_LIB O3SWEBDS_XML O3SWEBFLEX_MXML"

suffixList="java mxml jrxml png jpg jar xml"

filters="WebContent"


syncFiles() {

 suffix=$1
 module=$2
 srcdir="${module}_SRCDIR"
 dstdir="${module}_DSTDIR"
 srcdir=$(eval echo "$""$srcdir") 
 dstdir=$(eval echo "$""$dstdir") 

 if [ -f $srcdir ]
 then
	if [ ! -f $dstdir ]
	then
		echo "Ajout du fichier $dstdir -> $dstdir ";
	else
		echo "Copie du fichier $srcdir -> $dstdir ";
	fi
	cp -f $dstdir $dstdir;
 else
 	files=`cd $srcdir; find . -name \*.$suffix | egrep -v $filters`
 	for src in $files
 	do
		if [ ! -f $dstdir/$src ]
		then
			echo "Ajout du fichier $dstdir/$src -> $dstdir/$src ";
		else
			echo "Copie du fichier $srcdir/$src -> $dstdir/$src ";
		fi
		cp -f $srcdir/$src $dstdir/$src;
 	done
 fi
 
}


for i in $suffixList
do
	for j in $MODULES
	do
		syncFiles $i $j
	done
done
