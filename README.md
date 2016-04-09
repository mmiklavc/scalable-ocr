# ocr

Welcome to the project

So much of our data is represented as human readable scans of documents.
However, this kind of document-by-document analysis does not scale, so
it is becoming evermore common to need to ingest large numbers of PDFs
or scanned documents shows up in almost all sectors. Inevitably these
scanned documents must be converted to text for analysis. And since
dealing with unstructured data is one of the main selling points for a
platform like Hadoop, it means that we must convert large volumes of
potentially large documents into a textual representation. We will show
you how to use scalable open source tooling (Apache NiFi and Tesseract) to scalably convert volumes of PDFs and ingest into a platform that will allow you to analyze this data at scale.

#### Cutting a release for ocr

```bash
mvn release:prepare -Dscm-connection.url=<scm readonly url> -Dscm-developer-connection.url=<scm read-write url>
```

**Note**: The main pom assumes "scm:git:<url>" - simply pass in the URL portion as a build parameter as shown above.

Examples: [maven scm] (http://maven.apache.org/scm/git.html)

1. local git - file://localhost/foo/bar/mygitrepodir
1. github connection url (readonly) - git://github.com/mmiklavc/myproject.git
1. github developer connection url (read/write) - git@github.com:mmiklavc/myproject.git

Performing the release prepare will do the following high-level steps:

1. Change pom versions from X.X-SNAPSHOT to X.X
1. Commit the new poms for the release to Git
1. Tag the release commit in Git
1. Increment poms to a new SNAPSHOT version, e.g. Update from X.0-SNAPSHOT to X.1-SNAPSHOT
1. Commit the updated SNAPSHOT poms

*See [Maven release prepare] (http://maven.apache.org/maven-release/maven-release-plugin/examples/prepare-release.html) documentation for more detail*

