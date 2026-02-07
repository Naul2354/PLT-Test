pipeline {
    agent any

    tools {
        maven 'Maven' // Configure in Jenkins: Manage Jenkins → Global Tool Configuration
        jdk 'JDK-17' // Configure in Jenkins: Manage Jenkins → Global Tool Configuration
    }

    environment {
        // Chrome settings for headless mode
        CHROME_OPTIONS = '--headless --no-sandbox --disable-dev-shm-usage --disable-gpu'
    }

    parameters {
        booleanParam(
            name: 'HEADLESS_MODE',
            defaultValue: true,
            description: 'Run Chrome in headless mode (recommended for Jenkins)'
        )
        booleanParam(
            name: 'RUN_STUDENT_MANAGEMENT',
            defaultValue: true,
            description: 'Run Student Management Test'
        )
        booleanParam(
            name: 'RUN_ADDING_COURSE',
            defaultValue: true,
            description: 'Run Adding Course Test'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                echo '========================================='
                echo 'STAGE: Checkout Code'
                echo '========================================='
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                echo '========================================='
                echo 'STAGE: Verify Environment'
                echo '========================================='
                script {
                    sh '''
                        echo "Java Version:"
                        java -version

                        echo "\nMaven Version:"
                        mvn -version

                        echo "\nChrome Version:"
                        google-chrome --version || chromium-browser --version || echo "Chrome not found"

                        echo "\nChromeDriver Version:"
                        chromedriver --version || echo "ChromeDriver not found"

                        echo "\nWorking Directory:"
                        pwd

                        echo "\nDirectory Contents:"
                        ls -la
                    '''
                }
            }
        }

        stage('Clean & Compile') {
            steps {
                echo '========================================='
                echo 'STAGE: Clean & Compile'
                echo '========================================='
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests in Parallel') {
            parallel {
                stage('Student Management Test') {
                    when {
                        expression { params.RUN_STUDENT_MANAGEMENT == true }
                    }
                    steps {
                        script {
                            echo '========================================='
                            echo 'PARALLEL TEST 1: Student Management'
                            echo '========================================='

                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                sh '''
                                    echo "Starting Student Management Test..."
                                    mvn test -Dtest=Admin.StudentManagementTest \
                                        -Dmaven.test.failure.ignore=true \
                                        -DsurefireReportFormat=xml

                                    echo "Student Management Test completed"
                                '''
                            }
                        }
                    }
                    post {
                        always {
                            echo 'Archiving Student Management Test Results...'
                            junit allowEmptyResults: true,
                                  testResults: '**/target/surefire-reports/TEST-Admin.StudentManagementTest.xml'
                        }
                        success {
                            echo '✓ Student Management Test PASSED'
                        }
                        failure {
                            echo '✗ Student Management Test FAILED'
                        }
                    }
                }

                stage('Adding Course Test') {
                    when {
                        expression { params.RUN_ADDING_COURSE == true }
                    }
                    steps {
                        script {
                            echo '========================================='
                            echo 'PARALLEL TEST 2: Adding Course'
                            echo '========================================='

                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                sh '''
                                    echo "Starting Adding Course Test..."
                                    mvn test -Dtest=User.AddingCourseTest \
                                        -Dmaven.test.failure.ignore=true \
                                        -DsurefireReportFormat=xml

                                    echo "Adding Course Test completed"
                                '''
                            }
                        }
                    }
                    post {
                        always {
                            echo 'Archiving Adding Course Test Results...'
                            junit allowEmptyResults: true,
                                  testResults: '**/target/surefire-reports/TEST-User.AddingCourseTest.xml'
                        }
                        success {
                            echo '✓ Adding Course Test PASSED'
                        }
                        failure {
                            echo '✗ Adding Course Test FAILED'
                        }
                    }
                }
            }
        }

        stage('Aggregate Test Reports') {
            steps {
                echo '========================================='
                echo 'STAGE: Aggregate Test Reports'
                echo '========================================='
                script {
                    // Publish all test results
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'

                    // Archive test artifacts
                    archiveArtifacts artifacts: '**/target/surefire-reports/**/*',
                                     allowEmptyArchive: true

                    // Archive screenshots if any
                    archiveArtifacts artifacts: '**/screenshots/**/*.png',
                                     allowEmptyArchive: true

                    echo 'Test reports aggregated successfully'
                }
            }
        }
    }

    post {
        always {
            echo '========================================='
            echo 'POST-BUILD: Cleanup'
            echo '========================================='
            script {
                sh '''
                    echo "Killing any remaining Chrome processes..."
                    pkill -f chrome || true
                    pkill -f chromedriver || true

                    echo "Listing test reports:"
                    find . -name "*.xml" -path "*/surefire-reports/*" || true
                '''
            }
        }

        success {
            echo ''
            echo '╔═══════════════════════════════════════╗'
            echo '║   ✓ ALL TESTS PASSED SUCCESSFULLY!   ║'
            echo '╚═══════════════════════════════════════╝'
            echo ''
            echo "Build Number: ${BUILD_NUMBER}"
            echo "Build URL: ${BUILD_URL}"
            echo ''

            // Send success notification (configure email settings in Jenkins)
            script {
                try {
                    emailext(
                        subject: "✓ Jenkins Build #${BUILD_NUMBER} - SUCCESS",
                        body: """
                            <h2>Build Successful!</h2>
                            <p><strong>Build Number:</strong> ${BUILD_NUMBER}</p>
                            <p><strong>Build URL:</strong> <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                            <p><strong>Tests Executed:</strong></p>
                            <ul>
                                <li>Student Management Test: ${params.RUN_STUDENT_MANAGEMENT ? '✓ Executed' : '○ Skipped'}</li>
                                <li>Adding Course Test: ${params.RUN_ADDING_COURSE ? '✓ Executed' : '○ Skipped'}</li>
                            </ul>
                            <p><strong>Test Results:</strong> <a href="${BUILD_URL}testReport/">View Test Report</a></p>
                        """,
                        to: 'team@example.com',
                        mimeType: 'text/html',
                        attachLog: false
                    )
                } catch (Exception e) {
                    echo "Email notification not configured: ${e.message}"
                }
            }
        }

        failure {
            echo ''
            echo '╔═══════════════════════════════════════╗'
            echo '║      ✗ BUILD FAILED!                  ║'
            echo '╚═══════════════════════════════════════╝'
            echo ''
            echo "Build Number: ${BUILD_NUMBER}"
            echo "Build URL: ${BUILD_URL}"
            echo "Console Output: ${BUILD_URL}console"
            echo ''

            // Send failure notification
            script {
                try {
                    emailext(
                        subject: "✗ Jenkins Build #${BUILD_NUMBER} - FAILED",
                        body: """
                            <h2>Build Failed!</h2>
                            <p><strong>Build Number:</strong> ${BUILD_NUMBER}</p>
                            <p><strong>Build URL:</strong> <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                            <p><strong>Console Output:</strong> <a href="${BUILD_URL}console">View Console</a></p>
                            <p><strong>Tests Executed:</strong></p>
                            <ul>
                                <li>Student Management Test: ${params.RUN_STUDENT_MANAGEMENT ? 'Executed' : 'Skipped'}</li>
                                <li>Adding Course Test: ${params.RUN_ADDING_COURSE ? 'Executed' : 'Skipped'}</li>
                            </ul>
                            <p><strong>Test Results:</strong> <a href="${BUILD_URL}testReport/">View Test Report</a></p>
                        """,
                        to: 'team@example.com',
                        mimeType: 'text/html',
                        attachLog: true
                    )
                } catch (Exception e) {
                    echo "Email notification not configured: ${e.message}"
                }
            }
        }

        unstable {
            echo ''
            echo '╔═══════════════════════════════════════╗'
            echo '║   ⚠ BUILD UNSTABLE - Tests Failed    ║'
            echo '╚═══════════════════════════════════════╝'
            echo ''
            echo "Some tests failed. Check test report at: ${BUILD_URL}testReport/"
            echo ''
        }

        cleanup {
            echo 'Performing final cleanup...'
            cleanWs(
                deleteDirs: true,
                patterns: [
                    [pattern: '**/target/**', type: 'INCLUDE'],
                    [pattern: '**/.settings/**', type: 'INCLUDE']
                ]
            )
        }
    }
}
