post {

    always {

        archiveArtifacts artifacts: 'target/**/*', allowEmptyArchive: true

        publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/ExtentReport',
                reportFiles: 'index.html',
                reportName: 'Extent Report'
        ])
    }

    success {
        echo 'Build completed successfully.'
    }

    failure {
        echo 'Build failed.'
    }
}