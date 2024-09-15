from time import sleep
import sys
import os
from PyQt6.QtWidgets import *
from PyQt6.QtCore import *
from PyQt6.QtGui import *

import python.execute
from python.gamejolt import Gamejolt

terminate = False
jar_load = False
option = ""
options = {"Candy's 2 Deluxe": "candys2",
           "Candy's 3 Deluxe": "candys3"}
launcher_close = False
user = None
version = 3
GAMEJOLT = Gamejolt()

class RunJar(QThread):
    def run(self):
        global terminate
        global jar_load
        global option
        global launcher_close
        global user
        while not terminate:
            sleep(0.05)
            
        if (jar_load):
            print(option)
            python.execute.__execute__(option, user == "Guest")
            
        if not launcher_close:
            terminate = False
            jar_load = False
            self.run()
            
        print("Terminated")


class App(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Candy's Deluxe Launcher")
        self.windowIcon = "assets/images/logo.png"
        self.setFixedSize(1280, 720)
        self.create()
        
        self.games_map = {}
        self.runJarThread = RunJar()
        self.runJarThread.start()
        
        id = QFontDatabase.addApplicationFont("assets/fonts/candys3/candysFont.ttf")
        self.candysFont = QFontDatabase.applicationFontFamilies(id)
        id = QFontDatabase.addApplicationFont("assets/fonts/candys3/captionFont.ttf")
        self.captionFont = QFontDatabase.applicationFontFamilies(id)
        
        
    def __widgets__(self):
        color = self.__set_color__(48, 0, 0)
        self.topbar = QLabel(self)
        self.topbar.setStyleSheet(f"background-color: {color};")
        self.topbar.setGeometry(0, 0, 1280, 60)
        
        self.bottombar = QLabel(self)
        self.bottombar.setStyleSheet(f"background-color: {color};")
        self.bottombar.setGeometry(0, 540, 1280, 200)
        
        self.image = QPixmap("assets/images/deluxe_image.png")
        self.img_label = QLabel(self)
        self.img_label.setPixmap(self.image)
        self.img_label.setGeometry(0, 60, 1280, 480)

        if not os.path.exists("game/user/account.txt"):
            with open ("game/user/account.txt", "w"):
                pass
        elif os.path.getsize("game/user/account.txt") != 0:
            with open ("game/user/account.txt", "r") as file:
                username = file.readline()[:-1]
                token = file.readline()
                if (token.endswith("\n")):
                    token = token[:-1]
            if (self.__authentication__(username, token)):
                self.__launcher_widgets__()
            else:
                with open ("game/user/account.txt", "w") as file:
                    pass
                self.__login_widgets__()
        else:
            self.__login_widgets__()


    def __login_widgets__(self):
        color = self.__set_color__(32, 0, 0)
        login_color = self.__set_color__(32, 32, 164)
        guest_color = self.__set_color__(32, 164, 32)
        
        y = 550
        self.username_label = QLabel("Username:", self)
        self.username_label.setFont(QFont(self.captionFont[0], 16))
        self.username_label.setStyleSheet(f"color: white;")
        self.username_label.move(450, y)
        self.username_label.show()

        y += 32
        self.username_entry = QLineEdit(self)
        self.username_entry.setFont(QFont(self.captionFont[0], 16))
        self.username_entry.setStyleSheet(f"""
        color: white;
        background-color: {color};
        """)
        self.username_entry.move(450, y)
        self.username_entry.show()
        
        self.login = QLabel("Login", self)
        self.login.setFont(QFont(self.candysFont[0], 18))
        self.login.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.login.setStyleSheet(f"""
        color: white;
        background-color: {login_color};
        border: 2px solid {color};
        """)
        self.login.setGeometry(750, y - 4, 100, 50)
        self.login.setCursor(Qt.CursorShape.PointingHandCursor)
        self.login.mousePressEvent = self.__login__
        self.login.show()
        
        y += 40
        self.token_label = QLabel("Token:", self)
        self.token_label.setFont(QFont(self.captionFont[0], 16))
        self.token_label.setStyleSheet(f"color: white;")
        self.token_label.move(450, y)
        self.token_label.show()

        y += 32
        self.token_entry = QLineEdit(self)
        self.token_entry.setFont(QFont(self.captionFont[0], 16))
        self.token_entry.setEchoMode(QLineEdit.EchoMode.Password)
        self.token_entry.setStyleSheet(f"""
        color: white;
        background-color: {color};
        """)
        self.token_entry.move(450, y)
        self.token_entry.show()
        
        self.guest = QLabel("Guest", self)
        self.guest.setFont(QFont(self.candysFont[0], 18))
        self.guest.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.guest.setStyleSheet(f"""
        color: white;
        background-color: {guest_color};
        border: 2px solid {color};
        """)
        self.guest.setGeometry(750, y - 4, 100, 50)
        self.guest.setCursor(Qt.CursorShape.PointingHandCursor)
        self.guest.mousePressEvent = self.__guest_login__
        self.guest.show()
        
    
    def __login_dispose__(self):
        self.username_label.deleteLater()
        self.username_entry.deleteLater()
        self.login.deleteLater()
        self.guest.deleteLater()
        self.token_label.deleteLater()
        self.token_entry.deleteLater()
        
        
    def __launcher_widgets__(self):
        color = self.__set_color__(50, 0, 0)
        play_color = self.__set_color__(32, 148, 32)
        play_hover_color = self.__set_color__(24, 96, 24)
        dropdown_color = self.__set_color__(28, 0, 0)
        signout_color = self.__set_color__(80, 0, 0)
        
        left_curly = "{"
        right_curly = "}"
        
        self.play = QPushButton("Play", self)
        self.play.setFont(QFont(self.candysFont[0], 32))
        self.play.setStyleSheet(f"""
        QPushButton{left_curly}
        color: white;
        background-color: {play_color};
        border: 2px solid;
        text-align: center;
        {right_curly}
        QPushButton::hover{left_curly}
        background-color: {play_hover_color};
        {right_curly}
        """)
        self.play.setGeometry(550, 620, 180, 80)
        self.play.setCursor(Qt.CursorShape.PointingHandCursor)
        self.play.mousePressEvent = self.__load__
        self.play.show()
        
        self.dropdown = QLabel(self)
        self.dropdown.setGeometry(0, 540, 1280, 60)
        self.dropdown.setStyleSheet(f"""
        background-color: {dropdown_color};
        """)
        self.dropdown.show()
        
        global options
        self.gamebox = QComboBox(self)
        for name in options:
            self.gamebox.addItem(f"Game: {name}")
            
        self.gamebox.setMaxVisibleItems(4)
        self.gamebox.setGeometry(0, 540, 324, 60)
        self.gamebox.setFont(QFont(self.candysFont[0], 20))
        self.gamebox.setStyleSheet(f"""
        color: white;
        background-color: {dropdown_color};
        """)
        self.gamebox.show()
        
        self.signout = QPushButton("Sign out", self)
        self.signout.setGeometry(1132, 540, 148, 60)
        self.signout.setFont(QFont(self.candysFont[0], 20))
        self.signout.setStyleSheet(f"""
        QPushButton{left_curly}
        color: white;
        background-color: {color};
        border: 2px solid;
        text-align: center;
        {right_curly}
        QPushButton::hover{left_curly}
        background-color: {signout_color};
        {right_curly}
        """)
        self.signout.setCursor(Qt.CursorShape.PointingHandCursor)
        self.signout.mousePressEvent = self.__logout__
        self.signout.show()
        
        self.update_button = QPushButton("Check for Updates", self)
        self.update_button.setGeometry(876, 540, 256, 60)
        self.update_button.setFont(QFont(self.candysFont[0], 20))
        self.update_button.setStyleSheet(f"""
        QPushButton{left_curly}
        color: white;
        background-color: {color};
        border: 2px solid;
        text-align: center;
        {right_curly}
        QPushButton::hover{left_curly}
        background-color: {signout_color};
        {right_curly}
        """)
        self.update_button.setCursor(Qt.CursorShape.PointingHandCursor)
        self.update_button.mousePressEvent = self.__checkupdate__
        self.update_button.show()
        
        global user
        self.account_text = QLabel(f"Logged in as: {user}", self)
        self.account_text.setFont(QFont(self.captionFont[0], 16))
        self.account_text.setStyleSheet(f"""
        color: white;
        """)
        self.account_text.move(16, 12)
        self.account_text.show()
        
        
    def __messagewindow__(self, text, icon, dialogFlag):
        self.dialog = QMessageBox(parent=self)
        self.dialog.setText(text)
        if (icon == QMessageBox.Icon.Critical): self.dialog.setWindowTitle("Error!")
        else: self.dialog.setWindowTitle("Popup Message")
        self.dialog.setIcon(icon)
        if (dialogFlag == "ok"):
            self.dialog.setStandardButtons(QMessageBox.StandardButton.Ok)
        elif (dialogFlag == "okcancel"):
            self.dialog.setStandardButtons(QMessageBox.StandardButton.Ok | QMessageBox.StandardButton.Cancel)
        response = self.dialog.exec()
        return response
        
        
    def __launcher_dispose__(self):
        self.play.deleteLater()
        self.dropdown.deleteLater()
        self.gamebox.deleteLater()
        self.signout.deleteLater()
        self.update_button.deleteLater()
        self.account_text.deleteLater()


    def __gotoLauncher__(self):
        self.__login_dispose__()
        self.__launcher_widgets__()
        
    
    def __gotoLogin__(self):
        self.__launcher_dispose__()
        self.__login_widgets__()
    
    
    def __checkupdate__(self, event):
        global GAMEJOLT
        global version
        if (GAMEJOLT.__connection__()):
            API_URL = "https://api.gamejolt.com/api/game/v1/data-store/"
            params = {
                "game_id": GAMEJOLT.gameID,
                "key": "launcher_version",
                "format": "json"
            }
            response = GAMEJOLT.__send__(API_URL + "?", params).json()['response']
            if (response['success'] == 'true'):
                latest_version = int(response['data'])
                if (version < latest_version):
                    self.__messagewindow__("""
                        An update is available! Please 
                        delete this launcher and download 
                        the latest version of the launcher!
                        """, QMessageBox.Icon.Information, "ok")
                else:
                    self.__messagewindow__("Your launcher is up to date!", QMessageBox.Icon.Information, "ok")
            else:
                self.__messagewindow__("Something went wrong!", QMessageBox.Icon.Critical, "ok")
        else:
            self.__messagewindow__("No internet connection detected!", QMessageBox.Icon.Critical, "ok")

    
    def __logout__(self, event):
        with open ("game/user/account.txt", "w"):
            pass
        self.__gotoLogin__()
    
        
    def __authentication__(self, username, token):
        global GAMEJOLT
        if (GAMEJOLT.__connection__()):
            if (GAMEJOLT.__authenticate__(username, token)):
                user_id = GAMEJOLT.__getid__(username)
                if (not user_id):
                    self.__messagewindow__("Something went wrong! Can't read user information!", QMessageBox.Icon.Critical, "ok")
                    return False
                    
                global user
                user = username
                with open("game/user/account.txt", "w") as file:
                    file.write(f"{user}\n{token}\n{user_id}")
                return True
            else:
                self.__messagewindow__("Incorrect username or token!", QMessageBox.Icon.Critical, "ok")
        else:
            self.__messagewindow__("No internet connection detected!", QMessageBox.Icon.Critical, "ok")
        return False
        
        
    def __login__(self, event):
        bypass = self.__authentication__(self.username_entry.text(), self.token_entry.text())
        if bypass: self.__gotoLauncher__()
            
            
    def __guest_login__(self, event):
        global user
        user = "Guest"
        self.__gotoLauncher__()
            

    def __load__(self, event):
        global jar_load
        global option
        global terminate
        global launcher_close
        global GAMEJOLT
        if (user != "Guest" and not GAMEJOLT.__connection__()):
            self.__messagewindow__("No internet connection detected!", QMessageBox.Icon.Critical, "ok")
            return
        terminate = True
        jar_load = True
        option = options[self.gamebox.itemText(self.gamebox.currentIndex())[6:]]
        if not launcher_close: return
        self.setVisible(False)
        self.__launcher_dispose__()
        self.runJarThread.wait()
        sys.exit()
        
    
    def __set_color__(self, r, g, b):
        return f"#{r:02x}{g:02x}{b:02x}"
    
        