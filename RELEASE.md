# How to make a release

## Preparation

* Change the artifact ID in `pom.xml` to today's date, e.g.:

  ```
  2025.12.5-SNAPSHOT
  ```

* Update the version, date and URL in `Description.props` to reflect new
  version, e.g.:

  ```
  Version=2025.12.5
  Date=2025-12-05
  PackageURL=https://github.com/fracpete/djl-weka-package/releases/download/v2025.12.5/djl-2025.12.5.zip
  ```

## Weka package

* See the [DJL](DJL.md) document for generating the package
* Create a release tag on github (v2025.12.5)
* add release notes
* upload package archive from `dist`
