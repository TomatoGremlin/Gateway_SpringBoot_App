$apiUrl = "http://localhost:8083/json_api/history"

$clientRequests = @(
@{
    requestId = [guid]::NewGuid()
    baseCurrency = "EUR"
    timestamp = [math]::floor(([System.DateTime]::UtcNow - [System.DateTime]::ParseExact("1970-01-01", "yyyy-MM-dd", [System.Globalization.CultureInfo]::InvariantCulture)).TotalSeconds)
    client = "Client123"
    service = "EXT_SERVICE_1"
    period = 24
},
@{
    requestId = [guid]::NewGuid()
    baseCurrency = "EUR"
    timestamp = [math]::floor(([System.DateTime]::UtcNow - [System.DateTime]::ParseExact("1970-01-01", "yyyy-MM-dd", [System.Globalization.CultureInfo]::InvariantCulture)).TotalSeconds)
    client = "Client789"
    service = "EXT_SERVICE_2"
    period = 24
}
)

$headers = @{
    "Content-Type" = "application/json"
}

# Loop through each client request and make POST requests
foreach ($request in $clientRequests) {
    # Convert the request to JSON
    $requestBody = $request | ConvertTo-Json

    # Make the POST request
    $response = Invoke-RestMethod -Uri $apiUrl -Method Post -Headers $headers -Body $requestBody

    # Display the response
    Write-Host "Response for $($request.client):"
    $response
    Write-Host ""
}

#./makeClientRequestsHistory.ps1