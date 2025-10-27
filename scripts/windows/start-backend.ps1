[CmdletBinding()]
param(
    [string[]]$Services = @('gateway', 'user-service', 'product-service', 'order-service'),
    [switch]$IncludeSkyServer,
    [switch]$SkipPortCheck
)

$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path -Path (Join-Path $PSScriptRoot '..\..')).Path

$serviceMap = @{
    'gateway'        = @{ Path = 'mealexpress-gateway';      Port = 8088 }
    'user-service'   = @{ Path = 'mealexpress-user-service'; Port = 8081 }
    'product-service' = @{ Path = 'mealexpress-product-service'; Port = 8082 }
    'order-service'  = @{ Path = 'mealexpress-order-service'; Port = 8083 }
    'sky-server'     = @{ Path = 'sky-server';               Port = 8080 }
}

if ($IncludeSkyServer.IsPresent) {
    $Services += 'sky-server'
}
$Services = $Services | Where-Object { $_ } | Select-Object -Unique

if (-not $Services) {
    throw "No services specified."
}

function Resolve-MavenCommand {
    param([string]$ModulePath)
    $wrapper = Join-Path $ModulePath 'mvnw.cmd'
    if (Test-Path $wrapper) {
        return '.\mvnw.cmd'
    }
    if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
        throw "Maven is not available and mvnw.cmd is missing under $ModulePath."
    }
    return 'mvn'
}

function Ensure-PortFree {
    param(
        [int]$Port,
        [string]$ServiceName
    )
    $inUse = Test-NetConnection -ComputerName '127.0.0.1' -Port $Port -WarningAction SilentlyContinue
    if ($inUse.TcpTestSucceeded) {
        throw "Port $Port needed by $ServiceName is already in use. Stop the other process or override the port before continuing."
    }
}

foreach ($service in $Services) {
    if (-not $serviceMap.ContainsKey($service)) {
        throw "Unknown service '$service'. Allowed values: $($serviceMap.Keys -join ', ')"
    }

    $config = $serviceMap[$service]
    $modulePath = Resolve-Path -Path (Join-Path $repoRoot $config.Path)
    if (-not $SkipPortCheck.IsPresent) {
        Ensure-PortFree -Port $config.Port -ServiceName $service
    }

    $mavenCmd = Resolve-MavenCommand -ModulePath $modulePath.Path
    $commandText = "cd `"$($modulePath.Path)`"; $mavenCmd spring-boot:run -DskipTests"

    Write-Host "Launching $service on port $($config.Port)..."
    Start-Process -FilePath "powershell.exe" `
        -ArgumentList "-NoExit", "-Command", $commandText `
        -WorkingDirectory $modulePath.Path `
        -WindowStyle Normal
}

Write-Host "Requested services are starting in separate PowerShell windows."
