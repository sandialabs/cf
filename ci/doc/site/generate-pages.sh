#!/bin/sh -v

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


cd $EXEC_DIR

# Javadoc page
echo "Generate javadoc page"
cd javadoc
echo -e "--- \nlayout: default\ntitle: Javadoc\nsubitem: Developing\n--- \n\n# CF Javadoc\n" > ./index.md
find -maxdepth 1 -type d ! -path . -printf "- [%f](%f/index.html){:target='_blank'}\n" | sort -r >> ./index.md
cd ..

# Build page
echo "Generate packages page"
cd packages
echo -e "--- \nlayout: default\ntitle: Packages\nsubitem: Using\n--- \n\n# CF Packages\n" > ./index.md
find -maxdepth 1 -type d ! -path . -printf "- [%f](%f/)\n" | sort -r >> ./index.md

# For each build
for version in $(find -maxdepth 1 -type d ! -path . -printf '%f\n' | sort)
do 
	echo "Generate $version package page"
	cd $version
	echo -e "--- \nlayout: default\ntitle: Package $version\nsubitem: Using\n--- \n\n# P2 repository for CF latest\n" > ./index.md
	echo -e "## Repository URL:\n[https://credibilityframework.gitlab.io/cf/packages/$version/](https://credibilityframework.gitlab.io/cf/packages/$version/)\n\n" >> ./index.md
	echo -e "## Directory content:\n" >> ./index.md
	find -maxdepth 1 ! -name 'index.*' ! -path . -printf "- %f\n" | sort -r >> ./index.md
	cd ..
done

# Return to main page
cd $CUR_DIR