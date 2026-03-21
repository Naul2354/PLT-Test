#!/bin/bash
# Install Chrome in Jenkins Docker container
# Run this on your EC2 instance

CONTAINER_ID="d39979b28bbf"

echo "Installing Chrome in Jenkins container..."

docker exec -u root $CONTAINER_ID bash -c '
# Update and install dependencies
apt-get update
apt-get install -y wget gnupg unzip ca-certificates

# Add Google Chrome repository (modern method without apt-key)
wget -q -O /tmp/google-chrome.gpg https://dl-ssl.google.com/linux/linux_signing_key.pub
mkdir -p /etc/apt/keyrings
cat /tmp/google-chrome.gpg > /etc/apt/keyrings/google-chrome.gpg

echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list

# Install Chrome
apt-get update
apt-get install -y google-chrome-stable

# Verify installation
echo "Chrome version:"
google-chrome --version

# Install ChromeDriver
CHROME_VERSION=$(google-chrome --version | awk '\''{print $3}'\'')
CHROME_MAJOR=$(echo $CHROME_VERSION | cut -d. -f1)
echo "Installing ChromeDriver for Chrome major version: $CHROME_MAJOR"

# Get latest ChromeDriver version for this Chrome major version
CHROMEDRIVER_VERSION=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_MAJOR")
echo "ChromeDriver version: $CHROMEDRIVER_VERSION"

# Download and install ChromeDriver
cd /tmp
wget -q "https://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip"
unzip -o chromedriver_linux64.zip
chmod +x chromedriver
mv chromedriver /usr/local/bin/

# Verify ChromeDriver
echo "ChromeDriver version:"
chromedriver --version

echo "✓ Chrome and ChromeDriver installed successfully!"
'

echo "✓ Installation complete!"
