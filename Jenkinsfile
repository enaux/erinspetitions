pipeline {
    agent any

    stages {

        stage('GetProject') {
            steps {
                git 'https://github.com/enaux/erinspetitions.git'
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean:clean"
                sh "mvn dependency:copy-dependencies"
                sh "mvn compiler:compile"
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh "mvn package"
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts(
                    allowEmptyArchive: true,
                    artifacts: '**/erinspetitions*.war'
                )
            }
        }

        stage('Deploy') {
            steps {
                sh "docker build -f Dockerfile -t erinspetitions ."
                //sh "mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090"
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed.'

        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
