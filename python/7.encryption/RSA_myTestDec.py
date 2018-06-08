from Crypto.PublicKey import RSA
from Crypto.Cipher import AES, PKCS1_OAEP

file_in = open("encrypted_data.bin", "rb")

# 비대칭키 읽어온다.
private_key = RSA.import_key(open("private.pem").read())

enc_msg = file_in.read()

# 비밀키로 복호화한다.
cipher_rsa = PKCS1_OAEP.new(private_key)
dec_msg = cipher_rsa.decrypt(enc_msg)

print(dec_msg.decode("utf-8"))


