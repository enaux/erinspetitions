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
                    echo "=== What files exist in target/ ==="
                    ls -la target/
                    echo "=== Checking if WAR exists (any name) ==="
                    ls -la target/*.war* 2>/dev/null || echo "No war files found"
                    echo "=== Checking the actual WAR file ==="
                    # Try different possible names
                    if [ -f "target/erinspetitions.war" ]; then
                        jar tf target/erinspetitions.war | head -20
                    elif [ -f "target/erinspetitions.war.original" ]; then
                        jar tf target/erinspetitions.war.original | head -20
                    else
                        echo "ERROR: No WAR file found!"
                        exit 1
                    fi
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
