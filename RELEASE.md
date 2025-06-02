# How to make a release

## Preparation

* Change the artifact ID in `pom.xml` to today's date, e.g.:

  ```
  2025.6.30-SNAPSHOT
  ```

* Update the version, date and URL in `Description.props` to reflect new
  version, e.g.:

  ```
  Version=2025.6.30
  Date=2025-06-30
  PackageURL=https://github.com/fracpete/djl-weka-package/releases/download/v2025.6.30/djl-2025.6.30.zip
  ```

## Weka package

* Commit/push all changes

* Run the following command to generate the package archive for version `2025.6.30`:

  ```bash
  ant -f build_package.xml -Dpackage=djl-2025.6.30 clean make_package
  ```

* Create a release tag on github (v2025.6.30)
* add release notes
* upload package archive from `dist`


## Maven

* Run the following command to deploy the artifact:

  ```bash
  mvn release:clean release:prepare release:perform
  ```

* log into https://oss.sonatype.org and close/release artifacts

* After successful deployment, push the changes out:

  ```bash
  git push
  ````
