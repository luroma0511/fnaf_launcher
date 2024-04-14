import os

class Jars():

    def __init__(self):
        self.files = []
        self.list_files("res/versions", '')
    
    def list_files(self, directory, path):
        files = os.listdir(directory)
        for file in files:
            if (f'{path}{file}'.endswith('.jar')): 
                self.files.append(f'{path}{file}')
                print(f'{path}{file}')
                continue
            self.list_files(f'{directory}/{file}', f'{file}/')