from Crypto.Cipher import AES

key = b'Sixteen byte key'
cipher = AES.new(key, AES.MODE_EAX)
print(cipher)
nonce = cipher.nonce
print(nonce)
data=b"222"
ciphertext, tag = cipher.encrypt_and_digest(data)
print(ciphertext)
print(tag)


# let's assume that the key is somehow available again
cipher = AES.new(key, AES.MODE_EAX, nonce)
data = cipher.decrypt_and_verify(ciphertext, tag)
print(data)


