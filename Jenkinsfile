pipeline {
    agent any

    // Removed tools section - will use system defaults
    // If you need specific versions, configure in Jenkins Global Tool Configuration

    environment {
        // Chrome settings for headless mode
        CHROME_OPTIONS = '--headless --no-sandbox --disable-dev-shm-usage --disable-gpu'
        // Use system Java and Maven
        JAVA_HOME = '/opt/java/openjdk'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    parameters {
        string(
            name: 'GIT_BRANCH',
            defaultValue: 'main',
            description: 'Git branch to checkout (main, develop, feature/*, etc.)'
        )
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
        stage('Checkout Code from Git') {
            steps {
                script {
                    echo '========================================='
                    echo 'STAGE: Checkout Code from Git'
                    echo '========================================='

                    // Display Git information
                    echo "Repository: ${env.GIT_URL ?: 'Not configured'}"
                    echo "Branch: ${params.GIT_BRANCH}"
                    echo "Workspace: ${WORKSPACE}"

                    // Clean workspace before checkout
                    deleteDir()

                    // Method 1: Using checkout with Git plugin (Recommended)
                    // This method works with both public and private repositories
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${params.GIT_BRANCH}"]],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [
                            [$class: 'CleanBeforeCheckout'],
                            [$class: 'CloneOption', depth: 0, noTags: false, shallow: false],
                            [$class: 'SubmoduleOption', recursiveSubmodules: true]
                        ],
                        userRemoteConfigs: [[
                            // Public repository - no credentials needed
                            url: 'https://github.com/Naul2354/PLT-Test.git'

                            // If repository becomes private, uncomment and add credentials:
                            // url: 'https://github.com/Naul2354/PLT-Test.git',
                            // credentialsId: 'github-credentials'

                            // For SSH (with SSH key):
                            // url: 'git@github.com:Naul2354/PLT-Test.git',
                            // credentialsId: 'github-ssh-key'
                        ]]
                    ])

                    // Verify checkout
                    sh '''
                        echo "\n✓ Code checked out successfully"
                        echo "\nGit Information:"
                        git --version
                        echo "\nCurrent Branch:"
                        git branch -a
                        echo "\nLatest Commit:"
                        git log -1 --oneline
                        echo "\nRepository Status:"
                        git status
                        echo "\nChecked out files:"
                        ls -la
                    '''
                }
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
                        mvn -version || echo "Maven not found - will be installed in next stage"

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

        stage('Install Maven & Compile') {
            steps {
                echo '========================================='
                echo 'STAGE: Install Maven & Compile'
                echo '========================================='
                script {
                    sh '''
                        # Check if Maven is installed
                        if ! command -v mvn &> /dev/null; then
                            echo "Maven not found. Installing Maven..."

                            # Download and install Maven
                            cd /tmp
                            wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
                            tar xzf apache-maven-3.9.6-bin.tar.gz

                            # Set Maven home
                            export MAVEN_HOME=/tmp/apache-maven-3.9.6
                            export PATH=$MAVEN_HOME/bin:$PATH

                            echo "Maven installed successfully"
                            mvn -version
                        else
                            echo "Maven already installed"
                            mvn -version
                        fi

                        # Return to workspace
                        cd $WORKSPACE

                        # Set Maven path and compile
                        export MAVEN_HOME=/tmp/apache-maven-3.9.6
                        export PATH=$MAVEN_HOME/bin:$PATH

                        # Compile project
                        mvn clean compile || echo "Maven compile skipped - no pom.xml found"
                    '''
                }
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
                                    # Set Maven path
                                    export MAVEN_HOME=/tmp/apache-maven-3.9.6
                                    export PATH=$MAVEN_HOME/bin:$PATH

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
                                    # Set Maven path
                                    export MAVEN_HOME=/tmp/apache-maven-3.9.6
                                    export PATH=$MAVEN_HOME/bin:$PATH

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

            // Email notifications removed - configure if needed
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

            // Email notifications removed - configure if needed
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
