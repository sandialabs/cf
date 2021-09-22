#!/bin/sh
if [ -z "$1" ]
then
	export EXEC_DIR=$(pwd)
else
	export EXEC_DIR=$1
fi

if [ -d "$EXEC_DIR" ]; then
	echo "Generating pages structure into $EXEC_DIR"
else
    echo "$EXEC_DIR does not exists."
	return 1
fi

export CUR_DIR=$(pwd)

# Main page

cd $EXEC_DIR
echo "Generate main page"
echo "<html><body><h1>Credibility Framework listing:</h1>" > ./index.html
find -maxdepth 1 -type d ! -path . -printf "<a href='%f/index.html'>%f</a><br/>\n" | sort >> ./index.html
echo "</body></html>" >> ./index.html

# Javadoc page
echo "Generate javadoc page"
cd javadoc
echo "<html><body><h1>Credibility Framework Javadoc:</h1>" > ./index.html
find -maxdepth 1 -type d ! -path . -printf "<a href='%f/index.html'>%f</a><br/>\n" | sort -r >> ./index.html
echo "</body></html>" >> ./index.html
cd ..

# Build page
echo "Generate build page"
cd build
echo "<html><body><h1>Credibility Framework Builds:</h1>" > ./index.html
find -maxdepth 1 -type d ! -path . -printf "<a href='%f/index.html'>%f</a><br/>\n" | sort -r >> ./index.html
echo "</body></html>" >> ./index.html

# For each build
for version in $(find -maxdepth 1 -type d ! -path . -printf '%f\n' | sort)
do 
	echo "Generate $version build page"
	cd $version
	echo "<html><body><h1>P2 repository files for $version:</h1>" > ./index.html
	echo "<h2>Repository URL:</h2><a href='https://credibilityframework.gitlab.io/cf/build/$version/'>https://credibilityframework.gitlab.io/cf/build/$version/</a></p>" >> ./index.html
	echo "<h2>Directory content:</h2>" >> ./index.html
	echo "<ul>" >> ./index.html
	find -maxdepth 1 ! -name 'index.html' ! -path . -printf '<li>%f</li>\n' | sort >> ./index.html
	echo "</ul>" >> ./index.html
	echo "</body></html>" >> ./index.html
	cd ..
done

# Return to main page
cd $CUR_DIR