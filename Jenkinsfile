pipeline {
    agent any
    options {

    timeout(time: 60, unit: 'MINUTES')

    timestamps()

    skipDefaultCheckout()

    buildDiscarder(logRotator(
                    daysToKeepStr: '15',
                    numToKeepStr: '30',
                    artifactDaysToKeepStr: '15',
                    artifactNumToKeepStr: '10'
                ))
            }

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }
    
    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['Smoke','Regression','Sanity','API','UI']
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Select browser for execution'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['DEV', 'SIT', 'QA'],
            description: 'Select environment for execution'
        )
        booleanParam(
            name: 'PARALLEL_EXECUTION',
            defaultValue: true,
            description: 'Enable parallel test execution'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    def testSuite = params.TEST_SUITE
                    def browser = params.BROWSER
                    def environment = params.ENVIRONMENT
                    def parallel = params.PARALLEL_EXECUTION
                    
                    bat "mvn clean verify -Dbrowser=${browser} -Denvironment=${environment}"
                }
            }
        }

//         stage('SonarQube Analysis') {
//             steps {
//                 withSonarQubeEnv('SonarQube') {
//                     bat 'mvn sonar:sonar'
//                 }
//             }
//         }

//         stage('Quality Gate') {
//             steps {
//                 timeout(time: 5, unit: 'MINUTES') {
//                     waitForQualityGate abortPipeline: true
//                 }
//             }
//         }
//         stage('Publish Allure') {
//
//             steps {
//
//                 allure(
//
//                     includeProperties: false,
//
//                     results: [[path: 'target/allure-results']]
//
//                 )
//
//             }
//
//         }

                stage('Publish Extent Report') {
                    steps {
                        publishHTML(target: [
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'reports',
                            reportFiles: 'ExtentReport.html',
                            reportName: 'Extent Report'
                        ])
                    }
                }
            }

 post {

     always {

         archiveArtifacts artifacts: '''
         target/ExtentReport/**
         target/screenshots/**
         target/dependency-check-report.html
         ''', allowEmptyArchive: true
             )
                }
            }
//                publishCoverage adapters: [
//
//                    jacocoAdapter('target/site/jacoco/jacoco.xml')

  //             ]
               publishHTML(target: [

                   reportDir: 'target',

                   reportFiles: 'dependency-check-report.html',

                   reportName: 'OWASP Report',

                   keepAll: true,

                   alwaysLinkToLastBuild: true

               ])
     }

     success {

         emailext(

             subject: "✅ ${env.JOB_NAME} | SUCCESS | Build #${env.BUILD_NUMBER}",

             mimeType: 'text/html',

             to: 'dhanush.testing100@gmail.com',

             body: """
             <html>

             <body style="font-family:Arial;">

             <h2 style="color:green;">
             Automation Execution Successful
             </h2>

             <table border="1"
                    cellpadding="8"
                    cellspacing="0"
                    style="border-collapse:collapse;">

                 <tr bgcolor="#DFF0D8">
                     <th>Property</th>
                     <th>Value</th>
                 </tr>

                 <tr>
                     <td><b>Project</b></td>
                     <td>${env.JOB_NAME}</td>
                 </tr>

                 <tr>
                     <td><b>Build Number</b></td>
                     <td>#${env.BUILD_NUMBER}</td>
                 </tr>

                 <tr>
                     <td><b>Status</b></td>
                     <td style="color:green;"><b>SUCCESS</b></td>
                 </tr>

                 <tr>
                     <td><b>Build URL</b></td>
                     <td>
                     <a href="${env.BUILD_URL}">
                     ${env.BUILD_URL}
                     </a>
                     </td>
                 </tr>

             </table>

             <br>

             <h3>Attachments</h3>

             <ul>
                 <li>Extent Report</li>
             </ul>

             <br>

             <i>
             This is an automated email generated by Jenkins.
             </i>

             </body>

             </html>
             """,

             attachmentsPattern: 'reports/ExtentReport.html'

         )
     }

     failure {

         emailext(

             subject: "❌ ${env.JOB_NAME} | FAILED | Build #${env.BUILD_NUMBER}",

             mimeType: 'text/html',

             to: 'dhanush.testing100@gmail.com',

             body: """
             <html>

             <body style="font-family:Arial;">

             <h2 style="color:red;">
             Automation Execution Failed
             </h2>

             <table border="1"
                    cellpadding="8"
                    cellspacing="0"
                    style="border-collapse:collapse;">

                 <tr bgcolor="#F2DEDE">
                     <th>Property</th>
                     <th>Value</th>
                 </tr>

                 <tr>
                     <td><b>Project</b></td>
                     <td>${env.JOB_NAME}</td>
                 </tr>

                 <tr>
                     <td><b>Build Number</b></td>
                     <td>#${env.BUILD_NUMBER}</td>
                 </tr>

                 <tr>
                     <td><b>Status</b></td>
                     <td style="color:red;"><b>FAILED</b></td>
                 </tr>

                 <tr>
                     <td><b>Build URL</b></td>
                     <td>
                     <a href="${env.BUILD_URL}">
                     ${env.BUILD_URL}
                     </a>
                     </td>
                 </tr>

             </table>

             <br>

             <h3>Attachments</h3>

             <ul>
                 <li>Extent Report</li>
             </ul>

             <br>

             <i>
             Please check the attached report.
             </i>

             </body>

             </html>
             """,

             attachmentsPattern: 'reports/ExtentReport.html'
         )
     }

 }
}