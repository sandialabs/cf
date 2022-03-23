#!/bin/bash -v

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

# keep current directory to go back
CUR_DIR=$(pwd)

# Go into the execution directory
cd $EXEC_DIR

# Javadoc page
echo "Generate Javadoc page"
cd javadoc
echo -e "--- \nlayout: default\ntitle: Javadoc\nsubitem: Developing\n--- \n\n# CF Javadoc" > ./index.md
find -maxdepth 1 -type d ! -path . -printf "- [%f](%f/index.html){:target='_blank'}\n" | sort -r >> ./index.md
cd ..

# Downloads page
echo "Generate Downloads page"
cd packages
echo -e "--- \nlayout: default\ntitle: Downloads\nsubitem: Using\n--- \n" > ./index.md

latest_version=$(find -maxdepth 1 -type d ! -name 'latest' ! -path . -printf "%f\n" | sort | tail -1)
echo -e "# Latest Release ($latest_version)" >> ./index.md
[ "$(ls -A latest/eclipse)" ] && echo -e "## Eclipse plugin" >> ./index.md
[ "$(ls -A latest/eclipse)" ] && echo -e "- Update site (p2 repository): [$CI_PAGES_URL/packages/latest/eclipse/]($CI_PAGES_URL/packages/latest/eclipse/)" >> ./index.md
[ "$(ls -A latest/eclipse)" ] && echo -e "- Update site repo (zipped): [$P2_REPO_FILE_PREFIX.latest.zip]($CI_PAGES_URL/packages/latest/eclipse/$P2_REPO_FILE_PREFIX.latest.zip)\n" >> ./index.md
[ "$(ls -A latest/webapp)" ] && echo -e "## Webapp" >> ./index.md
[ "$(ls -A latest/webapp)" ] && echo -e "- Springboot webapp (zipped): [$WEBAPP_FILE_PREFIX.latest.zip]($CI_PAGES_URL/packages/latest/webapp/$WEBAPP_FILE_PREFIX.latest.zip)\n" >> ./index.md

echo -e "# Releases" >> ./index.md
find -maxdepth 1 -type d ! -name 'latest' ! -path . -printf "- [Release %f](%f/)\n" | sort -r >> ./index.md
# To find only stable releases: find -maxdepth 1 -type d -regex './[0-9]?.[0-9]?.[0-9]?' ! -path . -printf "%f\n" | sort | tail -1

# For each build
for version in $(find -maxdepth 1 -type d ! -path . -printf '%f\n' | sort)
do 
	echo "Generate $version package page"
	cd $version

	echo -e "--- \nlayout: default\ntitle: Release $version\nsubitem: Using\n--- \n\n" > ./index.md
	echo -e "# CF Release $version" >> ./index.md
	echo -e "This is the CF $version release.\n" >> ./index.md

	echo -e "## Downloads:" >> ./index.md
	[ -d "eclipse" ] && [ "$(ls -A eclipse)" ] && echo -e "### Eclipse plugin" >> ./index.md
	[ -d "eclipse" ] && [ "$(ls -A eclipse)" ] && echo -e "- Update site (p2 repository): [$CI_PAGES_URL/packages/$version/eclipse/]($CI_PAGES_URL/packages/$version/eclipse/)" >> ./index.md
	if [[ $version == "latest" ]]; then
		[ -d "eclipse" ] && [ "$(ls -A eclipse)" ] && echo -e "- Update site repo zipped: [$P2_REPO_FILE_PREFIX.latest.zip]($CI_PAGES_URL/packages/latest/eclipse/$P2_REPO_FILE_PREFIX.latest.zip)\n" >> ./index.md
	else
		[ -d "eclipse" ] && [ "$(ls -A eclipse)" ] && echo -e "- Update site repo zipped: [$P2_REPO_FILE_PREFIX.$version.zip]($CI_API_V4_URL/projects/$CI_PROJECT_ID/packages/generic/$PACKAGENAME/$version/$P2_REPO_FILE_PREFIX.$version.zip)\n" >> ./index.md
	fi

	[ -d "webapp" ] && [ "$(ls -A webapp)" ] && echo -e "### Webapp" >> ./index.md
	if [[ $version == "latest" ]]; then
		[ -d "webapp" ] && [ "$(ls -A webapp)" ] && echo -e "- Springboot webapp .jar file (zipped): [$WEBAPP_FILE_PREFIX.latest.zip]($CI_PAGES_URL/packages/latest/webapp/$WEBAPP_FILE_PREFIX.latest.zip)\n" >> ./index.md
	else
		[ -d "webapp" ] && [ "$(ls -A webapp)" ] && echo -e "- Springboot webapp .jar file (zipped): [$WEBAPP_FILE_PREFIX.$version.zip]($CI_API_V4_URL/projects/$CI_PROJECT_ID/packages/generic/$PACKAGENAME/$version/$WEBAPP_FILE_PREFIX.$version.zip)\n" >> ./index.md
	fi

	echo -e "## Directory Contents:" >> ./index.md
	# find -maxdepth 1 -type d ! -path . -printf "- <img src='{{ site.baseurl }}/assets/images/folder.png'/> %f\n" | sort >> ./index.md
	# find -maxdepth 1 -type f ! -name 'index.*' ! -path . -printf "- <img src='{{ site.baseurl }}/assets/images/file.png'/> %f\n" | sort >> ./index.md

	echo "Generate directory content"

	if [ -d "eclipse" ]; then
	
		cd eclipse

		echo -e "### Eclipse plugin" >> ../index.md
	
		for folder in $(find -maxdepth 1 -type d ! -path . -printf '%f\n' | sort)
		do 
			cd $folder
			
			echo "working on $folder"
			echo -e "<details>\n<summary><img src='{{ site.baseurl }}/assets/images/folder.png'/> $folder</summary>\n" >> ../../index.md
			find -maxdepth 1 -type d ! -path . -printf "&ensp;&ensp;&ensp;<img src='{{ site.baseurl }}/assets/images/folder.png'/> %f<br/>\n" | sort >> ../../index.md
			find -maxdepth 1 -type f ! -name 'index.*' ! -path . -printf "&ensp;&ensp;&ensp;<img src='{{ site.baseurl }}/assets/images/file.png'/> %f<br/>\n" | sort >> ../../index.md
			echo -e "</details>" >> ../../index.md
			
			cd ..
		done

		for file in $(find -maxdepth 1 -type f ! -path . -printf '%f\n' | sort)
		do 
			echo "working on $file"
			echo -e "&ensp;&ensp;<img src='{{ site.baseurl }}/assets/images/file.png'/> $file<br/>" >> ../index.md
		done

		cd ..
	fi

	if [ -d "webapp" ]; then
	
		cd webapp

		echo -e "### Webapp" >> ../index.md
	
		for folder in $(find -maxdepth 1 -type d ! -path . -printf '%f\n' | sort)
		do 
			cd $folder
			
			echo "working on $folder"
			echo -e "<details>\n<summary><img src='{{ site.baseurl }}/assets/images/folder.png'/> $folder</summary>\n" >> ../../index.md
			find -maxdepth 1 -type d ! -path . -printf "&ensp;&ensp;&ensp;<img src='{{ site.baseurl }}/assets/images/folder.png'/> %f<br/>\n" | sort >> ../../index.md
			find -maxdepth 1 -type f ! -name 'index.*' ! -path . -printf "&ensp;&ensp;&ensp;<img src='{{ site.baseurl }}/assets/images/file.png'/> %f<br/>\n" | sort >> ../../index.md
			echo -e "</details>" >> ../../index.md
			
			cd ..
		done

		for file in $(find -maxdepth 1 -type f ! -path . -printf '%f\n' | sort)
		do 
			echo "working on $file"
			echo -e "&ensp;&ensp;<img src='{{ site.baseurl }}/assets/images/file.png'/> $file<br/>" >> ../index.md
		done

		cd ..
	fi
	
	cd ..
done

# Return to current directory
cd $CUR_DIR