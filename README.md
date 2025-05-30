# Nonsense Generator
<img src="src/main/resources/org/example/images/logo.png" alt="Logo" width="250" height="300">

# Introduction
The Nonsense Generator allows you to get random sentences using as an imput the words from a sentence of your choosing.
# How to install
[Maven install guide](#maven)

[Google API key guide](#google-api-key)

[Nonsense Generator setup guide](#nonsense-generator-setup)
## Maven
This program requires Maven to run. If you have Maven already installed on your pc, you can skip to the next section.
To check if you have Maven installed from terminal you can use the command `mvn -v`
If Maven isn't installed you can either download it manually from [Maven's official website](https://maven.apache.org/download.cgi) or from terminal using these commands depending on the system

[Windows](#windows-install)

[Mac](#mac-install)

[Linux](#linux-install)
### Windows install
You can install Maven from shell using [Chocolatey](https://chocolatey.org/).
If you don't have chocolatey installed, you can install it from administrator PowerShell. First, run the Get-ExecutionPolicy. If it returns Restricted, run one of the two commands `Set-ExecutionPolicy AllSigned` or `Set-ExecutionPolicy Bypass -Scope Process`
Now run the following command in the Windows shell.
```
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1')) 
```
If there are no errors, Chocolatey will be installed. You can verify the installation using the `choco` or `choco -?` command.

Finally you can install Maven using the command `choco install maven -y` from administrator PowerShell.

### Mac install
You can install Maven from the terminal using [Homebrew](https://brew.sh).

You can check if Homebrew is installed by typing `brew --version` in the terminal.

Finally you can download Maven with the following command
```
brew install maven
```
To verify the program installed correctly you can type `mvn -version` in the terminal.

### Linux install
You can Install Maven on Linux by using the command
```
sudo apt install maven
```

To verify the program installed correctly you can type `mvn -version` in the terminal.

## Google Api key
To run this program you will need key to be able to run google's [Analyzing syntax api](https://cloud.google.com/natural-language/docs/analyzing-syntax).

First visit [Google cloud](https://console.cloud.google.com) and log into your account. Then open the drop-down menu on the top left and select `APIs & Services > Enabled APIs and Services`.

In this new page click on `Enable APIs and services` on the top of the page and search for `Cloud Natural Language API` and enable it.

Now that the API has been enabled we need to extract the key. To do so open the drop-down menu on the top left and select `APIs & Services > Credentials`.

On the **Service Accounts** section, select `Manage service accounts` then create a service account using `Create service account` on the top of the screen.

Once created, click on the three dots in the Actions tab then `Manage keys`. Finally create a new key and download it as **JSON**.

## Nonsense generator setup
To run the program it is recomended using [Intellij IDE](https://www.jetbrains.com/idea/) to run it as a Maven project.

Download the [latest release](https://github.com/Leggolta/SoftIng/releases) of the project, extract it to your pc and open it in Intellij.

Next copy the key you extracted earlier in the  `credentials` folder and name it `credentials.json`. In alternative you can just copy the content 
of the key in the empty `credentials.json` already in the folder.

Now on Intellij check for the Maven icon on the top right of the screen. In the screen that appears click on the spinning arrows then `Reload All Maven Projects` to download the dependencies.

Finally you can run the file using the `run.bat` or `run.command` depending on the system used.

To run the program from linux execute it from terminal using  `./run.sh`

In case of errors caused by missing permissions, run this command line before executing the code
**Linux/Mac:**
```
chmod +x run.sh
```




# Contributors
