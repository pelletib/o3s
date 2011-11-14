
// ////////////////////////////////////////////////////////////////////////////
// Compile with maven :
// ////////////////////////////////////////////////////////////////////////////

	Some flex libraries need to be added manually from a flex sdk. The following 
	commands are for a 4.0.0.10193 sdk version:
	
	The jar for the flex license (to use a license, specify the FLEX_LICENSE 
	environment variable) :
	mvn install:install-file -DgroupId=com.adobe.flex -DartifactId=license -Dversion=4.0.0.101933 -Dpackaging=jar -Dfile=${flexSDK_path}/lib/license.jar
	
   
