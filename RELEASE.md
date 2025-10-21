# How to make a release

## Preparation

* Change the artifact ID in `pom.xml` to today's date, e.g.:

  ```
  2025.10.21-SNAPSHOT
  ```

* Update the version, date and URL in `Description.props` to reflect new
  version, e.g.:

  ```
  Version=2025.10.21
  Date=2025-10-21
  PackageURL=https://github.com/fracpete/djl-weka-package/releases/download/v2025.10.21/djl-2025.10.21.zip
  ```

## Weka package

* See the [DJL](DJL.md) document for generating the package
* Create a release tag on github (v2025.10.21)
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
