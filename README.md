mappingtools
============

#Build notes
Succesfully builds using Eclipse EMF Runtime JARS 2.9.2 (2014/02/03). Note that this is not in central maven repo. The developer [manually installed](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html) the jars. 

#Future work
Maven POMS currently quote dependency on EMF 2.9.2 (2014/02/03). Would be better if this was less precise. It would be even better if the version it depended on was on the maven central reposity (no suitable candidate at the moment).