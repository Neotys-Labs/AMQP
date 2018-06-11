@Library('jenkins-groovy-lib')
import startNlWebIndus
startNlWebIndus(startMongo: false, disableSonar: true, sonarCloud: false)

node('master') {
    stage('archive'){
        archiveArtifacts '**/*.zip'
    }
}
