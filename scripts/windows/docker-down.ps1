[CmdletBinding()]
param(
    [string]$ComposeFile,
    [switch]$RemoveVolumes,
    [switch]$RemoveImages
)

$ErrorActionPreference = 'Stop'

function Resolve-ComposeFile {
    param([string]$PathFromUser)
    if ($PathFromUser) {
        return (Resolve-Path -Path $PathFromUser).Path
    }
    $repoRoot = (Resolve-Path -Path (Join-Path $PSScriptRoot '..\..')).Path
    $defaultPath = Join-Path (Split-Path -Parent $repoRoot) 'docker-compose.yml'
    if (-not (Test-Path $defaultPath)) {
        throw "Cannot find docker-compose.yml. Pass -ComposeFile to point to the correct location."
    }
    return (Resolve-Path -Path $defaultPath).Path
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "docker command is not available. Install Docker Desktop and retry."
}

$composeFilePath = Resolve-ComposeFile -PathFromUser $ComposeFile
Write-Host "Using compose file: $composeFilePath"

$args = @('compose', '-f', $composeFilePath, 'down', '--remove-orphans')
if ($RemoveVolumes.IsPresent) {
    $args += '--volumes'
}
if ($RemoveImages.IsPresent) {
    $args += @('--rmi', 'all')
}

& docker @args
if ($LASTEXITCODE -ne 0) {
    throw "docker $($args -join ' ') failed with exit code $LASTEXITCODE."
}

Write-Host "Docker stack is stopped."
