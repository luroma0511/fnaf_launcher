import tkinter as tk
from tkinter import ttk
from res.retrieve import Jars
from threading import Thread
from time import sleep
import res.execute


class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.jar_load = False
        window_width = 1280
        window_height = 720
        screen_width = self.winfo_screenwidth()
        screen_height = self.winfo_screenheight()
        x = int((screen_width / 2) - (window_width / 2))
        y = int((screen_height / 2) - (window_height / 2))
        self.option = ""
        self.geometry(f"{window_width}x{window_height}+{x}+{y}")
        self.resizable(False, False)

    def update_attribute(self, value):
        self.option = value
        print(self.option)

    def __version_update_options__(self, files):
        self.options = files

    def __close__(self):
        self.jar_load = True
        self.destroy()

    def __version_options__(self):
        self.value = tk.StringVar()
        self.value.set("Select version")

        drop = ttk.Combobox(self, state="readonly", textvariable=self.value, values=self.options)
        drop.place(x=200, y=200)
        drop.pack()
        drop.bind("<<ComboboxSelected>>", lambda e: self.update_attribute(drop.get()))

        self.B = tk.Button(self, text="Play", command=self.__close__)
        self.B.place(x=200, y=200)
        self.B.pack()

    def __is_tk_app_running__(self):
        try:
            return self.winfo_viewable()
        except:
            return False


def __load_jars__(app: App):
    jars = Jars()
    app.__version_update_options__(jars.files)
    app.__version_options__()
    while app.__is_tk_app_running__():
        sleep(0.05)

    if app.jar_load:
        res.execute.__execute__(app.option)


app = App()
thread = Thread(target=__load_jars__, args=(app,), daemon=True)
thread.start()
app.mainloop()

thread.join()
