pipeline {
    agent { label "!master && !windows_slave" }
    options {
        disableConcurrentBuilds()
    }

    tools {
        jdk 'jdk8'
    }

    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }


        stage('clean build') {
            steps {
                dir(WORKSPACE) {
                    sh './gradlew clean build'
                }
            }
        }


        stage('publish') {
            steps {
                dir(WORKSPACE) {
                    sh './gradlew publish'
                }
            }
        }
    }
}