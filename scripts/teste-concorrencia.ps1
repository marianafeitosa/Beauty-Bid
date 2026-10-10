$uriLogin = "http://localhost:8080/api/auth/login"
$uriLance = "http://localhost:8080/api/leiloes/6ac95502e02043140367d92a/lances"
$uriLeilao = "http://localhost:8080/api/leiloes/6ac95502e02043140367d92a"

# Os dois jobs aguardam o mesmo instante antes de enviar os lances.
$momentoDisparo = [DateTime]::UtcNow.AddSeconds(8)

Write-Host "Preparando teste de concorrencia..."
Write-Host ""

$mariJob = Start-Job -ArgumentList $uriLogin, $uriLance, $momentoDisparo -ScriptBlock {

    param(
        $uriLogin,
        $uriLance,
        $momentoDisparo
    )

    $loginBody = @{
        email = "mari@teste.com"
        senha = "123456"
    } | ConvertTo-Json -Compress

    Invoke-RestMethod `
        -Uri $uriLogin `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody `
        -SessionVariable sessao |
        Out-Null

    while ([DateTime]::UtcNow -lt $momentoDisparo) {
        Start-Sleep -Milliseconds 20
    }

    $lanceBody = @{
        valorCentavos = 25000
    } | ConvertTo-Json -Compress

    Invoke-RestMethod `
        -Uri $uriLance `
        -Method POST `
        -ContentType "application/json" `
        -Body $lanceBody `
        -WebSession $sessao
}

$anaJob = Start-Job -ArgumentList $uriLogin, $uriLance, $momentoDisparo -ScriptBlock {

    param(
        $uriLogin,
        $uriLance,
        $momentoDisparo
    )

    $loginBody = @{
        email = "ana@teste.com"
        senha = "123456"
    } | ConvertTo-Json -Compress

    Invoke-RestMethod `
        -Uri $uriLogin `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody `
        -SessionVariable sessao |
        Out-Null

    while ([DateTime]::UtcNow -lt $momentoDisparo) {
        Start-Sleep -Milliseconds 20
    }

    $lanceBody = @{
        valorCentavos = 26000
    } | ConvertTo-Json -Compress

    Invoke-RestMethod `
        -Uri $uriLance `
        -Method POST `
        -ContentType "application/json" `
        -Body $lanceBody `
        -WebSession $sessao
}

Write-Host "Aguardando os dois disparos..."

Wait-Job $mariJob, $anaJob | Out-Null

Write-Host ""
Write-Host "===== LANCE MARIANA - R$ 250 ====="

$mariResultado = Receive-Job $mariJob

$mariResultado |
    Format-List id,
                leilaoId,
                usuarioId,
                valorCentavos,
                status,
                motivoStatus,
                criadoEm

Write-Host ""
Write-Host "===== LANCE ANA - R$ 260 ====="

$anaResultado = Receive-Job $anaJob

$anaResultado |
    Format-List id,
                leilaoId,
                usuarioId,
                valorCentavos,
                status,
                motivoStatus,
                criadoEm

Remove-Job $mariJob, $anaJob

Write-Host ""
Write-Host "Aguardando o RabbitMQ processar..."

Start-Sleep -Seconds 4

Write-Host ""
Write-Host "===== RESULTADO FINAL DO LEILAO ====="

$resultadoFinal = Invoke-RestMethod `
    -Uri $uriLeilao `
    -Method GET

$resultadoFinal |
    Format-List id,
                titulo,
                valorAtualCentavos,
                vencedorId,
                status