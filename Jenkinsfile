pipeline {
    agent any

    environment {
        // Jenkins credentials configuration
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials') // Docker Hub credentials ID
        GITHUB_CREDENTIALS = credentials('github') // <-- Add this: GitHub PAT credential ID in Jenkins

        // Docker Hub Repository's name
        DOCKER_IMAGE = 'gutaozi/teedy'
        DOCKER_TAG = "${env.BUILD_NUMBER}" // use build number as tag
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Building image') {
            steps {
                script {
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }

        stage('Upload image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub_credentials') {
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
                        docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Run containers') {
            steps {
                script {
                    sh 'docker stop teedy-container-8082 || true'
                    sh 'docker stop teedy-container-8083 || true'
                    sh 'docker stop teedy-container-8084 || true'
                    sh 'docker rm teedy-container-8082 || true'
                    sh 'docker rm teedy-container-8083 || true'
                    sh 'docker rm teedy-container-8084 || true'
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
                        '--name teedy-container-8082 -d -p 8082:8080'
                    )
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
                        '--name teedy-container-8083 -d -p 8083:8080'
                    )
                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
                        '--name teedy-container-8084 -d -p 8084:8080'
                    )
                    sh 'docker ps --filter "name=teedy-container"'
                }
            }
        }
    }
}
