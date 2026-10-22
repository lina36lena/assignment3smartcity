param(
    [ValidateSet('demo', 'test')]
    [string]$Command = 'demo'
)

$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    if ($env:JAVA_HOME) {
        $javac = Join-Path $env:JAVA_HOME 'bin/javac.exe'
        $java = Join-Path $env:JAVA_HOME 'bin/java.exe'
    } else {
        $javac = 'javac'
        $java = 'java'
    }
    New-Item -ItemType Directory -Force -Path 'build' | Out-Null
    $sources = @(Get-ChildItem 'src', 'tests' -Recurse -Filter '*.java' |
        ForEach-Object { $_.FullName })
    & $javac -encoding UTF-8 -d build @sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    if ($Command -eq 'test') {
        & $java -cp build tests.FoundationTests
    } else {
        & $java -cp build app.CityPlanner
    }
    if ($LASTEXITCODE -ne 0) { throw "Command failed: $Command" }
} finally {
    Pop-Location
}
