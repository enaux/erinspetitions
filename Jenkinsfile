pipeline {
    agent any

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('GetProject') {
            steps {
                git branch: 'master', url: 'https://github.com/enaux/erinspetitions.git'
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean compile"
            }
        }

//         stage('Test') {
//             steps {
//                 echo 'Running tests'
//                 sh 'mvn test'
//             }
//         }

        stage('Package') {
            steps {
                sh "mvn package -DskipTests"
                sh "ls -la target/*.war"
            }
        }

        stage('Verify WAR') {
            steps {
                echo 'Verifying WAR structure...'
                echo '=== Checking for application classes in WEB-INF/classes ==='
                sh "jar tf target/erinspetitions.war | grep -E 'WEB-INF/classes/com/example' | head -10"
                echo '=== Checking for BOOT-INF (should NOT exist) ==='
                sh "jar tf target/erinspetitions.war | grep 'BOOT-INF' && echo 'ERROR: BOOT-INF found!' && exit 1 || echo 'OK: No BOOT-INF (correct for external Tomcat)'"
                echo '=== Checking for Spring Boot loader classes (should NOT exist at root) ==='
                sh "jar tf target/erinspetitions.war | grep '^org/springframework/boot/loader' && echo 'WARNING: Spring Boot loader found at root' || echo 'OK: No Spring Boot loader at root'"
                echo '=== Checking WAR file size ==='
                sh "ls -lh target/erinspetitions.war"
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
                echo '=== Stopping and removing old container ==='
                sh "docker rm -f erinspetitions-tomcat || true"

                echo '=== Removing old Docker image ==='
                sh "docker rmi -f erinspetitions:latest || true"

                echo '=== Building new Docker image ==='
                sh "docker build --no-cache -f Dockerfile -t erinspetitions:latest ."

                echo '=== Starting new container ==='
                sh "docker run --name erinspetitions-tomcat -p 9090:8080 --detach erinspetitions:latest"

                echo '=== Waiting for Tomcat to start (30 seconds) ==='
                sh "sleep 30"

                echo '=== Checking container status ==='
                sh "docker ps | grep erinspetitions-tomcat || echo 'WARNING: Container not running!'"

                echo '=== Checking deployed WAR contents ==='
                sh "docker exec erinspetitions-tomcat ls -la /usr/local/tomcat/webapps/ || echo 'Failed to list webapps'"
                sh "docker exec erinspetitions-tomcat ls -la /usr/local/tomcat/webapps/ROOT/ 2>/dev/null || echo 'ROOT folder not found or not extracted yet'"

                echo '=== Tomcat catalina.out logs ==='
                sh "docker exec erinspetitions-tomcat cat /usr/local/tomcat/logs/catalina.out 2>/dev/null | tail -100 || echo 'No catalina.out found'"

                echo '=== Docker container logs ==='
                sh "docker logs erinspetitions-tomcat 2>&1 | tail -100"

                echo '=== Testing HTTP response ==='
                sh "curl -I http://localhost:9090/ 2>/dev/null | head -5 || echo 'Curl failed - server may not be ready'"
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
