#!/usr/bin/env groovy

pipeline {
    agent { label "spg_builds" }

    options {
        disableConcurrentBuilds()
    }

    tools {
        jdk 'jdk8'
    }

    parameters {
        choice(
            name: 'Bump_Version',
            choices:"build\npatch\nminor\nmajor",
            description: "Please select which part of the version to bump" )
    }

    stages {
        stage ('Notify build started') {
            steps {
                slackSend(message: "Build Started - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)")
            }
        }

        stage('Initialize') {
            steps {
                sh '''
                    echo "Selected Bump_Version = ${Bump_Version}"
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage('Bump Version') {
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
                      echo $(semvertag bump "${Bump_Version}" --tag) > VERSION
                      echo "VERSION FILE CONTENT = `cat VERSION`"
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

    post {
        success {
            slackSend(message: "Build successful -${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)", color: 'good')
        }
        failure {
            slackSend(message: "Build failed - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)", color: 'danger')
        }
    }
}