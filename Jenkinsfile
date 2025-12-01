pipeline {
    agent any

    stages {

        stage('GetProject') {
            steps {
                git 'https://github.com/enaux/erinspetitions.git'
            }
        }

//         stage('Build') {
//             steps {
//                 sh "mvn clean:clean"
//                 sh "mvn dependency:copy-dependencies"
//                 sh "mvn compiler:compile"
//             }
//         }

//         stage('Test') {
//             steps {
//                 echo 'Running unit tests'
//                 sh 'mvn test -DskipTests'
//             }
// //             post {
// //                 always {
// //                     junit 'target/surefire-reports/*.xml'
// //                 }
// //             }
//         }

//         stage('Package') {
//             steps {
//                 sh "mvn package"
//             }
//         }

        stage('Build and Package') {
            steps {
                sh '''
                    echo "=== Building with Maven ==="
                    mvn clean compile package -DskipTests
                '''

                script {
                    if (!fileExists('target/erinspetitions.war')) {
                        error "Build failed - WAR file not created in target directory"
                    }
                }

                sh '''
                    echo "=== Build Successful ==="
                    ls -la target/
                '''
            }
        }

        stage('Troubleshoot') {
            steps {
                sh '''
                    echo "=== Project Analysis ==="
                    pwd
                    ls -la
                    echo "=== POM Analysis ==="
                    grep -A 2 -B 2 "<packaging>" pom.xml || echo "No packaging specified"
                    echo "=== Build Plugins ==="
                    grep -A 5 "<build>" pom.xml || echo "No build section"
                '''
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
                    sh "docker build -f Dockerfile -t erinspetitions ."
                    sh "docker rm -f erinspetitions-tomcat || true"
                    sh "docker run --name erinspetitions-tomcat -p 9090:8080 --detach erinspetitions:latest"
                    sh '''
                        echo "Waiting for Tomcat to start..."
                        sleep 30
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
