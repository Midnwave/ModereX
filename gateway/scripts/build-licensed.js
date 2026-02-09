#!/usr/bin/env node

/**
 * ModereX Licensed Build Script
 *
 * Builds a licensed JAR with embedded token for dev testing.
 * This script runs on the gateway server and creates a licensed build
 * from the latest GitHub code.
 *
 * Usage: node build-licensed.js <license-token> [tester-name]
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

// Configuration
const PLUGIN_REPO_URL = 'https://github.com/Midnwave/ModereX.git';
const BUILD_DIR = path.join(__dirname, '../../temp-build');
const LICENSED_BUILDS_DIR = path.join(__dirname, '../licensed-builds');
const GATEWAY_DB_PATH = path.join(__dirname, '../gateway-database.db'); // Adjust if different

// ANSI color codes
const colors = {
  reset: '\x1b[0m',
  bright: '\x1b[1m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  red: '\x1b[31m',
  cyan: '\x1b[36m',
};

function log(message, color = 'reset') {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

function error(message) {
  log(`❌ ${message}`, 'red');
}

function success(message) {
  log(`✓ ${message}`, 'green');
}

function info(message) {
  log(`ℹ ${message}`, 'cyan');
}

function warn(message) {
  log(`⚠ ${message}`, 'yellow');
}

function reportProgress(stage, progress, message) {
  // Output structured JSON for gateway to parse
  console.log(`__PROGRESS__${JSON.stringify({ stage, progress, message })}__PROGRESS__`);
}

// Parse arguments
const args = process.argv.slice(2);
if (args.length < 1) {
  error('Usage: node build-licensed.js <license-token> [tester-name]');
  process.exit(1);
}

const licenseToken = args[0];
const testerName = args[1] || 'Unknown';

// Validate UUID format
const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
if (!uuidRegex.test(licenseToken)) {
  error('Invalid license token format (must be UUID v4)');
  process.exit(1);
}

async function main() {
  log('\n╔════════════════════════════════════════════════════════════╗', 'bright');
  log('║          ModereX Licensed Build Generator                ║', 'bright');
  log('╚════════════════════════════════════════════════════════════╝\n', 'bright');

  info(`License Token: ${licenseToken.substring(0, 8)}...${licenseToken.substring(licenseToken.length - 4)}`);
  info(`Tester Name: ${testerName}\n`);

  try {
    reportProgress('init', 0, 'Starting build...');

    // Step 1: Verify license token exists in gateway database
    info('Step 1: Verifying license token...');
    reportProgress('verify', 10, 'Verifying license token...');
    // TODO: Add database check here if gateway uses SQL
    // For now, we'll skip this check
    success('License token verified (skipped for now - implement gateway DB check)\n');

    // Step 2: Clean and prepare build directory
    info('Step 2: Preparing build directory...');
    reportProgress('prepare', 20, 'Preparing build directory...');
    if (fs.existsSync(BUILD_DIR)) {
      fs.rmSync(BUILD_DIR, { recursive: true, force: true });
    }
    fs.mkdirSync(BUILD_DIR, { recursive: true });
    success(`Build directory created: ${BUILD_DIR}\n`);

    // Step 3: Clone the repository
    info('Step 3: Cloning ModereX repository...');
    reportProgress('clone', 30, 'Copying repository...');
    try {
      execSync(`git clone --depth 1 ${PLUGIN_REPO_URL} ${BUILD_DIR}`, {
        stdio: 'inherit',
      });
      success('Repository cloned successfully\n');
    } catch (err) {
      warn('Git clone failed - trying local copy instead...');

      // Fallback: copy from parent directory (if running in gateway/scripts)
      const localRepo = path.join(__dirname, '../../');
      if (fs.existsSync(path.join(localRepo, 'app'))) {
        info('Using local repository copy...');
        copyRecursive(localRepo, BUILD_DIR);
        success('Local repository copied successfully\n');
      } else {
        throw new Error('Neither git clone nor local copy available');
      }
    }

    // Step 4: Run Gradle build with license token
    info('Step 4: Building licensed JAR with Gradle...');
    reportProgress('build', 50, 'Building JAR with Gradle (this may take 1-2 minutes)...');
    const gradleCmd = process.platform === 'win32' ? '.\\gradlew.bat' : './gradlew';
    const buildCommand = `${gradleCmd} clean buildLicensed --no-configuration-cache -PlicenseToken=${licenseToken}`;

    info(`Running: ${buildCommand}`);
    execSync(buildCommand, {
      cwd: BUILD_DIR,
      stdio: 'inherit',
      shell: true,
    });
    success('JAR built successfully\n');

    // Step 5: Copy built JAR to licensed-builds directory
    info('Step 5: Copying JAR to licensed-builds directory...');
    reportProgress('finalize', 85, 'Finalizing build...');
    fs.mkdirSync(LICENSED_BUILDS_DIR, { recursive: true });

    const shortToken = licenseToken.substring(0, 8);
    const sourcePath = path.join(BUILD_DIR, 'releases', 'licensed', `ModereX-licensed-${shortToken}.jar`);
    const destPath = path.join(LICENSED_BUILDS_DIR, `ModereX-licensed-${shortToken}.jar`);

    if (!fs.existsSync(sourcePath)) {
      throw new Error(`Built JAR not found at: ${sourcePath}`);
    }

    fs.copyFileSync(sourcePath, destPath);
    const fileSize = (fs.statSync(destPath).size / (1024 * 1024)).toFixed(2);
    success(`JAR copied to: ${destPath} (${fileSize} MB)\n`);

    // Step 6: Record build metadata
    info('Step 6: Recording build metadata...');
    const metadata = {
      token: licenseToken,
      testerName,
      buildTimestamp: Date.now(),
      jarFilename: `ModereX-licensed-${shortToken}.jar`,
      jarPath: destPath,
      fileSize: `${fileSize} MB`,
    };

    const metadataPath = path.join(LICENSED_BUILDS_DIR, `ModereX-licensed-${shortToken}.json`);
    fs.writeFileSync(metadataPath, JSON.stringify(metadata, null, 2));
    success(`Metadata saved to: ${metadataPath}\n`);

    // Step 7: Clean up temporary build directory
    info('Step 7: Cleaning up...');
    reportProgress('cleanup', 95, 'Cleaning up...');
    fs.rmSync(BUILD_DIR, { recursive: true, force: true });
    success('Temporary build files removed\n');

    // Done!
    reportProgress('complete', 100, 'Build complete!');
    log('\n╔════════════════════════════════════════════════════════════╗', 'green');
    log('║            ✓ Licensed Build Complete!                     ║', 'green');
    log('╚════════════════════════════════════════════════════════════╝\n', 'green');

    success(`Licensed JAR: ${destPath}`);
    success(`File Size: ${fileSize} MB`);
    success(`Tester: ${testerName}`);
    success(`Token: ${shortToken}...${licenseToken.substring(licenseToken.length - 4)}\n`);

  } catch (err) {
    error(`\nBuild failed: ${err.message}`);
    error(err.stack);
    process.exit(1);
  }
}

// Helper: Recursive copy
function copyRecursive(src, dest) {
  const exists = fs.existsSync(src);
  const stats = exists && fs.statSync(src);
  const isDirectory = exists && stats.isDirectory();

  if (isDirectory) {
    if (!fs.existsSync(dest)) {
      fs.mkdirSync(dest, { recursive: true });
    }
    fs.readdirSync(src).forEach((childItemName) => {
      // Skip node_modules, .git, build directories
      if (['node_modules', '.git', 'build', '.gradle', 'temp-build', 'licensed-builds'].includes(childItemName)) {
        return;
      }
      copyRecursive(path.join(src, childItemName), path.join(dest, childItemName));
    });
  } else {
    fs.copyFileSync(src, dest);
  }
}

// Run the script
main();
