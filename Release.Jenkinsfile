#!/usr/bin/env groovy

pipeline {
    agent { label "!master && !windows_slave" }

    options {
        disableConcurrentBuilds()
    }

    tools {
        jdk 'jdk8'
    }

    parameters {
        choice(
            name: 'Bump_Version',
            choices:"Major\nMinor\nPatch\nBuild",
            description: "Please select which part of the version to bump" )
    }

    stages {
        stage('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage('Bump the build version and tag the Repository') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'f44bc5f1-30bd-4ab9-ad61-cc32caf1562a', keyFileVariable: 'private_key', passphraseVariable: '', usernameVariable: env.JENKINS_GITHUB_USER)]) {
                    sh '''
                      #!/bin/bash +x
                      virtualenv .venv
                      . .venv/bin/activate
                      pip install git+https://github.com/ministryofjustice/semvertag.git@1.1.0
                      eval $(ssh-agent)
                      ssh-add ${private_key}
                      ssh-add -l
                      git fetch --tags
                      echo $(semvertag bump ${params.Bump_Version} --tag) > VERSION
                    '''
                }
            }
        }

        stage('Clean Build') {
            steps {
                dir(WORKSPACE) {
                    sh './gradlew clean build'
                }
            }
            post {
                always {
                    junit 'build/test-results/**/*.xml'
                }
                failure {
                    sh '''echo The Pipeline failed!'''
                }
            }
        }

        stage('Publish') {
            steps {
                dir(WORKSPACE) {
                    sh './gradlew publish'
                }
            }
        }
    }
}