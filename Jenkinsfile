pipeline {
    agent {
        label 'alpine'
    }
    tools {
        maven '3.9.7'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Create Docker Image'){
            steps {
                withDockerServer([uri: 'tcp://172.18.0.2:2376']) {
                }
            }
        }
        stage('Push Docker Image'){
            steps{
                
            }
        }
    }
}