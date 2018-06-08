from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
import time

key = get_random_bytes(16)
print(key)
cipher = AES.new(key, AES.MODE_EAX)
data = b"This is a test."
ciphertext, tag = cipher.encrypt_and_digest(data)

file_out = open("encrypted.bin", "wb")
[file_out.write(x) for x in (cipher.nonce, tag, ciphertext)]

file_out.close()

# time.sleep(1)

file_in = open("encrypted.bin", "rb")
nonce, tag, ciphertext = (file_in.read(x) for x in (16, 16, -1))

# print(nonce)
# print(tag)
# print(ciphertext)
# let's assume that the key is somehow available again
cipher = AES.new(key, AES.MODE_EAX, nonce)
data = cipher.decrypt_and_verify(ciphertext, tag)
print(data)

file_in.close()