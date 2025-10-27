[CmdletBinding()]
param(
    [ValidateSet('serve', 'build')]
    [string]$Mode = 'serve',
    [switch]$InstallDependencies,
    [switch]$StartNginx,
    [string]$FrontendDir,
    [string]$NginxRoot
)

$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path -Path (Join-Path $PSScriptRoot '..\..')).Path

if (-not $FrontendDir) {
    $FrontendDir = Join-Path $repoRoot 'project-sky-admin-vue-ts'
}
$FrontendDir = (Resolve-Path -Path $FrontendDir).Path

if (-not $NginxRoot) {
    $NginxRoot = Join-Path $repoRoot 'nginx-1.20.2'
}

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    throw "npm is not available. Install Node.js 16 (or LTS) before running this script."
}

if ($InstallDependencies) {
    Write-Host "Installing npm dependencies..."
    Push-Location $FrontendDir
    & npm install --legacy-peer-deps
    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        throw "npm install failed with exit code $LASTEXITCODE."
    }
    Pop-Location
}

switch ($Mode) {
    'serve' {
        Write-Host "Starting Vue dev server..."
        Start-Process -FilePath "powershell.exe" `
            -ArgumentList "-NoExit", "-Command", "cd `"$FrontendDir`"; npm run serve" `
            -WorkingDirectory $FrontendDir `
            -WindowStyle Normal
    }
    'build' {
        Write-Host "Building production bundle..."
        Push-Location $FrontendDir
        & npm run build
        if ($LASTEXITCODE -ne 0) {
            Pop-Location
            throw "npm run build failed with exit code $LASTEXITCODE."
        }
        Pop-Location

        $distFolder = Join-Path $FrontendDir 'dist'
        if (-not (Test-Path $distFolder)) {
            throw "Build output folder '$distFolder' was not generated."
        }

        $target = Join-Path (Resolve-Path -Path $NginxRoot).Path 'html\sky'
        if (-not (Test-Path $target)) {
            New-Item -ItemType Directory -Force -Path $target | Out-Null
        }

        Write-Host "Syncing dist/ => $target"
        Get-ChildItem -Path $target -Force | Remove-Item -Force -Recurse
        Copy-Item -Path (Join-Path $distFolder '*') -Destination $target -Recurse

        if ($StartNginx) {
            $nginxExe = Join-Path $NginxRoot 'nginx.exe'
            if (-not (Test-Path $nginxExe)) {
                throw "Cannot find nginx.exe under $NginxRoot."
            }
            Write-Host "Starting Nginx..."
            Start-Process -FilePath $nginxExe -WorkingDirectory $NginxRoot
        }
    }
}

Write-Host "Frontend task '$Mode' completed. Active processes (if any) continue in their own windows."
