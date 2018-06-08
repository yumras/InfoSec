import requests
URL = 'http://192.168.110.116:8090/api/v1/addrecord'


# POST 방식 호출 (application/x-www-form-urlencoded)
data = {'ID': 'smrt0066', 'name': 'hml'}
res = requests.post(URL, data=data)
print(res)





