#!/bin/sh -v

if [ -z "$1" ]
then
	EXEC_DIR=$(pwd)
else
	EXEC_DIR=$1
fi

if [ -d "$EXEC_DIR" ]; then
	echo "Extracting packages into $EXEC_DIR"
else
    echo "$EXEC_DIR does not exists."
	return 1
fi

# keep current directory to go back
CUR_DIR=$(pwd)
# used to copy the first version package into the latest folder
FIRST="true"

# Go into the execution directory
cd $EXEC_DIR

# Get Gitlab packages information
echo "Getting Gitlab packages information"
wget -q -O packages.json $CI_API_V4_URL/projects/$CI_PROJECT_ID/packages

# Parse package version reversely
for version in $(jq -r .[].version ./packages.json | sort -r)
do
	# Create p2 repo for each package version sorted reversely
	PACKAGEURL="$CI_API_V4_URL/projects/$CI_PROJECT_ID/packages/generic/$PACKAGENAME/$version/$P2_REPO_FILE_PREFIX.$version.zip"
	echo "Checking package build artifacts from $PACKAGEURL"
	if wget --spider $PACKAGEURL 2>/dev/null; then
		echo "Getting package build artifacts from $PACKAGEURL"
		wget -q $PACKAGEURL
		mkdir -p packages/$version/eclipse/
		echo "Unzipping build package artifacts to packages/$version/eclipse/"
		unzip -q $P2_REPO_FILE_PREFIX.$version.zip -d packages/$version/eclipse/
		# execute the first time to add the latest package into the 'latest' folder
		if [[ $FIRST == "true" ]]; then
			echo "Creating latest package for $P2_REPO_FILE_PREFIX.$version.zip"
			cp -R packages/$version packages/latest
			cp $P2_REPO_FILE_PREFIX.$version.zip packages/latest/eclipse/$P2_REPO_FILE_PREFIX.latest.zip
		fi
		echo "Removing build package $P2_REPO_FILE_PREFIX.$version.zip"
		rm $P2_REPO_FILE_PREFIX.$version.zip
	else
		echo "Impossible to get build package artifact at $PACKAGEURL"
	fi

	# Create webapp for each package version sorted reversely
	PACKAGEURL="$CI_API_V4_URL/projects/$CI_PROJECT_ID/packages/generic/$PACKAGENAME/$version/$WEBAPP_FILE_PREFIX.$version.zip"
	echo "Checking webapp artifacts from $PACKAGEURL"
	if wget --spider $PACKAGEURL 2>/dev/null; then
		echo "Getting package build artifacts from $PACKAGEURL"
		wget -q $PACKAGEURL
		mkdir -p packages/$version/webapp/
		echo "Unzipping build package artifacts to packages/$version/webapp/"
		unzip -q $WEBAPP_FILE_PREFIX.$version.zip -d packages/$version/webapp/
		# execute the first time to add the latest package into the 'latest' folder
		if [[ $FIRST == "true" ]]; then
			echo "Creating latest package for $WEBAPP_FILE_PREFIX.$version.zip"
			cp -R packages/$version/webapp packages/latest/webapp
			cp $WEBAPP_FILE_PREFIX.$version.zip packages/latest/webapp/$WEBAPP_FILE_PREFIX.latest.zip
		fi
		echo "Removing build package $WEBAPP_FILE_PREFIX.$version.zip"
		rm $WEBAPP_FILE_PREFIX.$version.zip
	else
		echo "Impossible to get webapp artifacts at $PACKAGEURL"
	fi

	# Create javadoc for each package version sorted reversely
	JAVADOCPACKAGEURL="$CI_API_V4_URL/projects/$CI_PROJECT_ID/packages/generic/$PACKAGENAME/$version/$JAVADOC_FILE_PREFIX.$version.zip"
	echo "Checking package javadoc artifacts from $JAVADOCPACKAGEURL"
	if wget --spider $JAVADOCPACKAGEURL 2>/dev/null; then
		echo "Getting package javadoc artifacts from $JAVADOCPACKAGEURL"
		wget -q $JAVADOCPACKAGEURL
		mkdir -p javadoc/$version/
		echo "Unzipping javadoc package artifacts to javadoc/$version/"
		unzip -q $JAVADOC_FILE_PREFIX.$version.zip -d javadoc/$version/
		echo "Removing build package $JAVADOC_FILE_PREFIX.$version.zip"
		rm $JAVADOC_FILE_PREFIX.$version.zip
	else
		echo "Impossible to get javadoc package artifact at $JAVADOCPACKAGEURL"
	fi

	#used for the 'latest folder'
	FIRST="false"
done

# Clear files
rm packages.json

# Return to current directory
cd $CUR_DIR