[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$Module,
    [string]$LogFile = "$(Join-Path (Split-Path -Parent $PSScriptRoot) '..\runtime-logs')\$Module.log"
)

$ErrorActionPreference = 'Stop'
$repoRoot = Resolve-Path -Path (Join-Path $PSScriptRoot '..\..')
$modulePath = Resolve-Path -Path (Join-Path $repoRoot $Module)

if (-not (Test-Path -Path $modulePath)) {
    throw "Module path '$Module' does not exist."
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $LogFile) | Out-Null
Remove-Item -Force $LogFile -ErrorAction SilentlyContinue

"Starting $Module @ $(Get-Date -Format s)" | Out-File -FilePath $LogFile

$mvnw = Join-Path $modulePath 'mvnw.cmd'
if (Test-Path $mvnw) {
    & $mvnw spring-boot:run -DskipTests *>> $LogFile
} else {
    & mvn -f $modulePath spring-boot:run -DskipTests *>> $LogFile
}
