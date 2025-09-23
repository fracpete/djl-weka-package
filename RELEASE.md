# How to make a release

## Preparation

* Change the artifact ID in `pom.xml` to today's date, e.g.:

  ```
  2025.9.23-SNAPSHOT
  ```

* Update the version, date and URL in `Description.props` to reflect new
  version, e.g.:

  ```
  Version=2025.9.23
  Date=2025-09-23
  PackageURL=https://github.com/fracpete/djl-weka-package/releases/download/v2025.9.23/djl-2025.9.23.zip
  ```

## Weka package

* Commit/push all changes

* Run the following command to generate the package archive for version `2025.9.23`:

  ```bash
  ant -f build_package.xml -Dpackage=djl-2025.9.23 clean make_package
  ```

* Create a release tag on github (v2025.9.23)
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
