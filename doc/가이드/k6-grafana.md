# K6 테스트와 그라파냐 연동
<details>
<summary><b>K6 설치</b></summary>

- choco 설치
    ```bash
    Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    ```
- K6 설치
    ```bash
    choco install k6
    ```