import platform
import subprocess

def __execute__(game, is_guest):
    if is_guest:
        arg = "Guest"
    else:
        l = []
        with open("game/user/account.txt", "r") as file:
            l = file.readlines()
        arg = f"{l[0][:-1]}, {l[1][:-1]}, {l[2]}"

    command = ["game/java/openjdk-22/bin/java", 
               "-jar", 
               f"game/game/game.jar", 
               arg, 
               game]
    try:
        if platform.system() == "Windows":
            subprocess.run(command, check=True, creationflags=subprocess.CREATE_NO_WINDOW)
        elif platform.system() == "Linux":
            subprocess.run(command, check=True, shell=False)
    except subprocess.CalledProcessError as e:
        print("Error:", e)