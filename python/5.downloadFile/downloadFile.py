import shutil
import requests
import wget
import PIL
from PIL import Image

url = 'https://cdn.pixabay.com/photo/2018/01/20/14/26/panorama-3094696_960_720.jpg'

# Method 1
response = requests.get(url, stream=True)
if response.status_code == 200:
    with open('myImage.jpg', 'wb') as out_file:
        shutil.copyfileobj(response.raw, out_file)

# Method 2
filename = wget.download(url,out='./')
print(filename)

# Method 3
r = requests.get('https://cdn.pixabay.com/photo/2018/01/20/14/26/panorama-3094696_960_720.jpg', stream=True)
r.raise_for_status()
r.raw.decode_content = True  # Required to decompress gzip/deflate compressed responses.
with PIL.Image.open(r.raw) as img:
    img.show()
r.close()  # Safety when stream=True ensure the connection is released.