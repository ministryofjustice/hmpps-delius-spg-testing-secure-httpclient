# hmpps-delius-spg-testing-secure-httpclient
Client Library to facilitate message signing and mutual TLS connectivty

Upon merge to master, this will trigger a githook that runs this jenkins job:
https://jenkins.engineering-dev.probation.hmpps.dsd.io/job/DAMS/job/Artefacts/job/SPG/job/spg-testing-secure-httpclient-master/



In order to publish, this jenkins job will need to be run
https://jenkins.engineering-dev.probation.hmpps.dsd.io/job/DAMS/job/Artefacts/job/SPG/job/spg-testing-secure-httpclient-master-RELEASE/ 


which in turn will publish the jar to this s3 bucket in engineering account:
https://s3.console.aws.amazon.com/s3/buckets/tf-eu-west-2-hmpps-eng-dev-maven-repo-s3bucket/releases/spg-testing/spg-httpclient/

versioning appears to be set to 0.0.0 and will probably need more work ala payload generator