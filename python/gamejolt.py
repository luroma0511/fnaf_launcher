import requests
import hashlib


class Gamejolt:
    def __init__(self):
        self.gameID = "757875"
        self.key = "052918f9d5aa475732d8959161470e43"


    def __connection__(self):
        try:
            response = requests.get('https://www.google.com', timeout=5)
            response.raise_for_status()  # Raises an HTTPError for non-2xx status codes
            return True
        except requests.RequestException:
            return False


    def __send__(self, base_url, params):
        sorted_params = '&'.join(f'{k}={v}' for k, v in sorted(params.items()))
        url = base_url + sorted_params
        signature = hashlib.sha1((url + self.key).encode('utf-8')).hexdigest()
        
        url += "&signature=" + signature
        
        response = requests.get(url)
        return response


    def __authenticate__(self, username, token):
        base_url = 'https://api.gamejolt.com/api/game/v1_2/users/auth/?'
        params = {
            'game_id': self.gameID,
            'username': username,
            'user_token': token,
            'format': 'json'
        }
        
        response = self.__send__(base_url, params)
        return response.json()['response']['success'] == 'true'
    
    
    def __getid__(self, username):
        base_url = 'https://api.gamejolt.com/api/game/v1_2/users/?'
        params = {
            'game_id': self.gameID,
            'username': username,
            'format': 'json'
        }
        
        response = self.__send__(base_url, params).json()['response']
        if (response['success'] == 'true'):
            return response['users'][0]['id']
        return None