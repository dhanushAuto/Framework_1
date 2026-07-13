pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        SONAR_HOST_URL = 'http://localhost:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean install -DskipTests'
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonar') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('AI Sonar Analysis & Auto-Fix') {
            steps {
                // Run the AI Sonar analysis which now generates patches and verifies fixes
                sh 'mvn test -Dtest=SonarAITestRunner'
            }
        }

        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: 'reports/patches/*.patch, target/extent-report.html', allowEmptyArchive: true
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'reports', reportFiles: 'extent-report.html', reportName: 'AI Sonar Report'])
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
