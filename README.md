# weir
[WEIR: Extraction and Integration of Partially Overlapping Web Sources](http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf)

# What to expect to get
# Check log:
log/root(0).log.html

# Install libraries in local maven repository before compiling
mvn deploy:deploy-file -DgroupId=it.uniroma3 -DartifactId=utils -Dversion=1.1.1 -Durl=file:./libs/ -DrepositoryId=in-project -DupdateReleaseInfo=true -Dfile=libs/utils-1.1.1.jar

mvn deploy:deploy-file -DgroupId=it.uniroma3 -DartifactId=hypertextual-logging -Dversion=0.3 -Durl=file:./libs/ -DrepositoryId=in-project -DupdateReleaseInfo=true -Dfile=libs/hypertextual-logging-0.3.jar

mvn deploy:deploy-file -DgroupId=it.uniroma3.roadrunner -DartifactId=lfeq -Dversion=1.3.3 -Durl=file:./libs/ -DrepositoryId=in-project -DupdateReleaseInfo=true -Dfile=libs/lfeq-1.3.3.jar

mvn deploy:deploy-file -DgroupId=it.uniroma3.roadrunner -DartifactId=token -Dversion=1.6.1 -Durl=file:./libs/ -DrepositoryId=in-project -DupdateReleaseInfo=true -Dfile=libs/token-1.6.1.jar

#
mvn deploy:deploy-file -DgroupId=secondstring -DartifactId=secondstring -Dversion=20060615 -Durl=file:./libs/ -DrepositoryId=in-project -DupdateReleaseInfo=true -Dfile=libs/secondstring-20060615.jar


# With eclipse, run launch config: 
launch-config/'WEIR  nbaplayer.launch'

# Check log:
log/root(0).log.html
