[CmdletBinding()]
param(
    [string]$NameServer = 'rocketmq-namesrv:9876',
    [string]$Topic = 'MEAL_EXPRESS_SMOKE',
    [string]$ConsumerGroup = 'MEAL_EXPRESS_SMOKE_GROUP',
    [string]$DockerNetwork = 'projects_dev-network',
    [string]$MessageBody,
    [switch]$SkipTopicInit
)

$ErrorActionPreference = 'Stop'

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "docker command is required but was not found in PATH."
}

$timestampTag = [DateTime]::UtcNow.ToString('yyyyMMddHHmmss')
if (-not $MessageBody) {
    $MessageBody = "mealexpress-smoke-$timestampTag"
}
$messageKey = "mq-smoke-$timestampTag-$([Guid]::NewGuid().ToString('N'))"

Write-Verbose "Checking docker network '$DockerNetwork'..."
$networkArgs = @('network', 'inspect', $DockerNetwork)
$null = & docker @networkArgs 2>$null
if ($LASTEXITCODE -ne 0) {
    throw "Docker network '$DockerNetwork' is unavailable. Start dependencies with docker-up.ps1 first."
}

function Invoke-RocketMQCommand {
    param(
        [string]$CommandText,
        [hashtable]$Env = @{}
    )

    $dockerArgs = @('run', '--rm', '--network', $DockerNetwork)
    foreach ($key in $Env.Keys) {
        $dockerArgs += @('-e', "{0}={1}" -f $key, $Env[$key])
    }
    $wrapped = '"' + ($CommandText.Replace('"', '\"')) + '"'
    $dockerArgs += @('apache/rocketmq:4.9.4', 'sh', '-c', $wrapped)

    Write-Verbose ("docker command: docker {0}" -f ($dockerArgs -join ' '))

    $output = & docker @dockerArgs 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "RocketMQ command failed: $CommandText`n$output"
    }
    return ,$output
}

if (-not $SkipTopicInit.IsPresent) {
    Write-Host "Ensuring topic '$Topic' exists..."
    $topicCmd = "./mqadmin updateTopic -n {0} -c DefaultCluster -t {1} -r 1 -w 1" -f $NameServer, $Topic
    $null = Invoke-RocketMQCommand -CommandText $topicCmd
}

Write-Host "Sending test message to topic '$Topic'..."
$sendCmd = ('./mqadmin sendMessage -n {0} -t {1} -p {2} -k {3}' -f $NameServer, $Topic, $MessageBody, $messageKey)
Write-Verbose "sendMessage command: $sendCmd"
$sendOutput = Invoke-RocketMQCommand -CommandText $sendCmd
$sendDataLine = $sendOutput | Where-Object { $_ -and $_ -notmatch '^#' } | Select-Object -Last 1
if (-not $sendDataLine) {
    throw "sendMessage did not return data rows.`n$($sendOutput -join "`n")"
}
$sendTokens = [System.Text.RegularExpressions.Regex]::Split($sendDataLine.Trim(), '\s+')
if ($sendTokens.Length -lt 4) {
    throw "Unexpected sendMessage output format.`n$($sendOutput -join "`n")"
}
$brokerName = $sendTokens[0]
$queueId = [int]$sendTokens[1]
$messageId = $sendTokens[$sendTokens.Length - 1]
Write-Host "Message sent. MsgId=$messageId (Broker=$brokerName, Queue=$queueId)"

Write-Host "Querying queue offset for message key '$messageKey'..."
$queryCmd = "./mqadmin queryMsgByKey -n {0} -t {1} -k {2}" -f $NameServer, $Topic, $messageKey
Write-Verbose "queryMsgByKey command: $queryCmd"
$queryOutput = Invoke-RocketMQCommand -CommandText $queryCmd
$queryDataLine = $queryOutput | Where-Object { $_ -and $_ -notmatch '^#' } | Select-Object -Last 1
if (-not $queryDataLine) {
    throw "queryMsgByKey did not return message data.`n$($queryOutput -join "`n")"
}
$queryTokens = [System.Text.RegularExpressions.Regex]::Split($queryDataLine.Trim(), '\s+')
if ($queryTokens.Length -lt 3) {
    throw "Unable to resolve queue offset from queryMsgByKey output.`n$($queryOutput -join "`n")"
}
$queueOffset = [long]$queryTokens[2]

Write-Host "Consuming message from broker '$brokerName', queue $queueId, offset $queueOffset..."
$consumeCmd = "./mqadmin consumeMessage -n {0} -t {1} -c 1 -g {2} -b {3} -i {4} -o {5}" -f $NameServer, $Topic, $ConsumerGroup, $brokerName, $queueId, $queueOffset
Write-Verbose "consumeMessage command: $consumeCmd"
$consumeOutput = Invoke-RocketMQCommand -CommandText $consumeCmd
$consumeText = ($consumeOutput -join "`n")

if ($consumeText -notmatch 'Consume ok') {
    throw "consumeMessage did not report success.`n$consumeText"
}

if ($consumeText -notmatch [regex]::Escape($messageId)) {
    throw "consumeMessage output does not contain MsgId $messageId.`n$consumeText"
}

if ($consumeText -notmatch [regex]::Escape($MessageBody)) {
    throw "consumeMessage output does not contain expected body ($MessageBody).`n$consumeText"
}

Write-Host "RocketMQ self-test succeeded."
Write-Host "Summary:"
Write-Host "  NameServer : $NameServer"
Write-Host "  Topic      : $Topic"
Write-Host "  Group      : $ConsumerGroup"
Write-Host "  Broker     : $brokerName"
Write-Host "  QueueId    : $queueId"
Write-Host "  QueueOffset: $queueOffset"
Write-Host "  MessageKey : $messageKey"
Write-Host "  MessageId  : $messageId"
Write-Host "  Body       : $MessageBody"
