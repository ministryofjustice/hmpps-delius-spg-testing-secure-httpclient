pipeline {
    agent { label "!master && !windows_slave" }
    options {
        disableConcurrentBuilds()
    }

    tools {
        jdk 'jdk8'
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

        stage('Decide whether to publish') {
            steps {
                script {
                    env.PUBLISH_ARTIFACT = input message: 'User input required', ok: 'Publish',
                            parameters: [choice(name: 'PUBLISH ARTIFACT', choices: 'No\nYes', description: 'Choose "Yes" if you want to publish this build')]
                }
            }
        }

        stage('Publish Artifact') {
            when {
                environment name: 'PUBLISH_ARTIFACT', value: 'Yes'
            }
            steps {
                echo "User response = ${env.PUBLISH_ARTIFACT}"
            }
        }
    }
}