[CmdletBinding()]
param(
    [string]$ComposeFile,
    [switch]$PullImages,
    [switch]$ForceRecreate,
    [switch]$SkipHealthChecks
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
        throw "Cannot find docker-compose.yml. Pass -ComposeFile explicitly."
    }
    return (Resolve-Path -Path $defaultPath).Path
}

function Assert-Command {
    param([string]$CommandName)
    if (-not (Get-Command $CommandName -ErrorAction SilentlyContinue)) {
        throw "Required command '$CommandName' is not available in PATH."
    }
}

function Invoke-DockerCompose {
    param([string[]]$Args)
    & docker @Args
    if ($LASTEXITCODE -ne 0) {
        throw "docker $($Args -join ' ') failed with exit code $LASTEXITCODE."
    }
}

function Wait-ForContainer {
    param(
        [string]$Name,
        [int]$TimeoutSeconds = 240
    )

    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        $inspect = & docker inspect --format '{{json .State}}' $Name 2>$null
        if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($inspect)) {
            Start-Sleep -Seconds 3
            continue
        }

        $state = $inspect | ConvertFrom-Json
        if ($state.Status -eq 'running') {
            if ($state.Health) {
                $health = $state.Health.Status
                if ($health -eq 'healthy') {
                    return
                }
                if ($health -eq 'unhealthy') {
                    throw "$Name is unhealthy according to Docker health checks."
                }
            }
            else {
                return
            }
        }
        Start-Sleep -Seconds 5
    }
    throw "Timed out waiting for container '$Name' to become healthy."
}

Assert-Command -CommandName 'docker'

$composeFilePath = Resolve-ComposeFile -PathFromUser $ComposeFile
Write-Host "Using compose file: $composeFilePath"

$composeArgs = @('compose', '-f', $composeFilePath, 'up', '-d', '--remove-orphans')
if ($PullImages.IsPresent) {
    $composeArgs += @('--pull', 'always')
}
if ($ForceRecreate.IsPresent) {
    $composeArgs += '--force-recreate'
}

Invoke-DockerCompose -Args $composeArgs
Write-Host "Docker services are starting..."

if (-not $SkipHealthChecks) {
    $containers = @(
        'mysql',
        'redis',
        'elasticsearch',
        'kibana',
        'nacos',
        'rocketmq-namesrv',
        'rocketmq-broker',
        'seata-server',
        'sentinel-dashboard'
    )

    foreach ($container in $containers) {
        Write-Host "Waiting for $container to become healthy..."
        try {
            Wait-ForContainer -Name $container
            Write-Host "  $container is ready."
        }
        catch {
            Write-Warning $_.Exception.Message
        }
    }
}
else {
    Write-Host "Skipping container health checks as requested."
}

Write-Host "Infrastructure stack is up."
