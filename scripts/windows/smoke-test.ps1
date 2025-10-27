[CmdletBinding()]
[CmdletBinding()]
param(
    [string]$GatewayBaseUrl = 'http://localhost:8088',
[string]$FrontendUrl = 'http://localhost/',
    [switch]$SkipDocker,
    [switch]$SkipRocketMQ
)

$ErrorActionPreference = 'Stop'

$httpTargets = @(
    @{ Name = 'Gateway';       Url = "$GatewayBaseUrl/actuator/health" },
    @{ Name = 'User Service';  Url = 'http://localhost:8081/actuator/health' },
    @{ Name = 'Product Service'; Url = 'http://localhost:8082/actuator/health' },
    @{ Name = 'Order Service'; Url = 'http://localhost:8083/actuator/health' },
    @{ Name = 'Frontend (Nginx)'; Url = $FrontendUrl },
    @{ Name = 'Sentinel Dashboard'; Url = 'http://localhost:8858/' }
)

function Test-HttpEndpoint {
    param(
        [string]$Name,
        [string]$Url
    )
    Write-Host "Checking $Name => $Url"
    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
        if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 400) {
            Write-Host "  $Name is reachable (HTTP $($response.StatusCode))."
            return $true
        }
        else {
            Write-Warning "  $Name responded with HTTP $($response.StatusCode)."
            return $false
        }
    }
    catch {
        Write-Warning "  $Name check failed: $($_.Exception.Message)"
        return $false
    }
}

$httpFailures = 0
foreach ($target in $httpTargets) {
    if (-not (Test-HttpEndpoint -Name $target.Name -Url $target.Url)) {
        $httpFailures++
    }
}

$dockerFailures = 0
if (-not $SkipDocker) {
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Warning "docker command not found. Skipping container health checks."
    }
    else {
        $containers = @(
            @{ Name = 'mysql'; Port = 3306 },
            @{ Name = 'redis'; Port = 6379 },
            @{ Name = 'nacos'; Port = 8848 },
            @{ Name = 'rocketmq-namesrv'; Port = 9876 },
            @{ Name = 'rocketmq-broker'; Port = 10911 },
            @{ Name = 'seata-server'; Port = 8091 },
            @{ Name = 'sentinel-dashboard'; Port = 8858 }
        )

        foreach ($container in $containers) {
            $state = & docker inspect --format '{{.State.Status}}' $container.Name 2>$null
            if ($LASTEXITCODE -ne 0) {
                Write-Warning "  Unable to inspect container $($container.Name). Is it running?"
                $dockerFailures++
                continue
            }
            if ($state -ne 'running') {
                Write-Warning "  Container $($container.Name) state: $state"
                $dockerFailures++
            }
            elseif ($container.Port) {
                $result = Test-NetConnection -ComputerName '127.0.0.1' -Port $container.Port -WarningAction SilentlyContinue
                if (-not $result.TcpTestSucceeded) {
                    Write-Warning "  Port $($container.Port) for $($container.Name) is not reachable."
                    $dockerFailures++
                }
                else {
                    Write-Host "  $($container.Name) is running on port $($container.Port)."
                }
            }
        }
    }
}

$rocketFailures = 0
if (-not $SkipRocketMQ) {
    try {
        $scriptDir = Split-Path -Parent $PSCommandPath
        $mqScript = Join-Path $scriptDir 'mq-self-test.ps1'
        if (-not (Test-Path $mqScript)) {
            throw "MQ self-test script not found at $mqScript."
        }
        Write-Host "Running RocketMQ self-test..."
        & $mqScript -NameServer 'rocketmq-namesrv:9876' -Topic 'MEAL_EXPRESS_SMOKE' -ConsumerGroup 'MEAL_EXPRESS_SMOKE_GROUP' -DockerNetwork 'projects_dev-network' -SkipTopicInit
        if ($LASTEXITCODE -ne 0) {
            throw "mq-self-test.ps1 returned exit code $LASTEXITCODE"
        }
    }
    catch {
        Write-Warning "  RocketMQ self-test failed: $($_.Exception.Message)"
        $rocketFailures++
    }
}

$totalFailures = $httpFailures + $dockerFailures + $rocketFailures
if ($totalFailures -gt 0) {
    Write-Error "Smoke test failed: HTTP=$httpFailures Docker=$dockerFailures RocketMQ=$rocketFailures."
    exit 1
}

Write-Host "All smoke checks passed (HTTP + Docker + RocketMQ)."
