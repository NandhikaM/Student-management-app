pipeline {
    agent any

    environment {
        IMAGE_NAME = "student-mgmt"
        CONTAINER_NAME = "student-mgmt-container"
        APP_PORT = "8080"
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code from Git...'
                checkout scm
            }
        }

        stage('Build & Test (Maven)') {
            steps {
                echo 'Building the application and running unit tests...'
                sh 'mvn -B clean package'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest .'
            }
        }

        stage('Deploy Container') {
            steps {
                echo 'Stopping any existing container and deploying a new one...'
                sh '''
                    docker rm -f ${CONTAINER_NAME} || true
                    docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:8080 ${IMAGE_NAME}:latest
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Waiting for app to start and verifying health endpoint...'
                sh '''
                    sleep 10
                    curl -f http://localhost:${APP_PORT}/students/health
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully! Application is running.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs above.'
        }
    }
}
