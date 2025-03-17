```sh
# Submit a user creation request
curl -X POST -d "name=newuser&email=newuser@example.com&password=userpass123&message=Please approve my account" http://localhost:8080/docs-web/api/user_request/submit

# Login as the admin, get the cookie
curl -X POST -d "username=admin&password=admin" http://localhost:8080/docs-web/api/user/login -c cookies.txt

# View the list of pending registration requests
curl -X GET http://localhost:8080/docs-web/api/user_request/pending -b cookies.txt

# Accept the registration request
curl -X POST http://localhost:8080/docs-web/api/user_request/accept/newuser -b cookies.txt

# Reject 

# View the user list
curl -X GET http://localhost:8080/docs-web/api/user/list -b cookies.txt | json_pp
```