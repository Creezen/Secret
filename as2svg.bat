@echo off
setlocal enabledelayedexpansion
title Vector to SVG Converter
echo Please copy your Vector XML to clipboard, then press any key...
pause >nul

powershell -Command "$ErrorActionPreference='Stop'; Add-Type -AssemblyName System.Windows.Forms; $xmlText = [System.Windows.Forms.Clipboard]::GetText(); if ([string]::IsNullOrWhiteSpace($xmlText) -or $xmlText -notmatch '<vector') { Write-Host 'No valid vector XML found in clipboard.' -ForegroundColor Red; exit 1 } $xml=[xml]$xmlText; $svg='<svg xmlns=\"http://www.w3.org/2000/svg\"'; $w=$xml.vector.width -replace 'dp',''; $h=$xml.vector.height -replace 'dp',''; $vw=$xml.vector.viewportWidth; $vh=$xml.vector.viewportHeight; $svg+=' width=\"'+$w+'\" height=\"'+$h+'\" viewBox=\"0 0 '+$vw+' '+$vh+'\"'; $svg+='>'; if ($xml.vector.path) { foreach ($p in $xml.vector.path) { $d=$p.pathData; $fill=$p.fillColor; $stroke=$p.strokeColor; $sw=$p.strokeWidth; $svg+='<path d=\"'+$d+'\"'; if ($fill) { $svg+=' fill=\"'+$fill+'\"' } if ($stroke) { $svg+=' stroke=\"'+$stroke+'\"' } if ($sw) { $svg+=' stroke-width=\"'+$sw+'\"' } $svg+=' />'; } } $svg+='</svg>'; [System.Windows.Forms.Clipboard]::SetText($svg); Write-Host 'Conversion successful! SVG copied to clipboard.' -ForegroundColor Green"

if %errorlevel%==0 (
    echo Done. You can now paste the SVG.
) else (
    echo Conversion failed.
)
pause