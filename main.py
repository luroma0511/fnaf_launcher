import sys
from PyQt6.QtWidgets import QApplication
from python.window import App
        

q_app = QApplication(sys.argv)
app = App()
app.__widgets__()
app.show()
sys.exit(q_app.exec())