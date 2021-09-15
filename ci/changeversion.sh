
#Manual change
src/gov.sandia.cf.root/bundles/gov.sandia.cf.plugin/src/main/java/gov/sandia/cf/constants/CredibilityFrameworkConstants.java
src/gov.sandia.cf.root/features/gov.sandia.cf.feature/category.xml
src/gov.sandia.cf.root/features/gov.sandia.cf.feature/feature.xml
src/gov.sandia.cf.root/releng/gov.sandia.cf.update/site.xml
src/gov.sandia.cf.root/tests/gov.sandia.cf.plugin.tests/META-INF/MANIFEST.MF

# Maven tycho change
cd src/gov.sandia.cf/releng/gov.sandia.cf.configuration
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=0.1.2-SNAPSHOT versions:update-child-modules
