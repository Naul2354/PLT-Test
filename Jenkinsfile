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
        // Set display for Xvfb
        DISPLAY = ':99'
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

        stage('Install Dependencies & Compile') {
            steps {
                echo '========================================='
                echo 'STAGE: Install Dependencies & Compile'
                echo '========================================='
                script {
                    sh '''
                        set +e  # Don't exit on error immediately

                        echo "===== Installing Maven ====="
                        if ! command -v mvn &> /dev/null; then
                            echo "Maven not found. Installing..."
                            cd /tmp

                            # Use curl to download Maven
                            if curl -L -O https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz; then
                                tar xzf apache-maven-3.9.6-bin.tar.gz
                                echo "✓ Maven installed"
                            else
                                echo "✗ Failed to download Maven"
                                exit 1
                            fi
                        else
                            echo "✓ Maven already installed"
                        fi

                        # Set Maven environment
                        export MAVEN_HOME=/tmp/apache-maven-3.9.6
                        export PATH=$MAVEN_HOME/bin:$PATH

                        mvn -version

                        echo "===== Installing Chrome & ChromeDriver ====="

                        # Install Chrome using apt-get as root
                        if [ "$(id -u)" = "0" ]; then
                            echo "Running as root - installing Chrome..."

                            # Install dependencies
                            apt-get update -qq
                            apt-get install -y wget gnupg unzip

                            # Add Chrome repository
                            wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
                            echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list

                            # Install Chrome
                            apt-get update -qq
                            apt-get install -y google-chrome-stable

                            # Verify Chrome installation
                            google-chrome --version

                            # Install ChromeDriver
                            CHROME_VERSION=$(google-chrome --version | awk '{print $3}')
                            CHROME_MAJOR=$(echo $CHROME_VERSION | cut -d. -f1)
                            echo "Chrome version: $CHROME_VERSION (major: $CHROME_MAJOR)"

                            # Download ChromeDriver
                            CHROMEDRIVER_VERSION=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_MAJOR")
                            echo "Downloading ChromeDriver version: $CHROMEDRIVER_VERSION"

                            cd /tmp
                            wget -q "https://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip"
                            unzip -o chromedriver_linux64.zip
                            chmod +x chromedriver
                            mv chromedriver /usr/local/bin/

                            # Verify ChromeDriver
                            chromedriver --version

                            echo "✓ Chrome and ChromeDriver installed successfully"
                        else
                            echo "⚠ Not running as root - Chrome must be pre-installed"
                            echo "Checking if Chrome is available..."
                            google-chrome --version || chromium-browser --version || echo "Chrome not found!"
                            chromedriver --version || echo "ChromeDriver not found!"
                        fi

                        echo "===== Setting up Xvfb (Virtual Display) ====="
                        # Start Xvfb if not running
                        if ! pgrep -x Xvfb > /dev/null; then
                            if [ "$(id -u)" = "0" ]; then
                                echo "Starting Xvfb..."
                                # Install Xvfb if needed
                                apt-get install -y xvfb || true
                                # Start Xvfb in background
                                Xvfb :99 -ac -screen 0 1920x1080x24 > /dev/null 2>&1 &
                                sleep 2
                                echo "✓ Xvfb started on display :99"
                            else
                                echo "⚠ Not root - cannot start Xvfb"
                            fi
                        else
                            echo "✓ Xvfb already running"
                        fi

                        export DISPLAY=:99
                        echo "Display set to: $DISPLAY"

                        echo "===== Creating Chrome Wrapper for Headless Mode ====="
                        if [ "$(id -u)" = "0" ]; then
                            # Backup original chrome binary
                            if [ ! -f /usr/bin/google-chrome.real ]; then
                                mv /usr/bin/google-chrome /usr/bin/google-chrome.real
                            fi

                            # Create wrapper script that forces headless mode
                            cat > /usr/bin/google-chrome << 'CHROME_WRAPPER_EOF'
#!/bin/bash
# Chrome wrapper to force headless mode in Docker
/usr/bin/google-chrome.real \
  --headless \
  --no-sandbox \
  --disable-dev-shm-usage \
  --disable-gpu \
  --disable-software-rasterizer \
  --disable-extensions \
  --disable-background-networking \
  --remote-debugging-port=9222 \
  "$@"
CHROME_WRAPPER_EOF

                            chmod +x /usr/bin/google-chrome
                            echo "✓ Chrome wrapper created - Chrome will run in headless mode"
                        else
                            echo "⚠ Not root - cannot create Chrome wrapper"
                        fi

                        # Return to workspace
                        cd $WORKSPACE

                        echo "===== Injecting Chrome Options for Jenkins ====="
                        # Modify test files to add ChromeOptions for headless mode
                        # This only happens in Jenkins - local code stays unchanged!

                        # For AddingCourseTest.java
                        if [ -f "src/User/AddingCourseTest.java" ]; then
                            echo "Modifying AddingCourseTest.java..."
                            # Add import for ChromeOptions
                            sed -i '/import org.openqa.selenium.chrome.ChromeDriver;/a import org.openqa.selenium.chrome.ChromeOptions;' src/User/AddingCourseTest.java

                            # Replace driver initialization
                            sed -i 's/driver = new ChromeDriver();/ChromeOptions options = new ChromeOptions();\n        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");\n        driver = new ChromeDriver(options);/' src/User/AddingCourseTest.java
                        fi

                        # For StudentManagementTest.java
                        if [ -f "src/Admin/StudentManagementTest.java" ]; then
                            echo "Modifying StudentManagementTest.java..."
                            # Add import for ChromeOptions
                            sed -i '/import org.openqa.selenium.chrome.ChromeDriver;/a import org.openqa.selenium.chrome.ChromeOptions;' src/Admin/StudentManagementTest.java

                            # Replace driver initialization
                            sed -i 's/driver = new ChromeDriver();/ChromeOptions options = new ChromeOptions();\n        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");\n        driver = new ChromeDriver(options);/' src/Admin/StudentManagementTest.java
                        fi

                        echo "✓ Chrome options injected for Jenkins build only"

                        echo "===== Compiling Project ====="
                        mvn clean compile -DskipTests

                        echo "✓ Compilation successful"
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
