This page contains the best pratices of the CF project.

[Go back to home page](home)

## Contents

[[_TOC_]]


## Release CF Snapshot

1. **Merge all** necessary Merge Requests on `develop` branch
2. Be sure that the version number ended by -SNAPSHOT or .qualifier. See [Changing version number with Maven Tycho](#changing-version-number-with-maven-tycho)
7. **Launch** `deploy snapshot` job, a new tag will be created and the build will be deployed into the repository:

![image](uploads/d799d10249f9f920de5d02ff7c3857dc/image.png)

8. Enter the tag description into Repository > Tags > YOUR_TAG


## Release CF Release Candidate

1. **Merge all** necessary Merge Requests on `develop` branch
2. **Change version** number to the expected one (with .RCx). See [Changing version number with Maven Tycho](#changing-version-number-with-maven-tycho)
7. **Launch** `deploy snapshot` job, a new tag will be created and the build will be deployed into the repository:

![image](uploads/d799d10249f9f920de5d02ff7c3857dc/image.png)

8. Enter the tag description into Repository > Tags > YOUR_TAG


## Release CF version

1. **Merge all** necessary Merge Requests on `develop` branch
2. **Change version** number to the expected one (without -SNAPSHOT or .qualifier). See [Changing version number with Maven Tycho](#changing-version-number-with-maven-tycho)
5. **Generate Javadoc** with the following command `mvn javadoc:javadoc` and copy javadoc into the `doc/javadoc` folder of the project with the version name as parent folder

<img src="uploads/eb03cadb8349e016ffe49e78736fa2e0/Screen_Shot_2020-01-09_at_3.47.51_PM.png" height="350">

<img src="uploads/e52fc38b33d4ac6a0575b4676accbe9e/Screen_Shot_2020-01-09_at_3.33.30_PM_2.png" height="150">


4. **Launch** `deploy release` job and enter the `VERSION` variable manually by clicking on the **deploy stage icon** and `deploy release` job:

![image](uploads/1d08278a4147e8d392a80908fd296880/image.png)

5. **Confirm the version manually** to ensure the release action by adding Gitlab variable `VERSION`:

![image](/uploads/99d7495135662dba0964b866bc26a28f/image.png)

The following action will be done from `develop` branch:
- Add the CF build into the `build` folder in git
- Commit the build folder change
- Merge `develop` branch into `master`
- Create a tag on `master`
- Rebase `develop` branch on the latest `master` branch

6. **Update** the **gitlab wiki**:
- Duplicate all existing pages into a new folder starting with the version number.
- Create a link to the javadoc. The content of the folder ` doc/javadocs` is automatically published on gitlab at the following url `https://iwf.gitlab.io/cf/<VERSION_NUMBER>` (replace <VERSION_NUMBER> by your version number).
- Add a link to this new pages on the wiki home page.


## Changing version number with Maven Tycho

For **releases**, delete **SNAPSHOT** from the version number. 
For **snapshots**, add **SNAPSHOT** to the version number. 

1. Execute the following command on `src/gov.sandia.cf` folder (replace ${NEW_VERSION} with your version number): 
```
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="${NEW_VERSION}" versions:update-child-modules
```

2. Once done, execute the same command on `src/gov.sandia.cf/releng/gov.sandia.cf.configuration` folder to update the relative parent.

3. `gov.sandia.cf.feature`:
  - `category.xml`: the following file is not included in Tycho procedure. So change version number to `${NEW_VERSION}` in the following line:
```
<bundle id="gov.sandia.cf.plugin" version="${NEW_VERSION}">
```

## Changing version number manually

### RELEASE

You should delete all **-SNAPSHOT** and **.qualifier** identifiers after the version number in the following files: 
1. Change all `pom.xml` version number to `${NEW_VERSION}` in the expected one
2. `gov.sandia.cf.plugin`:
  - `MANIFEST.MF`: change version number to `${NEW_VERSION}` in the following line:
```
Bundle-Version: ${NEW_VERSION}
```
3. `gov.sandia.cf.feature`:
  - `category.xml`: change version number to `${NEW_VERSION}` in the following line:
```
<bundle id="gov.sandia.cf.plugin" version="${NEW_VERSION}">
```
  - `feature.xml`: change version number to `${NEW_VERSION}` in the following lines:
```
<feature
      id="gov.sandia.cf.feature"
      label="Credibility Framework"
      version="${NEW_VERSION}"
      provider-name="SANDIA NATIONAL LABORATORIES">
```
```
<description>
         id="gov.sandia.cf.plugin"
         download-size="0"
         install-size="0"
         version="${NEW_VERSION}"
         unpack="false"/>
```
4. `gov.sandia.cf.update`:
  - `site.xml`: change version number to `${NEW_VERSION}` in the following line:
```
<bundle id="gov.sandia.cf.plugin" version="${NEW_VERSION}">
```
  - `feature.xml`: change version number to `${NEW_VERSION}` in the following lines:
```
<feature url="features/gov.sandia.cf.feature_${NEW_VERSION}.jar" id="gov.sandia.cf.feature" version="${NEW_VERSION}">
```
5. `gov.sandia.cf.plugin.tests`:
  - `MANIFEST.MF`: change version number to `${NEW_VERSION}` in the following line:
```
Bundle-Version: ${NEW_VERSION}
```

NB: In the following line, **do not add .qualifier** after the version number:
```
Fragment-Host: gov.sandia.cf.plugin;bundle-version="${NEW_VERSION}"
```
6. `gov.sandia.cf.plugin.help`:
  - `MANIFEST.MF`: change version number to `${NEW_VERSION}` in the following line:
```
Bundle-Version: ${NEW_VERSION}
```

NB: In the following line, **do not add .qualifier** after the version number:
```
Fragment-Host: gov.sandia.cf.plugin;bundle-version="${NEW_VERSION}"
```

### SNAPSHOT

1. Change all `pom.xml` version number to `${NEW_VERSION}-SNAPSHOT` int the expected one
2. `gov.sandia.cf.plugin`:
  - `MANIFEST.MF`: change version number to `${NEW_VERSION}.qualifier` in the following line:
```
Bundle-Version: ${NEW_VERSION}.qualifier
```
3. `gov.sandia.cf.feature`:
  - `category.xml`: change version number to `${NEW_VERSION}.qualifier` in the following line:
```
<bundle id="gov.sandia.cf.plugin" version="${NEW_VERSION}.qualifier">
```
  - `feature.xml`: change version number to `${NEW_VERSION}.qualifier` in the following lines:
```
<feature
      id="gov.sandia.cf.feature"
      label="Credibility Framework"
      version="${NEW_VERSION}.qualifier"
      provider-name="SANDIA NATIONAL LABORATORIES">
```
```
<description>
         id="gov.sandia.cf.plugin"
         download-size="0"
         install-size="0"
         version="${NEW_VERSION}.qualifier"
         unpack="false"/>
```
4. `gov.sandia.cf.update`:
  - `site.xml`: change version number to `${NEW_VERSION}.qualifier` in the following line:
```
<bundle id="gov.sandia.cf.plugin" version="${NEW_VERSION}.qualifier">
```
  - `feature.xml`: change version number to `${NEW_VERSION}.qualifier` in the following lines:
```
<feature url="features/gov.sandia.cf.feature_${NEW_VERSION}.qualifier.jar" id="gov.sandia.cf.feature" version="${NEW_VERSION}">
```
5. `gov.sandia.cf.plugin.tests`:
  - `MANIFEST.MF`: change version number to `${NEW_VERSION}.qualifier` in the following line:
```
Bundle-Version: ${NEW_VERSION}.qualifier
```

In the following line, **do not add .qualifier** after the version number:
```
Fragment-Host: gov.sandia.cf.plugin;bundle-version="${NEW_VERSION}"
```
6. `gov.sandia.cf.plugin.help`:
  - `MANIFEST.MF`: change version number to `${NEW_VERSION}` in the following line:
```
Bundle-Version: ${NEW_VERSION}
```

NB: In the following line, **do not add .qualifier** after the version number:
```
Fragment-Host: gov.sandia.cf.plugin;bundle-version="${NEW_VERSION}"
```

### Files to change

Here are the file to change:

![image](uploads/48934e0391708f3cf65b3662ca92acdb/image.png)


[Go back to the top of the page](#content-body)
