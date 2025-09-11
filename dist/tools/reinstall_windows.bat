powershell -Command "$app = Get-WmiObject -Class Win32_Product | Where-Object { $_.Name -eq 'XPipe' }; if ($app -ne $null) {$app.Uninstall()}"
start /wait msiexec /i "%~1\build\dist\installers\windows\Release\en-us\xpipe-installer.msi" /quiet
