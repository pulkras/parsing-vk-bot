import vk_api
import os

# from vk_api.longpoll import VkLongPoll, VkEventType
from vk_api.utils import get_random_id
from dotenv import load_dotenv

class Bot:
    vk_session = None
    vk_api_access = None
    authorised = False

    def __init__(self):
        load_dotenv()

        self.vk_api_access = self.do_auth()

        if self.vk_api_access is not None:
            self.authorized = True

        self.default_user_id = os.getenv("user_id")

    def do_auth(self):
        token = os.getenv("code")

        try:
            self.vk_session = vk_api.VkApi(token=token)
            return self.vk_session.get_api()
        except Exception as e:
            print(e)
            return None

    def send_message(self, receiver_user_id: str = None, message_text: str = "проверка"):
        if not self.authorized:
            print("unauthorized")
            return

        if receiver_user_id is None:
            receiver_user_id = self.default_user_id

        try:
            self.vk_api_access.messages.send(user_id=receiver_user_id, message=message_text, random_id=get_random_id())
        except Exception as e:
            print(e)
    
