pipeline {
    agent any

    environment {
        // Jenkins credentials configuration
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials') // Docker Hub credentials ID
        GITHUB_CREDENTIALS = credentials('github') // GitHub PAT credential ID in Jenkins

        // Docker Hub Repository's name
        DOCKER_IMAGE = 'gutaozi/teedy'
        DOCKER_TAG = "${env.BUILD_NUMBER}" // use build number as tag

        // Kubernetes deployment variables
        DEPLOYMENT_NAME = "teedy" 
        CONTAINER_NAME = "docs"
        IMAGE_NAME = "gutaozi/teedy"
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

        stage('Start Minikube') {
            steps {
                sh '''
                if ! minikube status | grep -q "Running"; then
                  echo "Starting Minikube..."
                  minikube start
                else
                  echo "Minikube already running."
                fi
                '''
            }
        }

        stage('Set Image in K8s') {
            steps {
                sh 'kubectl set image deployment/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME}'
            }
        }

        stage('Verify K8s Deployment') {
            steps {
                sh 'kubectl rollout status deployment/${DEPLOYMENT_NAME}'
                sh 'kubectl get pods'
            }
        }
    }
}
