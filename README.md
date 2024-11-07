# OpenDRIVE to GeoJSON Converter

This tool converts [OpenDRIVE (XODR)](https://www.opendrive.org/) files into GeoJSON format, making it easier to work with road and traffic data in GIS systems and other geo-based applications.

## Contents
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Example](#example)
- [License](#license)

## Requirements
- **Java 17** or higher must be installed.
- The **fat JAR** file of the converter (`vcs-odr-converter-1.0.0.jar`) must be available.

## Installation

1. **Clone** or **download** the project from [GitHub](https://github.com/virtualcitySYSTEMS/opendriveconverter).
2. **Build the fat JAR** (if it’s not already available):
    - Using Gradle (if a build setup is present):
      ```bash
      ./gradlew shadowJar
      ```
    - The JAR will be generated under `build/libs/vcs-odr-converter-1.0.0.jar`.

3. Alternatively, **download the fat JAR** from the Releases page on GitHub.

## Usage

To convert an OpenDRIVE file to GeoJSON, run the program from the command line with the following syntax:

```bash
java -jar vcs-odr-converter-1.0.0.jar <input.xodr> <output_folder>
```

**Parameters:**
- `<input.xodr>`: Path to the input file in OpenDRIVE format.
- `<output_folder>`: Target folder for the generated GeoJSON file.

## Example

```bash
java -jar vcs-odr-converter-1.0.0.jar /path/to/yourfile.xodr /path/to/output_folder
```

This example reads the file `yourfile.xodr` and saves the GeoJSON output in `/path/to/output_folder`.

## License 

This project is licensed under the **MIT License**. 

Owner: virtualcitysystems GmbH 2024
