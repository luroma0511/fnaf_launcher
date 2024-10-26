from python.gamejolt import Gamejolt

API_URL = "https://api.gamejolt.com/api/game/v1/data-store/"
GAMEJOLT = Gamejolt()

def fetch_game_data():
    global API_URL
    global GAMEJOLT
    params = {
        "game_id": GAMEJOLT.gameID,
        "key": "launcher_version",
        "format": "json"
    }
    response = GAMEJOLT.__send__(API_URL + "?", params).json()['response']
    if (response['success'] == 'true'):
        return response['data']
    return None


def set_game_data(version):
    global API_URL
    global GAMEJOLT
    params = {
        "game_id": GAMEJOLT.gameID,
        "key": "launcher_version",
        "data": version,
        "format": "json"
    }
    response = GAMEJOLT.__send__(API_URL + "set/?", params).json()['response']
    if (response['success'] == 'true'):
        print("Successful!")
    else:
        print("Failed!")


def __update__(current_version):
    latest_version = fetch_game_data()
    print(latest_version)
    if (not latest_version):
        print("PROBLEM!")
    else:
        if latest_version != current_version:
            print("A new version is available. Please update your game.")
        else:
            print("You are using the latest version of the game.")

version = "4"
set_game_data(version)
# __update__(version)
