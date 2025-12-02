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
                sh "mvn clean compile"
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
                sh "mvn package -DskipTests"
                sh "ls -la target/*.war"
            }
        }

        stage('Verify WAR') {
                    steps {
                        echo 'Verifying WAR structure...'
                        sh "jar tf target/erinspetitions.war | grep -E 'WEB-INF/classes/com/example' | head -10"
                        sh "jar tf target/erinspetitions.war | grep 'BOOT-INF' && echo 'ERROR: BOOT-INF found - repackage not skipped!' && exit 1 || echo 'OK: No BOOT-INF (correct for external Tomcat)'"
                    }
                }

        stage('Archive') {
            steps {
                archiveArtifacts(
                    allowEmptyArchive: false,
                    artifacts: 'target/erinspetitions.war'
                )
            }
        }

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
                sh "docker build --no-cache -f Dockerfile -t erinspetitions:latest ."
                sh "docker rm -f erinspetitions-tomcat || true"
                sh "docker run --name erinspetitions-tomcat -p 9090:8080 --detach erinspetitions:latest"
                sh "sleep 10"
                sh "docker logs erinspetitions-tomcat 2>&1 | tail -30"
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
