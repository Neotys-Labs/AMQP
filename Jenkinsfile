@Library('jenkins-groovy-lib')
import startNlWebIndus
node{
	git credentialsId: 'github-neotys-rd'
}
startNlWebIndus(startMongo: false, disableSonar: true, sonarCloud: false)
