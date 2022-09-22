This page contains the best pratices of the CF project.

[Go back to home page](home)

## Contents

[[_TOC_]]




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

![image](uploads/fc6aecbc06e01fb2c544a73597b369f0/image.png)


## Release CF Snapshot

1. **Merge all** necessary Merge Requests on `develop` branch
2. Be sure that the version number ended by -SNAPSHOT or .qualifier. See [Changing version number with Maven Tycho](#changing-version-number-with-maven-tycho)
3. **Launch** `deploy snapshot` job, a new tag will be created and the build will be deployed into the repository:

![image](uploads/5d8c0d020cbc152b85bfd1679d6231db/image.png)

4. Enter the tag description into Repository > Tags > YOUR_TAG


## Release CF Release Candidate

1. **Merge all** necessary Merge Requests on `develop` branch
2. **Change version** number to the expected one (with .RCx). See [Changing version number with Maven Tycho](#changing-version-number-with-maven-tycho)
3. **Launch** `deploy snapshot` job, a new tag will be created and the build will be deployed into the repository:

![image](uploads/9553ea86e9c9a639de6c088ae4066cb6/image.png)

4. Enter the tag description into Repository > Tags > YOUR_TAG


## Release CF version

1. **Merge all** necessary Merge Requests on `develop` branch
2. **Change version** number to the expected one (without -SNAPSHOT or .qualifier). See [Changing version number with Maven Tycho](#changing-version-number-with-maven-tycho)
5. **Generate Javadoc** with the following command `mvn javadoc:javadoc` and copy javadoc into the `doc/javadoc` folder of the project with the version name as parent folder

![image](uploads/b47158830ee04313a5b7dcf84a406ea9/image.png)

![image](uploads/12d430707b078644b174eeac54f1bf3a/image.png)


4. **Launch** `deploy release` job and enter the `VERSION` variable manually by clicking on the **deploy stage icon** and `deploy release` job:

![image](uploads/cf131696a93dc202b919619a3c1cfab2/image.png)

5. **Confirm the version manually** to ensure the release action by adding Gitlab variable `VERSION`:

![image](uploads/c823c6a7f7feea5f1ff37550074fdf4e/image.png)

The following action will be done from `develop` branch:
- Add the CF build into the `build` folder in git
- Commit the build folder change
- Merge `develop` branch into `master`
- Create a tag on `master`
- Rebase `develop` branch on the latest `master` branch

6. **Update** the **gitlab wiki**:
- Duplicate all existing pages into a new folder starting with the version number.
- Create a link to the javadoc. The content of the folder ` doc/javadocs` is automatically published on gitlab at the following url `https://credibilityframework.gitlab.io/cf/<VERSION_NUMBER>` (replace <VERSION_NUMBER> by your version number).
- Add a link to this new pages on the wiki home page.


[Go back to the top of the page](#content-body)
