#!/usr/bin/env groovy

pipeline {
    agent { label "spg_builds" }
    options {
        disableConcurrentBuilds()
    }

    tools {
        jdk 'jdk8'
    }

    stages {
        stage('Notify build started') {
            steps {
                slackSend(message: "Build Started - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)")
            }
        }

        stage('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
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