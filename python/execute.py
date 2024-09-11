import os
import subprocess
import platform


def __execute__(game):
    with open("res/user/account.txt", "r") as file:
        account_list = file.readlines()

    current_path = os.getcwd()
    new_directory = f"{current_path}/res"
    os.chdir(new_directory)

    command = ["../java/openjdk-22/bin/java",
               "-jar",
               "../game/game.jar",
               account_list[0][:-1],
               account_list[1][:-1],
               account_list[2],
               game]
    try:
        if platform.system() == "Windows":
            subprocess.run(command, check=True, creationflags=subprocess.CREATE_NO_WINDOW)
        elif platform.system() == "Linux":
            subprocess.run(command, check=True, shell=False)
    except subprocess.CalledProcessError as e:
        print("Error:", e)

    os.chdir(current_path)
