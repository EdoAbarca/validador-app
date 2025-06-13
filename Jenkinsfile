pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'https://registry.hub.docker.com'  // Docker Hub
        APP_NAME = 'eddindocker/validador-app'
        APP_VERSION = 'latest'
        SONARQUBE_SCANNER = 'sonarqube-scanner'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/EdoAbarca/validador-app.git'
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    docker.image('maven:3.9.10-jdk-17').inside('-v $HOME/.m2:/root/.m2') {
                        sh 'mvn clean package'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh """
                    mvn sonar:sonar \
                        -Dsonar.projectKey=validador-app \
                        -Dsonar.host.url=http://sonarqube:9000
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${APP_NAME}:${APP_VERSION}", "--build-arg JAR_FILE=target/*.jar .")
                }
            }
        }

        stage('Run Tests in Container') {
            steps {
                script {
                    docker.image("${APP_NAME}:${APP_VERSION}").withRun('-p 8080:8080') { c ->
                        sleep(30) // Espera que la app inicie
                        sh 'curl http://localhost:8080/actuator/health'
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                branch 'main'
            }
            steps {
                script {
                    docker.withRegistry("${DOCKER_REGISTRY}", 'docker-hub-credentials') {
                        docker.image("${APP_NAME}:${APP_VERSION}").push()
                }
            }
        }
    }
    }

    post {
        always {
            cleanWs()
        }
        success {
            slackSend(color: 'good', message: "Build succeeded: ${env.BUILD_URL}")
        }
        failure {
            slackSend(color: 'danger', message: "Build failed: ${env.BUILD_URL}")
        }
    }
}