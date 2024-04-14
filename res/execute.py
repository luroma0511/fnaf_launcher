from pathlib import Path
import os
import subprocess

def __execute__(version):
    new_directory = f"{os.getcwd()}/res"
    os.chdir(new_directory)

    print(os.getcwd())

    # Check if the directory has been changed
    path = "versions"
    command = ["java\\jre1.8.0_351\\bin\\java.exe", "-jar", f"{path}/{version}"]
    try:
        subprocess.run(command, check=True, creationflags=subprocess.CREATE_NO_WINDOW)
    except subprocess.CalledProcessError as e:
        print("Error:", e)