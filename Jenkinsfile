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
                sh "mvn compiler:compile"
            }
        }

        stage('Check Dependencies') {
            steps {
                sh '''
                    echo "=== Checking for Tomcat dependencies ==="
                    mvn dependency:tree | grep -i tomcat
                    echo "=== Checking WAR contents ==="
                    jar tf target/erinspetitions.war | grep -i tomcat
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests'
                sh 'mvn test'
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

//         stage('Deploy') {
//             steps {
//                 sh "docker build -f Dockerfile -t erinspetitions ."
//                 sh "docker rm -f "myappcontainer" || true"
//                 sh "docker run --name "myappcontainer" -p 9090:8080 --detach erinspetitions:latest"
//             }
//         }

            stage('Approval') {
                steps {
                    timeout(time: 30, unit: 'MINUTES') {
                        input(
                            message: 'Approve deployment to Tomcat container?',
                            ok: 'Deploy',
                            submitterParameter: 'APPROVED_BY'
                        )
                    }
                }
            }

            stage('Deploy') {
                steps {
                    sh "docker build -f Dockerfile -t erinspetitions:latest ."
                    sh "docker rm -f erinspetitions-tomcat || true"
                    sh "docker run --name erinspetitions-tomcat -p 9090:8080 --detach erinspetitions:latest"
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
