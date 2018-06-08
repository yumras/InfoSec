import requests, json

URL = 'http://192.168.110.116:8090/api/v1/addrecord'
data = {'id': {'smrt0066': '12345678'},'name': {'hml': 'byebye'}, }
myHeader = {'Content-Type': 'application/json; charset=utf-8'}
# POST 방식 호출 (application/json)
res = requests.post(URL, headers=myHeader, data=json.dumps(data))
print(res)

