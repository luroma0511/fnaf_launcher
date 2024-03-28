import os

class Jars():

    def __init__(self):
        self.files = self.list_files("res/game/versions")
    
    def list_files(self, directory):
        arr = []
        files = os.listdir(directory)
        for file in files:
            if (file == 'dependencies'): continue
            print(file)
            arr.append(file)
        return arr