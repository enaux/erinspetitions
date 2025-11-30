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
                sh 'mvn test -DskipTests'
            }
//             post {
//                 always {
//                     junit 'target/surefire-reports/*.xml'
//                 }
//             }
        }

        stage('Package') {
            steps {
                sh 'export MAVEN_OPTS="-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=300"'
                sh "mvn package -DskipTests"
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
//                 //sh "mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090"
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
                    sh '''
                        docker stop erinspetitions-tomcat || true
                        docker rm erinspetitions-tomcat || true

                        docker run -d \\
                            --name erinspetitions-tomcat \\
                            -p 9090:8080 \\
                            -v $(pwd)/target/erinspetitions.war:/usr/local/tomcat/webapps/ROOT.war \\
                            tomcat:9.0-jre17

                        echo "Waiting for Tomcat to start..."
                        sleep 45
                    '''
                    echo "Deployed by: ${env.APPROVED_BY}"
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
